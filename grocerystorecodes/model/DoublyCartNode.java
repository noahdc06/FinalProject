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
   

public class DoublyCartNode {
    private final GroceryProduct product;
    private DoublyCartNode next;
    private DoublyCartNode prev;
    private int quantity;
    
    public DoublyCartNode(GroceryProduct product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public GroceryProduct getProduct() { 
        return product; }
    public DoublyCartNode getNext() { 
        return next; }
    public void setNext(DoublyCartNode next) { 
        this.next = next; }
    public DoublyCartNode getPrev() { 
        return prev; }
    public void setPrev(DoublyCartNode prev) {
        this.prev = prev; }
    public int getQuantity() { 
        return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity; }
    
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
    
    @Override
    public String toString() {
        return product.getName() + " x" + quantity;
    }
}
