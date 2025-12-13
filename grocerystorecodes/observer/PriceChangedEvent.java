package com.university.grocerystore.observer;

import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Event that occurs when a grocery product's price changes.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class PriceChangedEvent implements GroceryProductEvent {
    
    private final GroceryProduct product;
    private final double oldPrice;
    private final double newPrice;
    private final long timestamp;
    
    /**
     * Creates a new price changed event.
     * 
     * @param product the product whose price changed
     * @param oldPrice the previous price
     * @param newPrice the new price
     * @throws IllegalArgumentException if product is null or prices are negative
     */
    public PriceChangedEvent(GroceryProduct product, double oldPrice, double newPrice) {
        this.product = Objects.requireNonNull(product, "Product cannot be null");
        if (oldPrice < 0) {
            throw new IllegalArgumentException("Old price cannot be negative: " + oldPrice);
        }
        if (newPrice < 0) {
            throw new IllegalArgumentException("New price cannot be negative: " + newPrice);
        }
        
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
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
        return "PRICE_CHANGED";
    }
    
    @Override
    public String getDescription() {
        return String.format("Price changed for %s: $%.2f -> $%.2f (Change: $%.2f)",
            product.getName(), oldPrice, newPrice, newPrice - oldPrice);
    }
    
    /**
     * Gets the old price.
     * 
     * @return the previous price
     */
    public double getOldPrice() {
        return oldPrice;
    }
    
    /**
     * Gets the new price.
     * 
     * @return the new price
     */
    public double getNewPrice() {
        return newPrice;
    }
    
    /**
     * Gets the price change amount.
     * 
     * @return the price change (new - old)
     */
    public double getPriceChange() {
        return newPrice - oldPrice;
    }
    
    /**
     * Gets the price change percentage.
     * 
     * @return the price change percentage
     */
    public double getPriceChangePercentage() {
        if (oldPrice == 0) {
            return newPrice > 0 ? 100.0 : 0.0;
        }
        return ((newPrice - oldPrice) / oldPrice) * 100.0;
    }
    
    /**
     * Checks if the price increased.
     * 
     * @return true if the price increased
     */
    public boolean isPriceIncrease() {
        return newPrice > oldPrice;
    }
    
    /**
     * Checks if the price decreased.
     * 
     * @return true if the price decreased
     */
    public boolean isPriceDecrease() {
        return newPrice < oldPrice;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PriceChangedEvent that = (PriceChangedEvent) obj;
        return Double.compare(that.oldPrice, oldPrice) == 0 &&
               Double.compare(that.newPrice, newPrice) == 0 &&
               timestamp == that.timestamp &&
               Objects.equals(product, that.product);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(product, oldPrice, newPrice, timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("PriceChangedEvent[%s: $%.2f -> $%.2f at %d]",
            product.getName(), oldPrice, newPrice, timestamp);
    }
}