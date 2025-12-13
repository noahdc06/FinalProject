package com.university.grocerystore.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Iterator that filters products by type.
 * Only returns products of the specified type.
 */
public class ProductTypeIterator implements ProductIterator {
    
    private final List<GroceryProduct> products;
    private final GroceryProduct.ProductType targetType;
    private int currentIndex;
    private final int totalCount;
    
    /**
     * Creates a new product type iterator.
     * 
     * @param products the list of products to iterate over
     * @param targetType the type of products to filter for
     */
    public ProductTypeIterator(List<GroceryProduct> products, GroceryProduct.ProductType targetType) {
        this.products = new ArrayList<>(products);
        this.targetType = targetType;
        this.currentIndex = 0;
        this.totalCount = (int) products.stream()
            .filter(p -> p.getType() == targetType)
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
     * Gets the target type this iterator filters for.
     * 
     * @return the target product type
     */
    public GroceryProduct.ProductType getTargetType() {
        return targetType;
    }
    
    /**
     * Advances to the next product of the target type.
     */
    private void advanceToNext() {
        while (currentIndex < products.size() && 
               products.get(currentIndex).getType() != targetType) {
            currentIndex++;
        }
    }
    
    @Override
    public String toString() {
        return String.format("ProductTypeIterator[Type=%s, Position=%d/%d]",
            targetType, getCurrentPosition(), getTotalCount());
    }
}