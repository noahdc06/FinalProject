package com.university.grocerystore.api;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;

/**
 * Modern interface for polymorphic grocery store with async operations.
 * Extends the base GroceryStore with modern Java features.
 * 
 * @author Navid Mohaghegh
 * @version 4.0
 * @since 2024-09-15
 */
public interface ModernGroceryStore extends GroceryStore {
    
    /**
     * Modern InventoryStats using Java record for immutability.
     * Records provide built-in equals(), hashCode(), and toString() methods.
     * 
     * @param totalCount total number of products
     * @param averagePrice average price of all products
     * @param medianPrice median price of all products
     * @param uniqueTypes number of unique product types
     * @param perishableCount number of perishable products
     * @param nonPerishableCount number of non-perishable products
     */
    record ModernInventoryStats(
        int totalCount,
        double averagePrice,
        double medianPrice,
        int uniqueTypes,
        int perishableCount,
        int nonPerishableCount
    ) {
        /**
         * Compact constructor for validation.
         */
        public ModernInventoryStats {
            if (totalCount < 0) {
                throw new IllegalArgumentException("Total count cannot be negative");
            }
            if (averagePrice < 0) {
                throw new IllegalArgumentException("Average price cannot be negative");
            }
            if (medianPrice < 0) {
                throw new IllegalArgumentException("Median price cannot be negative");
            }
            if (uniqueTypes < 0) {
                throw new IllegalArgumentException("Unique types cannot be negative");
            }
            if (perishableCount < 0) {
                throw new IllegalArgumentException("Perishable count cannot be negative");
            }
            if (nonPerishableCount < 0) {
                throw new IllegalArgumentException("Non-perishable count cannot be negative");
            }
        }
        
        /**
         * Creates an empty stats instance.
         * 
         * @return stats with all zero values
         */
        public static ModernInventoryStats empty() {
            return new ModernInventoryStats(0, 0, 0, 0, 0, 0);
        }
        
        /**
         * Checks if the inventory is empty.
         * 
         * @return true if total count is zero
         */
        public boolean isEmpty() {
            return totalCount == 0;
        }
        
        /**
         * Gets the percentage of perishable products.
         * 
         * @return perishable percentage (0-100)
         */
        public double getPerishablePercentage() {
            return totalCount > 0 ? (perishableCount * 100.0) / totalCount : 0;
        }
        
        /**
         * Gets the percentage of non-perishable products.
         * 
         * @return non-perishable percentage (0-100)
         */
        public double getNonPerishablePercentage() {
            return totalCount > 0 ? (nonPerishableCount * 100.0) / totalCount : 0;
        }
        
        /**
         * Creates a summary string for reporting.
         * 
         * @return formatted summary
         */
        public String getSummary() {
            return String.format(
                """
                Inventory Statistics:
                - Total Items: %d
                - Average Price: $%.2f
                - Median Price: $%.2f
                - Unique Types: %d
                - Perishable Products: %d (%.1f%%)
                - Non-Perishable Products: %d (%.1f%%)
                """,
                totalCount, averagePrice, medianPrice, uniqueTypes,
                perishableCount, getPerishablePercentage(),
                nonPerishableCount, getNonPerishablePercentage()
            );
        }
    }
    
    /**
     * Batch operation result record.
     * 
     * @param successful number of successful operations
     * @param failed number of failed operations
     * @param errors list of error messages
     */
    record BatchOperationResult(
        int successful,
        int failed,
        List<String> errors
    ) {
        /**
         * Checks if all operations were successful.
         * 
         * @return true if no failures
         */
        public boolean isCompleteSuccess() {
            return failed == 0 && (errors == null || errors.isEmpty());
        }
        
        /**
         * Gets the total number of operations.
         * 
         * @return sum of successful and failed
         */
        public int totalOperations() {
            return successful + failed;
        }
        
        /**
         * Gets the success rate.
         * 
         * @return success percentage (0-100)
         */
        public double successRate() {
            int total = totalOperations();
            return total > 0 ? (successful * 100.0) / total : 0;
        }
    }
    
    /**
     * Search criteria record for complex searches.
     * 
     * @param name optional name search term
     * @param brand optional brand search term
     * @param type optional product type filter
     * @param minPrice optional minimum price
     * @param maxPrice optional maximum price
     * @param yearFrom optional start year
     * @param yearTo optional end year
     */
    record SearchCriteria(
        Optional<String> name,
        Optional<String> brand,
        Optional<GroceryProduct.ProductType> type,
        Optional<Double> minPrice,
        Optional<Double> maxPrice,
        Optional<Integer> yearFrom,
        Optional<Integer> yearTo
    ) {
        /**
         * Builder for SearchCriteria.
         */
        public static class Builder {
            private Optional<String> name = Optional.empty();
            private Optional<String> brand = Optional.empty();
            private Optional<GroceryProduct.ProductType> type = Optional.empty();
            private Optional<Double> minPrice = Optional.empty();
            private Optional<Double> maxPrice = Optional.empty();
            private Optional<Integer> yearFrom = Optional.empty();
            private Optional<Integer> yearTo = Optional.empty();
            
            public Builder withName(String name) {
                this.name = Optional.ofNullable(name);
                return this;
            }
            
            public Builder withBrand(String brand) {
                this.brand = Optional.ofNullable(brand);
                return this;
            }
            
            public Builder withType(GroceryProduct.ProductType type) {
                this.type = Optional.ofNullable(type);
                return this;
            }
            
            public Builder withPriceRange(Double min, Double max) {
                this.minPrice = Optional.ofNullable(min);
                this.maxPrice = Optional.ofNullable(max);
                return this;
            }
            
            public Builder withYearRange(Integer from, Integer to) {
                this.yearFrom = Optional.ofNullable(from);
                this.yearTo = Optional.ofNullable(to);
                return this;
            }
            
            public SearchCriteria build() {
                return new SearchCriteria(
                    name, brand, type, 
                    minPrice, maxPrice, 
                    yearFrom, yearTo
                );
            }
        }
        
        /**
         * Creates a new builder.
         * 
         * @return new builder instance
         */
        public static Builder builder() {
            return new Builder();
        }
        
        /**
         * Checks if any criteria is specified.
         * 
         * @return true if at least one criterion is present
         */
        public boolean hasAnyCriteria() {
            return name.isPresent() || brand.isPresent() || type.isPresent() ||
                   minPrice.isPresent() || maxPrice.isPresent() ||
                   yearFrom.isPresent() || yearTo.isPresent();
        }
    }
    
    // Async operation methods
    
    /**
     * Adds product asynchronously.
     * 
     * @param product the product to add
     * @return CompletableFuture with the result
     */
    CompletableFuture<Boolean> addProductAsync(GroceryProduct product);
    
    /**
     * Finds product by ID asynchronously.
     * 
     * @param id the product ID
     * @return CompletableFuture with the result
     */
    CompletableFuture<Optional<GroceryProduct>> findByIdAsync(String id);
    
    /**
     * Searches by name asynchronously.
     * 
     * @param name the name to search for
     * @return CompletableFuture with the results
     */
    CompletableFuture<List<GroceryProduct>> searchByNameAsync(String name);
    
    /**
     * Gets inventory statistics asynchronously.
     * 
     * @return CompletableFuture with the statistics
     */
    CompletableFuture<ModernInventoryStats> getModernInventoryStatsAsync();
    
    /**
     * Performs advanced search with multiple criteria.
     * 
     * @param criteria the search criteria
     * @return CompletableFuture with matching products
     */
    CompletableFuture<List<GroceryProduct>> advancedSearchAsync(SearchCriteria criteria);
    
    /**
     * Adds multiple products in batch.
     * 
     * @param products collection of products to add
     * @return CompletableFuture with batch operation result
     */
    CompletableFuture<BatchOperationResult> addProductsBatchAsync(List<GroceryProduct> products);
    
    /**
     * Removes multiple products in batch.
     * 
     * @param ids collection of IDs to remove
     * @return CompletableFuture with batch operation result
     */
    CompletableFuture<BatchOperationResult> removeProductsBatchAsync(List<String> ids);
}