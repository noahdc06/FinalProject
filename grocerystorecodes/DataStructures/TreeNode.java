package com.university.grocerystorecodes.datastructures;

/**
 * Simple TreeNode for BST
 */
public class TreeNode<T extends Comparable<T>> {
    public T data;
    public TreeNode<T> left;
    public TreeNode<T> right;

    public TreeNode(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}