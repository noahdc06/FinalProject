package com.university.grocerystorecodes.datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.EmptyStackException;

public class DataStructureTests {

    // ============ BST TESTS ============

    @Test
    void testBST_InsertAndContains() {
        BinarySearchTree<String> catalog = new BinarySearchTree<>();

        catalog.add("Milk");
        catalog.add("Bread");
        catalog.add("Eggs");

        assertEquals(3, catalog.size());
        assertTrue(catalog.contains("Milk"));
        assertFalse(catalog.contains("Butter"));
    }

    @Test
    void testBST_Sorting() {
        BinarySearchTree<String> catalog = new BinarySearchTree<>();

        // Add in random order
        catalog.add("Zucchini");
        catalog.add("Apple");
        catalog.add("Banana");

        List<String> sorted = catalog.getAllSorted();

        // Should be alphabetically sorted
        assertEquals("Apple", sorted.get(0));
        assertEquals("Banana", sorted.get(1));
        assertEquals("Zucchini", sorted.get(2));
    }

    @Test
    void testBST_EmptyAndClear() {
        BinarySearchTree<String> catalog = new BinarySearchTree<>();

        assertTrue(catalog.isEmpty());

        catalog.add("Product");
        assertFalse(catalog.isEmpty());

        catalog.clear();
        assertTrue(catalog.isEmpty());
        assertEquals(0, catalog.size());
    }

    @Test
    void testBST_Duplicates() {
        BinarySearchTree<String> catalog = new BinarySearchTree<>();

        catalog.add("Milk");
        catalog.add("Milk");
        catalog.add("Milk");

        // Should count only once
        assertEquals(1, catalog.size());
    }

    // ============ QUEUE TESTS ============

    @Test
    void testQueue_FIFO() {
        Queue<String> line = new Queue<>();

        line.enqueue("Alice");
        line.enqueue("Bob");
        line.enqueue("Charlie");

        assertEquals(3, line.size());
        assertEquals("Alice", line.dequeue()); // First in, first out
        assertEquals("Bob", line.dequeue());
        assertEquals("Charlie", line.dequeue());
        assertTrue(line.isEmpty());
    }

    @Test
    void testQueue_Peek() {
        Queue<String> line = new Queue<>();

        line.enqueue("Customer");

        assertEquals("Customer", line.peek());
        assertEquals(1, line.size()); // Size unchanged after peek
    }

    @Test
    void testQueue_EmptyException() {
        Queue<String> line = new Queue<>();

        assertThrows(NoSuchElementException.class, () -> line.dequeue());
        assertThrows(NoSuchElementException.class, () -> line.peek());
    }

    @Test
    void testQueue_SingleCustomer() {
        Queue<String> line = new Queue<>();

        line.enqueue("OnlyCustomer");
        assertEquals("OnlyCustomer", line.dequeue());
        assertTrue(line.isEmpty());
    }

    // ============ STACK TESTS ============

    @Test
    void testStack_LIFO() {
        Stack<String> orders = new Stack<>();

        orders.push("Order1");
        orders.push("Order2");
        orders.push("Order3");

        assertEquals(3, orders.size());
        assertEquals("Order3", orders.pop()); // Last in, first out
        assertEquals("Order2", orders.pop());
        assertEquals("Order1", orders.pop());
        assertTrue(orders.isEmpty());
    }

    @Test
    void testStack_Peek() {
        Stack<String> orders = new Stack<>();

        orders.push("RecentOrder");

        assertEquals("RecentOrder", orders.peek());
        assertEquals(1, orders.size()); // Size unchanged after peek
    }

    @Test
    void testStack_EmptyException() {
        Stack<String> orders = new Stack<>();

        assertThrows(EmptyStackException.class, () -> orders.pop());
        assertThrows(EmptyStackException.class, () -> orders.peek());
    }

    @Test
    void testStack_SingleOrder() {
        Stack<String> orders = new Stack<>();

        orders.push("SingleOrder");
        assertEquals("SingleOrder", orders.pop());
        assertTrue(orders.isEmpty());
    }

    // ============ INTEGRATION TEST ============

    @Test
    void testIntegration_GroceryStoreWorkflow() {
        // 1. Setup catalog
        BinarySearchTree<String> catalog = new BinarySearchTree<>();
        catalog.add("Milk");
        catalog.add("Bread");
        catalog.add("Eggs");

        assertTrue(catalog.contains("Milk"));
        assertEquals(3, catalog.size());

        // 2. Customer service line
        Queue<String> serviceLine = new Queue<>();
        serviceLine.enqueue("Customer1");
        serviceLine.enqueue("Customer2");

        assertEquals("Customer1", serviceLine.dequeue());
        assertEquals(1, serviceLine.size());

        // 3. Order processing
        Stack<String> recentOrders = new Stack<>();
        recentOrders.push("Order100");
        recentOrders.push("Order101");

        assertEquals("Order101", recentOrders.pop());
        assertEquals(1, recentOrders.size());

        // 4. Verify all still work
        catalog.add("Butter");
        assertEquals(4, catalog.size());

        serviceLine.enqueue("Customer3");
        assertEquals("Customer2", serviceLine.dequeue());

        recentOrders.push("Order102");
        assertEquals("Order102", recentOrders.pop());
    }

    @Test
    void testIntegration_LargeScale() {
        // Test with many items
        BinarySearchTree<Integer> catalog = new BinarySearchTree<>();
        Queue<Integer> line = new Queue<>();
        Stack<Integer> orders = new Stack<>();

        // Add 100 items to each
        for (int i = 1; i <= 100; i++) {
            catalog.add(i);
            line.enqueue(i);
            orders.push(i);
        }

        assertEquals(100, catalog.size());
        assertEquals(100, line.size());
        assertEquals(100, orders.size());

        // Process some
        for (int i = 1; i <= 50; i++) {
            assertEquals(i, line.dequeue());
        }

        for (int i = 100; i > 50; i--) {
            assertEquals(i, orders.pop());
        }

        assertEquals(50, line.size());
        assertEquals(50, orders.size());
    }
}