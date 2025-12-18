package com.university.grocerystorecodes.datastructures;

import java.util.EmptyStackException;

/**
 * Simple Stack for recent orders (last in, first out for packing)
 */
public class Stack<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> top;
    private int size;

    public Stack() {
        top = null;
        size = 0;
    }

    // Add new order
    public void push(T order) {
        Node<T> newNode = new Node<>(order);
        newNode.next = top;
        top = newNode;
        size++;
    }

    // Process most recent order
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T order = top.data;
        top = top.next;
        size--;
        return order;
    }

    // View most recent order
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}