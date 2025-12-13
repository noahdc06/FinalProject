package com.university.grocerystore.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.university.grocerystore.api.GroceryStore;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.GroceryProduct.ProductType;

/**
 * REST Controller for Grocery Product management operations.
 * 
 * <p>This controller demonstrates how the existing GroceryStore interface
 * can be easily exposed as REST endpoints without modifying the core
 * business logic. It showcases the extensibility of the hexagonal
 * architecture design.</p>
 * 
 * <p>All operations delegate to the existing GroceryStore implementation,
 * maintaining the same business logic and validation rules.</p>
 * 
 * @author Navid Mohaghegh
 * @version 1.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final GroceryStore groceryStore;

    /**
     * Constructor with dependency injection of GroceryStore.
     * 
     * @param groceryStore the grocery store implementation
     */
    public ProductController(GroceryStore groceryStore) {
        this.groceryStore = groceryStore;
    }

    /**
     * GET /api/products - Retrieve all products
     * 
     * @return list of all products
     */
    @GetMapping
    public ResponseEntity<List<GroceryProduct>> getAllProducts() {
        List<GroceryProduct> products = groceryStore.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id} - Retrieve a specific product by ID
     * 
     * @param id the product ID
     * @return the product if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroceryProduct> getProductById(@PathVariable("id") String id) {
        Optional<GroceryProduct> product = groceryStore.findById(id);
        return product.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/products - Create a new product
     * 
     * @param product the product to create
     * @return the created product or 400 if invalid
     */
    @PostMapping
    public ResponseEntity<GroceryProduct> createProduct(@RequestBody GroceryProduct product) {
        try {
            boolean added = groceryStore.addProduct(product);
            if (added) {
                return ResponseEntity.status(HttpStatus.CREATED).body(product);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/products/{id} - Update an existing product
     * 
     * @param id the product ID
     * @param product the updated product
     * @return the updated product or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<GroceryProduct> updateProduct(@PathVariable("id") String id, @RequestBody GroceryProduct product) {
        // Remove existing product and add the updated one
        Optional<GroceryProduct> existing = groceryStore.removeProduct(id);
        if (existing.isPresent()) {
            boolean added = groceryStore.addProduct(product);
            if (added) {
                return ResponseEntity.ok(product);
            } else {
                // Rollback: add the original product back
                groceryStore.addProduct(existing.get());
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/products/{id} - Delete a product
     * 
     * @param id the product ID
     * @return 204 if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
        Optional<GroceryProduct> removed = groceryStore.removeProduct(id);
        return removed.isPresent() ? 
            ResponseEntity.noContent().build() : 
            ResponseEntity.notFound().build();
    }

    /**
     * GET /api/products/search/name?q={query} - Search products by name
     * 
     * @param q the search query
     * @return list of matching products
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<GroceryProduct>> searchByName(@RequestParam("q") String q) {
        List<GroceryProduct> products = groceryStore.searchByName(q);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/search/brand?q={query} - Search products by brand
     * 
     * @param q the search query
     * @return list of matching products
     */
    @GetMapping("/search/brand")
    public ResponseEntity<List<GroceryProduct>> searchByBrand(@RequestParam("q") String q) {
        List<GroceryProduct> products = groceryStore.searchByBrand(q);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/type/{type} - Get products by type
     * 
     * @param type the product type
     * @return list of products of the specified type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<GroceryProduct>> getProductsByType(@PathVariable("type") ProductType type) {
        List<GroceryProduct> products = groceryStore.getProductsByType(type);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/recent?years={years} - Get recent products
     * 
     * @param years number of years to look back
     * @return list of recent products
     */
    @GetMapping("/recent")
    public ResponseEntity<List<GroceryProduct>> getRecentProducts(@RequestParam(value = "years", defaultValue = "5") int years) {
        List<GroceryProduct> products = groceryStore.findRecentProducts(years);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by price range.
     * 
     * @param min minimum price
     * @param max maximum price
     * @return list of products in price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<GroceryProduct>> getProductsByPriceRange(
            @RequestParam("min") double min, 
            @RequestParam("max") double max) {
        List<GroceryProduct> products = groceryStore.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/stats - Get inventory statistics
     * 
     * @return inventory statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<GroceryStore.InventoryStats> getInventoryStats() {
        GroceryStore.InventoryStats stats = groceryStore.getInventoryStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/products/count - Get total product count
     * 
     * @return total number of products
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getProductCount() {
        int count = groceryStore.size();
        return ResponseEntity.ok(count);
    }
}