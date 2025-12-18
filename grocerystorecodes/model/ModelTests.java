package com.university.grocerystorecodes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Iterator;

class ModelTests {
    
    // ============ GroceryProduct Hierarchy Tests ============
    
    @Test
    void testGroceryProduct_AbstractClass() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Campbell's", 2.99, 2024, 12, "SOUP");
        assertNotNull(product);
        assertEquals("P001", product.getId());
        assertEquals("Soup", product.getName());
        assertEquals(2.99, product.getPrice(), 0.001);
    }
    
    @Test
    void testCannedGood_Creation() {
        CannedGood canned = new CannedGood("C001", "Beans", "Heinz", 1.49, 2024, 24, "VEGETABLES");
        assertEquals("C001", canned.getId());
        assertEquals("Beans", canned.getName());
        assertEquals("VEGETABLES", canned.getType());
        assertEquals(24, canned.getShelfLifeMonths());
    }
    
    @Test
    void testFrozenFood_Creation() {
        FrozenFood frozen = new FrozenFood("F001", "Pizza", "DiGiorno", 8.99, 2024, 6, "PIZZA");
        assertEquals("F001", frozen.getId());
        assertEquals("PIZZA", frozen.getType());
        assertTrue(frozen instanceof Perishable);
    }
    
    @Test
    void testProduce_Creation() {
        Produce produce = new Produce("V001", "Apples", "Organic", 3.99, 2024, 7, "FRUIT");
        assertEquals("V001", produce.getId());
        assertEquals("FRUIT", produce.getType());
        assertTrue(produce instanceof Perishable);
    }
    
    @Test
    void testSnack_Creation() {
        Snack snack = new Snack("S001", "Chips", "Lays", 1.99, 2024, 9, "CHIPS");
        assertEquals("S001", snack.getId());
        assertEquals("CHIPS", snack.getType());
        assertEquals(9, snack.getShelfLifeMonths());
    }
    
    @Test
    void testPerishable_Interface() {
        FrozenFood frozen = new FrozenFood("F001", "Ice Cream", "Ben & Jerry's", 5.99, 2024, 3, "DESSERT");
        Produce produce = new Produce("V001", "Bananas", "Chiquita", 0.99, 2024, 5, "FRUIT");
        
        assertTrue(frozen.isPerishable());
        assertTrue(produce.isPerishable());
        
        assertTrue(frozen.getExpirationYear() > 2024);
        assertTrue(produce.getExpirationYear() > 2024);
    }
    
    // ============ GroceryItem Tests ============
    
    @Test
    void testGroceryItem_Creation() {
        GroceryItem item = new GroceryItem("I001", "Milk", "DairyCo", 2.99, 2024, 2025);
        assertEquals("I001", item.getSku());
        assertEquals("Milk", item.getName());
        assertEquals(2.99, item.getPrice(), 0.001);
        assertEquals(2024, item.getProductionYear());
        assertEquals(2025, item.getExpirationYear());
    }
    
    @Test
    void testGroceryItem_Expired() {
        GroceryItem expired = new GroceryItem("E001", "Old Milk", "Brand", 1.99, 2020, 2021);
        GroceryItem fresh = new GroceryItem("F001", "Fresh Milk", "Brand", 2.99, 2024, 2025);
        
        assertTrue(expired.isExpired());
        assertFalse(fresh.isExpired());
    }
    
    @Test
    void testGroceryItem_ShelfLife() {
        GroceryItem item = new GroceryItem("L001", "Cereal", "Brand", 3.99, 2023, 2025);
        assertEquals(2, item.getShelfLifeYears());
    }
    
    // ============ DoublyCartNode Tests ============
    
    @Test
    void testDoublyCartNode_Creation() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 2.99, 2024, 12, "SOUP");
        DoublyCartNode node = new DoublyCartNode(product, 3);
        
        assertEquals(product, node.getProduct());
        assertEquals(3, node.getQuantity());
        assertNull(node.getNext());
        assertNull(node.getPrev());
    }
    
    @Test
    void testDoublyCartNode_DefaultQuantity() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 2.99, 2024, 12, "SOUP");
        DoublyCartNode node = new DoublyCartNode(product);
        
        assertEquals(1, node.getQuantity());
    }
    
    @Test
    void testDoublyCartNode_InvalidCreation() {
        GroceryProduct product = new CannedGood("P001", "Soup", "Brand", 2.99, 2024, 12, "SOUP");
        
        assertThrows(IllegalArgumentException.class, () -> new DoublyCartNode(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new DoublyCartNode(product, 0));
        assertThrows(IllegalArgumentException.class, () -> new DoublyCartNode(product, -1));
    }
    
    @Test
    void testDoublyCartNode_InsertAfter() {
        GroceryProduct p1 = new CannedGood("P1", "Item1", "Brand", 1.99, 2024, 12, "TYPE");
        GroceryProduct p2 = new CannedGood("P2", "Item2", "Brand", 2.99, 2024, 12, "TYPE");
        GroceryProduct p3 = new CannedGood("P3", "Item3", "Brand", 3.99, 2024, 12, "TYPE");
        
        DoublyCartNode node1 = new DoublyCartNode(p1);
        DoublyCartNode node2 = new DoublyCartNode(p2);
        DoublyCartNode node3 = new DoublyCartNode(p3);
        
        node1.insertAfter(node2);
        node2.insertAfter(node3);
        
        assertEquals(node2, node1.getNext());
        assertEquals(node1, node2.getPrev());
        assertEquals(node3, node2.getNext());
        assertEquals(node2, node3.getPrev());
    }
    
    // ============ DoublyLinkedListCart Tests ============
    
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
        cart.addFirst(p2, 2);
        
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
        cart.addLast(p2, 3);
        
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
    void testDoublyLinkedListCart_IteratorEmpty() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        int count = 0;
        for (DoublyCartNode node : cart) {
            count++;
        }
        assertEquals(0, count);
    }
    
    // ============ Edge Cases ============
    
    @Test
    void testRemoveFromEmptyCart() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        
        assertNull(cart.removeFirst());
        assertNull(cart.removeLast());
        assertNull(cart.remove("INVALID"));
    }
    
    @Test
    void testRemoveNonExistentId() {
        DoublyLinkedListCart cart = new DoublyLinkedListCart();
        GroceryProduct p1 = new CannedGood("P1", "Item1", "Brand", 1.99, 2024, 12, "TYPE");
        cart.addLast(p1);
        
        assertNull(cart.remove("NONEXISTENT"));
        assertEquals(1, cart.size());
    }
}V
