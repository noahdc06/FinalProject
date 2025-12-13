package com.university.grocerystore.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.university.grocerystore.api.GroceryStore;
import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;
import com.university.grocerystore.model.Produce;

/**
 * Implementation of GroceryStore using ArrayList with polymorphic handling.
 * Demonstrates polymorphism, SOLID principles, and defensive programming.
 */
public class GroceryStoreImpl implements GroceryStore {
    
    private final List<GroceryProduct> inventory;
    private final Map<String, GroceryProduct> idIndex;
    
    /**
     * Creates a new empty grocery store.
     */
    public GroceryStoreImpl() {
        this.inventory = new ArrayList<>();
        this.idIndex = new HashMap<>();
    }
    
    /**
     * Creates a grocery store with initial products.
     * 
     * @param initialProducts products to add initially
     */
    public GroceryStoreImpl(Collection<GroceryProduct> initialProducts) {
        this();
        if (initialProducts != null) {
            for (GroceryProduct product : initialProducts) {
                addProduct(product);
            }
        }
    }
    
    @Override
    public synchronized boolean addProduct(GroceryProduct product) {
        if (product == null) {
            throw new NullPointerException("Cannot add null product");
        }
        
        if (idIndex.containsKey(product.getId())) {
            return false;
        }
        
        inventory.add(product);
        idIndex.put(product.getId(), product);
        return true;
    }
    
    @Override
    public synchronized Optional<GroceryProduct> removeProduct(String id) {
        if (id == null) {
            return Optional.empty();
        }
        
        GroceryProduct product = idIndex.remove(id);
        if (product != null) {
            inventory.remove(product);
            return Optional.of(product);
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<GroceryProduct> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(idIndex.get(id));
    }
    
    @Override
    public List<GroceryProduct> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = name.toLowerCase().trim();
        return inventory.stream()
            .filter(p -> p.getName().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> searchByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = brand.toLowerCase().trim();
        return inventory.stream()
            .filter(p -> p.getBrand().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> getProductsByType(GroceryProduct.ProductType type) {
        if (type == null) {
            return new ArrayList<>();
        }
        
        return inventory.stream()
            .filter(p -> p.getType() == type)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Perishable> getPerishableProducts() {
        return inventory.stream()
            .filter(p -> p instanceof Perishable)
            .map(p -> (Perishable) p)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> filterProducts(Predicate<GroceryProduct> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        return inventory.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> getProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return new ArrayList<>();
        }
        
        return inventory.stream()
            .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> getProductsByYear(int year) {
        return inventory.stream()
            .filter(p -> p.getProductionYear() == year)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> getAllProductsSorted() {
        List<GroceryProduct> sorted = new ArrayList<>(inventory);
        Collections.sort(sorted);
        return sorted;
    }
    
    @Override
    public List<GroceryProduct> getAllProducts() {
        return new ArrayList<>(inventory);
    }
    
    @Override
    public double getTotalInventoryValue() {
        return inventory.stream()
            .mapToDouble(GroceryProduct::getPrice)
            .sum();
    }
    
    @Override
    public double getTotalDiscountedValue() {
        return inventory.stream()
            .mapToDouble(GroceryProduct::getDiscountedPrice)
            .sum();
    }
    
    @Override
    public InventoryStats getInventoryStats() {
        if (inventory.isEmpty()) {
            return new InventoryStats(0, 0, 0, 0, 0, 0);
        }
        
        List<Double> prices = inventory.stream()
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
        
        int uniqueTypes = (int) inventory.stream()
            .map(GroceryProduct::getType)
            .distinct()
            .count();
        
        int perishableCount = (int) inventory.stream()
            .filter(p -> p instanceof Perishable)
            .count();
        
        int nonPerishableCount = (int) inventory.stream()
            .filter(p -> !(p instanceof Perishable))
            .count();
        
        return new InventoryStats(
            inventory.size(),
            averagePrice,
            medianPrice,
            uniqueTypes,
            perishableCount,
            nonPerishableCount
        );
    }
    
    @Override
    public synchronized void clearInventory() {
        inventory.clear();
        idIndex.clear();
    }
    
    @Override
    public int size() {
        return inventory.size();
    }
    
    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }
    
    @Override
    public List<GroceryProduct> findRecentProducts(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years cannot be negative: " + years);
        }
        
        int currentYear = java.time.Year.now().getValue();
        int cutoffYear = currentYear - years;
        
        return inventory.stream()
            .filter(product -> product.getProductionYear() >= cutoffYear)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> findByBrands(String... brands) {
        if (brands == null || brands.length == 0) {
            return new ArrayList<>();
        }
        
        Set<String> brandSet = Arrays.stream(brands)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
        
        if (brandSet.isEmpty()) {
            return new ArrayList<>();
        }
        
        return inventory.stream()
            .filter(product -> brandSet.contains(product.getBrand()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> findWithPredicate(Predicate<GroceryProduct> condition) {
        if (condition == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        return inventory.stream()
            .filter(condition)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryProduct> getSorted(Comparator<GroceryProduct> comparator) {
        if (comparator == null) {
            throw new NullPointerException("Comparator cannot be null");
        }
        
        return inventory.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
    
    /**
     * Demonstrates polymorphic behavior by getting display info for all products.
     * 
     * @return list of display strings
     */
    public List<String> getAllDisplayInfo() {
        return inventory.stream()
            .map(GroceryProduct::getDisplayInfo)
            .collect(Collectors.toList());
    }
    
    /**
     * Groups products by type for reporting.
     * 
     * @return map of type to products
     */
    public Map<GroceryProduct.ProductType, List<GroceryProduct>> groupByType() {
        return inventory.stream()
            .collect(Collectors.groupingBy(GroceryProduct::getType));
    }
    
    /**
     * Gets products with active discounts.
     * 
     * @return list of discounted products
     */
    public List<GroceryProduct> getDiscountedProducts() {
        return inventory.stream()
            .filter(p -> p.getDiscountRate() > 0)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculates total savings from discounts.
     * 
     * @return total discount amount
     */
    public double getTotalDiscountAmount() {
        return inventory.stream()
            .mapToDouble(p -> p.getPrice() * p.getDiscountRate())
            .sum();
    }
    
    @Override
    public String toString() {
        return String.format("GroceryStore[Size=%d, Types=%d, Value=$%.2f]",
            size(),
            groupByType().size(),
            getTotalInventoryValue());
    }
}