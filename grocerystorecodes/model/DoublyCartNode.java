package com.university.grocerystore.model;


/**
 * Node for linked list implementation of shopping cart.
 * Demonstrates fundamental linked list data structure principles.
 * 
 * Time Complexity Analysis:
 * - Insertion: O(1) at head/tail (with tail reference)
 * - Deletion: O(n) for arbitrary nodes (requires traversal)
 * - Search: O(n) worst-case (linear traversal)
 * 
 * Space Complexity: O(n) where n = number of items in cart
 * 
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Doubly linked list node for shopping cart containing grocery products.
 * Each node holds a product with quantity and maintains references to adjacent nodes.
 */
public class DoublyCartNode {
    private final GroceryProduct product;
    private DoublyCartNode next;
    private DoublyCartNode prev;
    private int quantity;
    
    /**
     * Constructs a new cart node with specified product and quantity.
     * @param product The grocery product to store in this node
     * @param quantity The quantity of the product (must be positive)
     * @throws IllegalArgumentException if product is null or quantity ≤ 0
     */
    public DoublyCartNode(GroceryProduct product, int quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid product or quantity");
        }
        this.product = product;
        this.quantity = quantity;
        this.next = null;
        this.prev = null;
    }
    
    /**
     * Constructs a new cart node with product (default quantity = 1).
     * @param product The grocery product to store in this node
     */
    public DoublyCartNode(GroceryProduct product) {
        this(product, 1);
    }
    
    /**
     * @return The grocery product stored in this node
     */
    public GroceryProduct getProduct() { 
        return product; 
    }
    
    /**
     * @return The next node in the linked list
     */
    public DoublyCartNode getNext() { 
        return next; 
    }
    
    /**
     * @param next The node to set as the next node
     */
    public void setNext(DoublyCartNode next) { 
        this.next = next; 
    }
    
    /**
     * @return The previous node in the linked list
     */
    public DoublyCartNode getPrev() { 
        return prev; 
    }
    
    /**
     * @param prev The node to set as the previous node
     */
    public void setPrev(DoublyCartNode prev) { 
        this.prev = prev; 
    }
    
  
    /**
     * Inserts a new node after this node in the linked list.
     * @param newNode The node to insert after this node
     */
    public void insertAfter(DoublyCartNode newNode) {
        if (newNode == null) return;
        DoublyCartNode oldNext = this.next;
        this.next = newNode;
        newNode.prev = this;
        if (oldNext != null) {
            newNode.next = oldNext;
            oldNext.prev = newNode;
        }
    }
}

