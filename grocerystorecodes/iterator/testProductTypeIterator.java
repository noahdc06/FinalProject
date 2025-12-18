package com.university.grocerystore.iterator;

import org.junit.jupiter.api.Test;
import com.university.grocerystore.model.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class IteratorPatternTests {
    
    private List<GroceryProduct> createTestProducts() {
        List<GroceryProduct> products = new ArrayList<>();
        products.add(new CannedGood("C001", "Beans", "Heinz", 0.99, 2024, 12, "VEG"));
        products.add(new CannedGood("C002", "Soup", "Campbell", 2.99, 2024, 12, "SOUP"));
        products.add(new FrozenFood("F001", "Pizza", "DiGiorno", 8.99, 2024, 
            "FREEZER", 500.0, false, 300, Perishable.ShelfLifeQuality.HIGH));
        products.add(new Snack("S001", "Chips", "Lays", 1.49, 2024, 
            "CHIPS", 200.0, 150, "BBQ", "None"));
        return products;
    }
    
    @Test
    void testPriceRangeIterator() {
        List<GroceryProduct> products = createTestProducts();
        ProductIterator iterator = new PriceRangeIterator(products, 1.0, 5.0);
        
        int count = 0;
        while (iterator.hasNext()) {
            GroceryProduct product = iterator.next();
            assertTrue(product.getPrice() >= 1.0);
            assertTrue(product.getPrice() <= 5.0);
            count++;
        }
        assertTrue(count > 0);
    }
    
    @Test
    void testPriceSortedIterator_Ascending() {
        List<GroceryProduct> products = createTestProducts();
        ProductIterator iterator = new PriceSortedIterator(products, true);
        
        double lastPrice = -1.0;
        while (iterator.hasNext()) {
            GroceryProduct product = iterator.next();
            assertTrue(product.getPrice() >= lastPrice);
            lastPrice = product.getPrice();
        }
    }
    
    @Test
    void testPriceSortedIterator_Descending() {
        List<GroceryProduct> products = createTestProducts();
        ProductIterator iterator = new PriceSortedIterator(products, false);
        
        double lastPrice = 9999.0;
        while (iterator.hasNext()) {
            GroceryProduct product = iterator.next();
            assertTrue(product.getPrice() <= lastPrice);
            lastPrice = product.getPrice();
        }
    }
    
    @Test
    void testProductIteratorFactory() {
        List<GroceryProduct> products = createTestProducts();
        ProductIteratorFactory factory = new ProductIteratorFactory();
        
        ProductIterator cheap = factory.createCheapIterator(products, 2.0);
        while (cheap.hasNext()) {
            GroceryProduct product = cheap.next();
            assertTrue(product.getPrice() <= 2.0);
        }
        
        ProductIterator snacks = factory.createSnackIterator(products);
        while (snacks.hasNext()) {
            GroceryProduct product = snacks.next();
            assertEquals("SNACK", product.getType().name());
        }
    }
    
    @Test
    void testIteratorReset() {
        List<GroceryProduct> products = createTestProducts();
        ProductIterator iterator = new PriceRangeIterator(products, 0.0, 10.0);
        
        List<GroceryProduct> firstPass = new ArrayList<>();
        while (iterator.hasNext()) {
            firstPass.add(iterator.next());
        }
        
        iterator.reset();
        
        List<GroceryProduct> secondPass = new ArrayList<>();
        while (iterator.hasNext()) {
            secondPass.add(iterator.next());
        }
        
        assertEquals(firstPass.size(), secondPass.size());
    }
    
    @Test
    void testIteratorCounts() {
        List<GroceryProduct> products = createTestProducts();
        ProductIterator iterator = new PriceRangeIterator(products, 0.0, 10.0);
        
        int total = iterator.getTotalCount();
        int count = 0;
        
        while (iterator.hasNext()) {
            iterator.next();
            count++;
            int remaining = iterator.getRemainingCount();
            assertEquals(total - count, remaining);
        }
        
        assertEquals(total, count);
    }
    
    @Test
    void testIteratorPosition() {
        List<GroceryProduct> products = createTestProducts();
        ProductIterator iterator = new PriceRangeIterator(products, 0.0, 10.0);
        
        assertTrue(iterator.isAtBeginning());
        
        int position = 0;
        while (iterator.hasNext()) {
            iterator.next();
            position = iterator.getCurrentPosition();
            assertTrue(position > 0);
        }
        
        assertTrue(iterator.isAtEnd());
    }
    
    @Test
    void testFindFirst() {
        List<GroceryProduct> products = createTestProducts();
        ProductIteratorFactory factory = new ProductIteratorFactory();
        ProductIterator iterator = new PriceRangeIterator(products, 0.0, 10.0);
        
        Optional<GroceryProduct> found = factory.findFirst(iterator, p -> p.getName().equals("Chips"));
        assertTrue(found.isPresent());
    }
    
    @Test
    void testCollectAll() {
        List<GroceryProduct> products = createTestProducts();
        ProductIteratorFactory factory = new ProductIteratorFactory();
        ProductIterator iterator = new PriceRangeIterator(products, 0.0, 10.0);
        
        List<GroceryProduct> all = factory.collectAll(iterator);
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }
    
    @Test
    void testEmptyIterator() {
        List<GroceryProduct> products = createTestProducts();
        ProductIterator iterator = new PriceRangeIterator(products, 100.0, 200.0);
        
        assertFalse(iterator.hasNext());
        assertEquals(0, iterator.getTotalCount());
    }
    
    @Test
    void testInvalidPriceRange() {
        List<GroceryProduct> products = createTestProducts();
        
        assertThrows(IllegalArgumentException.class, () -> 
            new PriceRangeIterator(products, 10.0, 1.0));
    }
}
