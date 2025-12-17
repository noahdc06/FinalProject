package com.university.grocerystore.visitor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;
import com.university.grocerystore.model.Produce;
import com.university.grocerystore.model.Snack;

class VisitorPatternTests {
    
    private StorageCostCalculator storageCalculator;
    private Produce apples;
    private FrozenFood frozenPeas;
    private CannedGood cannedCorn;
    private Snack potatoChips;
    
    @BeforeEach
    void setUp() {
        storageCalculator = new StorageCostCalculator();
        
        // Create test products
        apples = new Produce(
            "P001", "Apples", "Fresh Farms", 3.99, 2024,
            "Gala", 1.5, true, "USA", Perishable.ShelfLifeQuality.FRESH
        );
        
        frozenPeas = new FrozenFood(
            "F001", "Frozen Peas", "FrozenCo", 2.49, 2024,
            "Freezer", 0.5, true, 80, Perishable.ShelfLifeQuality.FRESH
        );
        
        cannedCorn = new CannedGood(
            "C001", "Canned Corn", "CanCo", 1.79, 2024,
            "15 oz", true, "Pressure Canning", 24
        );
        
        potatoChips = new Snack(
            "S001", "Potato Chips", "SnackBrand", 1.99, 2024,
            "Chips", 0.15, 150, "Sea Salt", "Gluten Free"
        );
    }
    
    // Test Case 1: Test individual product storage cost calculations
    @Test
    void testStorageCostCalculator_IndividualProducts() {
        // Calculate storage cost for each product individually
        double produceCost = storageCalculator.calculateStorageCost(apples);
        double frozenCost = storageCalculator.calculateStorageCost(frozenPeas);
        double cannedCost = storageCalculator.calculateStorageCost(cannedCorn);
        double snackCost = storageCalculator.calculateStorageCost(potatoChips);
        
        // Verify calculations based on shelf life days
        double expectedProduceCost = apples.getShelfLifeDays() * 0.10;
        double expectedFrozenCost = frozenPeas.getShelfLifeDays() * 0.15;
        double expectedCannedCost = cannedCorn.getShelfLifeDays() * 0.05;
        double expectedSnackCost = potatoChips.getShelfLifeDays() * 0.08;
        
        assertEquals(expectedProduceCost, produceCost, 0.001);
        assertEquals(expectedFrozenCost, frozenCost, 0.001);
        assertEquals(expectedCannedCost, cannedCost, 0.001);
        assertEquals(expectedSnackCost, snackCost, 0.001);
        
        // Verify different rates are applied
        assertTrue(produceCost > 0);
        assertTrue(frozenCost > 0);
        assertTrue(cannedCost > 0);
        assertTrue(snackCost > 0);
    }
    
    // Test Case 2: Test multiple products total storage cost
    @Test
    void testStorageCostCalculator_MultipleProducts() {
        // Create array of products
        GroceryProduct[] products = {apples, frozenPeas, cannedCorn, potatoChips};
        
        // Calculate total storage cost for all products
        double totalCost = storageCalculator.calculateTotalStorageCost(products);
        
        // Calculate expected total manually
        double expectedTotal = 
            (apples.getShelfLifeDays() * 0.10) +
            (frozenPeas.getShelfLifeDays() * 0.15) +
            (cannedCorn.getShelfLifeDays() * 0.05) +
            (potatoChips.getShelfLifeDays() * 0.08);
        
        assertEquals(expectedTotal, totalCost, 0.001);
        assertTrue(totalCost > 0);
        
        // Test that reset method works
        storageCalculator.reset();
        assertEquals(0.0, storageCalculator.getTotalStorageCost(), 0.001);
    }
    
    // Test Case 3: Test visitor pattern integration and edge cases
    @Test
    void testGroceryProductVisitor_PatternIntegration() {
        // Test each visit method directly
        storageCalculator.visit(apples);
        storageCalculator.visit(frozenPeas);
        storageCalculator.visit(cannedCorn);
        storageCalculator.visit(potatoChips);
        
        double totalCost = storageCalculator.getTotalStorageCost();
        double expectedTotal = 
            (apples.getShelfLifeDays() * 0.10) +
            (frozenPeas.getShelfLifeDays() * 0.15) +
            (cannedCorn.getShelfLifeDays() * 0.05) +
            (potatoChips.getShelfLifeDays() * 0.08);
        
        assertEquals(expectedTotal, totalCost, 0.001);
        
        // Test with different shelf life qualities
        Produce shortLifeProduce = new Produce(
            "P002", "Bananas", "Tropical", 2.49, 2024,
            "Cavendish", 1.0, false, "Ecuador", Perishable.ShelfLifeQuality.SHORT
        );
        
        // Reset and test new product
        storageCalculator.reset();
        double shortLifeCost = storageCalculator.calculateStorageCost(shortLifeProduce);
        double expectedShortLifeCost = shortLifeProduce.getShelfLifeDays() * 0.10;
        
        assertEquals(expectedShortLifeCost, shortLifeCost, 0.001);
        
        // Verify that shorter shelf life results in lower storage cost
        assertTrue(shortLifeCost < storageCalculator.calculateStorageCost(apples));
    }
}