package com.university.grocerystore.iterator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Iterator that returns products sorted by price.
 * Can sort in ascending or descending order.
 */
public class PriceSortedIterator implements ProductIterator {
    
    private final List<GroceryProduct> sortedProducts;
    private int currentIndex;
    private final boolean ascending;
    
    /**
     * Creates a new price sorted iterator.
     * 
     * @param products the list of products to iterate over
     * @param ascending true for ascending order, false for descending
     */
    public PriceSortedIterator(List<GroceryProduct> products, boolean ascending) {
        this.sortedProducts = new ArrayList<>(products);
        this.ascending = ascending;
        this.currentIndex = 0;
        
        // Sort products by price
        Comparator<GroceryProduct> priceComparator = Comparator.comparing(GroceryProduct::getPrice);
        if (!ascending) {
            priceComparator = priceComparator.reversed();
        }
        this.sortedProducts.sort(priceComparator);
    }
    
    @Override
    public boolean hasNext() {
        return currentIndex < sortedProducts.size();
    }
    
    @Override
    public GroceryProduct next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        return sortedProducts.get(currentIndex++);
    }
    
    @Override
    public void reset() {
        currentIndex = 0;
    }
    
    @Override
    public int getCurrentPosition() {
        return currentIndex;
    }
    
    @Override
    public int getTotalCount() {
        return sortedProducts.size();
    }
    
    @Override
    public int getRemainingCount() {
        return sortedProducts.size() - currentIndex;
    }
    
    @Override
    public boolean isAtBeginning() {
        return currentIndex == 0;
    }
    
    @Override
    public boolean isAtEnd() {
        return currentIndex >= sortedProducts.size();
    }
    
    /**
     * Gets the sort order.
     * 
     * @return true if ascending, false if descending
     */
    public boolean isAscending() {
        return ascending;
    }
    
    /**
     * Gets the current product without advancing the iterator.
     * 
     * @return the current product
     * @throws NoSuchElementException if at the end
     */
    public GroceryProduct peek() {
        if (currentIndex >= sortedProducts.size()) {
            throw new NoSuchElementException("No more elements");
        }
        return sortedProducts.get(currentIndex);
    }
    
    /**
     * Gets the next product without advancing the iterator.
     * 
     * @return the next product
     * @throws NoSuchElementException if no next element
     */
    public GroceryProduct peekNext() {
        if (currentIndex + 1 >= sortedProducts.size()) {
            throw new NoSuchElementException("No next element");
        }
        return sortedProducts.get(currentIndex + 1);
    }
    
    @Override
    public String toString() {
        return String.format("PriceSortedIterator[Order=%s, Position=%d/%d]",
            ascending ? "ASC" : "DESC", getCurrentPosition(), getTotalCount());
    }
}