package com.university.grocerystore.builder;

import java.util.ArrayList;
import java.util.List;

import com.university.grocerystore.composite.GroceryBundle;
import com.university.grocerystore.composite.GroceryComponent;
import com.university.grocerystore.composite.GroceryLeaf;
import com.university.grocerystore.model.GroceryProduct;

/**
 * Builder for creating GroceryBundle instances with a fluent interface.
 * Demonstrates the Builder pattern for complex composite object construction.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class GroceryBundleBuilder implements ComponentBuilder<GroceryBundle> {
    
    private String bundleName;
    private double bundleDiscount = 0.0;
    private final List<GroceryComponent> components = new ArrayList<>();
    
    /**
     * Sets the bundle name.
     * 
     * @param bundleName the bundle name
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder setBundleName(String bundleName) {
        this.bundleName = bundleName;
        return this;
    }
    
    /**
     * Sets the bundle discount rate.
     * 
     * @param discount the discount rate (0.0 to 1.0)
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder setBundleDiscount(double discount) {
        this.bundleDiscount = Math.max(0.0, Math.min(1.0, discount));
        return this;
    }
    
    /**
     * Sets the bundle discount percentage.
     * 
     * @param discountPercent the discount percentage (0.0 to 100.0)
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder setBundleDiscountPercent(double discountPercent) {
        return setBundleDiscount(discountPercent / 100.0);
    }
    
    /**
     * Adds a product to the bundle.
     * 
     * @param product the product to add
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder addProduct(GroceryProduct product) {
        if (product != null) {
            components.add(new GroceryLeaf(product));
        }
        return this;
    }
    
    /**
     * Adds a bundle to this bundle (nested bundles).
     * 
     * @param bundle the bundle to add
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder addBundle(GroceryBundle bundle) {
        if (bundle != null) {
            components.add(bundle);
        }
        return this;
    }
    
    /**
     * Adds a product component to the bundle.
     * 
     * @param component the component to add
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder addComponent(GroceryComponent component) {
        if (component != null) {
            components.add(component);
        }
        return this;
    }
    
    /**
     * Adds multiple products to the bundle.
     * 
     * @param products the products to add
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder addProducts(List<GroceryProduct> products) {
        if (products != null) {
            for (GroceryProduct product : products) {
                addProduct(product);
            }
        }
        return this;
    }
    
    /**
     * Adds multiple components to the bundle.
     * 
     * @param components the components to add
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder addComponents(List<GroceryComponent> components) {
        if (components != null) {
            for (GroceryComponent component : components) {
                addComponent(component);
            }
        }
        return this;
    }
    
    /**
     * Removes a product from the bundle.
     * 
     * @param product the product to remove
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder removeProduct(GroceryProduct product) {
        if (product != null) {
            components.removeIf(component -> 
                component instanceof GroceryLeaf && 
                ((GroceryLeaf) component).getProduct().equals(product));
        }
        return this;
    }
    
    /**
     * Removes a component from the bundle.
     * 
     * @param component the component to remove
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder removeComponent(GroceryComponent component) {
        components.remove(component);
        return this;
    }
    
    /**
     * Clears all components from the bundle.
     * 
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder clearComponents() {
        components.clear();
        return this;
    }
    
    /**
     * Sets a 10% discount.
     * 
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder setSmallDiscount() {
        return setBundleDiscount(0.10);
    }
    
    /**
     * Sets a 20% discount.
     * 
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder setMediumDiscount() {
        return setBundleDiscount(0.20);
    }
    
    /**
     * Sets a 30% discount.
     * 
     * @return this builder for method chaining
     */
    public GroceryBundleBuilder setLargeDiscount() {
        return setBundleDiscount(0.30);
    }
    
    @Override
    public GroceryBundle build() {
        validate();
        
        GroceryBundle bundle = new GroceryBundle(bundleName, bundleDiscount);
        for (GroceryComponent component : components) {
            bundle.addComponent(component);
        }
        return bundle;
    }
    
    @Override
    public void validate() {
        if (bundleName == null || bundleName.trim().isEmpty()) {
            throw new IllegalStateException("Bundle name is required");
        }
        if (bundleDiscount < 0.0 || bundleDiscount > 1.0) {
            throw new IllegalStateException("Bundle discount must be between 0.0 and 1.0: " + bundleDiscount);
        }
    }
    
    @Override
    public void reset() {
        this.bundleName = null;
        this.bundleDiscount = 0.0;
        this.components.clear();
    }
    
    /**
     * Gets the current bundle name.
     * 
     * @return the current bundle name
     */
    public String getBundleName() {
        return bundleName;
    }
    
    /**
     * Gets the current bundle discount.
     * 
     * @return the current bundle discount
     */
    public double getBundleDiscount() {
        return bundleDiscount;
    }
    
    /**
     * Gets the current bundle discount percentage.
     * 
     * @return the current bundle discount percentage
     */
    public double getBundleDiscountPercent() {
        return bundleDiscount * 100.0;
    }
    
    /**
     * Gets the current components.
     * 
     * @return the current components
     */
    public List<GroceryComponent> getComponents() {
        return new ArrayList<>(components);
    }
    
    /**
     * Gets the number of components.
     * 
     * @return the component count
     */
    public int getComponentCount() {
        return components.size();
    }
    
    /**
     * Checks if the bundle has any components.
     * 
     * @return true if the bundle has components
     */
    public boolean hasComponents() {
        return !components.isEmpty();
    }
    
    /**
     * Gets the total price of all components.
     * 
     * @return the total price
     */
    public double getTotalPrice() {
        return components.stream()
            .mapToDouble(GroceryComponent::getPrice)
            .sum();
    }
    
    /**
     * Gets the total discounted price of all components.
     * 
     * @return the total discounted price
     */
    public double getTotalDiscountedPrice() {
        double totalPrice = getTotalPrice();
        return totalPrice * (1.0 - bundleDiscount);
    }
    
    /**
     * Gets the total savings from the bundle discount.
     * 
     * @return the total savings
     */
    public double getTotalSavings() {
        return getTotalPrice() - getTotalDiscountedPrice();
    }
    
    @Override
    public String toString() {
        return String.format("GroceryBundleBuilder[Name=%s, Discount=%.1f%%, Components=%d, TotalPrice=$%.2f, Savings=$%.2f]",
            bundleName, getBundleDiscountPercent(), getComponentCount(), getTotalPrice(), getTotalSavings());
    }
}