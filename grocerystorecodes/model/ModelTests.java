package com.university.grocerystore.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Year;
import java.util.Iterator;

class ModelTests {
    
    @Test
    void testGroceryProduct_AbstractClass() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Campbell's", 2.99, 2024, 12, "SOUP");
        assertNotNull(product);
        assertEquals("P001", product.getId());
        assertEquals("Soup", product.getName());
        assertEquals(2.99, product.getPrice(), 0.001);
        assertEquals(GroceryProduct.ProductType.CANNED_GOOD, product.getType());
    }
    
    @Test
    void testCannedGood_Creation() {
        CannedGood canned = new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 12, "VEGETABLES");
        assertEquals("C001", canned.getId());
        assertEquals("Beans", canned.getName());
        assertEquals("Heinz", canned.getBrand());
    }
    
    @Test
    void testFrozenFood_Creation() {
        FrozenFood frozen = new FrozenFood("F001", "Pizza", "DiGiorno", 8.99, 2024, 
            "FREEZER", 500.0, false, 300, Perishable.ShelfLifeQuality.HIGH);
        assertEquals("F001", frozen.getId());
        assertEquals("Pizza", frozen.getName());
        assertEquals("DiGiorno", frozen.getBrand());
        assertEquals(Perishable.ShelfLifeQuality.HIGH, frozen.getShelfLifeQuality());
    }
    
    @Test
    void testProduce_Creation() {
        Produce produce = new Produce("V001", "Apples", "Organic Farms", 3.99, 2024, 
            "Gala", 1000.0, true, "USA", Perishable.ShelfLifeQuality.MEDIUM);
        assertEquals("V001", produce.getId());
        assertEquals("Apples", produce.getName());
        assertEquals("Organic Farms", produce.getBrand());
        assertTrue(produce.isOrganic());
    }
    
    @Test
    void testSnack_Creation() {
        Snack snack = new Snack("123456789012", "Chips", "Lays", 1.99, 2024, 
            "CHIPS", 200.0, 150, "BBQ", "No restrictions");
        assertEquals("123456789012", snack.getId());
        assertEquals("Chips", snack.getName());
        assertEquals("Lays", snack.getBrand());
        assertEquals("BBQ", snack.getFlavor());
    }
    
    @Test
    void testGroceryItem_Creation() {
        GroceryItem item = new GroceryItem("I001", "Milk", "DairyCo", 2.99, 2024, 2025);
        assertEquals("I001", item.getSku());
        assertEquals("Milk", item.getName());
        assertEquals("DairyCo", item.getBrand());
        assertEquals(2.99, item.getPrice(), 0.001);
        assertEquals(2024, item.getProductionYear());
        assertEquals(2025, item.getExpirationYear());
    }
    
    @Test
    void testGroceryItem_Expired() {
        int currentYear = Year.now().getValue();
        GroceryItem expired = new GroceryItem("E001", "Old Milk", "Brand", 1.99, 2020, 2021);
        GroceryItem fresh = new GroceryItem("F001", "Fresh Milk", "Brand", 2.99, currentYear, currentYear + 1);
        
        assertTrue(expired.isExpired());
        assertFalse(fresh.isExpired());
    }
    
    @Test
    void testGroceryItem_ShelfLife() {
        GroceryItem item = new GroceryItem("L001", "Cereal", "Brand", 3.99, 2023, 2025);
        assertEquals(2, item.getShelfLifeYears());
    }
    
    @Test
    void testDoublyCartNode_Creation() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 2.99, 2024, 12, "SOUP");
        DoublyCartNode node = new DoublyCartNode(product, 3);
        
        assertEquals(product, node.getProduct());
        assertNull(node.getNext());
        assertNull(node.getPrev());
    }
    
    @Test
    void testDoublyCartNode_DefaultQuantity() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 2.99, 2024, 12, "SOUP");
        DoublyCartNode node = new DoublyCartNode(product);
        
        assertNotNull(node.getProduct());
    }
    
    @Test
    void testDoublyCartNode_InvalidCreation() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 2.99, 2024, 12, "SOUP");
        
        assertThrows(IllegalArgumentException.class, () -> new DoublyCartNode(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new DoublyCartNode(product, 0));
    }
    
    @Test
    void testDoublyCartNode_InsertAfter() {
        GroceryProduct p1 = new CannedGood("P1", "Item1", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Item2", "Brand", 2.99, 2024, 12, "TYPE");
        
        DoublyCartNode node1 = new DoublyCartNode(p1);
        DoublyCartNode node2 = new DoublyCartNode(p2);
        
        node1.insertAfter(node2);
        
        assertEquals(node2, node1.getNext());
        assertEquals(node1, node2.getPrev());
    }
    
    @Test
    void testDoublyLinkedListCart_Empty() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.size());
        assertNull(cart.getFirst());
        assertNull(cart.getLast());
    }
    
    @Test
    void testDoublyLinkedListCart_AddFirst() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "First", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Second", "Brand", 2.99, 2024, 12, "TYPE");
        
        cart.addFirst(p1);
        cart.addFirst(p2);
        
        assertEquals(2, cart.size());
        assertEquals("Second", cart.getFirst().getProduct().getName());
        assertEquals("First", cart.getLast().getProduct().getName());
    }
    
    @Test
    void testDoublyLinkedListCart_AddLast() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "First", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Second", "Brand", 2.99, 2024, 12, "TYPE");
        
        cart.addLast(p1);
        cart.addLast(p2);
        
        assertEquals(2, cart.size());
        assertEquals("First", cart.getFirst().getProduct().getName());
        assertEquals("Second", cart.getLast().getProduct().getName());
    }
    
    @Test
    void testDoublyLinkedListCart_RemoveFirst() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "First", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Second", "Brand", 2.99, 2024, 12, "TYPE");
        
        cart.addLast(p1);
        cart.addLast(p2);
        
        GroceryProduct removed = cart.removeFirst();
        assertEquals("First", removed.getName());
        assertEquals(1, cart.size());
        assertEquals("Second", cart.getFirst().getProduct().getName());
    }
    
    @Test
    void testDoublyLinkedListCart_RemoveLast() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "First", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Second", "Brand", 2.99, 2024, 12, "TYPE");
        
        cart.addLast(p1);
        cart.addLast(p2);
        
        GroceryProduct removed = cart.removeLast();
        assertEquals("Second", removed.getName());
        assertEquals(1, cart.size());
        assertEquals("First", cart.getLast().getProduct().getName());
    }
    
    @Test
    void testDoublyLinkedListCart_RemoveById() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "Item1", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Item2", "Brand", 2.99, 2024, 12, "TYPE");
        GroceryProduct p3 = new CannedGood("P3", "Item3", "Brand", 3.99, 2024, 12, "TYPE");
        
        cart.addLast(p1);
        cart.addLast(p2);
        cart.addLast(p3);
        
        GroceryProduct removed = cart.remove("P2");
        assertEquals("Item2", removed.getName());
        assertEquals(2, cart.size());
        assertFalse(cart.contains("P2"));
        assertTrue(cart.contains("P1"));
        assertTrue(cart.contains("P3"));
    }
    
    @Test
    void testDoublyLinkedListCart_Contains() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "Item1", "Brand", 1.99, 2024, 12, "TYPE");
        
        cart.addLast(p1);
        
        assertTrue(cart.contains("P1"));
        assertFalse(cart.contains("P999"));
    }
    
    @Test
    void testDoublyLinkedListCart_Clear() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "Item1", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Item2", "Brand", 2.99, 2024, 12, "TYPE");
        
        cart.addLast(p1);
        cart.addLast(p2);
        
        assertEquals(2, cart.size());
        cart.clear();
        assertEquals(0, cart.size());
        assertTrue(cart.isEmpty());
        assertNull(cart.getFirst());
        assertNull(cart.getLast());
    }
    
    @Test
    void testDoublyLinkedListCart_Iterator() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "Item1", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Item2", "Brand", 2.99, 2024, 12, "TYPE");
        
        cart.addLast(p1);
        cart.addLast(p2);
        
        int count = 0;
        for (DoublyCartNode node : cart) {
            assertNotNull(node);
            count++;
        }
        assertEquals(2, count);
    }
    
    @Test
    void testPerishable_InterfaceMethods() {
        FrozenFood frozen = new FrozenFood("F001", "Ice Cream", "Ben & Jerry's", 5.99, 2024, 
            "FREEZER", 500.0, false, 250, Perishable.ShelfLifeQuality.HIGH);
        
        assertTrue(frozen.getShelfLifeDays() > 0);
        assertEquals(Perishable.ShelfLifeQuality.HIGH, frozen.getShelfLifeQuality());
        assertNotNull(frozen.getStorageRecommendation());
    }
    
    @Test
    void testGroceryItem_Comparable() {
        GroceryItem item1 = new GroceryItem("A001", "Apples", "Brand", 2.99, 2024, 2025);
        GroceryItem item2 = new GroceryItem("B001", "Bananas", "Brand", 1.99, 2024, 2025);
        GroceryItem item3 = new GroceryItem("A002", "Apples", "OtherBrand", 3.99, 2024, 2025);
        
        assertTrue(item1.compareTo(item2) < 0);
        assertTrue(item2.compareTo(item1) > 0);
        assertEquals(0, item1.compareTo(item3));
    }
    
    @Test
    void testRemoveFromEmptyCart() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        assertNull(cart.removeFirst());
        assertNull(cart.removeLast());
        assertNull(cart.remove("INVALID"));
    }
    
    @Test
    void testInvalidGroceryItem_SKU() {
        assertThrows(IllegalArgumentException.class, () -> 
            new GroceryItem("AB", "Milk", "Brand", 2.99, 2024, 2025));
    }
    
    @Test
    void testInvalidGroceryItem_Price() {
        assertThrows(IllegalArgumentException.class, () -> 
            new GroceryItem("ABC123", "Milk", "Brand", -1.99, 2024, 2025));
    }
    
    @Test
    void testInvalidGroceryItem_Year() {
        assertThrows(IllegalArgumentException.class, () -> 
            new GroceryItem("ABC123", "Milk", "Brand", 2.99, 1800, 2025));
    }
    
    @Test
    void testGroceryProduct_DiscountedPrice() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 10.00, 2024, 12, "SOUP");
        double discounted = product.getDiscountedPrice();
        assertTrue(discounted <= 10.00);
    }
    
    @Test
    void testProduce_OrganicDiscount() {
        Produce organic = new Produce("O001", "Carrots", "Organic Co", 3.99, 2024, 
            "Standard", 500.0, true, "USA", Perishable.ShelfLifeQuality.HIGH);
        Produce regular = new Produce("R001", "Carrots", "Regular Co", 3.99, 2024, 
            "Standard", 500.0, false, "USA", Perishable.ShelfLifeQuality.HIGH);
        
        assertNotEquals(organic.getDiscountRate(), regular.getDiscountRate());
    }
}
