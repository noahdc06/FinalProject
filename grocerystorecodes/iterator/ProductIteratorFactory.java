package com.university.grocerystore.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Factory for creating different types of product iterators.
 * Provides a centralized way to create iterators with different traversal strategies.
 * 
 * <p>This factory demonstrates the Factory pattern by providing methods to create
 * various types of iterators without exposing the concrete iterator classes.</p>
 */
public class ProductIteratorFactory {
    
    /**
     * Creates a type-filtering iterator.
     * 
     * @param products the list of products to iterate over
     * @param type the product type to filter for
     * @return iterator for products of the specified type
     */
    public ProductIterator createTypeIterator(List<GroceryProduct> products, GroceryProduct.ProductType type) {
        return new ProductTypeIterator(products, type);
    }
    
    /**
     * Creates a price-sorted iterator.
     * 
     * @param products the list of products to iterate over
     * @param ascending true for ascending order, false for descending
     * @return iterator for products sorted by price
     */
    public ProductIterator createPriceSortedIterator(List<GroceryProduct> products, boolean ascending) {
        return new PriceSortedIterator(products, ascending);
    }
    
    /**
     * Creates a price range iterator.
     * 
     * @param products the list of products to iterate over
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return iterator for products within the price range
     */
    public ProductIterator createPriceRangeIterator(List<GroceryProduct> products, double minPrice, double maxPrice) {
        return new PriceRangeIterator(products, minPrice, maxPrice);
    }
    
    /**
     * Creates a produce iterator.
     * 
     * @param products the list of products to iterate over
     * @return iterator for produce only
     */
    public ProductIterator createProduceIterator(List<GroceryProduct> products) {
        return new ProductTypeIterator(products, GroceryProduct.ProductType.PRODUCE);
    }
    
    /**
     * Creates an expensive products iterator.
     * 
     * @param products the list of products to iterate over
     * @param threshold the minimum price threshold
     * @return iterator for products above the price threshold
     */
    public ProductIterator createExpensiveIterator(List<GroceryProduct> products, double threshold) {
        return new PriceRangeIterator(products, threshold, Double.MAX_VALUE);
    }
    
    /**
     * Creates a cheap products iterator.
     * 
     * @param products the list of products to iterate over
     * @param threshold the maximum price threshold
     * @return iterator for products below the price threshold
     */
    public ProductIterator createCheapIterator(List<GroceryProduct> products, double threshold) {
        return new PriceRangeIterator(products, 0.0, threshold);
    }
    
    /**
     * Creates a frozen food iterator.
     * 
     * @param products the list of products to iterate over
     * @return iterator for frozen foods only
     */
    public ProductIterator createFrozenFoodIterator(List<GroceryProduct> products) {
        return new ProductTypeIterator(products, GroceryProduct.ProductType.FROZEN_FOOD);
    }
    
    /**
     * Creates a canned goods iterator.
     * 
     * @param products the list of products to iterate over
     * @return iterator for canned goods only
     */
    public ProductIterator createCannedGoodsIterator(List<GroceryProduct> products) {
        return new ProductTypeIterator(products, GroceryProduct.ProductType.CANNED_GOOD);
    }
    
    /**
     * Creates a snack iterator.
     * 
     * @param products the list of products to iterate over
     * @return iterator for snacks only
     */
    public ProductIterator createSnackIterator(List<GroceryProduct> products) {
        return new ProductTypeIterator(products, GroceryProduct.ProductType.SNACK);
    }
    
    /**
     * Creates a perishable products iterator.
     * 
     * @param products the list of products to iterate over
     * @return iterator for perishable products only
     */
    public ProductIterator createPerishableIterator(List<GroceryProduct> products) {
        return new PerishableIterator(products);
    }
    
    /**
     * Collects all products from an iterator into a list.
     * 
     * @param iterator the iterator to collect from
     * @return list of all products from the iterator
     */
    public List<GroceryProduct> collectAll(ProductIterator iterator) {
        List<GroceryProduct> result = new ArrayList<>();
        iterator.reset();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
    
    /**
     * Finds the first product matching a predicate.
     * 
     * @param iterator the iterator to search
     * @param predicate the predicate to test
     * @return Optional containing the first matching product
     */
    public Optional<GroceryProduct> findFirst(ProductIterator iterator, Predicate<GroceryProduct> predicate) {
        iterator.reset();
        while (iterator.hasNext()) {
            GroceryProduct product = iterator.next();
            if (predicate.test(product)) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Finds all products matching a predicate.
     * 
     * @param iterator the iterator to search
     * @param predicate the predicate to test
     * @return list of all matching products
     */
    public List<GroceryProduct> findAll(ProductIterator iterator, Predicate<GroceryProduct> predicate) {
        List<GroceryProduct> result = new ArrayList<>();
        iterator.reset();
        while (iterator.hasNext()) {
            GroceryProduct product = iterator.next();
            if (predicate.test(product)) {
                result.add(product);
            }
        }
        return result;
    }
    
    /**
     * Counts products matching a predicate.
     * 
     * @param iterator the iterator to count
     * @param predicate the predicate to test
     * @return the count of matching products
     */
    public int count(ProductIterator iterator, Predicate<GroceryProduct> predicate) {
        int count = 0;
        iterator.reset();
        while (iterator.hasNext()) {
            GroceryProduct product = iterator.next();
            if (predicate.test(product)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Checks if any product matches a predicate.
     * 
     * @param iterator the iterator to check
     * @param predicate the predicate to test
     * @return true if any product matches
     */
    public boolean anyMatch(ProductIterator iterator, Predicate<GroceryProduct> predicate) {
        iterator.reset();
        while (iterator.hasNext()) {
            GroceryProduct product = iterator.next();
            if (predicate.test(product)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if all products match a predicate.
     * 
     * @param iterator the iterator to check
     * @param predicate the predicate to test
     * @return true if all products match
     */
    public boolean allMatch(ProductIterator iterator, Predicate<GroceryProduct> predicate) {
        iterator.reset();
        while (iterator.hasNext()) {
            GroceryProduct product = iterator.next();
            if (!predicate.test(product)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets the total count of products in an iterator.
     * 
     * @param iterator the iterator to count
     * @return the total count
     */
    public int getTotalCount(ProductIterator iterator) {
        return iterator.getTotalCount();
    }
    
    /**
     * Gets the remaining count of products in an iterator.
     * 
     * @param iterator the iterator to check
     * @return the remaining count
     */
    public int getRemainingCount(ProductIterator iterator) {
        return iterator.getRemainingCount();
    }
    
    @Override
    public String toString() {
        return "ProductIteratorFactory[Available iterators: Type, PriceSorted, PriceRange, Produce, Expensive, Cheap, FrozenFood, CannedGoods, Snack, Perishable]";
    }
}