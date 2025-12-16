package com.university.grocerystore.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.grocerystore.model.GroceryProduct;

/**
 * JSON-based implementation of GroceryProductRepository (Adapter in hexagonal architecture).
 * Persists grocery products to a JSON file using Jackson for serialization.
 * 
 * <p>This adapter implements the GroceryProductRepository port, providing JSON file-based
 * persistence without the domain layer knowing about the storage mechanism.</p>
 */
public class JsonGroceryProductRepository implements GroceryProductRepository {
    
    private static final String SAFE_BASE_DIR = System.getProperty("user.dir") + "/data";
    private static final int MAX_PATH_LENGTH = 255;
    
    private final String filePath;
    private final ObjectMapper objectMapper;
    private final File dataFile;
    
    /**
     * Creates a new JSON grocery product repository.
     * 
     * @param filePath the path to the JSON file for persistence
     */
    public JsonGroceryProductRepository(String filePath) {
        // Validate and sanitize the file path to prevent path traversal
        this.filePath = validateAndSanitizePath(filePath);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
        this.dataFile = new File(this.filePath);
        
        // Ensure the directory exists
        File parentDir = dataFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }
    
    /**
     * Validates and sanitizes the file path to prevent path traversal attacks.
     * 
     * @param filePath the file path to validate
     * @return the validated and sanitized file path
     * @throws SecurityException if the path is invalid or attempts path traversal
     */
    private String validateAndSanitizePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        // Check for suspicious patterns BEFORE cleaning
        if (filePath.contains("../") || filePath.contains("..\\") || 
            filePath.contains("%2e%2e") || filePath.contains("%252e")) {
            throw new SecurityException("Invalid file path: potential path traversal detected");
        }
        
        // Remove any path traversal attempts
        String cleanPath = filePath.replaceAll("\\.\\./", "").replaceAll("\\.\\.", "");
        
        // Validate path length
        if (cleanPath.length() > MAX_PATH_LENGTH) {
            throw new IllegalArgumentException("File path exceeds maximum length");
        }
        
        try {
            // Normalize the path and ensure it's within safe directory
            Path normalizedPath = Paths.get(cleanPath).normalize();
            Path safePath = Paths.get(SAFE_BASE_DIR).normalize();
            
            // If the path is not absolute, make it relative to safe directory
            if (!normalizedPath.isAbsolute()) {
                normalizedPath = safePath.resolve(normalizedPath).normalize();
            }
            
            // Ensure the normalized path is within the safe directory
            if (!normalizedPath.startsWith(safePath)) {
                throw new SecurityException("File path must be within the safe directory: " + SAFE_BASE_DIR);
            }
            
            return normalizedPath.toString();
        } catch (Exception e) {
            if (e instanceof SecurityException) {
                throw (SecurityException) e;
            }
            throw new IllegalArgumentException("Invalid file path: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void save(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        try {
            List<GroceryProduct> products = loadAll();
            
            // Remove existing product with same ID if it exists
            products.removeIf(p -> p.getId().equals(product.getId()));
            
            // Add the new/updated product
            products.add(product);
            
            // Save to file using wrapper to ensure polymorphic serialization
            ProductsWrapper wrapper = new ProductsWrapper(products);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, wrapper);
            
        } catch (IOException e) {
            throw new RepositoryException("Failed to save product: " + product.getId(), e);
        }
    }
    
    @Override
    public Optional<GroceryProduct> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            List<GroceryProduct> products = loadAll();
            return products.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst();
                
        } catch (IOException e) {
            throw new RepositoryException("Failed to find product by ID: " + id, e);
        }
    }
    
    @Override
    public List<GroceryProduct> findAll() {
        try {
            return loadAll();
        } catch (IOException e) {
            throw new RepositoryException("Failed to load all products", e);
        }
    }
    
    @Override
    public boolean delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        try {
            List<GroceryProduct> products = loadAll();
            boolean removed = products.removeIf(p -> id.equals(p.getId()));
            
            if (removed) {
                ProductsWrapper wrapper = new ProductsWrapper(products);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, wrapper);
            }
            
            return removed;
            
        } catch (IOException e) {
            throw new RepositoryException("Failed to delete product: " + id, e);
        }
    }
    
    @Override
    public boolean exists(String id) {
        return findById(id).isPresent();
    }
    
    @Override
    public long count() {
        try {
            return loadAll().size();
        } catch (IOException e) {
            throw new RepositoryException("Failed to count products", e);
        }
    }
    
    @Override
    public void deleteAll() {
        try {
            ProductsWrapper wrapper = new ProductsWrapper(new ArrayList<GroceryProduct>());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, wrapper);
        } catch (IOException e) {
            throw new RepositoryException("Failed to clear all products", e);
        }
    }
    
    /**
     * Loads all products from the JSON file.
     * 
     * @return list of products
     * @throws IOException if file reading fails
     */
    private List<GroceryProduct> loadAll() throws IOException {
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }
        
        if (dataFile.length() == 0) {
            return new ArrayList<>();
        }
        
        try {
            ProductsWrapper wrapper = objectMapper.readValue(dataFile, ProductsWrapper.class);
            return wrapper.getProducts() != null ? wrapper.getProducts() : new ArrayList<>();
        } catch (IOException e) {
            // Log the error for debugging
            System.err.println("Failed to load products from " + dataFile + ": " + e.getMessage());
            // If JSON parsing fails, return empty list
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets the file path used for persistence.
     * 
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Checks if the data file exists.
     * 
     * @return true if the file exists
     */
    public boolean dataFileExists() {
        return dataFile.exists();
    }
    
    /**
     * Gets the size of the data file in bytes.
     * 
     * @return file size in bytes
     */
    public long getDataFileSize() {
        return dataFile.length();
    }
}
