package com.university.grocerystore.observer;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Observer that tracks inventory changes for grocery products.
 * Maintains counts and statistics for products in the system.
 */
public class InventoryObserver implements GroceryProductObserver {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryObserver.class);
    
    private final Map<String, Integer> inventoryCounts;
    private final Map<String, Double> totalValue;
    private int totalEvents;
    
    /**
     * Creates a new inventory observer.
     */
    public InventoryObserver() {
        this.inventoryCounts = new HashMap<>();
        this.totalValue = new HashMap<>();
        this.totalEvents = 0;
    }
    
    @Override
    public void onEvent(GroceryProductEvent event) {
        totalEvents++;
        
        switch (event.getEventType()) {
            case "PRODUCT_ADDED":
                handleProductAdded(event);
                break;
            case "PRICE_CHANGED":
                handlePriceChanged(event);
                break;
            default:
                // Handle other event types if needed
                break;
        }
    }
    
    /**
     * Handles product added events.
     */
    private void handleProductAdded(GroceryProductEvent event) {
        GroceryProduct product = event.getProduct();
        String productId = product.getId();
        
        // Update inventory count
        inventoryCounts.merge(productId, 1, Integer::sum);
        
        // Update total value
        totalValue.merge(productId, product.getPrice(), Double::sum);
        
        LOGGER.info("Inventory updated: {} count: {}", 
            productId, inventoryCounts.get(productId));
    }
    
    /**
     * Handles price changed events.
     */
    private void handlePriceChanged(GroceryProductEvent event) {
        if (event instanceof PriceChangedEvent) {
            PriceChangedEvent priceEvent = (PriceChangedEvent) event;
            GroceryProduct product = event.getProduct();
            String productId = product.getId();
            
            // Update total value with the price change
            double priceChange = priceEvent.getPriceChange();
            totalValue.merge(productId, priceChange, Double::sum);
            
            LOGGER.info("Price changed for {}: ${} -> ${}", 
                product.getName(), priceEvent.getOldPrice(), priceEvent.getNewPrice());
        }
    }
    
    /**
     * Gets the inventory count for a specific product.
     * 
     * @param productId the product ID
     * @return the inventory count
     */
    public int getInventoryCount(String productId) {
        return inventoryCounts.getOrDefault(productId, 0);
    }
    
    /**
     * Gets the total value for a specific product.
     * 
     * @param productId the product ID
     * @return the total value
     */
    public double getTotalValue(String productId) {
        return totalValue.getOrDefault(productId, 0.0);
    }
    
    /**
     * Gets the total number of products in inventory.
     * 
     * @return the total count
     */
    public int getTotalInventoryCount() {
        return inventoryCounts.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
    
    /**
     * Gets the total value of all products in inventory.
     * 
     * @return the total value
     */
    public double getTotalInventoryValue() {
        return totalValue.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
    
    /**
     * Gets the number of unique products in inventory.
     * 
     * @return the unique product count
     */
    public int getUniqueProductCount() {
        return inventoryCounts.size();
    }
    
    /**
     * Gets the total number of events processed.
     * 
     * @return the event count
     */
    public int getTotalEvents() {
        return totalEvents;
    }
    
    /**
     * Gets all inventory counts.
     * 
     * @return map of product ID to count
     */
    public Map<String, Integer> getAllInventoryCounts() {
        return new HashMap<>(inventoryCounts);
    }
    
    /**
     * Gets all total values.
     * 
     * @return map of product ID to total value
     */
    public Map<String, Double> getAllTotalValues() {
        return new HashMap<>(totalValue);
    }
    
    /**
     * Clears all inventory data.
     */
    public void clear() {
        inventoryCounts.clear();
        totalValue.clear();
        totalEvents = 0;
    }
    
    @Override
    public String getObserverName() {
        return "InventoryObserver";
    }
    
    @Override
    public String toString() {
        return String.format("InventoryObserver[Products=%d, TotalCount=%d, TotalValue=$%.2f, Events=%d]",
            getUniqueProductCount(), getTotalInventoryCount(), getTotalInventoryValue(), getTotalEvents());
    }
}
