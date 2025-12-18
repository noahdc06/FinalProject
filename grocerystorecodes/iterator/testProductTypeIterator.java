package com.university.grocerystorecodes.iterator;

import com.university.grocerystorecodes.model.*;
import com.university.grocerystorecodes.impl.GroceryStoreArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class IteratorPatternTests {
    
    @Test
    void testProductTypeIterator() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();
        store.addMaterial(new Produce("P001", "Apple", 1.99, 2024));
        store.addMaterial(new CannedGood("C001", "Soup", 2.49, 2024, "Can", 400, "2025-12-31"));
        store.addMaterial(new Produce("P002", "Banana", 0.99, 2024));
        
        ProductTypeIterator iterator = new ProductTypeIterator(store, "Produce");
        List<GroceryProduct> produceItems = new ArrayList<>();
        
        while (iterator.hasNext()) {
            produceItems.add(iterator.next());
        }
        
        assertEquals(2, produceItems.size());
        assertTrue(produceItems.stream().allMatch(p -> p instanceof Produce));
    }
    
    @Test
    void testPriceRangeIterator() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();
        store.addMaterial(new Produce("P001", "Apple", 1.99, 2024));
        store.addMaterial(new CannedGood("C001", "Soup", 2.49, 2024, "Can", 400, "2025-12-31"));
        store.addMaterial(new Produce("P002", "Banana", 0.99, 2024));
        store.addMaterial(new FrozenFood("F001", "Pizza", 5.99, 2024, -18.0, "2024-06-30", "Box"));
        
        PriceRangeIterator iterator = new PriceRangeIterator(store, 1.0, 3.0);
        List<GroceryProduct> affordableItems = new ArrayList<>();
        
        while (iterator.hasNext()) {
            affordableItems.add(iterator.next());
        }
        
        assertEquals(2, affordableItems.size()); // Apple and Soup
        assertTrue(affordableItems.stream().allMatch(p -> p.getPrice() >= 1.0 && p.getPrice() <= 3.0));
    }
    
    @Test
    void testPriceSortedIterator() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();
        store.addMaterial(new Produce("P001", "Apple", 1.99, 2024));
        store.addMaterial(new CannedGood("C001", "Soup", 2.49, 2024, "Can", 400, "2025-12-31"));
        store.addMaterial(new Produce("P002", "Banana", 0.99, 2024));
        
        PriceSortedIterator iterator = new PriceSortedIterator(store);
        List<Double> prices = new ArrayList<>();
        
        while (iterator.hasNext()) {
            prices.add(iterator.next().getPrice());
        }
        
        // Should be sorted ascending
        assertEquals(0.99, prices.get(0), 0.01);
        assertEquals(1.99, prices.get(1), 0.01);
        assertEquals(2.49, prices.get(2), 0.01);
    }
    
    @Test
    void testProductIteratorFactory() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();
        store.addMaterial(new Produce("P001", "Apple", 1.99, 2024));
        store.addMaterial(new CannedGood("C001", "Soup", 2.49, 2024, "Can", 400, "2025-12-31"));
        
        ProductIteratorFactory factory = new ProductIteratorFactory();
        
        ProductIterator typeIterator = factory.createTypeIterator(store, "Produce");
        assertNotNull(typeIterator);
        assertTrue(typeIterator.hasNext());
        
        ProductIterator priceIterator = factory.createPriceRangeIterator(store, 1.0, 3.0);
        assertNotNull(priceIterator);
        
        ProductIterator sortedIterator = factory.createPriceSortedIterator(store);
        assertNotNull(sortedIterator);
    }
    
    @Test
    void testEmptyIterator() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();
        ProductTypeIterator iterator = new ProductTypeIterator(store, "Produce");
        
        assertFalse(iterator.hasNext());
        assertThrows(java.util.NoSuchElementException.class, iterator::next);
    }
    
    @Test
    void testIteratorRemoveNotSupported() {
        GroceryStoreArrayList store = new GroceryStoreArrayList();
        store.addMaterial(new Produce("P001", "Apple", 1.99, 2024));
        ProductTypeIterator iterator = new ProductTypeIterator(store, "Produce");
        
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}
