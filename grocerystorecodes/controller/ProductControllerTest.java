package com.university.grocerystore.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.university.grocerystore.api.GroceryStore;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.GroceryProduct.ProductType;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    
    @Mock
    private GroceryStore groceryStore;
    
    @InjectMocks
    private ProductController productController;
    
    // Test Case 1: Test successful product retrieval by ID
    @Test
    void testGetProductById_Success() {
        // Arrange
        GroceryProduct testProduct = new GroceryProduct(
            "P001", 
            "Test Product", 
            "Test Brand", 
            19.99, 
            100, 
            ProductType.DAIRY, 
            2023
        );
        
        when(groceryStore.findById("P001")).thenReturn(Optional.of(testProduct));
        
        // Act
        ResponseEntity<GroceryProduct> response = productController.getProductById("P001");
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals("P001", response.getBody().getId());
        assertEquals("Test Product", response.getBody().getName());
        
        verify(groceryStore, times(1)).findById("P001");
    }
    
    // Test Case 2: Test product creation success
    @Test
    void testCreateProduct_Success() {
        // Arrange
        GroceryProduct newProduct = new GroceryProduct(
            "P002", 
            "New Product", 
            "New Brand", 
            29.99, 
            50, 
            ProductType.BAKERY, 
            2024
        );
        
        when(groceryStore.addProduct(newProduct)).thenReturn(true);
        
        // Act
        ResponseEntity<GroceryProduct> response = productController.createProduct(newProduct);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(newProduct, response.getBody());
        
        verify(groceryStore, times(1)).addProduct(newProduct);
    }
    
    // Test Case 3: Test getting all products
    @Test
    void testGetAllProducts_Success() {
        // Arrange
        GroceryProduct product1 = new GroceryProduct("P001", "Milk", "BrandA", 3.99, 100, ProductType.DAIRY, 2023);
        GroceryProduct product2 = new GroceryProduct("P002", "Bread", "BrandB", 2.49, 150, ProductType.BAKERY, 2024);
        List<GroceryProduct> expectedProducts = Arrays.asList(product1, product2);
        
        when(groceryStore.getAllProducts()).thenReturn(expectedProducts);
        
        // Act
        ResponseEntity<List<GroceryProduct>> response = productController.getAllProducts();
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Milk", response.getBody().get(0).getName());
        assertEquals("Bread", response.getBody().get(1).getName());
        
        verify(groceryStore, times(1)).getAllProducts();
    }
}