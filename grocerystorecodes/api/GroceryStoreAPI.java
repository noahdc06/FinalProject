package com.university.grocerystore.api;

import java.util.List;

import com.university.grocerystore.model.GroceryItem;

/**
 * Defines the contract for grocery store operations. This interface provides
 * an API for managing a grocery inventory, including CRUD operations, 
 * search functionality, and analytics.
 * 
 * <p>Implementations should ensure thread-safety if concurrent access is expected.</p>
 */
public interface GroceryStoreAPI {
    
    /**
     * Adds a grocery item to the inventory.
     * Duplicate SKUs are not allowed.
     * 
     * @param item the grocery item to add (non-null)
     * @return true if the item was added, false if SKU already exists or item is null
     */
    boolean add(GroceryItem item);
    
    /**
     * Removes a grocery item from the inventory by SKU.
     * 
     * @param sku the SKU of the item to remove
     * @return true if an item was removed, false if no item with that SKU exists
     */
    boolean removeBySku(String sku);
    
    /**
     * Finds a grocery item by its SKU.
     * 
     * @param sku the SKU to search for
     * @return the grocery item if found, null otherwise
     */
    GroceryItem findBySku(String sku);
    
    /**
     * Searches for grocery items by name (case-insensitive, partial match).
     * 
     * @param nameQuery the name or partial name to search for
     * @return list of matching items (never null, may be empty)
     */
    List<GroceryItem> findByName(String nameQuery);
    
    /**
     * Searches for grocery items by brand (case-insensitive, partial match).
     * 
     * @param brandQuery the brand name or partial name to search for
     * @return list of matching items (never null, may be empty)
     */
    List<GroceryItem> findByBrand(String brandQuery);
    
    /**
     * Finds all grocery items within a price range (inclusive).
     * 
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return list of items within the price range
     * @throws IllegalArgumentException if minPrice > maxPrice or prices are negative
     */
    List<GroceryItem> findByPriceRange(double minPrice, double maxPrice);
    
    /**
     * Finds all grocery items with a specific expiration year.
     * 
     * @param year the expiration year
     * @return list of items expiring in that year
     */
    List<GroceryItem> findByExpirationYear(int year);
    
    /**
     * Gets the number of grocery items in the inventory.
     * 
     * @return the total number of items
     */
    int size();
    
    /**
     * Calculates the total value of all grocery items in inventory.
     * 
     * @return sum of all item prices
     */
    double inventoryValue();
    
    /**
     * Finds the most expensive grocery item in the inventory.
     * 
     * @return the item with highest price, null if inventory is empty
     */
    GroceryItem getMostExpensive();
    
    /**
     * Finds the most recently produced grocery item (closest expiration date).
     * 
     * @return the item with the latest production/expiration year, null if inventory is empty
     */
    GroceryItem getMostRecent();
    
    /**
     * Creates a defensive copy of the inventory as an array.
     * Changes to the returned array will not affect the inventory.
     * 
     * @return array containing all grocery items in the inventory
     */
    GroceryItem[] snapshotArray();
    
    /**
     * Gets all grocery items in the inventory.
     * The returned list is a defensive copy.
     * 
     * @return list of all items (never null, may be empty)
     */
    List<GroceryItem> getAllItems();
}
