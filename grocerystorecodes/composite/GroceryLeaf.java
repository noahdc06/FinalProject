package com.university.grocerystore.composite;

import java.util.List;
import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Leaf component in the Composite pattern.
 * Represents an individual product that cannot contain other components.
 * 
 * <p>This class wraps a GroceryProduct object and implements the GroceryComponent interface,
 * allowing individual products to be treated uniformly with composite bundles.</p>
 */
public class GroceryLeaf implements GroceryComponent {
    
    private final GroceryProduct product;
    
    /**
     * Creates a new product leaf wrapping the specified product.
     * 
     * @param product the product to wrap
     * @throws IllegalArgumentException if product is null
     */
    public GroceryLeaf(GroceryProduct product) {
        this.product = Objects.requireNonNull(product, "Product cannot be null");
    }
    
    @Override
    public String getName() {
        return product.getName();
    }
    
    @Override
    public double getPrice() {
        return product.getPrice();
    }
    
    @Override
    public double getDiscountedPrice() {
        return product.getDiscountedPrice();
    }
    
    @Override
    public String getDescription() {
        return product.getDisplayInfo();
    }
    
    @Override
    public List<GroceryProduct> getProducts() {
        return List.of(product);
    }
    
    @Override
    public int getItemCount() {
        return 1;
    }
    
    @Override
    public double getDiscountRate() {
        return product.getDiscountRate();
    }
    
    @Override
    public boolean isLeaf() {
        return true;
    }
    
    /**
     * Gets the wrapped product.
     * 
     * @return the underlying product
     */
    public GroceryProduct getProduct() {
        return product;
    }
    
    /**
     * Gets the product ID.
     * 
     * @return the product ID
     */
    public String getId() {
        return product.getId();
    }
    
    /**
     * Gets the product type.
     * 
     * @return the product type
     */
    public GroceryProduct.ProductType getType() {
        return product.getType();
    }
    
    /**
     * Gets the product brand.
     * 
     * @return the product brand
     */
    public String getBrand() {
        return product.getBrand();
    }
    
    /**
     * Gets the product production year.
     * 
     * @return the production year
     */
    public int getProductionYear() {
        return product.getProductionYear();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        GroceryLeaf that = (GroceryLeaf) obj;
        return Objects.equals(product, that.product);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(product);
    }
    
    @Override
    public String toString() {
        return String.format("GroceryLeaf[%s]", product.getName());
    }
}
