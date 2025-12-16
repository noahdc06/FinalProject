package com.university.grocerystore.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Trie (Prefix Tree) data structure for efficient prefix-based product searching.
 * Provides O(m) time complexity for prefix searches where m is the length of the prefix.
 * 
 * <p>This implementation stores products at each node along the path, allowing for
 * efficient prefix-based lookups and autocomplete functionality.</p>
 */
public class ProductTrie {
    
    private final TrieNode root;
    
    /**
     * Creates a new empty product trie.
     */
    public ProductTrie() {
        this.root = new TrieNode();
    }
    
    /**
     * Inserts a product into the trie using its name as the key.
     * 
     * @param product the product to insert
     * @throws IllegalArgumentException if product is null
     */
    public void insert(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        String name = product.getName().toLowerCase();
        TrieNode current = root;
        
        // Add product to root for empty prefix searches
        current.products.add(product);
        
        // Traverse the trie, adding product to each node along the path
        for (char c : name.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
            current.products.add(product);
        }
        
        current.isEndOfWord = true;
    }
    
    /**
     * Searches for products with names that start with the given prefix.
     * 
     * @param prefix the prefix to search for
     * @return list of products matching the prefix
     */
    public List<GroceryProduct> searchByPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerPrefix = prefix.toLowerCase().trim();
        TrieNode current = root;
        
        // Navigate to the prefix node
        for (char c : lowerPrefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return Collections.emptyList();
            }
            current = current.children.get(c);
        }
        
        return new ArrayList<>(current.products);
    }
    
    /**
     * Searches for products with names that start with the given prefix,
     * limited to a maximum number of results.
     * 
     * @param prefix the prefix to search for
     * @param limit the maximum number of results to return
     * @return list of products matching the prefix, limited to the specified count
     */
    public List<GroceryProduct> searchByPrefixWithLimit(String prefix, int limit) {
        if (limit <= 0) {
            return new ArrayList<>();
        }
        List<GroceryProduct> results = searchByPrefix(prefix);
        return results.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Checks if any products exist with names starting with the given prefix.
     * 
     * @param prefix the prefix to check
     * @return true if products exist with the prefix
     */
    public boolean hasPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return false;
        }
        
        String lowerPrefix = prefix.toLowerCase().trim();
        TrieNode current = root;
        
        for (char c : lowerPrefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return false;
            }
            current = current.children.get(c);
        }
        
        return !current.products.isEmpty();
    }
    
    /**
     * Gets all products in the trie.
     * 
     * @return list of all products
     */
    public List<GroceryProduct> getAllProducts() {
        return new ArrayList<>(root.products);
    }
    
    /**
     * Removes a product from the trie.
     * 
     * @param product the product to remove
     * @return true if the product was removed, false if not found
     */
    public boolean remove(GroceryProduct product) {
        if (product == null) {
            return false;
        }
        
        String name = product.getName().toLowerCase();
        List<TrieNode> path = new ArrayList<>();
        TrieNode current = root;
        
        // Build path to the product
        path.add(current);
        for (char c : name.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return false; // Product not found
            }
            current = current.children.get(c);
            path.add(current);
        }
        
        // Remove product from all nodes in the path
        boolean removed = false;
        for (TrieNode node : path) {
            if (node.products.remove(product)) {
                removed = true;
            }
        }
        
        // Clean up empty nodes (optional optimization)
        cleanupEmptyNodes(path);
        
        return removed;
    }
    
    /**
     * Clears all products from the trie.
     */
    public void clear() {
        root.children.clear();
        root.products.clear();
        root.isEndOfWord = false;
    }
    
    /**
     * Gets the total number of products in the trie.
     * 
     * @return the number of products
     */
    public int size() {
        return root.products.size();
    }
    
    /**
     * Checks if the trie is empty.
     * 
     * @return true if no products are stored
     */
    public boolean isEmpty() {
        return root.products.isEmpty();
    }
    
    /**
     * Internal method to clean up empty nodes after removal.
     * 
     * @param path the path of nodes to potentially clean up
     */
    private void cleanupEmptyNodes(List<TrieNode> path) {
        // Remove empty leaf nodes (optional optimization)
        for (int i = path.size() - 1; i > 0; i--) {
            TrieNode node = path.get(i);
            if (node.products.isEmpty() && node.children.isEmpty()) {
                TrieNode parent = path.get(i - 1);
                // Find and remove the empty child
                parent.children.entrySet().removeIf(entry -> entry.getValue() == node);
            }
        }
    }
    
    /**
     * Internal node class for the trie structure.
     */
    private static class TrieNode {
        final Map<Character, TrieNode> children;
        final List<GroceryProduct> products;
        boolean isEndOfWord;
        
        TrieNode() {
            this.children = new HashMap<>();
            this.products = new ArrayList<>();
            this.isEndOfWord = false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("ProductTrie[Size=%d, Prefixes=%d]",
            size(),
            countPrefixes(root));
    }
    
    /**
     * Counts the total number of prefixes in the trie.
     * 
     * @param node the node to count from
     * @return the number of prefixes
     */
    private int countPrefixes(TrieNode node) {
        int count = node.isEndOfWord ? 1 : 0;
        for (TrieNode child : node.children.values()) {
            count += countPrefixes(child);
        }
        return count;
    }
}
