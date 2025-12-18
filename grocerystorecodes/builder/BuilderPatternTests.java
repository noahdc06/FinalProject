package com.university.grocerystore.builder;

import org.junit.jupiter.api.Test;
import com.university.grocerystore.model.*;
import com.university.grocerystore.composite.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BuilderPatternTests {
    
    @Test
    void testFrozenFoodBuilder() {
        FrozenFoodBuilder builder = new FrozenFoodBuilder();
        FrozenFood food = builder
            .setId("FF001")
            .setName("Pizza")
            .setBrand("DiGiorno")
            .setPrice(8.99)
            .setProductionYear(2024)
            .setStorageType("FREEZER")
            .setNetWeight(500.0)
            .setCalories(300)
            .build();
        
        assertNotNull(food);
        assertEquals("Pizza", food.getName());
        assertEquals(8.99, food.getPrice(), 0.001);
    }
    
    @Test
    void testFrozenFoodBuilder_Organic() {
        FrozenFoodBuilder builder = new FrozenFoodBuilder();
        FrozenFood food = builder
            .setId("FF002")
            .setName("Ice Cream")
            .setBrand("Ben & Jerry's")
            .setPrice(5.99)
            .setProductionYear(2024)
            .setStorageType("FREEZER")
            .setNetWeight(400.0)
            .makeOrganic()
            .setCalories(250)
            .build();
        
        assertTrue(food.isOrganic());
    }
    
    @Test
    void testFrozenFoodBuilder_ValidationError() {
        FrozenFoodBuilder builder = new FrozenFoodBuilder();
        assertThrows(IllegalStateException.class, builder::build);
    }
    
    @Test
    void testGroceryBundleBuilder() {
        GroceryBundleBuilder builder = new GroceryBundleBuilder();
        GroceryProduct product = new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG");
        
        GroceryBundle bundle = builder
            .setBundleName("Test Bundle")
            .setBundleDiscount(0.10)
            .addProduct(product)
            .build();
        
        assertEquals("Test Bundle", bundle.getName());
        assertEquals(0.10, bundle.getDiscountRate(), 0.001);
    }
    
    @Test
    void testGroceryBundleBuilder_MultipleProducts() {
        GroceryBundleBuilder builder = new GroceryBundleBuilder();
        GroceryProduct p1 = new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG");
        GroceryProduct p2 = new CannedGood("C002", "Soup", "Campbell", 2.99, 2024, 12, "SOUP");
        
        GroceryBundle bundle = builder
            .setBundleName("Combo Pack")
            .setBundleDiscountPercent(15.0)
            .addProduct(p1)
            .addProduct(p2)
            .build();
        
        assertEquals(2, bundle.getComponentCount());
    }
    
    @Test
    void testGroceryBundleBuilder_ValidationError() {
        GroceryBundleBuilder builder = new GroceryBundleBuilder();
        assertThrows(IllegalStateException.class, builder::build);
    }
    
    @Test
    void testGroceryDirector_PremiumFrozenFood() {
        FrozenFoodBuilder frozenBuilder = new FrozenFoodBuilder();
        GroceryBundleBuilder bundleBuilder = new GroceryBundleBuilder();
        GroceryDirector director = new GroceryDirector(frozenBuilder, bundleBuilder);
        
        GroceryProduct food = director.buildPremiumFrozenFood("P001", "Premium Pizza", "Brand", 12.99);
        
        assertEquals("Premium Pizza", food.getName());
        assertEquals(12.99, food.getPrice(), 0.001);
    }
    
    @Test
    void testGroceryDirector_MealDealBundle() {
        FrozenFoodBuilder frozenBuilder = new FrozenFoodBuilder();
        GroceryBundleBuilder bundleBuilder = new GroceryBundleBuilder();
        GroceryDirector director = new GroceryDirector(frozenBuilder, bundleBuilder);
        
        List<GroceryProduct> products = Arrays.asList(
            new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG"),
            new Snack("S001", "Chips", "Lays", 1.49, 2024, "CHIPS", 200.0, 150, "BBQ", "None")
        );
        
        GroceryBundle bundle = director.buildMealDealBundle("Meal Deal", products);
        
        assertEquals("Meal Deal", bundle.getName());
        assertEquals(0.20, bundle.getDiscountRate(), 0.001);
    }
    
    @Test
    void testGroceryDirector_BasicFrozenFood() {
        FrozenFoodBuilder frozenBuilder = new FrozenFoodBuilder();
        GroceryBundleBuilder bundleBuilder = new GroceryBundleBuilder();
        GroceryDirector director = new GroceryDirector(frozenBuilder, bundleBuilder);
        
        GroceryProduct food = director.buildBasicFrozenFood("B001", "Basic Pizza", "Brand", 6.99);
        
        assertEquals("Basic Pizza", food.getName());
    }
    
    @Test
    void testGroceryDirector_FamilyBundle() {
        FrozenFoodBuilder frozenBuilder = new FrozenFoodBuilder();
        GroceryBundleBuilder bundleBuilder = new GroceryBundleBuilder();
        GroceryDirector director = new GroceryDirector(frozenBuilder, bundleBuilder);
        
        List<GroceryProduct> products = Arrays.asList(
            new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG")
        );
        
        GroceryBundle bundle = director.buildFamilyBundle("Family Pack", products);
        
        assertEquals(0.25, bundle.getDiscountRate(), 0.001);
    }
    
    @Test
    void testGroceryDirector_CustomBundle() {
        FrozenFoodBuilder frozenBuilder = new FrozenFoodBuilder();
        GroceryBundleBuilder bundleBuilder = new GroceryBundleBuilder();
        GroceryDirector director = new GroceryDirector(frozenBuilder, bundleBuilder);
        
        List<GroceryProduct> products = Arrays.asList(
            new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEG")
        );
        
        GroceryBundle bundle = director.buildCustomBundle("Custom Bundle", products, 0.15);
        
        assertEquals(0.15, bundle.getDiscountRate(), 0.001);
    }
    
    @Test
    void testBuilderReset() {
        FrozenFoodBuilder builder = new FrozenFoodBuilder();
        builder.setId("FF001").setName("Pizza").setBrand("Brand");
        builder.reset();
        
        assertThrows(IllegalStateException.class, builder::build);
    }
}
