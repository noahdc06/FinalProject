package com.university.grocerystorecodes.builder;

import com.university.grocerystorecodes.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuilderPatternTests {
    
    @Test
    void testFrozenFoodBuilder() {
        FrozenFoodBuilder builder = new FrozenFoodBuilder();
        
        FrozenFood food = builder
            .setId("F001")
            .setName("Frozen Vegetables")
            .setPrice(3.99)
            .setYear(2024)
            .setTemperature(-18.0)
            .setExpirationDate("2024-12-31")
            .setPackaging("Plastic Bag")
            .setWeight(500)
            .setOrganic(true)
            .build();
        
        assertEquals("Frozen Vegetables", food.getName());
        assertEquals(3.99, food.getPrice());
        assertEquals(-18.0, food.getTemperature());
        assertTrue(food.isOrganic());
    }
    
    @Test
    void testProductBuilder() {
        ProductBuilder builder = new ProductBuilder();
        
        GroceryProduct product = builder
            .setId("P001")
            .setName("Test Product")
            .setPrice(9.99)
            .setYear(2024)
            .setCategory("Test")
            .setDescription("Test Description")
            .build();
        
        assertEquals("Test Product", product.getName());
        assertEquals(9.99, product.getPrice());
    }
    
    @Test
    void testGroceryBundleBuilder() {
        GroceryBundleBuilder builder = new GroceryBundleBuilder();
        
        GroceryBundle bundle = builder
            .setBundleName("Breakfast Bundle")
            .setDescription("Morning essentials")
            .setDiscount(0.15)
            .addProduct(new Produce("P001", "Apple", 1.99, 2024))
            .addProduct(new CannedGood("C001", "Orange Juice", 3.49, 2024, "Can", 350, "2025-01-31"))
            .build();
        
        assertEquals("Breakfast Bundle", bundle.getName());
        assertEquals(0.15, bundle.getDiscountRate());
        assertEquals(2, bundle.getItemCount());
    }
    
    @Test
    void testGroceryDirector() {
        GroceryDirector director = new GroceryDirector();
        
        GroceryBundle basicBundle = director.createBasicBundle();
        assertNotNull(basicBundle);
        assertTrue(basicBundle.getItemCount() > 0);
        
        GroceryBundle premiumBundle = director.createPremiumBundle();
        assertNotNull(premiumBundle);
        assertTrue(premiumBundle.getDiscountRate() > 0);
    }
    
    @Test
    void testBuilderValidation() {
        ProductBuilder builder = new ProductBuilder();
        
        assertThrows(IllegalStateException.class, () -> {
            builder.setPrice(-5.0).build();
        });
        
        assertThrows(IllegalStateException.class, () -> {
            builder.setId(null).build();
        });
    }
    
    @Test
    void testBuilderReset() {
        FrozenFoodBuilder builder = new FrozenFoodBuilder();
        
        builder.setId("F001").setName("Test").setPrice(10.0);
        builder.reset();
        
        assertThrows(IllegalStateException.class, () -> {
            builder.build();
        });
    }
}
