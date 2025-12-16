package com.university.grocerystore.observer;

/**
 * Subject interface for grocery product events in the Observer pattern.
 * Defines the contract for objects that can notify observers of grocery product events.
 * 
 * <p>This interface allows components to manage observers and broadcast events,
 * enabling the Observer pattern for loose coupling between event sources and handlers.</p>
 */
public interface GroceryProductSubject {
    
    /**
     * Adds an observer to receive grocery product events.
     * 
     * @param observer the observer to add
     * @throws IllegalArgumentException if observer is null
     */
    void addObserver(GroceryProductObserver observer);
    
    /**
     * Removes an observer from receiving grocery product events.
     * 
     * @param observer the observer to remove
     * @return true if the observer was removed, false if not found
     */
    boolean removeObserver(GroceryProductObserver observer);
    
    /**
     * Notifies all observers of a grocery product event.
     * 
     * @param event the event to broadcast
     * @throws IllegalArgumentException if event is null
     */
    void notifyObservers(GroceryProductEvent event);
    
    /**
     * Gets the number of registered observers.
     * 
     * @return the observer count
     */
    int getObserverCount();
    
    /**
     * Checks if any observers are registered.
     * 
     * @return true if no observers are registered
     */
    boolean hasNoObservers();
    
    /**
     * Clears all observers.
     */
    void clearObservers();
}
