package com.university.grocerystorecodes.datastructures;

import java.util.NoSuchElementException;

/**
 * Simple Queue for customer service line (first come, first served)
 */
public class Queue<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> front;
    private Node<T> back;
    private int size;

    public Queue() {
        front = back = null;
        size = 0;
    }

    // Customer joins line
    public void enqueue(T customer) {
        Node<T> newNode = new Node<>(customer);
        if (isEmpty()) {
            front = back = newNode;
        } else {
            back.next = newNode;
            back = newNode;
        }
        size++;
    }

    // Serve next customer
    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("No customers in line");
        T customer = front.data;
        front = front.next;
        if (front == null) back = null;
        size--;
        return customer;
    }

    // Peek at next customer without serving
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("No customers in line");
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }
}