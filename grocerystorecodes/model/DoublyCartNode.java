package com.university.grocerystore.model;

/**
 * Doubly linked list node for shopping cart containing grocery products.
 * Each node holds a product with quantity and maintains references to adjacent nodes.
 * 
 * Time Complexity Analysis:
 * - Insertion: O(1) at head/tail (with tail reference)
 * - Deletion: O(n) for arbitrary nodes (requires traversal)
 * - Search: O(n) worst-case (linear traversal)
 * 
 * Space Complexity: O(n) where n = number of items in cart
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
     * @throws IllegalArgumentException if product is null
     */
    public DoublyCartNode(GroceryProduct product) {
        this(product, 1);
    }
    
    /**
     * Gets the grocery product stored in this node.
     * @return The grocery product
     */
    public GroceryProduct getProduct() { 
        return product; 
    }
    
    /**
     * Gets the quantity of the product in this node.
     * @return The quantity
     */
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * Sets the quantity of the product in this node.
     * @param quantity The new quantity (must be positive)
     * @throws IllegalArgumentException if quantity ≤ 0
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
        this.quantity = quantity;
    }
    
    /**
     * Gets the next node in the linked list.
     * @return The next node, or null if this is the last node
     */
    public DoublyCartNode getNext() { 
        return next; 
    }
    
    /**
     * Sets the next node in the linked list.
     * @param next The node to set as the next node
     */
    public void setNext(DoublyCartNode next) { 
        this.next = next; 
    }
    
    /**
     * Gets the previous node in the linked list.
     * @return The previous node, or null if this is the first node
     */
    public DoublyCartNode getPrev() { 
        return prev; 
    }
    
    /**
     * Sets the previous node in the linked list.
     * @param prev The node to set as the previous node
     */
    public void setPrev(DoublyCartNode prev) { 
        this.prev = prev; 
    }
    
    /**
     * Gets the total price for this item (price × quantity).
     * @return The total price
     */
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
    
    /**
     * Inserts a new node after this node in the linked list.
     * @param newNode The node to insert after this node
     * @throws IllegalArgumentException if newNode is null
     */
    public void insertAfter(DoublyCartNode newNode) {
        if (newNode == null) {
            throw new IllegalArgumentException("New node cannot be null");
        }
        
        DoublyCartNode oldNext = this.next;
        this.next = newNode;
        newNode.prev = this;
        
        if (oldNext != null) {
            newNode.next = oldNext;
            oldNext.prev = newNode;
        }
    }
    
    /**
     * Returns a string representation of this node.
     * @return Formatted string with product name and quantity
     */
    @Override
    public String toString() {
        return String.format("DoublyCartNode[Product=%s, Quantity=%d, Total=$%.2f]", 
            product.getName(), quantity, getTotalPrice());
    }
}
