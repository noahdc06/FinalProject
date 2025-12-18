package com.university.grocerystorecodes.model;  

import com.university.grocerystorecodes.model.GroceryProduct;

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
public class DoublyCartNode {
    private final GroceryProduct product;
    private DoublyCartNode next;
    private DoublyCartNode prev;
    private int quantity;
    
    public DoublyCartNode(GroceryProduct product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.product = product;
        this.quantity = quantity;
        this.next = null;
        this.prev = null;
    }
    
    public DoublyCartNode(GroceryProduct product) {
        this(product, 1);
    }
    
    // ============ GETTERS AND SETTERS ============
    
    public GroceryProduct getProduct() { 
        return product; 
    }
    
    public DoublyCartNode getNext() { 
        return next; 
    }
    
    public void setNext(DoublyCartNode next) { 
        this.next = next; 
    }
    
    public DoublyCartNode getPrev() { 
        return prev; 
    }
    
    public void setPrev(DoublyCartNode prev) { 
        this.prev = prev; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(int quantity) { 
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity; 
    }
    
    // ============ QUANTITY OPERATIONS ============
    
    public void incrementQuantity(int amount) {
        this.quantity += amount;
    }
    
    public boolean decrementQuantity(int amount) {
        if (this.quantity - amount <= 0) {
            return false; // Signal that node should be removed
        }
        this.quantity -= amount;
        return true;
    }
    
    // ============ PRICE CALCULATIONS ============
    
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
    
    public double getUnitPrice() {
        return product.getPrice();
    }
    
    // ============ NAVIGATION HELPERS ============
    
    public boolean hasNext() { 
        return next != null; 
    }
    
    public boolean hasPrev() { 
        return prev != null; 
    }
    
    public boolean isFirst() { 
        return prev == null; 
    }
    
    public boolean isLast() { 
        return next == null; 
    }
    
    public boolean isMiddle() { 
        return prev != null && next != null; 
    }
    
    // ============ NODE MANIPULATION ============
    
    /**
     * Inserts a node after this node.
     * @param newNode node to insert
     */
    public void insertAfter(DoublyCartNode newNode) {
        if (newNode == null) return;
        
        // Save reference to current next
        DoublyCartNode oldNext = this.next;
        
        // Link this node to new node
        this.next = newNode;
        newNode.prev = this;
        
        // Link new node to old next
        if (oldNext != null) {
            newNode.next = oldNext;
            oldNext.prev = newNode;
        }
    }
    
    /**
     * Inserts a node before this node.
     * @param newNode node to insert
     */
    public void insertBefore(DoublyCartNode newNode) {
        if (newNode == null) return;
        
        // Save reference to current prev
        DoublyCartNode oldPrev = this.prev;
        
        // Link new node to this node
        newNode.next = this;
        this.prev = newNode;
        
        // Link old prev to new node
        if (oldPrev != null) {
            oldPrev.next = newNode;
            newNode.prev = oldPrev;
        }
    }
    
    /**
     * Removes this node from the list.
     * @return the product from removed node
     */
    public GroceryProduct remove() {
        // Link previous node to next node
        if (prev != null) {
            prev.next = next;
        }
        
        // Link next node to previous node
        if (next != null) {
            next.prev = prev;
        }
        
        // Clear this node's references
        next = null;
        prev = null;
        
        return product;
    }
    
    // ============ OBJECT METHODS ============
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DoublyCartNode)) return false;
        
        DoublyCartNode other = (DoublyCartNode) obj;
        return this.product.equals(other.product) && 
               this.quantity == other.quantity;
    }
    
    @Override
    public int hashCode() {
        return 31 * product.hashCode() + quantity;
    }
    
    @Override
    public String toString() {
        return String.format("DoublyCartNode[%s x%d = $%.2f]", 
            product.getName(), quantity, getTotalPrice());
    }
    
}
