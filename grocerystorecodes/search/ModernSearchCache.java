package com.university.grocerystore.search;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.logging.Logger;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Modern high-performance cache implementation with advanced features.
 * Features:
 * - Time-based and size-based eviction
 * - Async loading with CompletableFuture
 * - Statistics tracking
 * - Warm-up and refresh capabilities
 * - Thread-safe operations
 * 
 * @author Navid Mohaghegh
 * @version 4.0
 * @since 2024-09-15
 */
public class ModernSearchCache implements AutoCloseable {
    
    private static final Logger LOGGER = Logger.getLogger(ModernSearchCache.class.getName());
    
    /**
     * Cache entry with metadata.
     */
    private record CacheEntry(
        List<GroceryProduct> value,
        Instant createdAt,
        Instant lastAccessedAt,
        AtomicLong accessCount
    ) {
        /**
         * Creates a new cache entry.
         */
        public static CacheEntry of(List<GroceryProduct> value) {
            return new CacheEntry(
                Collections.unmodifiableList(new ArrayList<>(value)),
                Instant.now(),
                Instant.now(),
                new AtomicLong(1)
            );
        }
        
        /**
         * Records an access to this entry.
         */
        public CacheEntry recordAccess() {
            accessCount.incrementAndGet();
            return new CacheEntry(value, createdAt, Instant.now(), accessCount);
        }
        
        /**
         * Checks if the entry is expired.
         */
        public boolean isExpired(Duration ttl) {
            return Duration.between(createdAt, Instant.now()).compareTo(ttl) > 0;
        }
        
        /**
         * Checks if the entry is stale (not accessed recently).
         */
        public boolean isStale(Duration idleTime) {
            return Duration.between(lastAccessedAt, Instant.now()).compareTo(idleTime) > 0;
        }
    }
    
    /**
     * Cache statistics record.
     * 
     * @param hitCount number of cache hits
     * @param missCount number of cache misses
     * @param evictionCount number of cache evictions
     * @param loadCount number of cache loads
     * @param hitRate cache hit rate (0.0 to 1.0)
     * @param averageLoadTime average time to load data
     * @param size current cache size
     * @param totalAccessCount total number of cache accesses
     */
    public record CacheStats(
        long hitCount,
        long missCount,
        long evictionCount,
        long loadCount,
        double hitRate,
        double averageLoadTime,
        int size,
        long totalAccessCount
    ) {
        /**
         * Creates a summary string.
         */
        public String getSummary() {
            return String.format(
                """
                Cache Statistics:
                - Hit Rate: %.2f%%
                - Total Hits: %d
                - Total Misses: %d
                - Evictions: %d
                - Cache Size: %d
                - Average Load Time: %.2f ms
                - Total Accesses: %d
                """,
                hitRate * 100, hitCount, missCount, evictionCount,
                size, averageLoadTime, totalAccessCount
            );
        }
    }
    
    private final Map<String, CacheEntry> cache;
    private final ExecutorService loadingExecutor;
    private final ScheduledExecutorService maintenanceExecutor;
    private final int maxSize;
    private final Duration ttl;
    private final Duration idleTime;
    
    // Statistics
    private final LongAdder hitCount = new LongAdder();
    private final LongAdder missCount = new LongAdder();
    private final LongAdder evictionCount = new LongAdder();
    private final LongAdder loadCount = new LongAdder();
    private final LongAdder totalLoadTime = new LongAdder();
    
    private volatile boolean closed = false;
    
    /**
     * Creates a new modern cache with default settings.
     */
    public ModernSearchCache() {
        this(1000, Duration.ofMinutes(10), Duration.ofMinutes(5));
    }
    
    /**
     * Creates a new modern cache with custom settings.
     * 
     * @param maxSize maximum number of entries
     * @param ttl time to live for entries
     * @param idleTime maximum idle time before eviction
     */
    public ModernSearchCache(int maxSize, Duration ttl, Duration idleTime) {
        this.maxSize = maxSize;
        this.ttl = ttl;
        this.idleTime = idleTime;
        this.cache = new ConcurrentHashMap<>();
        
        // Use virtual threads if available, otherwise use cached thread pool
        this.loadingExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "Cache-Loader");
            t.setDaemon(true);
            return t;
        });
        
        this.maintenanceExecutor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "Cache-Maintenance");
            t.setDaemon(true);
            return t;
        });
        
        // Schedule periodic maintenance
        scheduleMaintenance();
    }
    
    private void scheduleMaintenance() {
        // Periodic cleanup of expired and stale entries
        maintenanceExecutor.scheduleWithFixedDelay(
            this::performMaintenance,
            1, 1, TimeUnit.MINUTES
        );
        
        // Periodic statistics logging
        maintenanceExecutor.scheduleWithFixedDelay(
            () -> LOGGER.info(getStats().getSummary()),
            5, 5, TimeUnit.MINUTES
        );
    }
    
    private void performMaintenance() {
        if (closed) return;
        
        int removed = 0;
        Iterator<Map.Entry<String, CacheEntry>> iterator = cache.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, CacheEntry> entry = iterator.next();
            CacheEntry cacheEntry = entry.getValue();
            
            if (cacheEntry.isExpired(ttl) || cacheEntry.isStale(idleTime)) {
                iterator.remove();
                evictionCount.increment();
                removed++;
            }
        }
        
        if (removed > 0) {
            LOGGER.fine("Maintenance: Removed " + removed + " expired/stale entries");
        }
        
        // Size-based eviction if needed
        if (cache.size() > maxSize) {
            performSizeBasedEviction();
        }
    }
    
    private void performSizeBasedEviction() {
        int toRemove = cache.size() - maxSize;
        if (toRemove <= 0) return;
        
        // Remove least recently accessed entries
        List<Map.Entry<String, CacheEntry>> entries = new ArrayList<>(cache.entrySet());
        entries.sort(Comparator.comparing(e -> e.getValue().lastAccessedAt));
        
        for (int i = 0; i < toRemove && i < entries.size(); i++) {
            cache.remove(entries.get(i).getKey());
            evictionCount.increment();
        }
        
        LOGGER.fine("Size-based eviction: Removed " + toRemove + " entries");
    }
    
    /**
     * Gets a value from the cache or loads it if not present.
     * 
     * @param key the cache key
     * @param loader function to load the value if not cached
     * @return the cached or loaded value
     */
    public List<GroceryProduct> get(String key, Function<String, List<GroceryProduct>> loader) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(loader, "Loader cannot be null");
        ensureNotClosed();
        
        CacheEntry entry = cache.get(key);
        
        if (entry != null && !entry.isExpired(ttl)) {
            // Cache hit
            hitCount.increment();
            cache.put(key, entry.recordAccess());
            return new ArrayList<>(entry.value);
        }
        
        // Cache miss
        missCount.increment();
        
        // Load synchronously
        long startTime = System.currentTimeMillis();
        List<GroceryProduct> value = loader.apply(key);
        long loadTime = System.currentTimeMillis() - startTime;
        
        loadCount.increment();
        totalLoadTime.add(loadTime);
        
        if (value != null && !value.isEmpty()) {
            put(key, value);
        }
        
        return value != null ? new ArrayList<>(value) : new ArrayList<>();
    }
    
    /**
     * Gets a value from the cache or loads it asynchronously.
     * 
     * @param key the cache key
     * @param asyncLoader async function to load the value
     * @return CompletableFuture with the result
     */
    public CompletableFuture<List<GroceryProduct>> getAsync(
            String key, 
            Function<String, CompletableFuture<List<GroceryProduct>>> asyncLoader) {
        
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(asyncLoader, "Async loader cannot be null");
        ensureNotClosed();
        
        CacheEntry entry = cache.get(key);
        
        if (entry != null && !entry.isExpired(ttl)) {
            // Cache hit
            hitCount.increment();
            cache.put(key, entry.recordAccess());
            return CompletableFuture.completedFuture(new ArrayList<>(entry.value));
        }
        
        // Cache miss - load asynchronously
        missCount.increment();
        
        long startTime = System.currentTimeMillis();
        
        return asyncLoader.apply(key)
            .thenApply(value -> {
                long loadTime = System.currentTimeMillis() - startTime;
                loadCount.increment();
                totalLoadTime.add(loadTime);
                
                if (value != null && !value.isEmpty()) {
                    put(key, value);
                }
                
                return value != null ? new ArrayList<>(value) : new ArrayList<>();
            });
    }
    
    /**
     * Puts a value in the cache.
     * 
     * @param key the cache key
     * @param value the value to cache
     */
    public void put(String key, List<GroceryProduct> value) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        ensureNotClosed();
        
        // Check size limit
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            performSizeBasedEviction();
        }
        
        cache.put(key, CacheEntry.of(value));
    }
    
    /**
     * Invalidates a cache entry.
     * 
     * @param key the key to invalidate
     * @return true if an entry was removed
     */
    public boolean invalidate(String key) {
        ensureNotClosed();
        CacheEntry removed = cache.remove(key);
        if (removed != null) {
            evictionCount.increment();
        }
        return removed != null;
    }
    
    /**
     * Invalidates all cache entries.
     */
    public void invalidateAll() {
        ensureNotClosed();
        int size = cache.size();
        cache.clear();
        evictionCount.add(size);
        LOGGER.info("Cache cleared: " + size + " entries invalidated");
    }
    
    /**
     * Invalidates entries matching a predicate.
     * 
     * @param predicate the predicate to test keys
     * @return number of entries invalidated
     */
    public int invalidateIf(java.util.function.Predicate<String> predicate) {
        ensureNotClosed();
        int removed = 0;
        
        Iterator<String> iterator = cache.keySet().iterator();
        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                iterator.remove();
                evictionCount.increment();
                removed++;
            }
        }
        
        return removed;
    }
    
    /**
     * Refreshes a cache entry asynchronously.
     * 
     * @param key the key to refresh
     * @param loader the loader function
     * @return CompletableFuture with the refreshed value
     */
    public CompletableFuture<List<GroceryProduct>> refresh(
            String key, 
            Function<String, List<GroceryProduct>> loader) {
        
        return CompletableFuture.supplyAsync(() -> {
            invalidate(key);
            return get(key, loader);
        }, loadingExecutor);
    }
    
    /**
     * Warms up the cache with predefined keys.
     * 
     * @param keys keys to warm up
     * @param loader the loader function
     * @return CompletableFuture that completes when warm-up is done
     */
    public CompletableFuture<Void> warmUp(
            Collection<String> keys,
            Function<String, List<GroceryProduct>> loader) {
        
        List<CompletableFuture<Void>> futures = keys.stream()
            .map(key -> CompletableFuture.runAsync(
                () -> get(key, loader), 
                loadingExecutor
            ))
            .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    /**
     * Gets the current cache size.
     * 
     * @return number of cached entries
     */
    public int size() {
        return cache.size();
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
     * Checks if a key is cached.
     * 
     * @param key the key to check
     * @return true if the key is cached and not expired
     */
    public boolean containsKey(String key) {
        CacheEntry entry = cache.get(key);
        return entry != null && !entry.isExpired(ttl);
    }
    
    /**
     * Gets cache statistics.
     * 
     * @return current cache statistics
     */
    public CacheStats getStats() {
        long hits = hitCount.sum();
        long misses = missCount.sum();
        long total = hits + misses;
        double hitRate = total > 0 ? (double) hits / total : 0.0;
        double avgLoadTime = loadCount.sum() > 0 
            ? (double) totalLoadTime.sum() / loadCount.sum() 
            : 0.0;
        
        return new CacheStats(
            hits,
            misses,
            evictionCount.sum(),
            loadCount.sum(),
            hitRate,
            avgLoadTime,
            cache.size(),
            total
        );
    }
    
    /**
     * Resets cache statistics.
     */
    public void resetStats() {
        hitCount.reset();
        missCount.reset();
        evictionCount.reset();
        loadCount.reset();
        totalLoadTime.reset();
        LOGGER.info("Cache statistics reset");
    }
    
    private void ensureNotClosed() {
        if (closed) {
            throw new IllegalStateException("Cache has been closed");
        }
    }
    
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            
            // Log final statistics
            LOGGER.info("Closing cache. Final stats: " + getStats().getSummary());
            
            // Clear cache
            cache.clear();
            
            // Shutdown executors
            loadingExecutor.shutdown();
            maintenanceExecutor.shutdown();
            
            try {
                if (!loadingExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    loadingExecutor.shutdownNow();
                }
                if (!maintenanceExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    maintenanceExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                loadingExecutor.shutdownNow();
                maintenanceExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}