package com.university.grocerystore.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Doubly linked list implementation for a shopping cart containing grocery products.
 * Supports efficient insertion/removal from both ends and bidirectional traversal.
 */
public class DoublyLinkedListCart implements Iterable<DoublyCartNode> {
    private DoublyCartNode head;
    private DoublyCartNode tail;
    private int size;
    
    /**
     * Constructs an empty shopping cart.
     */
    public DoublyLinkedListCart() {
        head = tail = null;
        size = 0;
    }
    
    /**
     * Adds a product to the beginning of the cart with default quantity (1).
     * @param product The grocery product to add
     */
    public void addFirst(GroceryProduct product) {
        addFirst(product, 1);
    }
    
    /**
     * Adds a product to the beginning of the cart with specified quantity.
     * @param product The grocery product to add
     * @param quantity The quantity of the product (must be positive)
     * @throws IllegalArgumentException if quantity <= 0
     */
    public void addFirst(GroceryProduct product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
        DoublyCartNode newNode = new DoublyCartNode(product, quantity);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
        }
        size++;
    }
    
    /**
     * Adds a product to the end of the cart with default quantity (1).
     * @param product The grocery product to add
     */
    public void addLast(GroceryProduct product) {
        addLast(product, 1);
    }
    
    /**
     * Adds a product to the end of the cart with specified quantity.
     * @param product The grocery product to add
     * @param quantity The quantity of the product (must be positive)
     * @throws IllegalArgumentException if quantity <= 0
     */
    public void addLast(GroceryProduct product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
        DoublyCartNode newNode = new DoublyCartNode(product, quantity);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        size++;
    }
    
    /**
     * Removes and returns the first product in the cart.
     * @return The removed grocery product, or null if cart is empty
     */
    public GroceryProduct removeFirst() {
        if (head == null) return null;
        GroceryProduct product = head.getProduct();
        removeNode(head);
        return product;
    }
    
    /**
     * Removes and returns the last product in the cart.
     * @return The removed grocery product, or null if cart is empty
     */
    public GroceryProduct removeLast() {
        if (tail == null) return null;
        GroceryProduct product = tail.getProduct();
        removeNode(tail);
        return product;
    }
    
    /**
     * Removes the product with the specified ID from the cart.
     * @param productId The ID of the product to remove
     * @return The removed grocery product, or null if not found
     */
    public GroceryProduct remove(String productId) {
        DoublyCartNode node = findNode(productId);
        if (node == null) return null;
        GroceryProduct product = node.getProduct();
        removeNode(node);
        return product;
    }
    
    /**
     * Finds a node containing a product with the specified ID.
     * @param productId The ID of the product to find
     * @return The node containing the product, or null if not found
     */
    private DoublyCartNode findNode(String productId) {
        DoublyCartNode current = head;
        while (current != null) {
            if (current.getProduct().getId().equals(productId)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }
    
    /**
     * Removes a specific node from the linked list.
     * @param node The node to remove
     * @return true if the node was successfully removed, false otherwise
     */
    private boolean removeNode(DoublyCartNode node) {
        if (node == null) return false;
        
        // Update previous node's next pointer
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            // Node is head, update head reference
            head = node.getNext();
        }
        
        // Update next node's previous pointer
        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            // Node is tail, update tail reference
            tail = node.getPrev();
        }
        
        // Clear node's references to help garbage collection
        node.setNext(null);
        node.setPrev(null);
        size--;
        return true;
    }
    
    /**
     * Gets the first node in the cart.
     * @return The first node, or null if cart is empty
     */
    public DoublyCartNode getFirst() { 
        return head; 
    }
    
    /**
     * Gets the last node in the cart.
     * @return The last node, or null if cart is empty
     */
    public DoublyCartNode getLast() { 
        return tail; 
    }
    
    /**
     * Gets the number of items in the cart.
     * @return The size of the cart
     */
    public int size() { 
        return size; 
    }
    
    /**
     * Checks if the cart is empty.
     * @return true if the cart is empty, false otherwise
     */
    public boolean isEmpty() { 
        return size == 0; 
    }
    
    /**
     * Checks if the cart contains a product with the specified ID.
     * @param productId The ID of the product to search for
     * @return true if the cart contains the product, false otherwise
     */
    public boolean contains(String productId) {
        return findNode(productId) != null;
    }
    
    /**
     * Removes all items from the cart.
     */
    public void clear() {
        // Clear all node references to help garbage collection
        DoublyCartNode current = head;
        while (current != null) {
            DoublyCartNode next = current.getNext();
            current.setNext(null);
            current.setPrev(null);
            current = next;
        }
        head = tail = null;
        size = 0;
    }
    
    /**
     * Returns an iterator over the nodes in the cart from first to last.
     * @return An iterator for forward traversal of the cart
     */
    @Override
    public Iterator<DoublyCartNode> iterator() {
        return new Iterator<DoublyCartNode>() {
            private DoublyCartNode current = head;
            
            @Override
            public boolean hasNext() {
                return current != null;
            }
            
            @Override
            public DoublyCartNode next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements in cart");
                }
                DoublyCartNode node = current;
                current = current.getNext();
                return node;
            }
        }
    }
}
