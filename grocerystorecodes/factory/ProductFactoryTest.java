package com.university.grocerystore.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;
import com.university.grocerystore.model.Produce;
import com.university.grocerystore.model.Snack;

class ProductFactoryTests {
    
    // Test Case 1: Test ProductFactory - Create different product types successfully
    @Test
    void testProductFactory_CreateVariousProductTypes() {
        // Create a Produce
        Map<String, Object> produceProps = new HashMap<>();
        produceProps.put("sku", "P001");
        produceProps.put("name", "Apples");
        produceProps.put("brand", "Fresh Farms");
        produceProps.put("price", 3.99);
        produceProps.put("productionYear", 2024);
        produceProps.put("variety", "Gala");
        produceProps.put("weight", 1.5);
        produceProps.put("organic", true);
        produceProps.put("countryOfOrigin", "USA");
        produceProps.put("quality", Perishable.ShelfLifeQuality.FRESH);
        
        GroceryProduct produce = ProductFactory.createProduct("PRODUCE", produceProps);
        assertTrue(produce instanceof Produce);
        assertEquals("Apples", produce.getName());
        assertEquals("Fresh Farms", produce.getBrand());
        assertEquals(3.99, produce.getPrice(), 0.001);
        
        // Create a FrozenFood
        Map<String, Object> frozenProps = new HashMap<>();
        frozenProps.put("sku", "F001");
        frozenProps.put("name", "Frozen Peas");
        frozenProps.put("brand", "FrozenCo");
        frozenProps.put("price", 2.49);
        frozenProps.put("productionYear", 2024);
        frozenProps.put("storageType", "Freezer");
        frozenProps.put("netWeight", 0.5);
        frozenProps.put("organic", true);
        frozenProps.put("calories", 80);
        frozenProps.put("quality", Perishable.ShelfLifeQuality.FRESH);
        
        GroceryProduct frozenFood = ProductFactory.createProduct("FROZEN_FOOD", frozenProps);
        assertTrue(frozenFood instanceof FrozenFood);
        assertEquals("Frozen Peas", frozenFood.getName());
        assertEquals("FrozenCo", frozenFood.getBrand());
        assertEquals(2.49, frozenFood.getPrice(), 0.001);
        
        // Create a Snack
        Map<String, Object> snackProps = new HashMap<>();
        snackProps.put("sku", "S001");
        snackProps.put("name", "Potato Chips");
        snackProps.put("brand", "SnackBrand");
        snackProps.put("price", 1.99);
        snackProps.put("productionYear", 2024);
        snackProps.put("snackType", "Chips");
        snackProps.put("netWeight", 0.15);
        snackProps.put("calories", 150);
        snackProps.put("flavor", "Sea Salt");
        snackProps.put("dietaryInfo", "Gluten Free");
        
        GroceryProduct snack = ProductFactory.createProduct("SNACK", snackProps);
        assertTrue(snack instanceof Snack);
        assertEquals("Potato Chips", snack.getName());
        assertEquals("SnackBrand", snack.getBrand());
        assertEquals(1.99, snack.getPrice(), 0.001);
    }
    
    // Test Case 2: Test AdvancedProductFactory - Enhanced validation and error handling
    @Test
    void testAdvancedProductFactory_CreationAndValidation() {
        // Test successful creation
        Map<String, Object> cannedProps = new HashMap<>();
        cannedProps.put("id", "C001");
        cannedProps.put("name", "Canned Corn");
        cannedProps.put("brand", "CanCo");
        cannedProps.put("price", 1.79);
        cannedProps.put("productionYear", 2024);
        cannedProps.put("canSize", "15 oz");
        cannedProps.put("recyclable", true);
        cannedProps.put("preservationMethod", "Pressure Canning");
        cannedProps.put("shelfLifeMonths", 24);
        
        GroceryProduct cannedGood = AdvancedProductFactory.createProduct("CANNED_GOOD", cannedProps);
        assertTrue(cannedGood instanceof CannedGood);
        assertEquals("Canned Corn", cannedGood.getName());
        assertEquals("CanCo", cannedGood.getBrand());
        assertEquals(1.79, cannedGood.getPrice(), 0.001);
        
        // Test validation method
        AdvancedProductFactory.validateRequiredProperties("CANNED_GOOD", cannedProps);
        
        // Test validation failure (missing required property)
        Map<String, Object> invalidProps = new HashMap<>(cannedProps);
        invalidProps.remove("name");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> AdvancedProductFactory.createProduct("CANNED_GOOD", invalidProps));
        assertTrue(exception.getMessage().contains("Required property missing"));
    }
    
    // Test Case 3: Test both factories - Error handling for invalid inputs
    @Test
    void testFactories_ErrorHandlingAndEdgeCases() {
        // Test null type
        Map<String, Object> props = new HashMap<>();
        props.put("name", "Test Product");
        
        assertThrows(NullPointerException.class, 
            () -> ProductFactory.createProduct(null, props));
        
        // Test null properties
        assertThrows(NullPointerException.class, 
            () -> ProductFactory.createProduct("PRODUCE", null));
        
        // Test invalid type
        assertThrows(IllegalArgumentException.class, 
            () -> ProductFactory.createProduct("INVALID_TYPE", props));
        
        // Test invalid properties (negative price)
        Map<String, Object> invalidPriceProps = new HashMap<>();
        invalidPriceProps.put("sku", "P001");
        invalidPriceProps.put("name", "Test");
        invalidPriceProps.put("brand", "Brand");
        invalidPriceProps.put("price", -10.0); // Invalid negative price
        invalidPriceProps.put("productionYear", 2024);
        invalidPriceProps.put("variety", "Test");
        invalidPriceProps.put("weight", 1.0);
        invalidPriceProps.put("organic", true);
        invalidPriceProps.put("countryOfOrigin", "USA");
        invalidPriceProps.put("quality", Perishable.ShelfLifeQuality.FRESH);
        
        // AdvancedProductFactory should catch negative price
        IllegalArgumentException priceException = assertThrows(IllegalArgumentException.class,
            () -> AdvancedProductFactory.createProduct("PRODUCE", invalidPriceProps));
        assertTrue(priceException.getMessage().contains("must be non-negative"));
    }
}