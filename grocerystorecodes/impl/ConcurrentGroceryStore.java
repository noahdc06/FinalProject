package com.university.grocerystore.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.university.grocerystore.api.GroceryStore;
import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;
import com.university.grocerystore.model.Produce;

/**
 * Thread-safe implementation of GroceryStore using synchronization primitives.
 * Demonstrates concurrency patterns and thread safety in multi-threaded environments.
 * 
 * <p>This implementation uses ReentrantReadWriteLock to optimize for read-heavy workloads
 * and ConcurrentHashMap for thread-safe storage with minimal locking overhead.</p>
 */
public class ConcurrentGroceryStore implements GroceryStore {
    
    private final Map<String, GroceryProduct> products;
    private final ReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    
    /**
     * Creates a new thread-safe grocery store.
     */
    public ConcurrentGroceryStore() {
        this.products = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }
    
    /**
     * Creates a grocery store with initial products.
     * 
     * @param initialProducts products to add initially
     */
    public ConcurrentGroceryStore(Collection<GroceryProduct> initialProducts) {
        this();
        if (initialProducts != null) {
            for (GroceryProduct product : initialProducts) {
                addProduct(product);
            }
        }
    }
    
    @Override
    public boolean addProduct(GroceryProduct product) {
        if (product == null) {
            throw new NullPointerException("Cannot add null product");
        }
        
        writeLock.lock();
        try {
            return products.putIfAbsent(product.getId(), product) == null;
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public Optional<GroceryProduct> removeProduct(String id) {
        if (id == null) {
            return Optional.empty();
        }
        
        writeLock.lock();
        try {
            return Optional.ofNullable(products.remove(id));
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public Optional<GroceryProduct> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        
        readLock.lock();
        try {
            return Optional.ofNullable(products.get(id));
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = name.toLowerCase().trim();
        readLock.lock();
        try {
            return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> searchByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = brand.toLowerCase().trim();
        readLock.lock();
        try {
            return products.values().stream()
                .filter(p -> p.getBrand().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> getProductsByType(GroceryProduct.ProductType type) {
        if (type == null) {
            return new ArrayList<>();
        }
        
        readLock.lock();
        try {
            return products.values().stream()
                .filter(p -> p.getType() == type)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<Perishable> getPerishableProducts() {
        readLock.lock();
        try {
            return products.values().stream()
                .filter(p -> p instanceof Perishable)
                .map(p -> (Perishable) p)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> filterProducts(Predicate<GroceryProduct> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        readLock.lock();
        try {
            return products.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> getProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return new ArrayList<>();
        }
        
        readLock.lock();
        try {
            return products.values().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> getProductsByYear(int year) {
        readLock.lock();
        try {
            return products.values().stream()
                .filter(p -> p.getProductionYear() == year)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> getAllProductsSorted() {
        readLock.lock();
        try {
            List<GroceryProduct> sorted = new ArrayList<>(products.values());
            Collections.sort(sorted);
            return sorted;
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> getAllProducts() {
        readLock.lock();
        try {
            return new ArrayList<>(products.values());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public double getTotalInventoryValue() {
        readLock.lock();
        try {
            return products.values().stream()
                .mapToDouble(GroceryProduct::getPrice)
                .sum();
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public double getTotalDiscountedValue() {
        readLock.lock();
        try {
            return products.values().stream()
                .mapToDouble(GroceryProduct::getDiscountedPrice)
                .sum();
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public InventoryStats getInventoryStats() {
        readLock.lock();
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
                .filter(p -> p instanceof Perishable)
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
            readLock.unlock();
        }
    }
    
    @Override
    public void clearInventory() {
        writeLock.lock();
        try {
            products.clear();
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public int size() {
        readLock.lock();
        try {
            return products.size();
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return products.isEmpty();
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> findRecentProducts(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years cannot be negative: " + years);
        }
        
        int currentYear = java.time.Year.now().getValue();
        int cutoffYear = currentYear - years;
        
        readLock.lock();
        try {
            return products.values().stream()
                .filter(product -> product.getProductionYear() >= cutoffYear)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> findByBrands(String... brands) {
        if (brands == null || brands.length == 0) {
            return new ArrayList<>();
        }
        
        Set<String> brandSet = java.util.Arrays.stream(brands)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
        
        if (brandSet.isEmpty()) {
            return new ArrayList<>();
        }
        
        readLock.lock();
        try {
            return products.values().stream()
                .filter(product -> brandSet.contains(product.getBrand()))
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> findWithPredicate(Predicate<GroceryProduct> condition) {
        if (condition == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        readLock.lock();
        try {
            return products.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> getSorted(Comparator<GroceryProduct> comparator) {
        if (comparator == null) {
            throw new NullPointerException("Comparator cannot be null");
        }
        
        readLock.lock();
        try {
            return products.values().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets all display info for products (thread-safe).
     * 
     * @return list of display strings
     */
    public List<String> getAllDisplayInfo() {
        readLock.lock();
        try {
            return products.values().stream()
                .map(GroceryProduct::getDisplayInfo)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Groups products by type for reporting (thread-safe).
     * 
     * @return map of type to products
     */
    public Map<GroceryProduct.ProductType, List<GroceryProduct>> groupByType() {
        readLock.lock();
        try {
            return products.values().stream()
                .collect(Collectors.groupingBy(GroceryProduct::getType));
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets products with active discounts (thread-safe).
     * 
     * @return list of discounted products
     */
    public List<GroceryProduct> getDiscountedProducts() {
        readLock.lock();
        try {
            return products.values().stream()
                .filter(p -> p.getDiscountRate() > 0)
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Calculates total savings from discounts (thread-safe).
     * 
     * @return total discount amount
     */
    public double getTotalDiscountAmount() {
        readLock.lock();
        try {
            return products.values().stream()
                .mapToDouble(p -> p.getPrice() * p.getDiscountRate())
                .sum();
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public String toString() {
        readLock.lock();
        try {
            return String.format("ConcurrentGroceryStore[Size=%d, Types=%d, Value=$%.2f]",
                size(),
                groupByType().size(),
                getTotalInventoryValue());
        } finally {
            readLock.unlock();
        }
    }
}