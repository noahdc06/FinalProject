package com.university.grocerystore.utils;

import com.university.grocerystore.model.GroceryItem;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class GroceryItemUtilsTest {
    
    @Test
    void testCountBeforeYear() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "Brand", 2.99, 2020, 2022),
            new GroceryItem("2", "Bread", "Brand", 1.99, 2023, 2024),
            null
        };
        
        assertEquals(1, GroceryItemUtils.countBeforeYear(items, 2022));
        assertEquals(0, GroceryItemUtils.countBeforeYear(null, 2022));
    }
    
    @Test
    void testCountByBrand() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "DairyCo", 2.99, 2020, 2022),
            new GroceryItem("2", "Bread", "BakeryCo", 1.99, 2023, 2024),
            new GroceryItem("3", "Cheese", "DairyCo", 3.99, 2023, 2024)
        };
        
        assertEquals(2, GroceryItemUtils.countByBrand(items, "DairyCo"));
        assertEquals(1, GroceryItemUtils.countByBrand(items, "BakeryCo"));
        assertEquals(0, GroceryItemUtils.countByBrand(items, "Unknown"));
    }
    
    @Test
    void testFilterPriceAtMost() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "Brand", 2.99, 2020, 2022),
            new GroceryItem("2", "Bread", "Brand", 1.99, 2023, 2024),
            new GroceryItem("3", "Cheese", "Brand", 3.99, 2023, 2024)
        };
        
        GroceryItem[] result = GroceryItemUtils.filterPriceAtMost(items, 2.50);
        assertEquals(1, result.length);
        assertEquals("Bread", result[0].getName());
        
        assertThrows(IllegalArgumentException.class, () -> GroceryItemUtils.filterPriceAtMost(items, -1));
    }
    
    @Test
    void testSortByPrice() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("3", "Cheese", "Brand", 3.99, 2023, 2024),
            new GroceryItem("1", "Milk", "Brand", 2.99, 2020, 2022),
            new GroceryItem("2", "Bread", "Brand", 1.99, 2023, 2024)
        };
        
        GroceryItemUtils.sortByPrice(items);
        
        assertEquals("Bread", items[0].getName());
        assertEquals("Milk", items[1].getName());
        assertEquals("Cheese", items[2].getName());
    }
    
    @Test
    void testAveragePrice() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "Brand", 2.00, 2020, 2022),
            new GroceryItem("2", "Bread", "Brand", 4.00, 2023, 2024),
            null
        };
        
        assertEquals(3.00, GroceryItemUtils.averagePrice(items), 0.001);
        assertEquals(0.0, GroceryItemUtils.averagePrice(null));
    }
    
    @Test
    void testFindOldest() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "Brand", 2.99, 2020, 2022),
            new GroceryItem("2", "Bread", "Brand", 1.99, 2023, 2024),
            null
        };
        
        GroceryItem oldest = GroceryItemUtils.findOldest(items);
        assertNotNull(oldest);
        assertEquals("Milk", oldest.getName());
        
        assertNull(GroceryItemUtils.findOldest(null));
    }
    
    @Test
    void testMerge() {
        GroceryItem[] arr1 = new GroceryItem[]{
            new GroceryItem("1", "Milk", "Brand", 2.99, 2020, 2022)
        };
        
        GroceryItem[] arr2 = new GroceryItem[]{
            new GroceryItem("2", "Bread", "Brand", 1.99, 2023, 2024)
        };
        
        GroceryItem[] merged = GroceryItemUtils.merge(arr1, arr2);
        assertEquals(2, merged.length);
    }
    
    @Test
    void testFilterExpired() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Old Milk", "Brand", 2.99, 2020, 2022),
            new GroceryItem("2", "Fresh Bread", "Brand", 1.99, 2023, 2024),
            null
        };
        
        GroceryItem[] expired = GroceryItemUtils.filterExpired(items);
        assertEquals(1, expired.length);
        assertEquals("Old Milk", expired[0].getName());
    }
    
    @Test
    void testCountByBrandMap() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "DairyCo", 2.99, 2020, 2022),
            new GroceryItem("2", "Cheese", "DairyCo", 3.99, 2023, 2024),
            new GroceryItem("3", "Bread", "BakeryCo", 1.99, 2023, 2024)
        };
        
        Map<String, Integer> counts = GroceryItemUtils.countByBrand(items);
        assertEquals(2, (int) counts.get("DairyCo"));
        assertEquals(1, (int) counts.get("BakeryCo"));
    }
    
    @Test
    void testFindLongestName() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "Brand", 2.99, 2020, 2022),
            new GroceryItem("2", "Whole Wheat Bread", "Brand", 1.99, 2023, 2024),
            new GroceryItem("3", "Cheese", "Brand", 3.99, 2023, 2024)
        };
        
        GroceryItem longest = GroceryItemUtils.findLongestName(items);
        assertEquals("Whole Wheat Bread", longest.getName());
    }
    
    @Test
    void testAverageShelfLife() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "Brand", 2.99, 2020, 2021),
            new GroceryItem("2", "Bread", "Brand", 1.99, 2023, 2024)
        };
        
        double avg = GroceryItemUtils.averageShelfLife(items);
        assertEquals(1.0, avg, 0.001);
    }
    
    @Test
    void testFindMostExpensive() {
        GroceryItem[] items = new GroceryItem[]{
            new GroceryItem("1", "Milk", "Brand", 2.99, 2020, 2022),
            new GroceryItem("2", "Bread", "Brand", 1.99, 2023, 2024),
            new GroceryItem("3", "Cheese", "Brand", 4.99, 2023, 2024)
        };
        
        GroceryItem mostExpensive = GroceryItemUtils.findMostExpensive(items);
        assertEquals("Cheese", mostExpensive.getName());
    }
    
    @Test
    void testLoggerFactory() {
        assertNotNull(LoggerFactory.getLogger(GroceryItemUtilsTest.class));
        assertNotNull(LoggerFactory.getLogger("TestLogger"));
        
        assertThrows(UnsupportedOperationException.class, LoggerFactory::new);
    }
}
