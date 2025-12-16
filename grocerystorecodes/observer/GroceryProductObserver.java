package com.university.grocerystore.observer;

/**
 * Observer interface for grocery product-related events in the Observer pattern.
 * Defines the contract for objects that need to be notified of grocery product events.
 * 
 * <p>This interface allows components to subscribe to grocery product events and be
 * automatically notified when events occur, enabling loose coupling between
 * the event source and event handlers.</p>
 */
public interface GroceryProductObserver {
    
    /**
     * Called when a grocery product event occurs.
     * 
     * @param event the grocery product event that occurred
     */
    void onEvent(GroceryProductEvent event);
    
    /**
     * Gets the name of this observer for identification purposes.
     * 
     * @return the observer name
     */
    default String getObserverName() {
        return getClass().getSimpleName();
    }
    
    /**
     * Called when the observer is added to a subject.
     * Can be used for initialization or setup.
     * 
     * @param subject the subject this observer was added to
     */
    default void onAdded(GroceryProductSubject subject) {
        // Default implementation does nothing
    }
    
    /**
     * Called when the observer is removed from a subject.
     * Can be used for cleanup.
     * 
     * @param subject the subject this observer was removed from
     */
    default void onRemoved(GroceryProductSubject subject) {
        // Default implementation does nothing
    }
}
