package com.university.grocerystore.repository;

import java.util.List;
import java.util.Optional;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Port interface for grocery product persistence operations in hexagonal architecture.
 * Defines the contract for grocery product storage without specifying implementation details.
 * 
 * <p>This interface represents the "port" in the ports and adapters pattern,
 * allowing the domain layer to interact with persistence without being coupled
 * to specific storage technologies (JSON, database, etc.).</p>
 */
public interface GroceryProductRepository {
    
    /**
     * Saves a grocery product to the repository.
     * If a product with the same ID already exists, it will be updated.
     * 
     * @param product the grocery product to save
     * @throws RepositoryException if the save operation fails
     */
    void save(GroceryProduct product);
    
    /**
     * Finds a grocery product by its unique identifier.
     * 
     * @param id the product ID
     * @return the product if found, empty Optional otherwise
     * @throws RepositoryException if the find operation fails
     */
    Optional<GroceryProduct> findById(String id);
    
    /**
     * Retrieves all grocery products from the repository.
     * 
     * @return list of all products
     * @throws RepositoryException if the retrieval operation fails
     */
    List<GroceryProduct> findAll();
    
    /**
     * Deletes a grocery product by its ID.
     * 
     * @param id the product ID to delete
     * @return true if the product was deleted, false if not found
     * @throws RepositoryException if the delete operation fails
     */
    boolean delete(String id);
    
    /**
     * Checks if a grocery product with the given ID exists.
     * 
     * @param id the product ID
     * @return true if the product exists, false otherwise
     * @throws RepositoryException if the check operation fails
     */
    boolean exists(String id);
    
    /**
     * Counts the total number of grocery products in the repository.
     * 
     * @return the number of products
     * @throws RepositoryException if the count operation fails
     */
    long count();
    
    /**
     * Deletes all grocery products from the repository.
     * 
     * @throws RepositoryException if the clear operation fails
     */
    void deleteAll();
}
