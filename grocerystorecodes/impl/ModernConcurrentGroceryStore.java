package com.university.grocerystore.impl;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.grocerystore.api.GroceryStore;
import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;
import com.university.grocerystore.model.Produce;

/**
 * Modern thread-safe implementation of GroceryStore using best practices.
 * Features:
 * - StampedLock for optimized read performance
 * - ExecutorService for async operations
 * - CompletableFuture for non-blocking operations
 * - Proper resource management with AutoCloseable
 * - Virtual thread support (when available)
 */
public class ModernConcurrentGroceryStore implements GroceryStore, AutoCloseable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ModernConcurrentGroceryStore.class);
    
    private final Map<String, GroceryProduct> products;
    private final StampedLock stampedLock;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;
    private volatile boolean closed = false;
    
    /**
     * Creates a new modern thread-safe grocery store.
     */
    public ModernConcurrentGroceryStore() {
        this.products = new ConcurrentHashMap<>();
        this.stampedLock = new StampedLock();
        
        // Use ForkJoinPool for better work-stealing behavior
        this.executorService = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors(),
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            null, 
            true // Enable async mode for better throughput
        );
        
        this.scheduledExecutor = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "GroceryStore-Scheduler");
            t.setDaemon(true);
            return t;
        });
        
        // Schedule periodic cleanup tasks
        scheduleMaintenanceTasks();
    }
    
    /**
     * Creates a grocery store with initial products.
     * 
     * @param initialProducts products to add initially
     */
    public ModernConcurrentGroceryStore(Collection<GroceryProduct> initialProducts) {
        this();
        if (initialProducts != null) {
            // Parallel addition for better performance
            initialProducts.parallelStream().forEach(this::addProduct);
        }
    }
    
    private void scheduleMaintenanceTasks() {
        // Example: periodic cache cleanup or metrics collection
        scheduledExecutor.scheduleAtFixedRate(
            this::performMaintenance, 
            1, 1, TimeUnit.HOURS
        );
    }
    
    private void performMaintenance() {
        if (!closed) {
            // Maintenance tasks like clearing old data, collecting metrics, etc.
            // This is a placeholder for actual maintenance logic
        }
    }
    
    @Override
    public boolean addProduct(GroceryProduct product) {
        Objects.requireNonNull(product, "Product cannot be null");
        ensureNotClosed();
        
        long stamp = stampedLock.writeLock();
        try {
            return products.putIfAbsent(product.getId(), product) == null;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }
    
    /**
     * Adds product asynchronously.
     * 
     * @param product the product to add
     * @return CompletableFuture with the result
     */
    public CompletableFuture<Boolean> addProductAsync(GroceryProduct product) {
        return CompletableFuture.supplyAsync(
            () -> addProduct(product), 
            executorService
        );
    }
    
    /**
     * Adds multiple products in batch asynchronously.
     * 
     * @param products collection of products to add
     * @return CompletableFuture with results map
     */
    public CompletableFuture<Map<String, Boolean>> addProductsBatchAsync(Collection<GroceryProduct> products) {
        List<CompletableFuture<Map.Entry<String, Boolean>>> futures = products.stream()
            .map(product -> CompletableFuture.supplyAsync(
                () -> Map.entry(product.getId(), addProduct(product)),
                executorService
            ))
            .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                )));
    }
    
    @Override
    public Optional<GroceryProduct> removeProduct(String id) {
        if (id == null) {
            return Optional.empty();
        }
        ensureNotClosed();
        
        long stamp = stampedLock.writeLock();
        try {
            return Optional.ofNullable(products.remove(id));
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }
    
    @Override
    public Optional<GroceryProduct> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        ensureNotClosed();
        
        // Try optimistic read first for better performance
        long stamp = stampedLock.tryOptimisticRead();
        GroceryProduct product = products.get(id);
        
        if (!stampedLock.validate(stamp)) {
            // Optimistic read failed, acquire read lock
            stamp = stampedLock.readLock();
            try {
                product = products.get(id);
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        
        return Optional.ofNullable(product);
    }
    
    /**
     * Finds product by ID asynchronously.
     * 
     * @param id the product ID
     * @return CompletableFuture with the result
     */
    public CompletableFuture<Optional<GroceryProduct>> findByIdAsync(String id) {
        return CompletableFuture.supplyAsync(
            () -> findById(id), 
            executorService
        );
    }
    
    /**
     * Finds multiple products by IDs asynchronously.
     * 
     * @param ids list of product IDs
     * @return CompletableFuture with results map
     */
    public CompletableFuture<Map<String, GroceryProduct>> findByIdsAsync(List<String> ids) {
        List<CompletableFuture<Map.Entry<String, Optional<GroceryProduct>>>> futures = ids.stream()
            .map(id -> CompletableFuture.supplyAsync(
                () -> Map.entry(id, findById(id)),
                executorService
            ))
            .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().get()
                )));
    }
    
    @Override
    public List<GroceryProduct> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        ensureNotClosed();
        
        String searchTerm = name.toLowerCase().trim();
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    /**
     * Searches by name asynchronously.
     * 
     * @param name the name to search for
     * @return CompletableFuture with the results
     */
    public CompletableFuture<List<GroceryProduct>> searchByNameAsync(String name) {
        return CompletableFuture.supplyAsync(
            () -> searchByName(name), 
            executorService
        );
    }
    
    @Override
    public List<GroceryProduct> searchByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return new ArrayList<>();
        }
        ensureNotClosed();
        
        String searchTerm = brand.toLowerCase().trim();
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(p -> p.getBrand().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> getProductsByType(GroceryProduct.ProductType type) {
        if (type == null) {
            return new ArrayList<>();
        }
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(p -> p.getType() == type)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<Perishable> getPerishableProducts() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(Perishable.class::isInstance)
                .map(Perishable.class::cast)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> filterProducts(Predicate<GroceryProduct> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(predicate)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> getProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return new ArrayList<>();
        }
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> getProductsByYear(int year) {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(p -> p.getProductionYear() == year)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> getAllProductsSorted() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().stream()
                .sorted()
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> getAllProducts() {
        ensureNotClosed();
        
        // Use optimistic read for better performance
        long stamp = stampedLock.tryOptimisticRead();
        List<GroceryProduct> result = new ArrayList<>(products.values());
        
        if (!stampedLock.validate(stamp)) {
            stamp = stampedLock.readLock();
            try {
                result = new ArrayList<>(products.values());
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        
        return result;
    }
    
    @Override
    public double getTotalInventoryValue() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .mapToDouble(GroceryProduct::getPrice)
                .sum();
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    /**
     * Gets total inventory value asynchronously.
     * 
     * @return CompletableFuture with the total value
     */
    public CompletableFuture<Double> getTotalInventoryValueAsync() {
        return CompletableFuture.supplyAsync(
            this::getTotalInventoryValue, 
            executorService
        );
    }
    
    @Override
    public double getTotalDiscountedValue() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .mapToDouble(GroceryProduct::getDiscountedPrice)
                .sum();
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public InventoryStats getInventoryStats() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            if (products.isEmpty()) {
                return new InventoryStats(0, 0, 0, 0, 0, 0);
            }
            
            List<Double> prices = products.values().stream()
                .map(GroceryProduct::getPrice)
                .sorted()
                .collect(Collectors.toList());
            
            double averagePrice = prices.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            
            double medianPrice = prices.size() % 2 == 0
                ? (prices.get(prices.size() / 2 - 1) + prices.get(prices.size() / 2)) / 2
                : prices.get(prices.size() / 2);
            
            int uniqueTypes = (int) products.values().stream()
                .map(GroceryProduct::getType)
                .distinct()
                .count();
            
            int perishableCount = (int) products.values().stream()
                .filter(Perishable.class::isInstance)
                .count();
            
            int nonPerishableCount = (int) products.values().stream()
                .filter(p -> !(p instanceof Perishable))
                .count();
            
            return new InventoryStats(
                products.size(),
                averagePrice,
                medianPrice,
                uniqueTypes,
                perishableCount,
                nonPerishableCount
            );
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    /**
     * Gets inventory statistics asynchronously.
     * 
     * @return CompletableFuture with the statistics
     */
    public CompletableFuture<InventoryStats> getInventoryStatsAsync() {
        return CompletableFuture.supplyAsync(
            this::getInventoryStats, 
            executorService
        );
    }
    
    @Override
    public void clearInventory() {
        ensureNotClosed();
        
        long stamp = stampedLock.writeLock();
        try {
            products.clear();
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }
    
    @Override
    public int size() {
        ensureNotClosed();
        
        // Use optimistic read for size check
        long stamp = stampedLock.tryOptimisticRead();
        int size = products.size();
        
        if (!stampedLock.validate(stamp)) {
            stamp = stampedLock.readLock();
            try {
                size = products.size();
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    
    @Override
    public List<GroceryProduct> findRecentProducts(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years cannot be negative: " + years);
        }
        ensureNotClosed();
        
        int currentYear = java.time.Year.now().getValue();
        int cutoffYear = currentYear - years;
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(product -> product.getProductionYear() >= cutoffYear)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> findByBrands(String... brands) {
        if (brands == null || brands.length == 0) {
            return new ArrayList<>();
        }
        ensureNotClosed();
        
        Set<String> brandSet = Arrays.stream(brands)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
        
        if (brandSet.isEmpty()) {
            return new ArrayList<>();
        }
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(product -> brandSet.contains(product.getBrand()))
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> findWithPredicate(Predicate<GroceryProduct> condition) {
        Objects.requireNonNull(condition, "Predicate cannot be null");
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(condition)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    @Override
    public List<GroceryProduct> getSorted(Comparator<GroceryProduct> comparator) {
        Objects.requireNonNull(comparator, "Comparator cannot be null");
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    /**
     * Performs parallel search across multiple criteria.
     * 
     * @param name optional name search term
     * @param brand optional brand search term
     * @param type optional product type
     * @return CompletableFuture with combined results
     */
    public CompletableFuture<List<GroceryProduct>> parallelSearchAsync(
            String name, String brand, GroceryProduct.ProductType type) {
        
        List<CompletableFuture<List<GroceryProduct>>> searches = new ArrayList<>();
        
        if (name != null && !name.trim().isEmpty()) {
            searches.add(searchByNameAsync(name));
        }
        if (brand != null && !brand.trim().isEmpty()) {
            searches.add(CompletableFuture.supplyAsync(
                () -> searchByBrand(brand), executorService));
        }
        if (type != null) {
            searches.add(CompletableFuture.supplyAsync(
                () -> getProductsByType(type), executorService));
        }
        
        if (searches.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        return CompletableFuture.allOf(searches.toArray(new CompletableFuture[0]))
            .thenApply(v -> searches.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList()));
    }
    
    /**
     * Groups products by type for reporting.
     * 
     * @return map of type to products
     */
    public Map<GroceryProduct.ProductType, List<GroceryProduct>> groupByType() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().stream()
                .collect(Collectors.groupingBy(GroceryProduct::getType));
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    /**
     * Gets products with active discounts.
     * 
     * @return list of discounted products
     */
    public List<GroceryProduct> getDiscountedProducts() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .filter(p -> p.getDiscountRate() > 0)
                .collect(Collectors.toList());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    /**
     * Calculates total savings from discounts.
     * 
     * @return total discount amount
     */
    public double getTotalDiscountAmount() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return products.values().parallelStream()
                .mapToDouble(p -> p.getPrice() * p.getDiscountRate())
                .sum();
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
    
    private void ensureNotClosed() {
        if (closed) {
            throw new IllegalStateException("GroceryStore has been closed");
        }
    }
    
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            
            // Shutdown executors gracefully
            executorService.shutdown();
            scheduledExecutor.shutdown();
            
            try {
                // Wait for existing tasks to complete
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        LOGGER.error("ExecutorService did not terminate within timeout");
                    }
                }
                
                if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledExecutor.shutdownNow();
                    if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        LOGGER.error("ScheduledExecutor did not terminate within timeout");
                    }
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Override
    public String toString() {
        ensureNotClosed();
        
        long stamp = stampedLock.readLock();
        try {
            return String.format("ModernConcurrentGroceryStore[Size=%d, Types=%d, Value=$%.2f]",
                size(),
                groupByType().size(),
                getTotalInventoryValue());
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }
}