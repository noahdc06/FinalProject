package com.university.grocerystore.model;

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
     */
    public DoublyCartNode(GroceryProduct product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
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
