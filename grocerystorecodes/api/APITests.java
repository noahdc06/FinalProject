package com.university.grocerystore.api;

import org.junit.jupiter.api.Test;
import com.university.grocerystore.model.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class APITests {
    
    @Test
    void testGroceryStoreAPI_AddAndRemove() {
        GroceryStoreAPI store = new GroceryStoreImpl();
        GroceryItem item = new GroceryItem("SKU001", "Milk", "DairyCo", 2.99, 2024, 2025);
        
        assertTrue(store.add(item));
        assertEquals(1, store.size());
        
        assertTrue(store.removeBySku("SKU001"));
        assertEquals(0, store.size());
    }
    
    @Test
    void testGroceryStoreAPI_FindBySku() {
        GroceryStoreAPI store = new GroceryStoreImpl();
        GroceryItem item = new GroceryItem("SKU001", "Milk", "DairyCo", 2.99, 2024, 2025);
        store.add(item);
        
        GroceryItem found = store.findBySku("SKU001");
        assertNotNull(found);
        assertEquals("Milk", found.getName());
        
        assertNull(store.findBySku("INVALID"));
    }
    
    @Test
    void testGroceryStoreAPI_FindByName() {
        GroceryStoreAPI store = new GroceryStoreImpl();
        store.add(new GroceryItem("SKU001", "Whole Milk", "DairyCo", 3.99, 2024, 2025));
        store.add(new GroceryItem("SKU002", "Chocolate Milk", "DairyCo", 4.99, 2024, 2025));
        
        List<GroceryItem> results = store.findByName("Milk");
        assertEquals(2, results.size());
    }
    
    @Test
    void testGroceryStoreAPI_PriceRange() {
        GroceryStoreAPI store = new GroceryStoreImpl();
        store.add(new GroceryItem("SKU001", "Cheap", "Brand", 1.99, 2024, 2025));
        store.add(new GroceryItem("SKU002", "Expensive", "Brand", 9.99, 2024, 2025));
        
        List<GroceryItem> cheap = store.findByPriceRange(0.0, 5.0);
        assertEquals(1, cheap.size());
        assertEquals("Cheap", cheap.get(0).getName());
    }
    
    @Test
    void testGroceryStoreAPI_InventoryValue() {
        GroceryStoreAPI store = new GroceryStoreImpl();
        store.add(new GroceryItem("SKU001", "Item1", "Brand", 2.00, 2024, 2025));
        store.add(new GroceryItem("SKU002", "Item2", "Brand", 3.00, 2024, 2025));
        
        assertEquals(5.00, store.inventoryValue(), 0.001);
    }
    
    @Test
    void testGroceryStore_AddAndFindProduct() {
        GroceryStore store = new GroceryStoreArrayList();
        GroceryProduct product = new CannedGood("P001", "Soup", "Campbell", 2.99, 2024, 12, "SOUP");
        
        assertTrue(store.addProduct(product));
        Optional<GroceryProduct> found = store.findById("P001");
        
        assertTrue(found.isPresent());
        assertEquals("Soup", found.get().getName());
    }
    
    @Test
    void testGroceryStore_RemoveProduct() {
        GroceryStore store = new GroceryStoreArrayList();
        GroceryProduct product = new CannedGood("P001", "Soup", "Campbell", 2.99, 2024, 12, "SOUP");
        store.addProduct(product);
        
        Optional<GroceryProduct> removed = store.removeProduct("P001");
        assertTrue(removed.isPresent());
        assertEquals("Soup", removed.get().getName());
        assertTrue(store.findById("P001").isEmpty());
    }
    
    @Test
    void testGroceryStore_SearchByName() {
        GroceryStore store = new GroceryStoreArrayList();
        store.addProduct(new CannedGood("P001", "Tomato Soup", "Campbell", 2.99, 2024, 12, "SOUP"));
        store.addProduct(new CannedGood("P002", "Chicken Soup", "Campbell", 3.99, 2024, 12, "SOUP"));
        
        List<GroceryProduct> results = store.searchByName("Soup");
        assertEquals(2, results.size());
    }
    
    @Test
    void testGroceryStore_GetProductsByType() {
        GroceryStore store = new GroceryStoreArrayList();
        store.addProduct(new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG"));
        store.addProduct(new Snack("S001", "Chips", "Lays", 1.49, 2024, "CHIPS", 200.0, 150, "BBQ", "None"));
        
        List<GroceryProduct> snacks = store.getProductsByType(GroceryProduct.ProductType.SNACK);
        assertEquals(1, snacks.size());
        assertEquals("Chips", snacks.get(0).getName());
    }
    
    @Test
    void testGroceryStore_FilterProducts() {
        GroceryStore store = new GroceryStoreArrayList();
        store.addProduct(new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG"));
        store.addProduct(new CannedGood("C002", "Soup", "Campbell", 2.99, 2024, 12, "SOUP"));
        
        List<GroceryProduct> cheap = store.filterProducts(p -> p.getPrice() < 2.0);
        assertEquals(1, cheap.size());
        assertEquals("Beans", cheap.get(0).getName());
    }
    
    @Test
    void testGroceryStore_GetProductsByPriceRange() {
        GroceryStore store = new GroceryStoreArrayList();
        store.addProduct(new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG"));
        store.addProduct(new CannedGood("C002", "Soup", "Campbell", 2.99, 2024, 12, "SOUP"));
        
        List<GroceryProduct> range = store.getProductsByPriceRange(1.0, 2.0);
        assertEquals(1, range.size());
        assertEquals("Beans", range.get(0).getName());
    }
    
    @Test
    void testGroceryStore_GetTotalInventoryValue() {
        GroceryStore store = new GroceryStoreArrayList();
        store.addProduct(new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG"));
        store.addProduct(new CannedGood("C002", "Soup", "Campbell", 2.99, 2024, 12, "SOUP"));
        
        assertEquals(4.48, store.getTotalInventoryValue(), 0.01);
    }
    
    @Test
    void testGroceryStore_SizeAndIsEmpty() {
        GroceryStore store = new GroceryStoreArrayList();
        assertTrue(store.isEmpty());
        assertEquals(0, store.size());
        
        store.addProduct(new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG"));
        assertFalse(store.isEmpty());
        assertEquals(1, store.size());
    }
    
    @Test
    void testModernGroceryStore_SearchCriteria() {
        ModernGroceryStore.SearchCriteria criteria = ModernGroceryStore.SearchCriteria.builder()
            .withName("Soup")
            .withPriceRange(1.0, 5.0)
            .build();
        
        assertTrue(criteria.hasAnyCriteria());
        assertEquals("Soup", criteria.name().orElse(""));
    }
    
    @Test
    void testModernGroceryStore_InventoryStats() {
        ModernGroceryStore.ModernInventoryStats stats = new ModernGroceryStore.ModernInventoryStats(
            10, 5.50, 5.00, 3, 4, 6
        );
        
        assertEquals(10, stats.totalCount());
        assertEquals(5.50, stats.averagePrice(), 0.001);
        assertNotNull(stats.getSummary());
    }
}
