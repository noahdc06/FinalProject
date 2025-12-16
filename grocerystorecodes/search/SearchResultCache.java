package com.university.grocerystore.search;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.university.grocerystore.model.GroceryProduct;

/**
 * LRU (Least Recently Used) cache for search results.
 * Evicts least recently used items when the cache reaches its maximum size.
 * 
 * <p>This implementation provides O(1) average time complexity for get and put operations
 * by using a HashMap for O(1) lookups and a Deque for O(1) access order tracking.</p>
 */
public class SearchResultCache {
    
    private final int maxSize;
    private final Map<String, CacheEntry> cache;
    private final Deque<String> accessOrder;
    
    /**
     * Creates a new search result cache with the specified maximum size.
     * 
     * @param maxSize the maximum number of entries the cache can hold
     * @throws IllegalArgumentException if maxSize is not positive
     */
    public SearchResultCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Cache size must be positive: " + maxSize);
        }
        
        this.maxSize = maxSize;
        this.cache = new HashMap<>();
        this.accessOrder = new LinkedList<>();
    }
    
    /**
     * Retrieves cached search results for the given key.
     * Updates the access order to mark this entry as recently used.
     * 
     * @param key the cache key
     * @return Optional containing the cached results if found
     */
    public Optional<List<GroceryProduct>> get(String key) {
        if (key == null) {
            return Optional.empty();
        }
        
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            // Move to end (most recently used)
            accessOrder.remove(key);
            accessOrder.addLast(key);
            return Optional.of(new ArrayList<>(entry.results));
        }
        
        return Optional.empty();
    }
    
    /**
     * Stores search results in the cache with the given key.
     * Evicts the least recently used entry if the cache is full.
     * 
     * @param key the cache key
     * @param results the search results to cache
     * @throws IllegalArgumentException if key is null
     */
    public void put(String key, List<GroceryProduct> results) {
        if (key == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        
        if (results == null) {
            throw new IllegalArgumentException("Search results cannot be null");
        }
        
        // If cache is full, remove least recently used entry
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            evictLRU();
        }
        
        // Store the new entry
        cache.put(key, new CacheEntry(results));
        
        // Update access order
        accessOrder.remove(key); // Remove if already exists
        accessOrder.addLast(key);
    }
    
    /**
     * Checks if the cache contains results for the given key.
     * 
     * @param key the cache key
     * @return true if the key exists in the cache
     */
    public boolean containsKey(String key) {
        return key != null && cache.containsKey(key);
    }
    
    /**
     * Removes the entry with the given key from the cache.
     * 
     * @param key the cache key to remove
     * @return true if an entry was removed, false if key not found
     */
    public boolean remove(String key) {
        if (key == null) {
            return false;
        }
        
        CacheEntry removed = cache.remove(key);
        accessOrder.remove(key);
        return removed != null;
    }
    
    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        cache.clear();
        accessOrder.clear();
    }
    
    /**
     * Gets the current number of entries in the cache.
     * 
     * @return the cache size
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Gets the maximum number of entries the cache can hold.
     * 
     * @return the maximum cache size
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * Checks if the cache is empty.
     * 
     * @return true if no entries are cached
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }
    
    /**
     * Checks if the cache is full.
     * 
     * @return true if the cache has reached its maximum size
     */
    public boolean isFull() {
        return cache.size() >= maxSize;
    }
    
    /**
     * Gets cache statistics including hit ratio and average age.
     * 
     * @return cache statistics
     */
    public CacheStats getStats() {
        long totalRequests = 0;
        long totalHits = 0;
        long totalAge = 0;
        long currentTime = System.currentTimeMillis();
        
        for (CacheEntry entry : cache.values()) {
            totalRequests += entry.accessCount;
            totalHits += entry.hitCount;
            totalAge += (currentTime - entry.timestamp);
        }
        
        double hitRatio = totalRequests > 0 ? (double) totalHits / totalRequests : 0.0;
        double averageAge = cache.size() > 0 ? (double) totalAge / cache.size() : 0.0;
        
        return new CacheStats(
            cache.size(),
            maxSize,
            hitRatio,
            averageAge,
            totalRequests,
            totalHits
        );
    }
    
    /**
     * Evicts the least recently used entry from the cache.
     */
    private void evictLRU() {
        if (!accessOrder.isEmpty()) {
            String lruKey = accessOrder.removeFirst();
            cache.remove(lruKey);
        }
    }
    
    /**
     * Internal class representing a cache entry.
     */
    private static class CacheEntry {
        final List<GroceryProduct> results;
        final long timestamp;
        long accessCount;
        long hitCount;
        
        CacheEntry(List<GroceryProduct> results) {
            this.results = new ArrayList<>(results);
            this.timestamp = System.currentTimeMillis();
            this.accessCount = 0;
            this.hitCount = 0;
        }
    }
    
    /**
     * Statistics class for cache performance monitoring.
     */
    public static class CacheStats {
        private final int currentSize;
        private final int maxSize;
        private final double hitRatio;
        private final double averageAge;
        private final long totalRequests;
        private final long totalHits;
        
        public CacheStats(int currentSize, int maxSize, double hitRatio, 
                         double averageAge, long totalRequests, long totalHits) {
            this.currentSize = currentSize;
            this.maxSize = maxSize;
            this.hitRatio = hitRatio;
            this.averageAge = averageAge;
            this.totalRequests = totalRequests;
            this.totalHits = totalHits;
        }
        
        public int getCurrentSize() { return currentSize; }
        public int getMaxSize() { return maxSize; }
        public double getHitRatio() { return hitRatio; }
        public double getAverageAge() { return averageAge; }
        public long getTotalRequests() { return totalRequests; }
        public long getTotalHits() { return totalHits; }
        
        @Override
        public String toString() {
            return String.format("CacheStats[Size=%d/%d, HitRatio=%.2f%%, AvgAge=%.0fms, Requests=%d, Hits=%d]",
                currentSize, maxSize, hitRatio * 100, averageAge, totalRequests, totalHits);
        }
    }
    
    @Override
    public String toString() {
        return String.format("SearchResultCache[Size=%d/%d, HitRatio=%.2f%%]",
            size(), maxSize, getStats().getHitRatio() * 100);
    }
}
