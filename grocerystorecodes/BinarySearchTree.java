package com.university.grocerystorecodes.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple BST for organizing grocery products by name
 */
public class BinarySearchTree<T extends Comparable<T>> {
    private TreeNode<T> root;
    private int size;

    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    // Add product to catalog
    public void add(T product) {
        root = add(root, product);
        size++;
    }

    private TreeNode<T> add(TreeNode<T> node, T product) {
        if (node == null) return new TreeNode<>(product);

        if (product.compareTo(node.data) < 0) {
            node.left = add(node.left, product);
        } else if (product.compareTo(node.data) > 0) {
            node.right = add(node.right, product);
        }
        return node;
    }

    // Check if product exists
    public boolean contains(T product) {
        return contains(root, product);
    }

    private boolean contains(TreeNode<T> node, T product) {
        if (node == null) return false;

        int cmp = product.compareTo(node.data);
        if (cmp == 0) return true;
        if (cmp < 0) return contains(node.left, product);
        return contains(node.right, product);
    }

    // Get all products sorted by name
    public List<T> getAllSorted() {
        List<T> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(TreeNode<T> node, List<T> result) {
        if (node == null) return;
        inOrder(node.left, result);
        result.add(node.data);
        inOrder(node.right, result);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }
}