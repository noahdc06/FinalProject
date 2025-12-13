package com.university.grocerystore.iterator;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Iterator interface for traversing collections of products.
 * Provides a uniform way to iterate over products with different traversal strategies.
 * 
 * <p>This interface demonstrates the Iterator pattern by encapsulating traversal logic
 * and allowing different iteration strategies to be implemented.</p>
 */
public interface ProductIterator {
    
    /**
     * Checks if there are more products to iterate over.
     * 
     * @return true if there are more products
     */
    boolean hasNext();
    
    /**
     * Gets the next product in the iteration.
     * 
     * @return the next product
     * @throws java.util.NoSuchElementException if there are no more products
     */
    GroceryProduct next();
    
    /**
     * Resets the iterator to the beginning.
     */
    void reset();
    
    /**
     * Gets the current position in the iteration.
     * 
     * @return the current position (0-based)
     */
    int getCurrentPosition();
    
    /**
     * Gets the total number of products that can be iterated.
     * 
     * @return the total count
     */
    int getTotalCount();
    
    /**
     * Gets the number of products remaining to be iterated.
     * 
     * @return the remaining count
     */
    int getRemainingCount();
    
    /**
     * Checks if the iterator is at the beginning.
     * 
     * @return true if at the beginning
     */
    boolean isAtBeginning();
    
    /**
     * Checks if the iterator is at the end.
     * 
     * @return true if at the end
     */
    boolean isAtEnd();
}