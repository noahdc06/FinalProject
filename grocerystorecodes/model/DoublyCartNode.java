package com.university.grocerystorecodes.model;


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

public class DoublyCartNode {
    private final GroceryProduct product;
    private DoublyCartNode next;
    private DoublyCartNode prev;
    private int quantity;
    
    /**
     * @param product The grocery product
     * @param quantity The quantity
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
     * @param product The grocery product
     */
    public DoublyCartNode(GroceryProduct product) {
        this(product, 1);
    }
    
    /**
     * @return The grocery product
     */
    public GroceryProduct getProduct() { 
        return product; 
    }
    
    /**
     * @return The next node
     */
    public DoublyCartNode getNext() { 
        return next; 
    }
    
    /**
     * @param next The next node
     */
    public void setNext(DoublyCartNode next) { 
        this.next = next; 
    }
    
    /**
     * @return The previous node
     */
    public DoublyCartNode getPrev() { 
        return prev; 
    }
    
    /**
     * @param prev The previous node
     */
    public void setPrev(DoublyCartNode prev) { 
        this.prev = prev; 
    }
    
    /**
     * @return The quantity
     */
    public int getQuantity() { 
        return quantity; 
    }
    
    /**
     * @param quantity The new quantity
     */
    public void setQuantity(int quantity) { 
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = quantity; 
    }
    
    /**
     * @return Total price
     */
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
    
    /**
     * @param newNode The node to insert
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
