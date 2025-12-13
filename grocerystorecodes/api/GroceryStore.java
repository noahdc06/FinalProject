package com.university.grocerystore.api;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;

/**
 * Interface defining operations for a polymorphic grocery store that demonstrates interface segregation and
 * dependency inversion principles. This interface is extensible to other types of grocery products.
 * 
 * <p>The Interface Segregation Principle (ISP) states that clients should not be forced to depend on interfaces they
 * do not use. In practice, this means designing smaller, more focused interfaces rather than large, monolithic ones.
 * For example, instead of creating a single Machine interface with methods like print(), scan(), and fax(), which
 * every implementation must support, ISP encourages splitting these into multiple role-specific interfaces (Printer,
 * Scanner, FaxMachine). This ensures that implementing classes only provide the functionality relevant to them,
 * leading to higher cohesion, reduced coupling, and easier maintenance. ISP directly combats the rigidity and
 * fragility that arise when changes in unused methods propagate across unrelated parts of the system.</p>
 *
 * <p>The Dependency Inversion Principle (DIP) requires that high-level modules depend on abstractions, not concrete
 * implementations. Instead of having business logic directly tied to low-level details (like a specific database
 * driver or logging mechanism), DIP advocates for using interfaces or abstract classes as boundaries. For instance,
 * a PaymentService should rely on a PaymentProcessor interface rather than a hardcoded StripeProcessor.  This allows
 * developers to substitute implementations without altering higher-level policies, supporting testability,
 * scalability, and adaptability to new technologies. By inverting the dependency direction, DIP ensures that both
 * high- and low-level modules evolve independently, fostering loosely coupled, extensible architectures.</p>
 *
 * <p>This interface extends the concept of a grocery store to handle
 * various types of grocery products using polymorphism.</p>
 *
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public interface GroceryStore {

    /**
     * Adds a grocery product to the store inventory.
     *
     * @param product the grocery product to add
     * @return true if added successfully, false if duplicate ID
     */
    boolean addProduct(GroceryProduct product);

    /**
     * Removes a grocery product by its ID.
     *
     * @param id the product ID
     * @return the removed product, or Optional.empty() if not found
     */
    Optional<GroceryProduct> removeProduct(String id);

    /**
     * Finds a grocery product by its ID.
     *
     * @param id the product ID
     * @return the product, or Optional.empty() if not found
     */
    Optional<GroceryProduct> findById(String id);

    /**
     * Searches products by name (case-insensitive partial match).
     *
     * @param name the name to search for
     * @return list of matching products
     */
    List<GroceryProduct> searchByName(String name);

    /**
     * Searches products by brand (manufacturer/company).
     *
     * @param brand the brand name
     * @return list of matching products
     */
    List<GroceryProduct> searchByBrand(String brand);

    /**
     * Gets all products of a specific type.
     * Demonstrates polymorphic filtering.
     *
     * @param type the product type
     * @return list of products of the specified type
     */
    List<GroceryProduct> getProductsByType(GroceryProduct.ProductType type);

    /**
     * Gets all products that implement the Perishable interface.
     * Demonstrates interface-based polymorphism.
     *
     * @return list of perishable products
     */
    List<Perishable> getPerishableProducts();

    /**
     * Filters products by a custom predicate.
     * Demonstrates functional programming with polymorphism.
     *
     * @param predicate the filter condition
     * @return filtered list of products
     */
    List<GroceryProduct> filterProducts(Predicate<GroceryProduct> predicate);

    /**
     * Finds products produced in the last N years.
     *
     * @param years the number of years to look back
     * @return list of recent products
     */
    List<GroceryProduct> findRecentProducts(int years);

    /**
     * Finds products by multiple brands (OR condition).
     *
     * @param brands the brand names to search for
     * @return list of products by any of the specified brands
     */
    List<GroceryProduct> findByBrands(String... brands);

    /**
     * Finds products with custom filtering using predicate.
     *
     * @param condition the filter condition
     * @return filtered list of products
     */
    List<GroceryProduct> findWithPredicate(Predicate<GroceryProduct> condition);

    /**
     * Gets products sorted by custom comparator.
     *
     * @param comparator the sorting comparator
     * @return sorted list of products
     */
    List<GroceryProduct> getSorted(java.util.Comparator<GroceryProduct> comparator);

    /**
     * Gets products within a price range.
     *
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return list of products in price range
     */
    List<GroceryProduct> getProductsByPriceRange(double minPrice, double maxPrice);

    /**
     * Gets products with a specific expiration/production year.
     *
     * @param year the year
     * @return list of products from that year
     */
    List<GroceryProduct> getProductsByYear(int year);

    /**
     * Gets all products sorted by name.
     *
     * @return sorted list of all products
     */
    List<GroceryProduct> getAllProductsSorted();

    /**
     * Gets all products (unsorted).
     *
     * @return list of all products
     */
    List<GroceryProduct> getAllProducts();

    /**
     * Calculates total inventory value.
     *
     * @return sum of all product prices
     */
    double getTotalInventoryValue();

    /**
     * Calculates total discounted inventory value.
     * Uses polymorphic discount calculation.
     *
     * @return sum of all discounted prices
     */
    double getTotalDiscountedValue();

    /**
     * Gets inventory statistics.
     *
     * @return statistics object with counts and averages
     */
    InventoryStats getInventoryStats();

    /**
     * Clears all products from the store.
     */
    void clearInventory();

    /**
     * Gets the number of products in inventory.
     *
     * @return product count
     */
    int size();

    /**
     * Checks if the inventory is empty.
     *
     * @return true if no products in inventory
     */
    boolean isEmpty();

    /**
     * Statistics class for inventory analysis.
     */
    class InventoryStats {
        private final int totalCount;
        private final double averagePrice;
        private final double medianPrice;
        private final int uniqueTypes;
        private final int perishableCount;
        private final int nonPerishableCount;

        public InventoryStats(int totalCount, double averagePrice, double medianPrice,
                              int uniqueTypes, int perishableCount, int nonPerishableCount) {
            this.totalCount = totalCount;
            this.averagePrice = averagePrice;
            this.medianPrice = medianPrice;
            this.uniqueTypes = uniqueTypes;
            this.perishableCount = perishableCount;
            this.nonPerishableCount = nonPerishableCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public double getAveragePrice() {
            return averagePrice;
        }

        public double getMedianPrice() {
            return medianPrice;
        }

        public int getUniqueTypes() {
            return uniqueTypes;
        }

        public int getPerishableCount() {
            return perishableCount;
        }

        public int getNonPerishableCount() {
            return nonPerishableCount;
        }

        @Override
        public String toString() {
            return String.format("InventoryStats[Total=%d, AvgPrice=$%.2f, MedianPrice=$%.2f, Types=%d, Perishable=%d, NonPerishable=%d]",
                    totalCount, averagePrice, medianPrice, uniqueTypes, perishableCount, nonPerishableCount);
        }
    }
}