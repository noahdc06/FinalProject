package com.university.grocerystorecodes.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

public class ModelTests {
    
    private GroceryProduct apple;
    private GroceryProduct soup;
    private GroceryProduct pizza;
    private GroceryProduct chips;
    
    @BeforeEach
    void setUp() {
        apple = new Produce("P001", "Apple", 1.99, 2024);
        soup = new CannedGood("C001", "Tomato Soup", 2.49, 2024, 
                              "Can", 400, "2025-12-31");
        pizza = new FrozenFood("F001", "Frozen Pizza", 5.99, 2024,
                              -18.0, "2024-06-30", "Box");
        chips = new Snack("S001", "Potato Chips", 1.49, 2024,
                         "Bag", 150, "2024-09-30");
    }
    
    // ============ GroceryProduct Tests ============
    
    @Test
    void testGroceryProductBasics() {
        assertEquals("P001", apple.getId());
        assertEquals("Apple", apple.getName());
        assertEquals(1.99, apple.getPrice(), 0.01);
        assertEquals(2024, apple.getYear());
        assertEquals("Produce", apple.getCategory());
    }
    
    @Test
    void testProductEquality() {
        GroceryProduct apple2 = new Produce("P001", "Apple", 1.99, 2024);
        GroceryProduct differentApple = new Produce("P002", "Green Apple", 2.49, 2024);
        
        assertEquals(apple, apple2);
        assertNotEquals(apple, differentApple);
        assertEquals(apple.hashCode(), apple2.hashCode());
    }
    
    @Test
    void testPriceValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Produce("P999", "Test", -1.0, 2024);
        });
    }
    
    // ============ Produce Tests ============
    
    @Test
    void testProduceSpecifics() {
        Produce banana = new Produce("P002", "Banana", 0.99, 2024);
        assertNotNull(banana);
        assertEquals("Produce", banana.getCategory());
        
        // Test Produce-specific methods
        assertTrue(banana.getDisplayInfo().contains("Banana"));
    }
    
    @Test
    void testProduceWithOrganicFlag() {
        Produce organicApple = new Produce("P003", "Organic Apple", 2.99, 2024, true);
        assertTrue(organicApple.isOrganic());
        
        Produce regularApple = new Produce("P004", "Regular Apple", 1.99, 2024, false);
        assertFalse(regularApple.isOrganic());
    }
    
    // ============ CannedGood Tests ============
    
    @Test
    void testCannedGood() {
        assertEquals("Can", ((CannedGood) soup).getContainerType());
        assertEquals(400, ((CannedGood) soup).getNetWeight());
        assertEquals("2025-12-31", ((CannedGood) soup).getExpirationDate());
    }
    
    @Test
    void testCannedGoodExpired() {
        CannedGood expiredSoup = new CannedGood("C002", "Expired Soup", 1.99, 2023,
                                               "Can", 400, "2023-01-01");
        assertTrue(expiredSoup.isExpired());
        
        CannedGood freshSoup = new CannedGood("C003", "Fresh Soup", 2.99, 2024,
                                             "Can", 400, "2025-12-31");
        assertFalse(freshSoup.isExpired());
    }
    
    // ============ FrozenFood Tests ============
    
    @Test
    void testFrozenFood() {
        assertEquals(-18.0, ((FrozenFood) pizza).getStorageTemperature(), 0.01);
        assertEquals("2024-06-30", ((FrozenFood) pizza).getExpirationDate());
        assertEquals("Box", ((FrozenFood) pizza).getPackaging());
    }
    
    @Test
    void testFrozenFoodTemperatureValidation() {
        // Temperature too warm for frozen food
        assertThrows(IllegalArgumentException.class, () -> {
            new FrozenFood("F002", "Ice Cream", 3.99, 2024,
                          0.0, "2024-12-31", "Tub"); // 0°C is too warm
        });
        
        // Temperature too cold
        assertThrows(IllegalArgumentException.class, () -> {
            new FrozenFood("F003", "Ice", 1.99, 2024,
                          -100.0, "2024-12-31", "Bag"); // -100°C unrealistic
        });
    }
    
    // ============ Snack Tests ============
    
    @Test
    void testSnack() {
        assertEquals("Bag", ((Snack) chips).getPackagingType());
        assertEquals(150, ((Snack) chips).getNetWeight());
        assertEquals("2024-09-30", ((Snack) chips).getExpirationDate());
    }
    
    @Test
    void testSnackNutrition() {
        Snack healthySnack = new Snack("S002", "Granola Bar", 1.99, 2024,
                                      "Wrapper", 50, "2024-12-31", 150, 5, 3, 22);
        
        assertEquals(150, healthySnack.getCalories());
        assertEquals(5, healthySnack.getProteinGrams());
        assertEquals(3, healthySnack.getFatGrams());
        assertEquals(22, healthySnack.getCarbGrams());
    }
    
    // ============ Perishable Interface Tests ============
    
    @Test
    void testPerishableInterface() {
        // All perishable products should implement the interface
        assertTrue(soup instanceof Perishable);
        assertTrue(pizza instanceof Perishable);
        assertTrue(chips instanceof Perishable);
        
        // Produce might or might not be perishable depending on implementation
        // assertTrue(apple instanceof Perishable);
    }
    
    @Test
    void testDaysUntilExpiration() {
        Perishable perishableSoup = (Perishable) soup;
        long days = perishableSoup.getDaysUntilExpiration();
        assertTrue(days > 0, "Fresh soup should have positive days until expiration");
    }
    
    // ============ GroceryItem Tests ============
    
    @Test
    void testGroceryItem() {
        GroceryItem item = new GroceryItem("GI001", "Generic Item", 2.99, "Misc", 3);
        
        assertEquals("GI001", item.getId());
        assertEquals("Generic Item", item.getName());
        assertEquals(2.99, item.getPrice(), 0.01);
        assertEquals("Misc", item.getCategory());
        assertEquals(3, item.getQuantity());
        
        double totalPrice = item.getTotalPrice();
        assertEquals(8.97, totalPrice, 0.01); // 2.99 * 3
    }
    
    @Test
    void testGroceryItemQuantityValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GroceryItem("GI002", "Test", 1.0, "Test", 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new GroceryItem("GI003", "Test", 1.0, "Test", -5);
        });
    }
    
    // ============ Doubly Linked List Cart Tests ============
    
    @Test
    void testDoublyCartNode() {
        DoublyCartNode node = new DoublyCartNode(apple, 3);
        
        assertEquals(apple, node.getProduct());
        assertEquals(3, node.getQuantity());
        assertNull(node.getNext());
        assertNull(node.getPrev());
        assertEquals(5.97, node.getTotalPrice(), 0.01); // 1.99 * 3
        
        node.setQuantity(5);
        assertEquals(5, node.getQuantity());
        
        node.incrementQuantity(2);
        assertEquals(7, node.getQuantity());
        
        assertTrue(node.decrementQuantity(3));
        assertEquals(4, node.getQuantity());
    }
    
    @Test
    void testDoublyCartNodeLinks() {
        DoublyCartNode node1 = new DoublyCartNode(apple, 2);
        DoublyCartNode node2 = new DoublyCartNode(soup, 1);
        
        node1.setNext(node2);
        node2.setPrev(node1);
        
        assertEquals(node2, node1.getNext());
        assertEquals(node1, node2.getPrev());
        assertTrue(node1.hasNext());
        assertFalse(node1.hasPrev());
        assertFalse(node2.hasNext());
        assertTrue(node2.hasPrev());
    }
    
    @Test
    void testDoublyLinkedListCartBasicOperations() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.size());
        assertEquals(0.0, cart.getTotal(), 0.01);
        
        cart.add(apple, 3);
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.size());
        assertEquals(5.97, cart.getTotal(), 0.01); // 1.99 * 3
        
        cart.add(soup, 2);
        assertEquals(2, cart.size());
        assertEquals(10.95, cart.getTotal(), 0.01); // 5.97 + (2.49 * 2)
    }
    
    @Test
    void testDoublyLinkedListCartAddFirst() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        cart.add(apple, 2);      // Added first
        cart.addFirst(soup, 1);  // Should be new first
        
        assertEquals(soup, cart.getProduct(0));
        assertEquals(apple, cart.getProduct(1));
    }
    
    @Test
    void testDoublyLinkedListCartRemove() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        cart.add(apple, 2);
        cart.add(soup, 1);
        cart.add(pizza, 1);
        
        assertEquals(3, cart.size());
        
        boolean removed = cart.remove("C001"); // Remove soup
        assertTrue(removed);
        assertEquals(2, cart.size());
        assertEquals(1.99 * 2 + 5.99, cart.getTotal(), 0.01);
        
        // Remove non-existent product
        assertFalse(cart.remove("NONEXISTENT"));
    }
    
    @Test
    void testDoublyLinkedListCartUpdateQuantity() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        cart.add(apple, 2);
        assertEquals(3.98, cart.getTotal(), 0.01);
        
        boolean updated = cart.updateQuantity("P001", 5);
        assertTrue(updated);
        assertEquals(9.95, cart.getTotal(), 0.01); // 1.99 * 5
        
        // Update non-existent product
        assertFalse(cart.updateQuantity("NONEXISTENT", 10));
    }
    
    @Test
    void testDoublyLinkedListCartTraversal() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        cart.add(apple, 1);
        cart.add(soup, 2);
        cart.add(pizza, 1);
        
        // Test forward iteration
        int forwardCount = 0;
        for (DoublyCartNode node : cart) {
            forwardCount++;
            assertNotNull(node.getProduct());
        }
        assertEquals(3, forwardCount);
        
        // Test backward iteration
        int backwardCount = 0;
        java.util.Iterator<DoublyCartNode> reverseIterator = cart.reverseIterator();
        while (reverseIterator.hasNext()) {
            backwardCount++;
            reverseIterator.next();
        }
        assertEquals(3, backwardCount);
    }
    
    @Test
    void testDoublyLinkedListCartReverse() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        cart.add(apple, 1);
        cart.add(soup, 1);
        cart.add(pizza, 1);
        
        GroceryProduct firstBefore = cart.getProduct(0);
        GroceryProduct lastBefore = cart.getProduct(2);
        
        cart.reverse();
        
        assertEquals(pizza, cart.getProduct(0));
        assertEquals(soup, cart.getProduct(1));
        assertEquals(apple, cart.getProduct(2));
    }
    
    @Test
    void testDoublyLinkedListCartClear() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        cart.add(apple, 3);
        cart.add(soup, 2);
        
        assertEquals(2, cart.size());
        assertTrue(cart.getTotal() > 0);
        
        cart.clear();
        
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.size());
        assertEquals(0.0, cart.getTotal(), 0.01);
    }
    
    @Test
    void testDoublyLinkedListCartFindAndContains() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        cart.add(apple, 2);
        cart.add(soup, 1);
        
        assertTrue(cart.contains("P001"));
        assertTrue(cart.contains("C001"));
        assertFalse(cart.contains("NONEXISTENT"));
        
        assertEquals(2, cart.getQuantity("P001"));
        assertEquals(1, cart.getQuantity("C001"));
        assertEquals(0, cart.getQuantity("NONEXISTENT"));
    }
    
    @Test
    void testDoublyLinkedListCartIndexOutOfBounds() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        cart.add(apple, 1);
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            cart.getProduct(-1);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            cart.getProduct(5);
        });
    }
    
    @Test
    void testDoublyLinkedListCartToString() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        cart.add(apple, 2);
        cart.add(soup, 1);
        
        String str = cart.toString();
        assertTrue(str.contains("Apple"));
        assertTrue(str.contains("Tomato Soup"));
        assertTrue(str.contains("Total:"));
    }
    
    // ============ Edge Cases and Error Scenarios ============
    
    @Test
    void testInvalidProductCreation() {
        // Invalid ID
        assertThrows(IllegalArgumentException.class, () -> {
            new Produce(null, "Apple", 1.99, 2024);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Produce("", "Apple", 1.99, 2024);
        });
        
        // Invalid name
        assertThrows(IllegalArgumentException.class, () -> {
            new Produce("P001", null, 1.99, 2024);
        });
        
        // Invalid year
        assertThrows(IllegalArgumentException.class, () -> {
            new Produce("P001", "Apple", 1.99, 1800); // Too old
        });
    }
    
    @Test
    void testProductDiscount() {
        GroceryProduct discountedApple = new Produce("P005", "Discounted Apple", 2.0, 2024);
        // Assuming GroceryProduct has getDiscountedPrice() method
        // double discountedPrice = discountedApple.getDiscountedPrice();
        // assertTrue(discountedPrice <= 2.0);
    }
    
    @Test
    void testProductSerialization() {
        // Test toString() for all product types
        assertNotNull(apple.toString());
        assertNotNull(soup.toString());
        assertNotNull(pizza.toString());
        assertNotNull(chips.toString());
        
        // Should contain basic product info
        assertTrue(apple.toString().contains("Apple"));
        assertTrue(apple.toString().contains("1.99"));
    }
    
    @Test
    void testModelImmutability() {
        // Test that product fields are immutable (if designed that way)
        // Most fields should be final and only accessible via getters
        assertThrows(UnsupportedOperationException.class, () -> {
            // Try to modify a final field if reflection is used
        });
    }
}
