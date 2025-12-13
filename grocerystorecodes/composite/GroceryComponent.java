package com.university.grocerystore.composite;

import java.util.List;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Component interface for the Composite pattern.
 * 
 * <p>Represents both individual products and composite bundles uniformly. The Composite Pattern is a structural design
 * pattern that allows developers to treat individual objects and groups of objects uniformly. This is particularly 
 * useful when working with tree-like structures, where both leaf objects (e.g., individual grocery items) and composite 
 * objects (e.g., bundles of grocery items) need to share the same interface.</p>
 * 
 * <p>The pattern promotes the principle of "compose objects into tree structures to represent part-whole hierarchies."
 * With Composite, clients can operate on single objects and entire compositions through a common interface,
 * simplifying the design and improving extensibility.</p>
 * 
 * <p>This interface allows clients to treat individual products and product bundles uniformly, enabling 
 * recursive composition and polymorphic operations.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * GroceryComponent product = new GroceryLeaf(new FrozenFood(...));
 * GroceryComponent bundle = new GroceryBundle("Meal Deal Pack", 0.1);
 * bundle.addComponent(product);
 * 
 * // Both can be treated uniformly
 * double price = component.getPrice();
 * String info = component.getDisplayInfo();
 * }</pre>
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 * @see GroceryLeaf
 * @see GroceryBundle
 * @see GroceryProduct
 */
public interface GroceryComponent {
    
    /**
     * Gets the name of this component.
     * For individual products, this is the product name.
     * For bundles, this is the bundle name.
     * 
     * @return the component name
     */
    String getName();
    
    /**
     * Gets the total price of this component.
     * For individual products, this is the product price.
     * For bundles, this is the sum of all contained products.
     * 
     * @return the total price
     */
    double getPrice();
    
    /**
     * Gets the discounted price of this component.
     * For individual products, this applies the product's discount.
     * For bundles, this applies the bundle discount to the total price.
     * 
     * @return the discounted price
     */
    double getDiscountedPrice();
    
    /**
     * Gets a description of this component.
     * Provides detailed information about the component and its contents.
     * 
     * @return the component description
     */
    String getDescription();
    
    /**
     * Gets all products contained in this component.
     * For individual products, returns a list containing only itself.
     * For bundles, returns all products in the bundle (recursively).
     * 
     * @return list of all contained products
     */
    List<GroceryProduct> getProducts();
    
    /**
     * Gets the total number of items in this component.
     * For individual products, returns 1.
     * For bundles, returns the sum of all contained items.
     * 
     * @return the total item count
     */
    int getItemCount();
    
    /**
     * Gets the discount rate applied to this component.
     * 
     * @return the discount rate (0.0 to 1.0)
     */
    double getDiscountRate();
    
    /**
     * Checks if this component is a leaf (individual product).
     * 
     * @return true if this is a leaf component
     */
    boolean isLeaf();
    
    /**
     * Checks if this component is a composite (bundle).
     * 
     * @return true if this is a composite component
     */
    default boolean isComposite() {
        return !isLeaf();
    }
}