package com.university.grocerystore.observer;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Base interface for grocery product-related domain events in the Observer pattern.
 * Represents events that occur in the grocery product management system.
 * 
 * <p>This interface defines the contract for all grocery product events, allowing
 * the system to broadcast domain events to interested observers without
 * tight coupling between components.</p>
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public interface GroceryProductEvent {
    
    /**
     * Gets the grocery product associated with this event.
     * 
     * @return the grocery product
     */
    GroceryProduct getProduct();
    
    /**
     * Gets the timestamp when this event occurred.
     * 
     * @return the event timestamp in milliseconds
     */
    long getTimestamp();
    
    /**
     * Gets the type of this event.
     * 
     * @return the event type
     */
    String getEventType();
    
    /**
     * Gets a description of this event.
     * 
     * @return the event description
     */
    String getDescription();
}