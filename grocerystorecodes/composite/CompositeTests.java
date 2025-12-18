package com.university.grocerystore.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.Produce;
import com.university.grocerystore.model.GroceryProduct.ProductType;

class CompositeTests {

    private BundleService bundleService;
    private Produce apple;
    private FrozenFood pizza;
    private FrozenFood iceCream;

    @BeforeEach
    void setUp() {
        bundleService = new BundleService();

        // Create test products
        apple = new Produce("P001", "Apple", 1.99, 2024, "Fresh Farms", "Gala", 0.5, true, "USA", null);
        pizza = new FrozenFood("F001", "Pizza", 5.99, 2024, "FrozenCo", -18.0, 0.8, true, 450);
        iceCream = new FrozenFood("F002", "Ice Cream", 3.49, 2024, "IceCo", -20.0, 0.5, false, 250);
    }

    @Test
    void testGroceryLeaf_CreationAndProperties() {
        GroceryLeaf leaf = new GroceryLeaf(apple);

        assertEquals("Apple", leaf.getName());
        assertEquals(1.99, leaf.getPrice(), 0.001);
        assertEquals(1, leaf.getItemCount());
        assertTrue(leaf.isLeaf());
        assertEquals(ProductType.PRODUCE, leaf.getType());
        assertEquals(apple, leaf.getProduct());
    }

    @Test
    void testGroceryLeaf_NullProduct() {
        assertThrows(IllegalArgumentException.class, () -> new GroceryLeaf(null));
    }

    @Test
    void testGroceryBundle_CreationAndBasicOperations() {
        GroceryBundle bundle = new GroceryBundle("Fruit Pack", 0.1);

        assertEquals("Fruit Pack", bundle.getName());
        assertEquals(0.1, bundle.getDiscountRate(), 0.001);
        assertEquals(0, bundle.getItemCount());
        assertFalse(bundle.isLeaf());
        assertTrue(bundle.isComposite());

        bundle.addComponent(new GroceryLeaf(apple));
        assertEquals(1, bundle.getItemCount());
        assertEquals(1.99, bundle.getPrice(), 0.001);
        assertEquals(1.79, bundle.getDiscountedPrice(), 0.01); // 10% discount
        assertEquals(0.20, bundle.getTotalSavings(), 0.01);
    }

    @Test
    void testGroceryBundle_AddNullComponent() {
        GroceryBundle bundle = new GroceryBundle("Test Bundle", 0.1);
        assertThrows(IllegalArgumentException.class, () -> bundle.addComponent(null));
    }

    @Test
    void testGroceryBundle_NestedBundles() {
        // Create parent bundle
        GroceryBundle mealDeal = new GroceryBundle("Meal Deal", 0.15);

        // Create child bundle
        GroceryBundle dessertPack = new GroceryBundle("Desserts", 0.05);
        dessertPack.addComponent(new GroceryLeaf(iceCream));

        // Add to parent
        mealDeal.addComponent(new GroceryLeaf(pizza));
        mealDeal.addComponent(dessertPack);

        assertEquals(2, mealDeal.getItemCount());
        assertEquals(9.48, mealDeal.getPrice(), 0.01); // 5.99 + 3.49
        assertEquals(8.06, mealDeal.getDiscountedPrice(), 0.01); // 15% off total
    }

    @Test
    void testGroceryBundle_RemoveComponent() {
        GroceryBundle bundle = new GroceryBundle("Test Bundle", 0.1);
        GroceryLeaf leaf = new GroceryLeaf(apple);

        bundle.addComponent(leaf);
        assertEquals(1, bundle.getItemCount());

        assertTrue(bundle.removeComponent(leaf));
        assertEquals(0, bundle.getItemCount());

        assertFalse(bundle.removeComponent(leaf)); // Already removed
    }

    @Test
    void testGroceryBundle_ContainsType() {
        GroceryBundle bundle = new GroceryBundle("Mixed Bundle", 0.1);
        bundle.addComponent(new GroceryLeaf(apple));
        bundle.addComponent(new GroceryLeaf(pizza));

        assertTrue(bundle.containsType(ProductType.PRODUCE));
        assertTrue(bundle.containsType(ProductType.FROZEN_FOOD));
        assertFalse(bundle.containsType(ProductType.CANNED_GOOD));
    }

    @Test
    void testGroceryBundle_CircularReferencePrevention() {
        GroceryBundle bundle1 = new GroceryBundle("Bundle1", 0.1);
        GroceryBundle bundle2 = new GroceryBundle("Bundle2", 0.1);

        bundle1.addComponent(bundle2);

        // Should not be able to create circular reference
        assertThrows(IllegalArgumentException.class, () -> bundle2.addComponent(bundle1));

        // Should not be able to add bundle to itself
        assertThrows(IllegalArgumentException.class, () -> bundle1.addComponent(bundle1));
    }

    @Test
    void testBundleService_CreateAndRetrieveBundles() {
        GroceryBundle bundle = bundleService.createBundle("Summer Special", 0.2);

        assertEquals("Summer Special", bundle.getName());
        assertEquals(0.2, bundle.getDiscountRate(), 0.001);

        Optional<GroceryBundle> retrieved = bundleService.getBundle("Summer Special");
        assertTrue(retrieved.isPresent());
        assertEquals(bundle, retrieved.get());
    }

    @Test
    void testBundleService_DuplicateBundleCreation() {
        bundleService.createBundle("Test Bundle", 0.1);

        assertThrows(IllegalArgumentException.class,
                () -> bundleService.createBundle("Test Bundle", 0.2));
    }

    @Test
    void testBundleService_AddProductsToBundle() {
        bundleService.createBundle("Frozen Foods", 0.1);

        bundleService.addToBundle("Frozen Foods", pizza);
        bundleService.addToBundle("Frozen Foods", iceCream);

        Optional<GroceryBundle> bundle = bundleService.getBundle("Frozen Foods");
        assertTrue(bundle.isPresent());
        assertEquals(2, bundle.get().getItemCount());
        assertEquals(9.48, bundle.get().getPrice(), 0.01);
    }

    @Test
    void testBundleService_NestedBundleManagement() {
        bundleService.createBundle("Meal Package", 0.15);
        bundleService.createBundle("Side Items", 0.05);

        bundleService.addToBundle("Side Items", iceCream);
        bundleService.addBundleToBundle("Meal Package", "Side Items");

        GroceryBundle parent = bundleService.getBundle("Meal Package").get();
        assertEquals(1, parent.getItemCount());
        assertTrue(parent.containsType(ProductType.FROZEN_FOOD));
    }

    @Test
    void testBundleService_RemoveFromBundle() {
        bundleService.createBundle("Test Bundle", 0.1);
        bundleService.addToBundle("Test Bundle", apple);

        assertTrue(bundleService.removeFromBundle("Test Bundle", apple));
        assertFalse(bundleService.removeFromBundle("Test Bundle", pizza)); // Not in bundle
    }

    @Test
    void testBundleService_BundleNotFoundOperations() {
        assertThrows(IllegalArgumentException.class,
                () -> bundleService.addToBundle("NonExistent", apple));

        assertThrows(IllegalArgumentException.class,
                () -> bundleService.calculateBundleSavings("NonExistent"));
    }

    @Test
    void testBundleService_SavingsCalculation() {
        bundleService.createBundle("Discount Bundle", 0.25);
        bundleService.addToBundle("Discount Bundle", pizza);
        bundleService.addToBundle("Discount Bundle", iceCream);

        double savings = bundleService.calculateBundleSavings("Discount Bundle");
        assertEquals(2.37, savings, 0.01); // 25% of 9.48

        double totalSavings = bundleService.calculateTotalSavings();
        assertEquals(2.37, totalSavings, 0.01);
    }

    @Test
    void testBundleService_StatisticsAndFiltering() {
        // Create multiple bundles
        bundleService.createBundle("Frozen Bundle", 0.15);
        bundleService.createBundle("Produce Bundle", 0.10);
        bundleService.createBundle("Mixed Bundle", 0.20);

        bundleService.addToBundle("Frozen Bundle", pizza);
        bundleService.addToBundle("Produce Bundle", apple);
        bundleService.addToBundle("Mixed Bundle", pizza);
        bundleService.addToBundle("Mixed Bundle", apple);

        // Test bundle count
        assertEquals(3, bundleService.getBundleCount());
        assertFalse(bundleService.isEmpty());

        // Test filtering by product type
        List<GroceryBundle> frozenBundles = bundleService.getBundlesByProductType(ProductType.FROZEN_FOOD);
        assertEquals(2, frozenBundles.size());

        List<GroceryBundle> produceBundles = bundleService.getBundlesByProductType(ProductType.PRODUCE);
        assertEquals(2, produceBundles.size());

        // Test filtering by discount
        List<GroceryBundle> highDiscountBundles = bundleService.getBundlesByDiscount(0.15);
        assertEquals(2, highDiscountBundles.size());

        // Test bundle statistics
        BundleService.BundleStats stats = bundleService.getBundleStats();
        assertEquals(3, stats.getTotalBundles());
        assertEquals(4, stats.getTotalItems()); // Total items across all bundles
    }

    @Test
    void testBundleService_ClearAndRemoveOperations() {
        bundleService.createBundle("Test Bundle", 0.1);
        bundleService.addToBundle("Test Bundle", apple);

        assertEquals(1, bundleService.getBundleCount());

        assertTrue(bundleService.removeBundle("Test Bundle"));
        assertEquals(0, bundleService.getBundleCount());

        assertFalse(bundleService.removeBundle("NonExistent"));
    }

    @Test
    void testBundleService_EmptyBundleOperations() {
        assertTrue(bundleService.isEmpty());
        assertEquals(0, bundleService.getBundleCount());

        BundleService.BundleStats stats = bundleService.getBundleStats();
        assertEquals(0, stats.getTotalBundles());
        assertEquals(0, stats.getTotalItems());
        assertEquals(0.0, stats.getAverageDiscount(), 0.001);
    }

    @Test
    void testIntegration_CompleteBundleWorkflow() {
        // 1. Create bundles
        GroceryBundle breakfast = bundleService.createBundle("Breakfast Pack", 0.1);
        GroceryBundle lunch = bundleService.createBundle("Lunch Special", 0.15);

        // 2. Add products
        bundleService.addToBundle("Breakfast Pack", apple);
        bundleService.addToBundle("Lunch Special", pizza);
        bundleService.addToBundle("Lunch Special", iceCream);

        // 3. Verify bundle contents
        assertEquals(1, breakfast.getItemCount());
        assertEquals(2, lunch.getItemCount());

        // 4. Calculate prices
        assertEquals(1.99, breakfast.getPrice(), 0.01);
        assertEquals(9.48, lunch.getPrice(), 0.01);

        // 5. Calculate discounts
        assertEquals(0.20, breakfast.getTotalSavings(), 0.01);
        assertEquals(1.42, lunch.getTotalSavings(), 0.01);

        // 6. Total statistics
        assertEquals(1.62, bundleService.calculateTotalSavings(), 0.01);
        assertEquals(11.47, bundleService.getTotalBundleValue(), 0.01);
        assertEquals(9.85, bundleService.getTotalDiscountedBundleValue(), 0.01);

        // 7. Clear all
        bundleService.clearAllBundles();
        assertTrue(bundleService.isEmpty());
    }
}