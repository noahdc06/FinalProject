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
public class ShoppingCartNode {
    private final GroceryProduct product;
    private ShoppingCartNode next;
    private int quantity;
    
    public ShoppingCartNode(GroceryProduct product) {
        this(product, 1);
    }
    
    public ShoppingCartNode(GroceryProduct product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.product = product;
        this.quantity = quantity;
        this.next = null;
    }
    
    // Getters and setters
    public GroceryProduct getProduct() { return product; }
    public ShoppingCartNode getNext() { return next; }
    public void setNext(ShoppingCartNode next) { this.next = next; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = quantity; 
    }
    
    public void incrementQuantity(int amount) {
        this.quantity += amount;
    }
    
    public void decrementQuantity(int amount) {
        if (this.quantity - amount <= 0) {
            throw new IllegalStateException("Quantity would become non-positive");
        }
        this.quantity -= amount;
    }
    
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
    
    @Override
    public String toString() {
        return String.format("ShoppingCartNode[%s x%d = $%.2f]", 
            product.getName(), quantity, getTotalPrice());
    }

}

