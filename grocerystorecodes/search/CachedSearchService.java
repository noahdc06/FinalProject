package com.university.grocerystore.search;

import java.util.List;
import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.repository.GroceryProductRepository;

/**
 * Service that integrates Trie-based prefix search with LRU caching for optimal performance.
 * Provides fast prefix-based product searching with intelligent result caching.
 * 
 * <p>This service combines the efficiency of Trie data structures for prefix searching
 * with LRU caching to avoid repeated computation for frequently accessed queries.</p>
 */
public class CachedSearchService {
    
    private final ProductTrie trie;
    private final SearchResultCache cache;
    private final GroceryProductRepository repository;
    
    /**
     * Creates a new cached search service.
     * 
     * @param repository the product repository to search
     * @param cacheSize the maximum number of cached search results
     */
    public CachedSearchService(GroceryProductRepository repository, int cacheSize) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.trie = new ProductTrie();
        this.cache = new SearchResultCache(cacheSize);
        initializeTrie();
    }
    
    /**
     * Searches for products by name prefix with caching.
     * 
     * @param prefix the name prefix to search for
     * @return list of products matching the prefix
     */
    public List<GroceryProduct> searchByPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return List.of();
        }
        
        String cacheKey = "prefix:" + prefix.toLowerCase().trim();
        
        // Check cache first
        var cached = cache.get(cacheKey);
        if (cached.isPresent()) {
            return cached.get();
        }
        
        // Perform search using trie
        List<GroceryProduct> results = trie.searchByPrefix(prefix);
        
        // Cache results
        cache.put(cacheKey, results);
        
        return results;
    }
    
    /**
     * Searches for products by name prefix with result limit and caching.
     * 
     * @param prefix the name prefix to search for
     * @param limit the maximum number of results to return
     * @return list of products matching the prefix, limited to the specified count
     */
    public List<GroceryProduct> searchByPrefixWithLimit(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty() || limit <= 0) {
            return List.of();
        }
        
        String cacheKey = "prefix:" + prefix.toLowerCase().trim() + ":limit:" + limit;
        
        // Check cache first
        var cached = cache.get(cacheKey);
        if (cached.isPresent()) {
            return cached.get();
        }
        
        // Perform search using trie
        List<GroceryProduct> results = trie.searchByPrefixWithLimit(prefix, limit);
        
        // Cache results
        cache.put(cacheKey, results);
        
        return results;
    }
    
    /**
     * Adds a product to the search index and invalidates relevant cache entries.
     * 
     * @param product the product to add
     */
    public void addProduct(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        trie.insert(product);
        invalidateCacheForProduct(product);
    }
    
    /**
     * Removes a product from the search index and invalidates relevant cache entries.
     * 
     * @param product the product to remove
     */
    public void removeProduct(GroceryProduct product) {
        if (product == null) {
            return;
        }
        
        trie.remove(product);
        invalidateCacheForProduct(product);
    }
    
    /**
     * Refreshes the search index from the repository.
     * Clears the cache to ensure consistency.
     */
    public void refreshIndex() {
        trie.clear();
        cache.clear();
        initializeTrie();
    }
    
    /**
     * Gets cache statistics for performance monitoring.
     * 
     * @return cache statistics
     */
    public SearchResultCache.CacheStats getCacheStats() {
        return cache.getStats();
    }
    
    /**
     * Gets the current size of the search index.
     * 
     * @return the number of products in the index
     */
    public int getIndexSize() {
        return trie.size();
    }
    
    /**
     * Checks if the search index is empty.
     * 
     * @return true if no products are indexed
     */
    public boolean isIndexEmpty() {
        return trie.isEmpty();
    }
    
    /**
     * Clears the search index and cache.
     */
    public void clear() {
        trie.clear();
        cache.clear();
    }
    
    /**
     * Initializes the trie with all products from the repository.
     */
    private void initializeTrie() {
        List<GroceryProduct> products = repository.findAll();
        for (GroceryProduct product : products) {
            trie.insert(product);
        }
    }
    
    /**
     * Invalidates cache entries that might be affected by changes to a product.
     * 
     * @param product the product that was changed
     */
    private void invalidateCacheForProduct(GroceryProduct product) {
        String name = product.getName().toLowerCase();
        
        // Invalidate cache entries for all possible prefixes of the product's name
        for (int i = 1; i <= name.length(); i++) {
            String prefix = name.substring(0, i);
            String cacheKey = "prefix:" + prefix;
            cache.remove(cacheKey);
            
            // Also remove limit-based cache entries
            for (int limit = 10; limit <= 100; limit += 10) {
                String limitCacheKey = cacheKey + ":limit:" + limit;
                cache.remove(limitCacheKey);
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format("CachedSearchService[IndexSize=%d, CacheSize=%d/%d, HitRatio=%.2f%%]",
            getIndexSize(),
            cache.size(),
            cache.getMaxSize(),
            getCacheStats().getHitRatio() * 100);
    }
}
