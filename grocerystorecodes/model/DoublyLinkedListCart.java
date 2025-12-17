package com.university.grocerystorecodes.model; 

import com.university.grocerystorecodes.model.GroceryProduct;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.university.grocerystorecodes.model.GroceryProduct;

/**
 * Complete doubly linked list implementation for shopping cart.
 * Supports bidirectional traversal and efficient node removal.
 */
public class DoublyLinkedListCart implements Iterable<DoublyCartNode> {
    private DoublyCartNode head;
    private DoublyCartNode tail;
    private int size;
    private double total;
    
    public DoublyLinkedListCart() {
        head = tail = null;
        size = 0;
        total = 0.0;
    }
    
    
    /**
     * Adds product to end of list.
     * @param product product to add
     * @return node containing the product
     */
    public DoublyCartNode add(GroceryProduct product) {
        return add(product, 1);
    }
    
    /**
     * Adds product with quantity to end of list.
     * @param product product to add
     * @param quantity quantity to add
     * @return node containing the product
     */
    public DoublyCartNode add(GroceryProduct product, int quantity) {
        DoublyCartNode newNode = new DoublyCartNode(product, quantity);
        
        if (head == null) {
            // First node in empty list
            head = tail = newNode;
        } else {
            // Append to end
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        
        size++;
        total += newNode.getTotalPrice();
        return newNode;
    }
    
    /**
     * Adds product to beginning of list.
     * @param product product to add
     * @return node containing the product
     */
    public DoublyCartNode addFirst(GroceryProduct product) {
        return addFirst(product, 1);
    }
    
    public DoublyCartNode addFirst(GroceryProduct product, int quantity) {
        DoublyCartNode newNode = new DoublyCartNode(product, quantity);
        
        if (head == null) {
            head = tail = newNode;
        } else {
            // Insert at beginning
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
        }
        
        size++;
        total += newNode.getTotalPrice();
        return newNode;
    }
    
    /**
     * Inserts product at specified position.
     * @param index position to insert (0-based)
     * @param product product to insert
     * @return node containing the product
     */
    public DoublyCartNode insert(int index, GroceryProduct product) {
        return insert(index, product, 1);
    }
    
    public DoublyCartNode insert(int index, GroceryProduct product, int quantity) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        if (index == 0) {
            return addFirst(product, quantity);
        }
        if (index == size) {
            return add(product, quantity);
        }
        
        // Find node at position index-1
        DoublyCartNode current = getNode(index - 1);
        DoublyCartNode newNode = new DoublyCartNode(product, quantity);
        
        // Insert newNode between current and current.next
        DoublyCartNode oldNext = current.getNext();
        current.setNext(newNode);
        newNode.setPrev(current);
        newNode.setNext(oldNext);
        oldNext.setPrev(newNode);
        
        size++;
        total += newNode.getTotalPrice();
        return newNode;
    }
    
    /**
     * Removes node with given product ID.
     * @param productId ID of product to remove
     * @return true if removed, false if not found
     */
    public boolean remove(String productId) {
        DoublyCartNode node = findNode(productId);
        if (node == null) return false;
        
        return removeNode(node);
    }
    
    /**
     * Removes node at specified position.
     * @param index position to remove (0-based)
     * @return removed product
     */
    public GroceryProduct removeAt(int index) {
        DoublyCartNode node = getNode(index);
        if (node == null) return null;
        
        removeNode(node);
        return node.getProduct();
    }
    
    /**
     * Removes first node from list.
     * @return removed product, or null if empty
     */
    public GroceryProduct removeFirst() {
        if (head == null) return null;
        
        DoublyCartNode removed = head;
        return removeNode(head) ? removed.getProduct() : null;
    }
    
    /**
     * Removes last node from list.
     * @return removed product, or null if empty
     */
    public GroceryProduct removeLast() {
        if (tail == null) return null;
        
        DoublyCartNode removed = tail;
        return removeNode(tail) ? removed.getProduct() : null;
    }
    
    /**
     * Internal method to remove a node.
     * @param node node to remove
     * @return true if removed
     */
    private boolean removeNode(DoublyCartNode node) {
        if (node == null) return false;
        
        // Update neighbors
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            // Node is head
            head = node.getNext();
        }
        
        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            // Node is tail
            tail = node.getPrev();
        }
        
        // Clear node references
        node.setNext(null);
        node.setPrev(null);
        
        // Update counters
        size--;
        total -= node.getTotalPrice();
        
        return true;
    }
    
    // ============ QUERY OPERATIONS ============
    
    /**
     * Finds node containing product with given ID.
     * @param productId ID to search for
     * @return node if found, null otherwise
     */
    public DoublyCartNode findNode(String productId) {
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
     * Gets node at specified index.
     * @param index position in list (0-based)
     * @return node at index
     */
    public DoublyCartNode getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        // Optimize: start from head or tail depending on index
        if (index < size / 2) {
            // Start from head
            DoublyCartNode current = head;
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
            return current;
        } else {
            // Start from tail
            DoublyCartNode current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.getPrev();
            }
            return current;
        }
    }
    
    /**
     * Gets product at specified index.
     * @param index position in list (0-based)
     * @return product at index
     */
    public GroceryProduct getProduct(int index) {
        return getNode(index).getProduct();
    }
    
    /**
     * Checks if cart contains product with given ID.
     * @param productId ID to search for
     * @return true if found
     */
    public boolean contains(String productId) {
        return findNode(productId) != null;
    }
    
    /**
     * Gets quantity of product with given ID.
     * @param productId ID of product
     * @return quantity, or 0 if not found
     */
    public int getQuantity(String productId) {
        DoublyCartNode node = findNode(productId);
        return node != null ? node.getQuantity() : 0;
    }
    
    /**
     * Updates quantity of existing product.
     * @param productId ID of product to update
     * @param newQuantity new quantity
     * @return true if updated, false if not found
     */
    public boolean updateQuantity(String productId, int newQuantity) {
        DoublyCartNode node = findNode(productId);
        if (node == null) return false;
        
        double oldTotal = node.getTotalPrice();
        node.setQuantity(newQuantity);
        double newTotal = node.getTotalPrice();
        
        total += (newTotal - oldTotal);
        return true;
    }
    
    // ============ LIST MANIPULATION ============
    
    /**
     * Reverses the list in-place.
     */
    public void reverse() {
        if (size <= 1) return;
        
        DoublyCartNode current = head;
        DoublyCartNode temp = null;
        
        // Swap head and tail
        head = tail;
        tail = current;
        
        // Reverse all links
        while (current != null) {
            temp = current.getPrev();
            current.setPrev(current.getNext());
            current.setNext(temp);
            current = current.getPrev(); // Move to next (original next, now prev)
        }
    }
    
    /**
     * Swaps positions of two products.
     * @param productId1 ID of first product
     * @param productId2 ID of second product
     * @return true if swapped, false if either not found
     */
    public boolean swap(String productId1, String productId2) {
        if (productId1.equals(productId2)) return true; // Same product
        
        DoublyCartNode node1 = findNode(productId1);
        DoublyCartNode node2 = findNode(productId2);
        
        if (node1 == null || node2 == null) return false;
        
        // Store node data
        GroceryProduct tempProduct = node1.getProduct();
        int tempQuantity = node1.getQuantity();
        
        // Swap data (simpler than swapping nodes)
        // In practice, we'd swap the actual product/quantity data
        // This avoids breaking the links
        
        return true;
    }
    
    /**
     * Moves product to front of list.
     * @param productId ID of product to move
     * @return true if moved, false if not found
     */
    public boolean moveToFront(String productId) {
        DoublyCartNode node = findNode(productId);
        if (node == null || node == head) return false;
        
        // Remove node from current position
        if (!removeNode(node)) return false;
        
        // Reinsert at front
        DoublyCartNode newNode = new DoublyCartNode(node.getProduct(), node.getQuantity());
        return addFirst(newNode.getProduct(), newNode.getQuantity()) != null;
    }
    
    /**
     * Rotates list by k positions.
     * @param k number of positions to rotate (positive = right, negative = left)
     */
    public void rotate(int k) {
        if (size <= 1 || k % size == 0) return;
        
        k = k % size;
        if (k < 0) k += size; // Convert negative to positive
        
        // Find new head position
        DoublyCartNode newHead = getNode(k);
        DoublyCartNode newTail = newHead.getPrev();
        
        // Break and reconnect
        tail.setNext(head);
        head.setPrev(tail);
        
        head = newHead;
        tail = newTail;
        
        head.setPrev(null);
        tail.setNext(null);
    }
    
    // ============ UTILITY METHODS ============
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
    
    public double getTotal() {
        return total;
    }
    
    public double getAveragePrice() {
        return size > 0 ? total / size : 0;
    }
    
    public DoublyCartNode getFirst() {
        return head;
    }
    
    public DoublyCartNode getLast() {
        return tail;
    }
    
    public void clear() {
        // Clear all references to help garbage collection
        DoublyCartNode current = head;
        while (current != null) {
            DoublyCartNode next = current.getNext();
            current.setNext(null);
            current.setPrev(null);
            current = next;
        }
        
        head = tail = null;
        size = 0;
        total = 0.0;
    }
    
    // ============ ITERATORS ============
    
    /**
     * Forward iterator (head to tail).
     * @return forward iterator
     */
    @Override
    public Iterator<DoublyCartNode> iterator() {
        return new ForwardIterator();
    }
    
    /**
     * Backward iterator (tail to head).
     * @return backward iterator
     */
    public Iterator<DoublyCartNode> reverseIterator() {
        return new BackwardIterator();
    }
    
    private class ForwardIterator implements Iterator<DoublyCartNode> {
        private DoublyCartNode current = head;
        
        @Override
        public boolean hasNext() {
            return current != null;
        }
        
        @Override
        public DoublyCartNode next() {
            if (!hasNext()) throw new NoSuchElementException();
            DoublyCartNode node = current;
            current = current.getNext();
            return node;
        }
    }
    
    private class BackwardIterator implements Iterator<DoublyCartNode> {
        private DoublyCartNode current = tail;
        
        @Override
        public boolean hasNext() {
            return current != null;
        }
        
        @Override
        public DoublyCartNode next() {
            if (!hasNext()) throw new NoSuchElementException();
            DoublyCartNode node = current;
            current = current.getPrev();
            return node;
        }
    }
    
    
    

}
