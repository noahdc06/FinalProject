package com.university.grocerystore.repository;

import com.university.grocerystore.model.GroceryProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
 * Wrapper class to ensure proper JSON serialization of GroceryProduct polymorphic types.
 * This wrapper ensures that Jackson includes type information for each GroceryProduct.
 */
public class ProductsWrapper {
    @JsonProperty("products")
    private List<GroceryProduct> products;
    
    public ProductsWrapper() {
        this.products = new ArrayList<>();
    }
    
    public ProductsWrapper(List<GroceryProduct> products) {
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }
    
    public List<GroceryProduct> getProducts() {
        return products;
    }
    
    public void setProducts(List<GroceryProduct> products) {
        this.products = products;
    }
}