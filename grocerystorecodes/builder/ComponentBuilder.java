package com.university.grocerystore.builder;

import com.university.grocerystore.composite.GroceryComponent;

/**
 * Builder interface for creating GroceryComponent instances.
 * Provides a fluent interface for constructing complex component objects.
 * 
 * <p>This interface demonstrates the Builder pattern by providing a way to
 * construct complex objects step by step with a fluent interface.</p>
 * 
 * @param <T> the type of component being built
 */
public interface ComponentBuilder<T extends GroceryComponent> {
    
    /**
     * Builds and returns the component instance.
     * 
     * @return the built component
     * @throws IllegalStateException if required fields are missing or invalid
     */
    T build();
    
    /**
     * Validates that all required fields are set.
     * 
     * @throws IllegalStateException if validation fails
     */
    void validate();
    
    /**
     * Resets the builder to its initial state.
     */
    void reset();
}
