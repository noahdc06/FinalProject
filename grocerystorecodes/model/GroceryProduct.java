package com.university.grocerystore.model;

import java.time.Year;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

/**
 * Abstract base class representing any product in the store inventory.
 * Demonstrates abstraction and inheritance in OOP design.
 * 
 * <p>This class provides common properties and behavior for all grocery products
 * including fresh produce, frozen foods, canned goods, and snacks.</p>
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class",
    visible = true
)
@JsonSubTypes({
    @Type(value = Produce.class, name = "Produce"),
    @Type(value = FrozenFood.class, name = "FrozenFood"),
    @Type(value = CannedGood.class, name = "CannedGood"),
    @Type(value = Snack.class, name = "Snack")
})
public abstract class GroceryProduct implements Comparable<GroceryProduct> {
    
    protected static final int MIN_PRODUCTION_YEAR = 1900;
    
    protected final String id;
    protected final String name;
    protected final double price;
    protected final int productionYear;
    protected final ProductType type;
    
    /**
     * Enumeration of product types for polymorphic behavior.
     */
    public enum ProductType {
        PRODUCE("Fresh Produce"),
        FROZEN_FOOD("Frozen Food"),
        CANNED_GOOD("Canned Good"),
        SNACK("Snack Food"),
        BAKERY("Bakery Item"),
        DAIRY("Dairy Product"),
        MEAT("Meat Product"),
        SEAFOOD("Seafood"),
        BEVERAGE("Beverage");
        
        private final String displayName;
        
        ProductType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Protected constructor for subclasses.
     * 
     * @param id unique identifier for the product
     * @param name the product name
     * @param price the price in dollars
     * @param productionYear the production/manufacturing year
     * @param type the type of product
     */
    protected GroceryProduct(String id, String name, double price, int productionYear, ProductType type) {
        this.id = validateId(id);
        this.name = validateStringField(name, "Name");
        this.price = validatePrice(price);
        this.productionYear = validateYear(productionYear);
        this.type = Objects.requireNonNull(type, "Product type cannot be null");
    }
    
    /**
     * Abstract method to get the brand/manufacturer of the product.
     * Implementation varies by product type.
     * 
     * @return the brand name
     */
    public abstract String getBrand();
    
    /**
     * Abstract method to get a formatted display string.
     * Each product type should provide its own formatting.
     * 
     * @return formatted display string
     */
    public abstract String getDisplayInfo();
    
    /**
     * Template method for calculating discounted price.
     * Subclasses can override getDiscountRate() to customize.
     * 
     * @return discounted price
     */
    public final double getDiscountedPrice() {
        return price * (1.0 - getDiscountRate());
    }
    
    /**
     * Hook method for discount rate. Default is no discount.
     * Subclasses can override to provide type-specific discounts.
     * 
     * @return discount rate between 0.0 and 1.0
     */
    public double getDiscountRate() {
        return 0.0;
    }
    
    protected String validateId(String id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }
        return id.trim();
    }
    
    protected String validateStringField(String value, String fieldName) {
        if (value == null) {
            throw new NullPointerException(fieldName + " cannot be null");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
    
    protected double validatePrice(double price) {
        if (price < 0.0) {
            throw new IllegalArgumentException(
                "Price cannot be negative. Provided: " + price);
        }
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new IllegalArgumentException(
                "Price must be a valid number. Provided: " + price);
        }
        return price;
    }
    
    protected int validateYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < MIN_PRODUCTION_YEAR || year > currentYear + 1) {
            throw new IllegalArgumentException(
                String.format("Year must be between %d and %d. Provided: %d",
                    MIN_PRODUCTION_YEAR, currentYear + 1, year));
        }
        return year;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getProductionYear() {
        return productionYear;
    }
    
    public ProductType getType() {
        return type;
    }
    
    /**
     * Compares products by name for natural ordering.
     */
    @Override
    public int compareTo(GroceryProduct other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare to null GroceryProduct");
        }
        int nameComparison = this.name.compareToIgnoreCase(other.name);
        if (nameComparison != 0) {
            return nameComparison;
        }
        return this.id.compareTo(other.id);
    }
    
    /**
     * Products are equal if they have the same ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GroceryProduct)) return false;
        GroceryProduct other = (GroceryProduct) obj;
        return id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s[ID=%s, Name='%s', Price=$%.2f, ProductionYear=%d]",
            type.getDisplayName(), id, name, price, productionYear);
    }
}