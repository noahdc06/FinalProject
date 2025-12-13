package com.university.grocerystore.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Iterator that filters products by price range.
 * Only returns products within the specified price range.
 */
public class PriceRangeIterator implements ProductIterator {
    
    private final List<GroceryProduct> products;
    private final double minPrice;
    private final double maxPrice;
    private int currentIndex;
    private final int totalCount;
    
    /**
     * Creates a new price range iterator.
     * 
     * @param products the list of products to iterate over
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @throws IllegalArgumentException if price range is invalid
     */
    public PriceRangeIterator(List<GroceryProduct> products, double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            throw new IllegalArgumentException("Invalid price range: " + minPrice + " to " + maxPrice);
        }
        
        this.products = new ArrayList<>(products);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.currentIndex = 0;
        this.totalCount = (int) products.stream()
            .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
            .count();
        advanceToNext();
    }
    
    @Override
    public boolean hasNext() {
        return currentIndex < products.size();
    }
    
    @Override
    public GroceryProduct next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        
        GroceryProduct current = products.get(currentIndex);
        currentIndex++;
        advanceToNext();
        return current;
    }
    
    @Override
    public void reset() {
        currentIndex = 0;
        advanceToNext();
    }
    
    @Override
    public int getCurrentPosition() {
        return currentIndex;
    }
    
    @Override
    public int getTotalCount() {
        return totalCount;
    }
    
    @Override
    public int getRemainingCount() {
        return totalCount - getCurrentPosition();
    }
    
    @Override
    public boolean isAtBeginning() {
        return currentIndex == 0;
    }
    
    @Override
    public boolean isAtEnd() {
        return currentIndex >= products.size();
    }
    
    /**
     * Gets the minimum price.
     * 
     * @return the minimum price
     */
    public double getMinPrice() {
        return minPrice;
    }
    
    /**
     * Gets the maximum price.
     * 
     * @return the maximum price
     */
    public double getMaxPrice() {
        return maxPrice;
    }
    
    /**
     * Gets the price range.
     * 
     * @return the price range
     */
    public double getPriceRange() {
        return maxPrice - minPrice;
    }
    
    /**
     * Advances to the next product within the price range.
     */
    private void advanceToNext() {
        while (currentIndex < products.size()) {
            GroceryProduct product = products.get(currentIndex);
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                break;
            }
            currentIndex++;
        }
    }
    
    @Override
    public String toString() {
        return String.format("PriceRangeIterator[Range=$%.2f-$%.2f, Position=%d/%d]",
            minPrice, maxPrice, getCurrentPosition(), getTotalCount());
    }
}