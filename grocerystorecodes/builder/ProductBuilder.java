package com.university.grocerystore.builder;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Builder interface for creating GroceryProduct instances.
 * Provides a fluent interface for constructing complex product objects.
 * 
 * <p>This interface demonstrates the Builder pattern by providing a way to
 * construct complex objects step by step with a fluent interface.</p>
 * 
 * @param <T> the type of product being built
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public interface ProductBuilder<T extends GroceryProduct> {
    
    /**
     * Builds and returns the product instance.
     * 
     * @return the built product
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