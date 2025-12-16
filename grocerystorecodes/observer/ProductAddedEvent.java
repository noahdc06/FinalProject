package com.university.grocerystore.observer;

import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Event that occurs when a grocery product is added to the system.
 */
public class ProductAddedEvent implements GroceryProductEvent {
    
    private final GroceryProduct product;
    private final long timestamp;
    
    /**
     * Creates a new product added event.
     * 
     * @param product the product that was added
     * @throws IllegalArgumentException if product is null
     */
    public ProductAddedEvent(GroceryProduct product) {
        this.product = Objects.requireNonNull(product, "Product cannot be null");
        this.timestamp = System.currentTimeMillis();
    }
    
    @Override
    public GroceryProduct getProduct() {
        return product;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String getEventType() {
        return "PRODUCT_ADDED";
    }
    
    @Override
    public String getDescription() {
        return String.format("Product added: %s (ID: %s, Price: $%.2f)",
            product.getName(), product.getId(), product.getPrice());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ProductAddedEvent that = (ProductAddedEvent) obj;
        return timestamp == that.timestamp &&
               Objects.equals(product, that.product);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(product, timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("ProductAddedEvent[%s at %d]", product.getName(), timestamp);
    }
}
