package com.university.grocerystore.service;

import java.util.List;
import java.util.Optional;

import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.repository.GroceryProductRepository;

/**
 * Domain service that orchestrates business logic for grocery product management.
 * Demonstrates hexagonal architecture by coordinating between domain logic and infrastructure.
 * 
 * <p>This service encapsulates business rules and coordinates between the domain layer
 * and the repository layer, maintaining clean separation of concerns.</p>
 */
public class GroceryProductService {
    
    private final GroceryProductRepository repository;
    
    /**
     * Creates a new grocery product service with the specified repository.
     * 
     * @param repository the product repository to use
     */
    public GroceryProductService(GroceryProductRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Adds a product to the system with business logic validation.
     * 
     * @param product the product to add
     * @throws InvalidProductException if the product is invalid
     */
    public void addProduct(GroceryProduct product) {
        validateProduct(product);
        repository.save(product);
    }
    
    /**
     * Updates an existing product with business logic validation.
     * 
     * @param product the product to update
     * @throws InvalidProductException if the product is invalid
     * @throws ProductNotFoundException if the product doesn't exist
     */
    public void updateProduct(GroceryProduct product) {
        validateProduct(product);
        
        if (!repository.exists(product.getId())) {
            throw new ProductNotFoundException("Product not found: " + product.getId());
        }
        
        repository.save(product);
    }
    
    /**
     * Finds a product by its ID.
     * 
     * @param id the product ID
     * @return the product if found
     * @throws ProductNotFoundException if the product doesn't exist
     */
    public GroceryProduct findProduct(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
    }
    
    /**
     * Finds a product by its ID, returning Optional.
     * 
     * @param id the product ID
     * @return Optional containing the product if found
     */
    public Optional<GroceryProduct> findProductOptional(String id) {
        return repository.findById(id);
    }
    
    /**
     * Gets all products in the system.
     * 
     * @return list of all products
     */
    public List<GroceryProduct> getAllProducts() {
        return repository.findAll();
    }
    
    /**
     * Removes a product from the system.
     * 
     * @param id the product ID to remove
     * @return true if the product was removed, false if not found
     */
    public boolean removeProduct(String id) {
        return repository.delete(id);
    }
    
    /**
     * Checks if a product exists in the system.
     * 
     * @param id the product ID
     * @return true if the product exists
     */
    public boolean productExists(String id) {
        return repository.exists(id);
    }
    
    /**
     * Gets the total number of products in the system.
     * 
     * @return the product count
     */
    public long getProductCount() {
        return repository.count();
    }
    
    /**
     * Clears all products from the system.
     */
    public void clearAllProducts() {
        repository.deleteAll();
    }
    
    /**
     * Validates a product according to business rules.
     * 
     * @param product the product to validate
     * @throws InvalidProductException if validation fails
     */
    private void validateProduct(GroceryProduct product) {
        if (product == null) {
            throw new InvalidProductException("Product cannot be null");
        }
        
        if (product.getId() == null || product.getId().trim().isEmpty()) {
            throw new InvalidProductException("Product ID cannot be null or empty");
        }
        
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidProductException("Product name cannot be null or empty");
        }
        
        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            throw new InvalidProductException("Product brand cannot be null or empty");
        }
        
        if (product.getPrice() < 0) {
            throw new InvalidProductException("Product price cannot be negative: " + product.getPrice());
        }
        
        if (product.getProductionYear() < 1900 || product.getProductionYear() > 2100) {
            throw new InvalidProductException("Product production year must be between 1900 and 2100: " + product.getProductionYear());
        }
    }
    
    /**
     * Exception thrown when a product is invalid according to business rules.
     */
    public static class InvalidProductException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        public InvalidProductException(String message) {
            super(message);
        }
        
        public InvalidProductException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Exception thrown when a requested product is not found.
     */
    public static class ProductNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        public ProductNotFoundException(String message) {
            super(message);
        }
        
        public ProductNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
