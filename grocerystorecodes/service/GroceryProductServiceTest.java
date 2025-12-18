package com.university.grocerystore.service;

import org.junit.jupiter.api.Test;
import com.university.grocerystore.model.*;
import static org.junit.jupiter.api.Assertions.*;

class GroceryProductServiceTest {
    
    @Test
    void testAddProduct_Valid() {
        GroceryProductService service = new GroceryProductService(null);
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 2.99, 2024, 12, "TYPE");
        
        assertDoesNotThrow(() -> service.addProduct(product));
    }
    
    @Test
    void testAddProduct_NullProduct() {
        GroceryProductService service = new GroceryProductService(null);
        
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> service.addProduct(null)
        );
        assertEquals("Product cannot be null", ex.getMessage());
    }
    
    @Test
    void testAddProduct_NegativePrice() {
        GroceryProductService service = new GroceryProductService(null);
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", -1.99, 2024, 12, "TYPE");
        
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> service.addProduct(product)
        );
        assertTrue(ex.getMessage().contains("price cannot be negative"));
    }
    
    @Test
    void testAddProduct_InvalidYear() {
        GroceryProductService service = new GroceryProductService(null);
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 2.99, 1800, 12, "TYPE");
        
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> service.addProduct(product)
        );
        assertTrue(ex.getMessage().contains("production year"));
    }
    
    @Test
    void testAddProduct_EmptyName() {
        GroceryProductService service = new GroceryProductService(null);
        GroceryProduct product = new CannedGood("P001", "", "Brand", 2.99, 2024, 12, "TYPE");
        
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> service.addProduct(product)
        );
        assertEquals("Product name cannot be null or empty", ex.getMessage());
    }
    
    @Test
    void testAddProduct_EmptyBrand() {
        GroceryProductService service = new GroceryProductService(null);
        GroceryProduct product = new CannedGood("P001", "Soup", "", 2.99, 2024, 12, "TYPE");
        
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> service.addProduct(product)
        );
        assertEquals("Product brand cannot be null or empty", ex.getMessage());
    }
    
    @Test
    void testAddProduct_EmptyId() {
        GroceryProductService service = new GroceryProductService(null);
        GroceryProduct product = new CannedGood("", "Soup", "Brand", 2.99, 2024, 12, "TYPE");
        
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> service.addProduct(product)
        );
        assertEquals("Product ID cannot be null or empty", ex.getMessage());
    }
}codes
