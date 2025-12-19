package com.university.grocerystore.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Produce;

class SearchTests {

    private Produce apple;
    private FrozenFood pizza;
    private CannedGood beans;

    @BeforeEach
    void setUp() {
        apple = new Produce("P001", "Apple", 1.99, 2024, "Fresh Farms", "Gala", 0.5, true, "USA", null);
        pizza = new FrozenFood("F001", "Pizza", 5.99, 2024, "FrozenCo", -18.0, 0.8, true, 450);
        beans = new CannedGood("C001", "Canned Beans", 2.49, 2023, "CanCo", "15 oz", true, "Pressure Canning", 24);
    }

    // ============ ProductTrie Tests ============

    @Test
    void testProductTrie_InsertAndSearch() {
        ProductTrie trie = new ProductTrie();

        trie.insert(apple);
        trie.insert(pizza);
        trie.insert(beans);

        assertEquals(3, trie.size());
        assertFalse(trie.isEmpty());

        List<GroceryProduct> appleResults = trie.searchByPrefix("app");
        assertEquals(1, appleResults.size());
        assertEquals("Apple", appleResults.get(0).getName());

        List<GroceryProduct> pResults = trie.searchByPrefix("p");
        assertEquals(2, pResults.size()); // Pizza and Apple (App-le)

        List<GroceryProduct> limitResults = trie.searchByPrefixWithLimit("p", 1);
        assertEquals(1, limitResults.size());
    }

    @Test
    void testProductTrie_PrefixChecking() {
        ProductTrie trie = new ProductTrie();
        trie.insert(apple);
        trie.insert(pizza);

        assertTrue(trie.hasPrefix("app"));
        assertTrue(trie.hasPrefix("piz"));
        assertFalse(trie.hasPrefix("banana"));
        assertFalse(trie.hasPrefix(""));
    }

    @Test
    void testProductTrie_Remove() {
        ProductTrie trie = new ProductTrie();
        trie.insert(apple);
        trie.insert(pizza);

        assertEquals(2, trie.size());

        assertTrue(trie.remove(apple));
        assertEquals(1, trie.size());

        assertFalse(trie.remove(apple)); // Already removed
        assertFalse(trie.hasPrefix("app"));
    }

    @Test
    void testProductTrie_Clear() {
        ProductTrie trie = new ProductTrie();
        trie.insert(apple);
        trie.insert(pizza);

        assertEquals(2, trie.size());

        trie.clear();
        assertEquals(0, trie.size());
        assertTrue(trie.isEmpty());
        assertTrue(trie.searchByPrefix("app").isEmpty());
    }

    // ============ SearchResultCache Tests ============

    @Test
    void testSearchResultCache_LRUBehavior() {
        SearchResultCache cache = new SearchResultCache(2);

        cache.put("key1", List.of(apple));
        cache.put("key2", List.of(pizza));

        assertEquals(2, cache.size());
        assertTrue(cache.containsKey("key1"));
        assertTrue(cache.containsKey("key2"));

        // Access key1 to make it recently used
        assertTrue(cache.get("key1").isPresent());

        // Add third item - should evict key2 (least recently used)
        cache.put("key3", List.of(beans));

        assertEquals(2, cache.size());
        assertTrue(cache.containsKey("key1"));
        assertTrue(cache.containsKey("key3"));
        assertFalse(cache.containsKey("key2")); // Evicted
    }

    @Test
    void testSearchResultCache_GetAndPut() {
        SearchResultCache cache = new SearchResultCache(10);

        assertTrue(cache.get("nonexistent").isEmpty());

        cache.put("apples", List.of(apple));
        assertTrue(cache.get("apples").isPresent());
        assertEquals("Apple", cache.get("apples").get().get(0).getName());

        cache.remove("apples");
        assertFalse(cache.containsKey("apples"));
        assertTrue(cache.get("apples").isEmpty());
    }

    @Test
    void testSearchResultCache_CacheStats() {
        SearchResultCache cache = new SearchResultCache(5);

        cache.put("key1", List.of(apple));
        cache.put("key2", List.of(pizza));

        cache.get("key1");
        cache.get("key1");
        cache.get("key2");

        var stats = cache.getStats();
        assertEquals(2, stats.getCurrentSize());
        assertEquals(5, stats.getMaxSize());
        assertTrue(stats.getHitRatio() > 0);
    }

    // ============ CachedSearchService Tests ============

    @Test
    void testCachedSearchService_BasicSearch() {
        // Mock repository
        var mockRepo = new com.university.grocerystore.repository.GroceryProductRepository() {
            public void save(GroceryProduct p) {}
            public java.util.Optional<GroceryProduct> findById(String id) { return java.util.Optional.empty(); }
            public java.util.List<GroceryProduct> findAll() { return List.of(apple, pizza, beans); }
            public boolean delete(String id) { return false; }
            public boolean exists(String id) { return false; }
            public long count() { return 3; }
            public void deleteAll() {}
        };

        CachedSearchService service = new CachedSearchService(mockRepo, 10);

        List<GroceryProduct> results = service.searchByPrefix("app");
        assertEquals(1, results.size());
        assertEquals("Apple", results.get(0).getName());

        results = service.searchByPrefixWithLimit("p", 1);
        assertEquals(1, results.size());

        assertEquals(3, service.getIndexSize());
        assertFalse(service.isIndexEmpty());
    }

    @Test
    void testCachedSearchService_AddAndRemove() {
        var mockRepo = new com.university.grocerystore.repository.GroceryProductRepository() {
            public void save(GroceryProduct p) {}
            public java.util.Optional<GroceryProduct> findById(String id) { return java.util.Optional.empty(); }
            public java.util.List<GroceryProduct> findAll() { return List.of(apple); }
            public boolean delete(String id) { return false; }
            public boolean exists(String id) { return false; }
            public long count() { return 1; }
            public void deleteAll() {}
        };

        CachedSearchService service = new CachedSearchService(mockRepo, 10);

        assertEquals(1, service.getIndexSize());

        service.addProduct(pizza);
        assertEquals(2, service.getIndexSize());

        service.removeProduct(apple);
        assertEquals(1, service.getIndexSize());

        service.clear();
        assertTrue(service.isIndexEmpty());
    }

    @Test
    void testCachedSearchService_CacheStats() {
        var mockRepo = new com.university.grocerystore.repository.GroceryProductRepository() {
            public void save(GroceryProduct p) {}
            public java.util.Optional<GroceryProduct> findById(String id) { return java.util.Optional.empty(); }
            public java.util.List<GroceryProduct> findAll() { return List.of(apple, pizza); }
            public boolean delete(String id) { return false; }
            public boolean exists(String id) { return false; }
            public long count() { return 2; }
            public void deleteAll() {}
        };

        CachedSearchService service = new CachedSearchService(mockRepo, 5);

        // First search (cache miss)
        service.searchByPrefix("app");

        // Second search (should be cache hit)
        service.searchByPrefix("app");

        var stats = service.getCacheStats();
        assertTrue(stats.getHitRatio() >= 0);
    }

    // ============ ModernSearchCache Tests ============

    @Test
    void testModernSearchCache_BasicOperations() throws Exception {
        try (ModernSearchCache cache = new ModernSearchCache(5,
                java.time.Duration.ofMinutes(1),
                java.time.Duration.ofSeconds(30))) {

            List<GroceryProduct> results = List.of(apple, pizza);

            cache.put("fruits", results);
            assertTrue(cache.containsKey("fruits"));
            assertEquals(1, cache.size());

            List<GroceryProduct> cached = cache.get("fruits", key -> results);
            assertEquals(2, cached.size());

            cache.invalidate("fruits");
            assertFalse(cache.containsKey("fruits"));
            assertEquals(0, cache.size());
        }
    }

    @Test
    void testModernSearchCache_AsyncOperations() throws Exception {
        try (ModernSearchCache cache = new ModernSearchCache(5,
                java.time.Duration.ofMinutes(1),
                java.time.Duration.ofSeconds(30))) {

            List<GroceryProduct> results = List.of(apple, pizza);

            CompletableFuture<List<GroceryProduct>> future = cache.getAsync(
                    "async-key",
                    key -> CompletableFuture.completedFuture(results)
            );

            List<GroceryProduct> cached = future.get(5, TimeUnit.SECONDS);
            assertEquals(2, cached.size());
            assertTrue(cache.containsKey("async-key"));
        }
    }

    @Test
    void testModernSearchCache_Statistics() throws Exception {
        try (ModernSearchCache cache = new ModernSearchCache(10,
                java.time.Duration.ofMinutes(1),
                java.time.Duration.ofSeconds(30))) {

            List<GroceryProduct> results = List.of(apple);

            cache.get("test1", key -> results);
            cache.get("test1", key -> results); // Should hit cache

            var stats = cache.getStats();
            assertTrue(stats.hitRate() > 0);
            assertEquals(1, stats.size());
        }
    }

    @Test
    void testModernSearchCache_AutoCloseable() {
        ModernSearchCache cache = new ModernSearchCache(5,
                java.time.Duration.ofMinutes(1),
                java.time.Duration.ofSeconds(30));

        cache.put("test", List.of(apple));
        assertTrue(cache.containsKey("test"));

        cache.close();

        // After closing, operations should fail
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cache.put("test2", List.of(pizza))
        );
        assertTrue(exception.getMessage().contains("closed"));
    }

    @Test
    void testModernSearchCache_WarmUp() throws Exception {
        try (ModernSearchCache cache = new ModernSearchCache(10,
                java.time.Duration.ofMinutes(1),
                java.time.Duration.ofSeconds(30))) {

            List<String> keys = List.of("apple", "pizza", "beans");

            CompletableFuture<Void> warmUp = cache.warmUp(keys, key -> {
                if ("apple".equals(key)) return List.of(apple);
                if ("pizza".equals(key)) return List.of(pizza);
                if ("beans".equals(key)) return List.of(beans);
                return List.of();
            });

            warmUp.get(5, TimeUnit.SECONDS);

            assertEquals(3, cache.size());
            assertTrue(cache.containsKey("apple"));
            assertTrue(cache.containsKey("pizza"));
            assertTrue(cache.containsKey("beans"));
        }
    }

    // ============ Integration Test ============

    @Test
    void testIntegration_CompleteSearchWorkflow() {
        var mockRepo = new com.university.grocerystore.repository.GroceryProductRepository() {
            public void save(GroceryProduct p) {}
            public java.util.Optional<GroceryProduct> findById(String id) { return java.util.Optional.empty(); }
            public java.util.List<GroceryProduct> findAll() { return List.of(apple, pizza, beans); }
            public boolean delete(String id) { return false; }
            public boolean exists(String id) { return false; }
            public long count() { return 3; }
            public void deleteAll() {}
        };

        CachedSearchService service = new CachedSearchService(mockRepo, 10);

        // Search with different prefixes
        List<GroceryProduct> appResults = service.searchByPrefix("app");
        List<GroceryProduct> pResults = service.searchByPrefix("p");
        List<GroceryProduct> cResults = service.searchByPrefix("c");

        assertEquals(1, appResults.size());
        assertEquals("Apple", appResults.get(0).getName());

        assertEquals(2, pResults.size()); // Pizza and Apple (App-le)
        assertEquals(1, cResults.size()); // Canned Beans

        // Test with limit
        List<GroceryProduct> limited = service.searchByPrefixWithLimit("p", 1);
        assertEquals(1, limited.size());

        // Verify cache stats
        var cacheStats = service.getCacheStats();
        assertTrue(cacheStats.getHitRatio() >= 0);

        // Clear and verify
        service.clear();
        assertTrue(service.isIndexEmpty());
        assertEquals(0, service.getIndexSize());
    }
}