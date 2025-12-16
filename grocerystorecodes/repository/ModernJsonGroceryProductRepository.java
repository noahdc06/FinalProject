package com.university.grocerystore.repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.university.grocerystore.model.GroceryProduct;

/**
 * Modern JSON-based implementation of GroceryProductRepository using best practices.
 * Features:
 * - Try-with-resources for automatic resource management
 * - NIO.2 for better file operations
 * - Thread-safe file operations with ReadWriteLock
 * - Better error handling and logging
 * - Atomic file operations to prevent corruption
 */
public class ModernJsonGroceryProductRepository implements GroceryProductRepository, AutoCloseable {
    
    private static final Logger LOGGER = Logger.getLogger(ModernJsonGroceryProductRepository.class.getName());
    private static final String SAFE_BASE_DIR = System.getProperty("user.dir") + "/data";
    private static final int MAX_PATH_LENGTH = 255;
    
    private final Path dataFile;
    private final Path backupFile;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock fileLock;
    private volatile boolean closed = false;
    
    /**
     * Creates a new modern JSON grocery product repository.
     * 
     * @param filePath the path to the JSON file for persistence
     */
    public ModernJsonGroceryProductRepository(String filePath) {
        Path validatedPath = validateAndSanitizePath(filePath);
        this.dataFile = validatedPath;
        this.backupFile = Paths.get(validatedPath.toString() + ".backup");
        this.fileLock = new ReentrantReadWriteLock();
        
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.findAndRegisterModules(); // For Java 8 time support
        
        initializeStorage();
    }
    
    private void initializeStorage() {
        try {
            // Ensure the directory exists
            Path parentDir = dataFile.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                LOGGER.info("Created directory: " + parentDir);
            }
            
            // Create empty file if it doesn't exist
            if (!Files.exists(dataFile)) {
                saveAtomic(new ArrayList<>());
                LOGGER.info("Created new data file: " + dataFile);
            }
        } catch (IOException e) {
            throw new RepositoryException("Failed to initialize storage", e);
        }
    }
    
    /**
     * Validates and sanitizes the file path to prevent path traversal attacks.
     * 
     * @param filePath the file path to validate
     * @return the validated and sanitized Path
     * @throws SecurityException if the path is invalid or attempts path traversal
     */
    private Path validateAndSanitizePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        // Remove any path traversal attempts
        String cleanPath = filePath.replaceAll("\\.\\./", "").replaceAll("\\.\\.", "");
        
        // Check for suspicious patterns
        if (cleanPath.contains("../") || cleanPath.contains("..\\") || 
            cleanPath.contains("%2e%2e") || cleanPath.contains("%252e")) {
            throw new SecurityException("Invalid file path: potential path traversal detected");
        }
        
        // Validate path length
        if (cleanPath.length() > MAX_PATH_LENGTH) {
            throw new IllegalArgumentException("File path exceeds maximum length");
        }
        
        try {
            // Normalize the path
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
            
            return normalizedPath;
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
        ensureNotClosed();
        
        fileLock.writeLock().lock();
        try {
            List<GroceryProduct> products = loadAllInternal();
            
            // Remove existing product with same ID if it exists
            products.removeIf(p -> p.getId().equals(product.getId()));
            
            // Add the new/updated product
            products.add(product);
            
            // Save atomically using wrapper
            saveAtomic(products);
            
            LOGGER.fine("Saved product: " + product.getId());
        } catch (IOException e) {
            throw new RepositoryException("Failed to save product: " + product.getId(), e);
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    /**
     * Saves multiple products in batch for better performance.
     * 
     * @param productsToSave collection of products to save
     */
    public void saveAll(Collection<GroceryProduct> productsToSave) {
        Objects.requireNonNull(productsToSave, "Products collection cannot be null");
        ensureNotClosed();
        
        if (productsToSave.isEmpty()) {
            return;
        }
        
        fileLock.writeLock().lock();
        try {
            List<GroceryProduct> products = loadAllInternal();
            
            // Create a map for efficient lookup
            Map<String, GroceryProduct> productMap = new HashMap<>();
            for (GroceryProduct p : products) {
                productMap.put(p.getId(), p);
            }
            
            // Update or add new products
            for (GroceryProduct product : productsToSave) {
                if (product != null) {
                    productMap.put(product.getId(), product);
                }
            }
            
            // Save atomically
            saveAtomic(new ArrayList<>(productMap.values()));
            
            LOGGER.fine("Saved " + productsToSave.size() + " products in batch");
        } catch (IOException e) {
            throw new RepositoryException("Failed to save products batch", e);
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    @Override
    public Optional<GroceryProduct> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        ensureNotClosed();
        
        fileLock.readLock().lock();
        try {
            List<GroceryProduct> products = loadAllInternal();
            return products.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to find product by ID: " + id, e);
            return Optional.empty();
        } finally {
            fileLock.readLock().unlock();
        }
    }
    
    @Override
    public List<GroceryProduct> findAll() {
        ensureNotClosed();
        
        fileLock.readLock().lock();
        try {
            return new ArrayList<>(loadAllInternal());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load all products", e);
            return new ArrayList<>();
        } finally {
            fileLock.readLock().unlock();
        }
    }
    
    @Override
    public boolean delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        ensureNotClosed();
        
        fileLock.writeLock().lock();
        try {
            List<GroceryProduct> products = loadAllInternal();
            boolean removed = products.removeIf(p -> id.equals(p.getId()));
            
            if (removed) {
                saveAtomic(products);
                LOGGER.fine("Deleted product: " + id);
            }
            
            return removed;
        } catch (IOException e) {
            throw new RepositoryException("Failed to delete product: " + id, e);
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    /**
     * Deletes multiple products in batch.
     * 
     * @param ids collection of IDs to delete
     * @return number of products deleted
     */
    public int deleteAll(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        ensureNotClosed();
        
        fileLock.writeLock().lock();
        try {
            List<GroceryProduct> products = loadAllInternal();
            Set<String> idSet = new HashSet<>(ids);
            int originalSize = products.size();
            
            products.removeIf(p -> idSet.contains(p.getId()));
            int deleted = originalSize - products.size();
            
            if (deleted > 0) {
                saveAtomic(products);
                LOGGER.fine("Deleted " + deleted + " products in batch");
            }
            
            return deleted;
        } catch (IOException e) {
            throw new RepositoryException("Failed to delete products batch", e);
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean exists(String id) {
        return findById(id).isPresent();
    }
    
    @Override
    public long count() {
        ensureNotClosed();
        
        fileLock.readLock().lock();
        try {
            return loadAllInternal().size();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to count products", e);
            return 0;
        } finally {
            fileLock.readLock().unlock();
        }
    }
    
    @Override
    public void deleteAll() {
        ensureNotClosed();
        
        fileLock.writeLock().lock();
        try {
            saveAtomic(new ArrayList<>());
            LOGGER.info("Cleared all products from repository");
        } catch (IOException e) {
            throw new RepositoryException("Failed to clear all products", e);
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    /**
     * Loads all products from the JSON file using try-with-resources.
     * 
     * @return list of products
     * @throws IOException if file reading fails
     */
    private List<GroceryProduct> loadAllInternal() throws IOException {
        if (!Files.exists(dataFile) || Files.size(dataFile) == 0) {
            return new ArrayList<>();
        }
        
        // Use try-with-resources for automatic resource management
        try (BufferedReader reader = Files.newBufferedReader(dataFile)) {
            ProductsWrapper wrapper = objectMapper.readValue(reader, ProductsWrapper.class);
            return wrapper.getProducts() != null ? wrapper.getProducts() : new ArrayList<>();
        } catch (IOException e) {
            // Try to restore from backup if main file is corrupted
            if (Files.exists(backupFile)) {
                LOGGER.warning("Main file corrupted, attempting to restore from backup");
                try (BufferedReader backupReader = Files.newBufferedReader(backupFile)) {
                    ProductsWrapper wrapper = objectMapper.readValue(backupReader, ProductsWrapper.class);
                    // Restore the main file from backup
                    Files.copy(backupFile, dataFile, 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    return wrapper.getProducts() != null ? wrapper.getProducts() : new ArrayList<>();
                } catch (IOException backupError) {
                    LOGGER.log(Level.SEVERE, "Failed to restore from backup", backupError);
                    return new ArrayList<>();
                }
            }
            LOGGER.log(Level.WARNING, "Failed to parse JSON, returning empty list", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Saves products atomically to prevent corruption.
     * 
     * @param products list of products to save
     * @throws IOException if saving fails
     */
    private void saveAtomic(List<GroceryProduct> products) throws IOException {
        // Create backup of current file if it exists
        if (Files.exists(dataFile)) {
            Files.copy(dataFile, backupFile, 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Write to temporary file first
        Path tempFile = Files.createTempFile(dataFile.getParent(), "temp", ".json");
        
        try {
            // Use try-with-resources for writing with wrapper
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                ProductsWrapper wrapper = new ProductsWrapper(products);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, wrapper);
            }
            
            // Atomic move (rename) - this is atomic on most file systems
            Files.move(tempFile, dataFile, 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            
        } catch (IOException e) {
            // Clean up temp file if operation failed
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException deleteError) {
                LOGGER.log(Level.WARNING, "Failed to delete temp file", deleteError);
            }
            throw e;
        }
    }
    
    /**
     * Creates a backup of the current data file.
     * 
     * @return true if backup was created successfully
     */
    public boolean createBackup() {
        ensureNotClosed();
        
        fileLock.readLock().lock();
        try {
            if (Files.exists(dataFile)) {
                Path backupPath = Paths.get(dataFile + "." + System.currentTimeMillis() + ".backup");
                Files.copy(dataFile, backupPath);
                LOGGER.info("Created backup: " + backupPath);
                return true;
            }
            return false;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to create backup", e);
            return false;
        } finally {
            fileLock.readLock().unlock();
        }
    }
    
    /**
     * Restores data from a backup file.
     * 
     * @param backupPath path to the backup file
     * @return true if restore was successful
     */
    public boolean restoreFromBackup(String backupPath) {
        Objects.requireNonNull(backupPath, "Backup path cannot be null");
        ensureNotClosed();
        
        Path backup = Paths.get(backupPath);
        if (!Files.exists(backup)) {
            LOGGER.warning("Backup file does not exist: " + backupPath);
            return false;
        }
        
        fileLock.writeLock().lock();
        try {
            // Validate backup file can be read
            ProductsWrapper wrapper;
            try (BufferedReader reader = Files.newBufferedReader(backup)) {
                wrapper = objectMapper.readValue(reader, ProductsWrapper.class);
            }
            
            // If validation successful, replace current file
            Files.copy(backup, dataFile, 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            LOGGER.info("Restored from backup: " + backupPath);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to restore from backup: " + e.getMessage(), e);
            e.printStackTrace();
            return false;
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    /**
     * Gets the file path used for persistence.
     * 
     * @return the file path
     */
    public String getFilePath() {
        return dataFile.toString();
    }
    
    /**
     * Checks if the data file exists.
     * 
     * @return true if the file exists
     */
    public boolean dataFileExists() {
        return Files.exists(dataFile);
    }
    
    /**
     * Gets the size of the data file in bytes.
     * 
     * @return file size in bytes
     */
    public long getDataFileSize() {
        try {
            return Files.size(dataFile);
        } catch (IOException e) {
            return 0;
        }
    }
    
    /**
     * Performs maintenance operations like cleanup and optimization.
     */
    public void performMaintenance() {
        ensureNotClosed();
        
        fileLock.writeLock().lock();
        try {
            // Clean up old backup files
            Path parent = dataFile.getParent();
            if (parent != null) {
                long cutoffTime = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000); // 7 days
                
                Files.list(parent)
                    .filter(path -> path.toString().endsWith(".backup"))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            LOGGER.fine("Deleted old backup: " + path);
                        } catch (IOException e) {
                            LOGGER.warning("Failed to delete old backup: " + path);
                        }
                    });
            }
            
            // Compact the JSON file by rewriting it
            List<GroceryProduct> products = loadAllInternal();
            saveAtomic(products);
            
            LOGGER.info("Maintenance completed");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Maintenance failed", e);
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    private void ensureNotClosed() {
        if (closed) {
            throw new IllegalStateException("Repository has been closed");
        }
    }
    
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            LOGGER.info("Repository closed");
        }
    }
}
