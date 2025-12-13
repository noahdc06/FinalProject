package com.university.grocerystore.decorator;

import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Abstract base class for product decorators in the Decorator pattern.
 * Provides a common interface for decorating products with additional features.
 * 
 * <p>This class implements the GroceryProduct interface and delegates all operations
 * to the wrapped product, allowing subclasses to override specific methods
 * to add new functionality without modifying the original product classes.</p>
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public abstract class ProductDecorator extends GroceryProduct {
    
    protected final GroceryProduct decoratedProduct;
    
    /**
     * Creates a new product decorator wrapping the specified product.
     * 
     * @param product the product to decorate
     * @throws IllegalArgumentException if product is null
     */
    public ProductDecorator(GroceryProduct product) {
        super(product.getId(), product.getName(), product.getPrice(), product.getProductionYear(), product.getType());
        this.decoratedProduct = Objects.requireNonNull(product, "Product cannot be null");
    }
    
    // Implement abstract methods from GroceryProduct by delegating to wrapped product
    @Override
    public String getBrand() {
        return decoratedProduct.getBrand();
    }
    
    @Override
    public String getDisplayInfo() {
        return decoratedProduct.getDisplayInfo();
    }
    
    @Override
    public double getDiscountRate() {
        return decoratedProduct.getDiscountRate();
    }
    
    /**
     * Gets the decorated product.
     * 
     * @return the wrapped product
     */
    public GroceryProduct getDecoratedProduct() {
        return decoratedProduct;
    }
    
    /**
     * Gets the base product (unwraps all decorators).
     * 
     * @return the original product without any decorations
     */
    public GroceryProduct getBaseProduct() {
        GroceryProduct current = decoratedProduct;
        while (current instanceof ProductDecorator) {
            current = ((ProductDecorator) current).getDecoratedProduct();
        }
        return current;
    }
    
    /**
     * Gets the number of decorators applied to this product.
     * 
     * @return the decorator count
     */
    public int getDecoratorCount() {
        int count = 0;
        GroceryProduct current = decoratedProduct;
        while (current instanceof ProductDecorator) {
            count++;
            current = ((ProductDecorator) current).getDecoratedProduct();
        }
        return count;
    }
    
    /**
     * Checks if this product has any decorators applied.
     * 
     * @return true if decorators are applied
     */
    public boolean hasDecorators() {
        return getDecoratorCount() > 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ProductDecorator that = (ProductDecorator) obj;
        return Objects.equals(decoratedProduct, that.decoratedProduct);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(decoratedProduct);
    }
    
    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), decoratedProduct.getName());
    }
}