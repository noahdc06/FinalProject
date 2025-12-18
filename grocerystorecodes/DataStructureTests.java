package com.university.grocerystorecodes.datastructures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataStructureTests {

    @Test
    void testBSTForProducts() {
        BinarySearchTree<String> catalog = new BinarySearchTree<>();

        catalog.add("Apple");
        catalog.add("Banana");
        catalog.add("Carrot");

        assertTrue(catalog.contains("Banana"));
        assertFalse(catalog.contains("Durian"));
        assertEquals(3, catalog.size());

        // Products should be sorted alphabetically
        assertEquals("[Apple, Banana, Carrot]", catalog.getAllSorted().toString());
    }

    @Test
    void testQueueForCustomerLine() {
        Queue<String> serviceLine = new Queue<>();

        serviceLine.enqueue("Alice");
        serviceLine.enqueue("Bob");
        serviceLine.enqueue("Charlie");

        assertEquals(3, serviceLine.size());
        assertEquals("Alice", serviceLine.dequeue()); // First come, first served
        assertEquals("Bob", serviceLine.peek());
        assertEquals("Bob", serviceLine.dequeue());
        assertEquals("Charlie", serviceLine.dequeue());
        assertTrue(serviceLine.isEmpty());
    }

    @Test
    void testStackForOrders() {
        Stack<String> recentOrders = new Stack<>();

        recentOrders.push("Order #1001");
        recentOrders.push("Order #1002");
        recentOrders.push("Order #1003");

        assertEquals(3, recentOrders.size());
        assertEquals("Order #1003", recentOrders.pop()); // Last in, first out
        assertEquals("Order #1002", recentOrders.pop());
        assertEquals("Order #1001", recentOrders.pop());
        assertTrue(recentOrders.isEmpty());
    }

    @Test
    void testEmptyQueueException() {
        Queue<String> queue = new Queue<>();
        assertThrows(NoSuchElementException.class, () -> queue.dequeue());
    }

    @Test
    void testEmptyStackException() {
        Stack<String> stack = new Stack<>();
        assertThrows(EmptyStackException.class, () -> stack.pop());
    }
}