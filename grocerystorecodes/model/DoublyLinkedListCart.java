package com.university.grocerystorecodes.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Complete doubly linked list implementation for shopping cart.
 * Supports bidirectional traversal and efficient node removal.
 */
package com.university.grocerystorecodes.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedListCart implements Iterable<DoublyCartNode> {
    private DoublyCartNode head;
    private DoublyCartNode tail;
    private int size;
    
    public DoublyLinkedListCart() {
        head = tail = null;
        size = 0;
    }
    
    // Add methods
    public void addFirst(GroceryProduct product, int quantity) {
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
    
    public void addLast(GroceryProduct product, int quantity) {
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
    
    // Remove methods
    public GroceryProduct removeFirst() {
        if (head == null) return null;
        GroceryProduct product = head.getProduct();
        removeNode(head);
        return product;
    }
    
    public GroceryProduct removeLast() {
        if (tail == null) return null;
        GroceryProduct product = tail.getProduct();
        removeNode(tail);
        return product;
    }
    
    public GroceryProduct remove(String productId) {
        DoublyCartNode node = findNode(productId);
        if (node == null) return null;
        GroceryProduct product = node.getProduct();
        removeNode(node);
        return product;
    }
    
    // Find and remove helpers
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
    
    private void removeNode(DoublyCartNode node) {
        if (node == null) return;
        
        if (node == head) head = node.getNext();
        if (node == tail) tail = node.getPrev();
        
        if (node.getPrev() != null) node.getPrev().setNext(node.getNext());
        if (node.getNext() != null) node.getNext().setPrev(node.getPrev());
        
        node.setNext(null);
        node.setPrev(null);
        size--;
    }
    
    // Getters
    public DoublyCartNode getFirst() { return head; }
    public DoublyCartNode getLast() { return tail; }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    
    // Price calculations
    public double getTotal() {
        double total = 0;
        DoublyCartNode current = head;
        while (current != null) {
            total += current.getTotalPrice();
            current = current.getNext();
        }
        return total;
    }
    
    // Quantity operations
    public boolean contains(String productId) {
        return findNode(productId) != null;
    }
    
    public int getQuantity(String productId) {
        DoublyCartNode node = findNode(productId);
        return node != null ? node.getQuantity() : 0;
    }
    
    public boolean updateQuantity(String productId, int quantity) {
        DoublyCartNode node = findNode(productId);
        if (node == null) return false;
        node.setQuantity(quantity);
        return true;
    }
    
    public void clear() {
        head = tail = null;
        size = 0;
    }
    
    // Iterator
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
                if (!hasNext()) throw new NoSuchElementException();
                DoublyCartNode node = current;
                current = current.getNext();
                return node;
            }
        };
    }
    
    // Reverse iterator (doubly linked bonus)
    public Iterator<DoublyCartNode> reverseIterator() {
        return new Iterator<DoublyCartNode>() {
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
        };
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cart[");
        DoublyCartNode current = head;
        while (current != null) {
            sb.append(current.toString());
            if (current.getNext() != null) sb.append(" <-> ");
            current = current.getNext();
        }
        sb.append("]");
        return sb.toString();
    }
}
