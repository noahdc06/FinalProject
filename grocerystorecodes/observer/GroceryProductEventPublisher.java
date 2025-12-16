package com.university.grocerystore.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Concrete implementation of GroceryProductSubject that publishes grocery product events to observers.
 * Provides thread-safe event publishing and observer management.
 * 
 * <p>This class demonstrates the Observer pattern by managing a list of observers
 * and broadcasting events to all registered observers when grocery product events occur.</p>
 */
public class GroceryProductEventPublisher implements GroceryProductSubject {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GroceryProductEventPublisher.class);
    private final List<GroceryProductObserver> observers;
    
    /**
     * Creates a new grocery product event publisher.
     */
    public GroceryProductEventPublisher() {
        // Use CopyOnWriteArrayList for thread safety
        this.observers = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public void addObserver(GroceryProductObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        
        if (!observers.contains(observer)) {
            observers.add(observer);
            observer.onAdded(this);
        }
    }
    
    @Override
    public boolean removeObserver(GroceryProductObserver observer) {
        if (observer == null) {
            return false;
        }
        
        boolean removed = observers.remove(observer);
        if (removed) {
            observer.onRemoved(this);
        }
        return removed;
    }
    
    @Override
    public void notifyObservers(GroceryProductEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        
        for (GroceryProductObserver observer : observers) {
            try {
                observer.onEvent(event);
            } catch (Exception e) {
                // Log error but don't let one observer's failure affect others
                LOGGER.error("Observer {} failed to handle event: {}", 
                    observer.getObserverName(), event.getEventType(), e);
            }
        }
    }
    
    @Override
    public int getObserverCount() {
        return observers.size();
    }
    
    @Override
    public boolean hasNoObservers() {
        return observers.isEmpty();
    }
    
    @Override
    public void clearObservers() {
        // Notify observers they are being removed
        for (GroceryProductObserver observer : observers) {
            try {
                observer.onRemoved(this);
            } catch (Exception e) {
                LOGGER.warn("Error notifying observer of removal", e);
            }
        }
        observers.clear();
    }
    
    /**
     * Publishes a product added event.
     * 
     * @param product the product that was added
     */
    public void publishProductAdded(GroceryProduct product) {
        notifyObservers(new ProductAddedEvent(product));
    }
    
    /**
     * Publishes a price changed event.
     * 
     * @param product the product whose price changed
     * @param oldPrice the previous price
     * @param newPrice the new price
     */
    public void publishPriceChanged(GroceryProduct product, double oldPrice, double newPrice) {
        notifyObservers(new PriceChangedEvent(product, oldPrice, newPrice));
    }
    
    /**
     * Publishes a custom grocery product event.
     * 
     * @param event the event to publish
     */
    public void publishEvent(GroceryProductEvent event) {
        notifyObservers(event);
    }
    
    /**
     * Gets a list of all registered observers.
     * 
     * @return list of observers (defensive copy)
     */
    public List<GroceryProductObserver> getObservers() {
        return new ArrayList<>(observers);
    }
    
    /**
     * Checks if a specific observer is registered.
     * 
     * @param observer the observer to check
     * @return true if the observer is registered
     */
    public boolean hasObserver(GroceryProductObserver observer) {
        return observers.contains(observer);
    }
    
    /**
     * Gets observers of a specific type.
     * 
     * @param observerType the type of observers to find
     * @return list of observers of the specified type
     */
    public <T extends GroceryProductObserver> List<T> getObserversOfType(Class<T> observerType) {
        List<T> result = new ArrayList<>();
        for (GroceryProductObserver observer : observers) {
            if (observerType.isInstance(observer)) {
                result.add(observerType.cast(observer));
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("GroceryProductEventPublisher[Observers=%d]", getObserverCount());
    }
}
