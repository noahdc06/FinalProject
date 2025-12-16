package com.university.grocerystore.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Service for managing grocery bundles using the Composite pattern.
 * Provides operations for creating, managing, and analyzing grocery bundles.
 * 
 * <p>This service demonstrates the Composite pattern in action by providing
 * a high-level interface for working with both individual products and bundles.</p>
 */
public class BundleService {
    
    private final Map<String, GroceryBundle> bundles;
    
    /**
     * Creates a new bundle service.
     */
    public BundleService() {
        this.bundles = new HashMap<>();
    }
    
    /**
     * Creates a new grocery bundle with the specified name and discount.
     * 
     * @param name the bundle name
     * @param discount the discount rate (0.0 to 1.0)
     * @return the created bundle
     * @throws IllegalArgumentException if name is null or empty
     */
    public GroceryBundle createBundle(String name, double discount) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bundle name cannot be null or empty");
        }
        
        String bundleName = name.trim();
        if (bundles.containsKey(bundleName)) {
            throw new IllegalArgumentException("Bundle already exists: " + bundleName);
        }
        
        GroceryBundle bundle = new GroceryBundle(bundleName, discount);
        bundles.put(bundleName, bundle);
        return bundle;
    }
    
    /**
     * Adds a product to the specified bundle.
     * 
     * @param bundleName the name of the bundle
     * @param product the product to add
     * @throws IllegalArgumentException if bundle not found or product is null
     */
    public void addToBundle(String bundleName, GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        GroceryBundle bundle = bundles.get(bundleName);
        if (bundle == null) {
            throw new IllegalArgumentException("Bundle not found: " + bundleName);
        }
        
        bundle.addComponent(new GroceryLeaf(product));
    }
    
    /**
     * Adds a bundle to another bundle (nested bundles).
     * 
     * @param parentBundleName the name of the parent bundle
     * @param childBundleName the name of the child bundle to add
     * @throws IllegalArgumentException if either bundle is not found
     */
    public void addBundleToBundle(String parentBundleName, String childBundleName) {
        GroceryBundle parent = bundles.get(parentBundleName);
        GroceryBundle child = bundles.get(childBundleName);
        
        if (parent == null) {
            throw new IllegalArgumentException("Parent bundle not found: " + parentBundleName);
        }
        if (child == null) {
            throw new IllegalArgumentException("Child bundle not found: " + childBundleName);
        }
        
        parent.addComponent(child);
    }
    
    /**
     * Adds a component to the specified bundle.
     * 
     * @param bundleName the name of the bundle
     * @param component the component to add
     * @throws IllegalArgumentException if bundle not found or component is null
     */
    public void addComponentToBundle(String bundleName, GroceryComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        
        GroceryBundle bundle = bundles.get(bundleName);
        if (bundle == null) {
            throw new IllegalArgumentException("Bundle not found: " + bundleName);
        }
        
        bundle.addComponent(component);
    }
    
    /**
     * Removes a product from the specified bundle.
     * 
     * @param bundleName the name of the bundle
     * @param product the product to remove
     * @return true if the product was removed, false if not found
     */
    public boolean removeFromBundle(String bundleName, GroceryProduct product) {
        GroceryBundle bundle = bundles.get(bundleName);
        if (bundle == null) {
            return false;
        }
        
        return bundle.removeComponent(new GroceryLeaf(product));
    }
    
    /**
     * Gets a bundle by name.
     * 
     * @param name the bundle name
     * @return Optional containing the bundle if found
     */
    public Optional<GroceryBundle> getBundle(String name) {
        return Optional.ofNullable(bundles.get(name));
    }
    
    /**
     * Gets all bundles.
     * 
     * @return list of all bundles
     */
    public List<GroceryBundle> getAllBundles() {
        return new ArrayList<>(bundles.values());
    }
    
    /**
     * Gets all bundle names.
     * 
     * @return list of bundle names
     */
    public List<String> getBundleNames() {
        return new ArrayList<>(bundles.keySet());
    }
    
    /**
     * Calculates the total savings for a bundle.
     * 
     * @param bundleName the name of the bundle
     * @return the total savings amount
     * @throws IllegalArgumentException if bundle not found
     */
    public double calculateBundleSavings(String bundleName) {
        GroceryBundle bundle = bundles.get(bundleName);
        if (bundle == null) {
            throw new IllegalArgumentException("Bundle not found: " + bundleName);
        }
        
        return bundle.getTotalSavings();
    }
    
    /**
     * Calculates the total savings across all bundles.
     * 
     * @return the total savings amount
     */
    public double calculateTotalSavings() {
        return bundles.values().stream()
            .mapToDouble(GroceryBundle::getTotalSavings)
            .sum();
    }
    
    /**
     * Gets the total value of all bundles.
     * 
     * @return the total value
     */
    public double getTotalBundleValue() {
        return bundles.values().stream()
            .mapToDouble(GroceryBundle::getPrice)
            .sum();
    }
    
    /**
     * Gets the total discounted value of all bundles.
     * 
     * @return the total discounted value
     */
    public double getTotalDiscountedBundleValue() {
        return bundles.values().stream()
            .mapToDouble(GroceryBundle::getDiscountedPrice)
            .sum();
    }
    
    /**
     * Gets bundles that contain products of the specified type.
     * 
     * @param type the product type to filter by
     * @return list of bundles containing the specified type
     */
    public List<GroceryBundle> getBundlesByProductType(GroceryProduct.ProductType type) {
        return bundles.values().stream()
            .filter(bundle -> bundle.containsType(type))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets bundles with discounts greater than or equal to the specified threshold.
     * 
     * @param minDiscount the minimum discount rate
     * @return list of bundles meeting the criteria
     */
    public List<GroceryBundle> getBundlesByDiscount(double minDiscount) {
        return bundles.values().stream()
            .filter(bundle -> bundle.getDiscountRate() >= minDiscount)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets bundles with values within the specified range.
     * 
     * @param minValue the minimum value
     * @param maxValue the maximum value
     * @return list of bundles within the value range
     */
    public List<GroceryBundle> getBundlesByValueRange(double minValue, double maxValue) {
        return bundles.values().stream()
            .filter(bundle -> {
                double value = bundle.getPrice();
                return value >= minValue && value <= maxValue;
            })
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Removes a bundle by name.
     * 
     * @param name the bundle name
     * @return true if the bundle was removed, false if not found
     */
    public boolean removeBundle(String name) {
        return bundles.remove(name) != null;
    }
    
    /**
     * Clears all bundles.
     */
    public void clearAllBundles() {
        bundles.clear();
    }
    
    /**
     * Gets the number of bundles.
     * 
     * @return the bundle count
     */
    public int getBundleCount() {
        return bundles.size();
    }
    
    /**
     * Checks if any bundles exist.
     * 
     * @return true if no bundles exist
     */
    public boolean isEmpty() {
        return bundles.isEmpty();
    }
    
    /**
     * Gets bundle statistics.
     * 
     * @return bundle statistics
     */
    public BundleStats getBundleStats() {
        int totalBundles = bundles.size();
        int totalItems = bundles.values().stream()
            .mapToInt(GroceryBundle::getItemCount)
            .sum();
        double totalValue = getTotalBundleValue();
        double totalDiscountedValue = getTotalDiscountedBundleValue();
        double totalSavings = calculateTotalSavings();
        double averageDiscount = bundles.values().stream()
            .mapToDouble(GroceryBundle::getDiscountRate)
            .average()
            .orElse(0.0);
        
        return new BundleStats(
            totalBundles,
            totalItems,
            totalValue,
            totalDiscountedValue,
            totalSavings,
            averageDiscount
        );
    }
    
    /**
     * Statistics class for bundle analysis.
     */
    public static class BundleStats {
        private final int totalBundles;
        private final int totalItems;
        private final double totalValue;
        private final double totalDiscountedValue;
        private final double totalSavings;
        private final double averageDiscount;
        
        public BundleStats(int totalBundles, int totalItems, double totalValue,
                          double totalDiscountedValue, double totalSavings, double averageDiscount) {
            this.totalBundles = totalBundles;
            this.totalItems = totalItems;
            this.totalValue = totalValue;
            this.totalDiscountedValue = totalDiscountedValue;
            this.totalSavings = totalSavings;
            this.averageDiscount = averageDiscount;
        }
        
        public int getTotalBundles() { return totalBundles; }
        public int getTotalItems() { return totalItems; }
        public double getTotalValue() { return totalValue; }
        public double getTotalDiscountedValue() { return totalDiscountedValue; }
        public double getTotalSavings() { return totalSavings; }
        public double getAverageDiscount() { return averageDiscount; }
        
        @Override
        public String toString() {
            return String.format("BundleStats[Bundles=%d, Items=%d, Value=$%.2f, Discounted=$%.2f, Savings=$%.2f, AvgDiscount=%.1f%%]",
                totalBundles, totalItems, totalValue, totalDiscountedValue, totalSavings, averageDiscount * 100);
        }
    }
    
    @Override
    public String toString() {
        return String.format("BundleService[Bundles=%d, TotalValue=$%.2f, TotalSavings=$%.2f]",
            getBundleCount(), getTotalBundleValue(), calculateTotalSavings());
    }
}
