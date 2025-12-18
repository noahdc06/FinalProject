package com.university.grocerystore.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryItem;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;
import com.university.grocerystore.model.Produce;

class ImplTests {

    private Produce apple;
    private FrozenFood pizza;
    private CannedGood beans;
    private GroceryItem simpleItem;

    @BeforeEach
    void setUp() {
        apple = new Produce("P001", "Apple", 1.99, 2024, "Fresh Farms", "Gala", 0.5, true, "USA", null);
        pizza = new FrozenFood("F001", "Pizza", 5.99, 2024, "FrozenCo", -18.0, 0.8, true, 450);
        beans = new CannedGood("C001", "Canned Beans", 2.49, 2023, "CanCo", "15 oz", true, "Pressure Canning", 24);
        simpleItem = new GroceryItem("S001", "Chips", "SnackCo", 1.99, 2024, 2025);
    }

    // ============ GroceryStoreArrayList Tests ============

    @Test
    void testGroceryStoreArrayList_AddAndRetrieve() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();

        assertTrue(store.add(simpleItem));
        assertEquals(1, store.size());

        GroceryItem found = store.findBySku("S001");
        assertEquals("Chips", found.getName());
        assertEquals(1.99, found.getPrice(), 0.001);
    }

    @Test
    void testGroceryStoreArrayList_DuplicateSku() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();

        assertTrue(store.add(simpleItem));
        assertFalse(store.add(simpleItem)); // Duplicate should fail

        assertEquals(1, store.size());
    }

    @Test
    void testGroceryStoreArrayList_SearchAndFilter() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();
        store.add(simpleItem);
        store.add(new GroceryItem("S002", "Cookies", "CookieCo", 2.99, 2024, 2025));

        List<GroceryItem> results = store.findByName("chip");
        assertEquals(1, results.size());
        assertEquals("Chips", results.get(0).getName());

        results = store.findByPriceRange(1.50, 3.00);
        assertEquals(2, results.size());
    }

    @Test
    void testGroceryStoreArrayList_Statistics() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();
        store.add(simpleItem);
        store.add(new GroceryItem("S002", "Cookies", "CookieCo", 2.99, 2024, 2025));

        assertEquals(2, store.size());
        assertEquals(4.98, store.inventoryValue(), 0.01);
        assertEquals("Cookies", store.getMostExpensive().getName());
    }

    // ============ GroceryStoreImpl Tests ============

    @Test
    void testGroceryStoreImpl_AddAndRemoveProducts() {
        GroceryStoreImpl store = new GroceryStoreImpl();

        assertTrue(store.addProduct(apple));
        assertTrue(store.addProduct(pizza));
        assertEquals(2, store.size());

        Optional<GroceryProduct> removed = store.removeProduct("P001");
        assertTrue(removed.isPresent());
        assertEquals("Apple", removed.get().getName());
        assertEquals(1, store.size());
    }

    @Test
    void testGroceryStoreImpl_SearchOperations() {
        GroceryStoreImpl store = new GroceryStoreImpl();
        store.addProduct(apple);
        store.addProduct(pizza);
        store.addProduct(beans);

        List<GroceryProduct> byName = store.searchByName("piz");
        assertEquals(1, byName.size());
        assertEquals("Pizza", byName.get(0).getName());

        List<GroceryProduct> byBrand = store.searchByBrand("Fresh");
        assertEquals(1, byBrand.size());
        assertEquals("Apple", byBrand.get(0).getName());

        List<GroceryProduct> byPrice = store.getProductsByPriceRange(2.00, 6.00);
        assertEquals(2, byPrice.size()); // Pizza and Beans
    }

    @Test
    void testGroceryStoreImpl_PolymorphicHandling() {
        GroceryStoreImpl store = new GroceryStoreImpl();
        store.addProduct(apple); // Produce
        store.addProduct(pizza); // FrozenFood
        store.addProduct(beans); // CannedGood

        List<Perishable> perishables = store.getPerishableProducts();
        assertEquals(2, perishables.size()); // Apple and Pizza

        List<GroceryProduct> sorted = store.getAllProductsSorted();
        assertEquals("Apple", sorted.get(0).getName()); // Sorted by name
        assertEquals("Canned Beans", sorted.get(1).getName());
        assertEquals("Pizza", sorted.get(2).getName());
    }

    @Test
    void testGroceryStoreImpl_InventoryStatistics() {
        GroceryStoreImpl store = new GroceryStoreImpl();
        store.addProduct(apple);
        store.addProduct(pizza);
        store.addProduct(beans);

        GroceryStore.InventoryStats stats = store.getInventoryStats();
        assertEquals(3, stats.totalProducts());
        assertEquals(3, stats.uniqueProductTypes());
        assertEquals(2, stats.perishableCount());
        assertEquals(1, stats.nonPerishableCount());

        assertEquals(10.47, store.getTotalInventoryValue(), 0.01);
        assertTrue(store.getTotalDiscountedValue() <= store.getTotalInventoryValue());
    }

    @Test
    void testGroceryStoreImpl_AdvancedFiltering() {
        GroceryStoreImpl store = new GroceryStoreImpl();
        store.addProduct(apple);
        store.addProduct(pizza);
        store.addProduct(beans);

        List<GroceryProduct> recent = store.findRecentProducts(2);
        assertTrue(recent.size() >= 2); // Products from last 2 years

        List<GroceryProduct> byBrands = store.findByBrands("Fresh Farms", "FrozenCo");
        assertEquals(2, byBrands.size());

        List<GroceryProduct> predicateResults = store.findWithPredicate(
                p -> p.getPrice() < 3.00
        );
        assertEquals(2, predicateResults.size()); // Apple and Beans
    }

    // ============ ConcurrentGroceryStore Tests ============

    @Test
    void testConcurrentGroceryStore_ThreadSafeOperations() {
        ConcurrentGroceryStore store = new ConcurrentGroceryStore();

        assertTrue(store.addProduct(apple));
        assertTrue(store.addProduct(pizza));
        assertEquals(2, store.size());

        Optional<GroceryProduct> found = store.findById("F001");
        assertTrue(found.isPresent());
        assertEquals("Pizza", found.get().getName());

        store.clearInventory();
        assertEquals(0, store.size());
        assertTrue(store.isEmpty());
    }

    @Test
    void testConcurrentGroceryStore_ConcurrentSearch() {
        ConcurrentGroceryStore store = new ConcurrentGroceryStore();
        store.addProduct(apple);
        store.addProduct(pizza);
        store.addProduct(beans);

        List<GroceryProduct> all = store.getAllProducts();
        assertEquals(3, all.size());

        List<GroceryProduct> sorted = store.getAllProductsSorted();
        assertEquals("Apple", sorted.get(0).getName());

        double totalValue = store.getTotalInventoryValue();
        assertEquals(10.47, totalValue, 0.01);
    }

    @Test
    void testConcurrentGroceryStore_GroupByType() {
        ConcurrentGroceryStore store = new ConcurrentGroceryStore();
        store.addProduct(apple);
        store.addProduct(pizza);
        store.addProduct(beans);

        var grouped = store.groupByType();
        assertTrue(grouped.containsKey(GroceryProduct.ProductType.PRODUCE));
        assertTrue(grouped.containsKey(GroceryProduct.ProductType.FROZEN_FOOD));
        assertTrue(grouped.containsKey(GroceryProduct.ProductType.CANNED_GOOD));
    }

    // ============ ModernConcurrentGroceryStore Tests ============

    @Test
    void testModernConcurrentGroceryStore_BasicOperations() {
        try (ModernConcurrentGroceryStore store = new ModernConcurrentGroceryStore()) {
            assertTrue(store.addProduct(apple));
            assertTrue(store.addProduct(pizza));
            assertEquals(2, store.size());

            Optional<GroceryProduct> found = store.findById("P001");
            assertTrue(found.isPresent());
            assertEquals("Apple", found.get().getName());
        }
    }

    @Test
    void testModernConcurrentGroceryStore_AsyncOperations() throws Exception {
        try (ModernConcurrentGroceryStore store = new ModernConcurrentGroceryStore()) {
            store.addProduct(apple);
            store.addProduct(pizza);

            // Test async search
            CompletableFuture<List<GroceryProduct>> asyncSearch = store.searchByNameAsync("piz");
            List<GroceryProduct> results = asyncSearch.get(5, TimeUnit.SECONDS);
            assertEquals(1, results.size());
            assertEquals("Pizza", results.get(0).getName());

            // Test async stats
            CompletableFuture<GroceryStore.InventoryStats> asyncStats = store.getInventoryStatsAsync();
            GroceryStore.InventoryStats stats = asyncStats.get(5, TimeUnit.SECONDS);
            assertEquals(2, stats.totalProducts());
        }
    }

    @Test
    void testModernConcurrentGroceryStore_ParallelSearch() throws Exception {
        try (ModernConcurrentGroceryStore store = new ModernConcurrentGroceryStore()) {
            store.addProduct(apple);
            store.addProduct(pizza);
            store.addProduct(beans);

            CompletableFuture<List<GroceryProduct>> parallelResults =
                    store.parallelSearchAsync("Apple", "FrozenCo", GroceryProduct.ProductType.CANNED_GOOD);

            List<GroceryProduct> results = parallelResults.get(5, TimeUnit.SECONDS);
            assertEquals(3, results.size()); // All three products match different criteria
        }
    }

    @Test
    void testModernConcurrentGroceryStore_OptimisticReads() {
        try (ModernConcurrentGroceryStore store = new ModernConcurrentGroceryStore()) {
            // Add many products
            for (int i = 0; i < 100; i++) {
                store.addProduct(new Produce(
                        "P" + i, "Product" + i,
                        1.0 + i, 2024, "Brand", "Type",
                        1.0, false, "Origin", null
                ));
            }

            // getAllProducts uses optimistic reads
            List<GroceryProduct> allProducts = store.getAllProducts();
            assertEquals(100, allProducts.size());

            // size() also uses optimistic reads
            assertEquals(100, store.size());
        }
    }

    @Test
    void testModernConcurrentGroceryStore_CloseResourceManagement() {
        ModernConcurrentGroceryStore store = new ModernConcurrentGroceryStore();
        store.addProduct(apple);
        store.addProduct(pizza);

        assertEquals(2, store.size());

        store.close();

        // Store should be closed
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> store.size()
        );
        assertTrue(exception.getMessage().contains("closed"));
    }

    @Test
    void testModernConcurrentGroceryStore_BatchOperations() throws Exception {
        try (ModernConcurrentGroceryStore store = new ModernConcurrentGroceryStore()) {
            List<GroceryProduct> products = Arrays.asList(apple, pizza, beans);

            CompletableFuture<com.university.grocerystore.impl.ModernConcurrentGroceryStore> batchFuture =
                    CompletableFuture.supplyAsync(() -> {
                        products.forEach(store::addProduct);
                        return store;
                    });

            batchFuture.get(5, TimeUnit.SECONDS);
            assertEquals(3, store.size());

            // Test async batch retrieval
            List<String> ids = Arrays.asList("P001", "F001", "C001");
            CompletableFuture<java.util.Map<String, GroceryProduct>> batchFind =
                    store.findByIdsAsync(ids);

            java.util.Map<String, GroceryProduct> results = batchFind.get(5, TimeUnit.SECONDS);
            assertEquals(3, results.size());
            assertEquals("Apple", results.get("P001").getName());
        }
    }

    // ============ Cross-Implementation Tests ============

    @Test
    void testAllImplementations_ConsistentBehavior() {
        // Test that all implementations handle basic operations consistently
        GroceryStoreArrayList arrayListStore = new GroceryStoreArrayList();
        GroceryStoreImpl implStore = new GroceryStoreImpl();
        ConcurrentGroceryStore concurrentStore = new ConcurrentGroceryStore();

        // All should support adding
        assertTrue(arrayListStore.add(simpleItem));
        assertTrue(implStore.addProduct(apple));
        assertTrue(concurrentStore.addProduct(pizza));

        // All should support retrieval
        assertNotNull(arrayListStore.findBySku("S001"));
        assertTrue(implStore.findById("P001").isPresent());
        assertTrue(concurrentStore.findById("F001").isPresent());

        // All should report size
        assertEquals(1, arrayListStore.size());
        assertEquals(1, implStore.size());
        assertEquals(1, concurrentStore.size());
    }

    @Test
    void testImplementationProgression_FeaturesComparison() {
        // Demonstrate feature progression across implementations

        // 1. Basic ArrayList implementation
        GroceryStoreArrayList basic = new GroceryStoreArrayList();
        basic.add(simpleItem);
        assertTrue(basic.getAllItems().contains(simpleItem));

        // 2. Full polymorphic implementation
        GroceryStoreImpl polymorphic = new GroceryStoreImpl();
        polymorphic.addProduct(apple);
        polymorphic.addProduct(pizza);
        assertEquals(2, polymorphic.getPerishableProducts().size());

        // 3. Thread-safe implementation
        ConcurrentGroceryStore concurrent = new ConcurrentGroceryStore();
        concurrent.addProduct(apple);
        concurrent.addProduct(pizza);
        concurrent.addProduct(beans);
        assertEquals(3, concurrent.getAllProducts().size());

        // 4. Modern concurrent implementation
        try (ModernConcurrentGroceryStore modern = new ModernConcurrentGroceryStore()) {
            modern.addProduct(apple);
            modern.addProduct(pizza);
            assertFalse(modern.isEmpty());

            // Can perform async operations
            CompletableFuture<Double> asyncValue = modern.getTotalInventoryValueAsync();
            assertNotNull(asyncValue);
        }
    }
}