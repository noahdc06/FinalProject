package com.university.grocerystore.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.university.grocerystore.api.GroceryStoreAPI;
import com.university.grocerystore.model.GroceryItem;

/**
 * ArrayList-based implementation of the GroceryStoreAPI.
 * 
 * <p>This implementation uses an ArrayList for storage and enforces
 * SKU uniqueness. All collection returns are defensive copies to
 * maintain encapsulation.</p>
 * 
 * <p>Performance characteristics:</p>
 * <ul>
 *   <li>add: O(n) - due to uniqueness check</li>
 *   <li>removeBySku: O(n) - linear search required</li>
 *   <li>findBySku: O(n) - linear search required</li>
 *   <li>size: O(1) - ArrayList maintains size</li>
 * </ul>
 * 
 * <p>Note: This implementation is NOT thread-safe. For concurrent access,
 * consider using synchronization or ConcurrentHashMap.</p>
 */
public class GroceryStoreArrayList implements GroceryStoreAPI {
    
    private final List<GroceryItem> inventory;
    
    /**
     * Creates a new empty grocery store.
     */
    public GroceryStoreArrayList() {
        this.inventory = new ArrayList<>();
    }
    
    /**
     * Creates a grocery store with initial items.
     * 
     * @param initialItems items to add initially (may be null or empty)
     */
    public GroceryStoreArrayList(Collection<GroceryItem> initialItems) {
        this.inventory = new ArrayList<>();
        if (initialItems != null) {
            for (GroceryItem item : initialItems) {
                add(item);
            }
        }
    }
    
    @Override
    public boolean add(GroceryItem item) {
        if (item == null) {
            return false;
        }
        
        // Check for duplicate SKU
        if (findBySku(item.getSku()) != null) {
            return false;
        }
        
        return inventory.add(item);
    }
    
    @Override
    public boolean removeBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            return false;
        }
        
        return inventory.removeIf(item -> item.getSku().equals(sku));
    }
    
    @Override
    public GroceryItem findBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            return null;
        }
        
        for (GroceryItem item : inventory) {
            if (item.getSku().equals(sku)) {
                return item;
            }
        }
        return null;
    }
    
    @Override
    public List<GroceryItem> findByName(String nameQuery) {
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String query = nameQuery.toLowerCase().trim();
        return inventory.stream()
            .filter(item -> item.getName().toLowerCase().contains(query))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryItem> findByBrand(String brandQuery) {
        if (brandQuery == null || brandQuery.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String query = brandQuery.toLowerCase().trim();
        return inventory.stream()
            .filter(item -> item.getBrand().toLowerCase().contains(query))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryItem> findByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException(
                "Minimum price cannot be greater than maximum price");
        }
        
        return inventory.stream()
            .filter(item -> item.getPrice() >= minPrice && item.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GroceryItem> findByExpirationYear(int year) {
        return inventory.stream()
            .filter(item -> item.getExpirationYear() == year)
            .collect(Collectors.toList());
    }
    
    @Override
    public int size() {
        return inventory.size();
    }
    
    @Override
    public double inventoryValue() {
        return inventory.stream()
            .mapToDouble(GroceryItem::getPrice)
            .sum();
    }
    
    @Override
    public GroceryItem getMostExpensive() {
        return inventory.stream()
            .max(Comparator.comparingDouble(GroceryItem::getPrice))
            .orElse(null);
    }
    
    @Override
    public GroceryItem getMostRecent() {
        return inventory.stream()
            .max(Comparator.comparingInt(GroceryItem::getProductionYear))
            .orElse(null);
    }
    
    @Override
    public GroceryItem[] snapshotArray() {
        return inventory.toArray(new GroceryItem[0]);
    }
    
    @Override
    public List<GroceryItem> getAllItems() {
        return new ArrayList<>(inventory);
    }
    
    /**
     * Clears all items from the inventory.
     * Useful for testing and bulk operations.
     */
    public void clear() {
        inventory.clear();
    }
    
    /**
     * Sorts the inventory by name (alphabetical order).
     */
    public void sortByName() {
        Collections.sort(inventory);
    }
    
    /**
     * Sorts the inventory by price (ascending).
     */
    public void sortByPrice() {
        inventory.sort(Comparator.comparingDouble(GroceryItem::getPrice));
    }
    
    /**
     * Sorts the inventory by production year (ascending).
     */
    public void sortByProductionYear() {
        inventory.sort(Comparator.comparingInt(GroceryItem::getProductionYear));
    }
    
    /**
     * Gets statistics about the inventory.
     * 
     * @return map with statistics (size, total_value, avg_price, etc.)
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("size", size());
        stats.put("total_value", inventoryValue());
        stats.put("average_price", inventory.isEmpty() ? 0.0 : inventoryValue() / size());
        
        if (!inventory.isEmpty()) {
            stats.put("min_production_year", inventory.stream()
                .mapToInt(GroceryItem::getProductionYear).min().orElse(0));
            stats.put("max_production_year", inventory.stream()
                .mapToInt(GroceryItem::getProductionYear).max().orElse(0));
            stats.put("unique_brands", inventory.stream()
                .map(GroceryItem::getBrand).distinct().count());
        }
        
        return stats;
    }
    
    @Override
    public String toString() {
        return String.format("GroceryStoreArrayList[size=%d, value=$%.2f]", 
            size(), inventoryValue());
    }
}