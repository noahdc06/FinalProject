package com.university.grocerystore.model;

import java.time.Year;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents an immutable grocery item in the store inventory.
 * Items are uniquely identified by their SKU.
 * 
 * <p>This class is immutable and thread-safe. All fields are validated
 * during construction to ensure data integrity.</p>
 */
public final class GroceryItem implements Comparable<GroceryItem> {
    
    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9]{3,20}$");
    private static final int MIN_PRODUCTION_YEAR = 1900;
    
    private final String sku;
    private final String name;
    private final String brand;
    private final double price;
    private final int productionYear;
    private final int expirationYear;
    
    /**
     * Creates a new GroceryItem with validation.
     * 
     * @param sku the Stock Keeping Unit (alphanumeric, 3-20 characters)
     * @param name the item name (non-null, non-blank)
     * @param brand the brand name (non-null, non-blank)
     * @param price the price in dollars (non-negative)
     * @param productionYear the production year (1900 to current year + 1)
     * @param expirationYear the expiration year (must be >= production year)
     * @throws IllegalArgumentException if any parameter is invalid
     * @throws NullPointerException if any string parameter is null
     */
    public GroceryItem(String sku, String name, String brand, double price, 
                      int productionYear, int expirationYear) {
        this.sku = validateSku(sku);
        this.name = validateStringField(name, "Name");
        this.brand = validateStringField(brand, "Brand");
        this.price = validatePrice(price);
        this.productionYear = validateYear(productionYear, "Production year");
        this.expirationYear = validateExpirationYear(expirationYear, productionYear);
    }
    
    private String validateSku(String sku) {
        if (sku == null) {
            throw new NullPointerException("SKU cannot be null");
        }
        
        String cleaned = sku.trim().toUpperCase();
        
        if (!SKU_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException(
                "SKU must be 3-20 alphanumeric characters. Provided: " + sku);
        }
        
        return cleaned;
    }
    
    private String validateStringField(String value, String fieldName) {
        if (value == null) {
            throw new NullPointerException(fieldName + " cannot be null");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
    
    private double validatePrice(double price) {
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
    
    private int validateYear(int year, String fieldName) {
        int currentYear = Year.now().getValue();
        if (year < MIN_PRODUCTION_YEAR || year > currentYear + 1) {
            throw new IllegalArgumentException(
                String.format("%s must be between %d and %d. Provided: %d",
                    fieldName, MIN_PRODUCTION_YEAR, currentYear + 1, year));
        }
        return year;
    }
    
    private int validateExpirationYear(int expirationYear, int productionYear) {
        int currentYear = Year.now().getValue();
        if (expirationYear < productionYear) {
            throw new IllegalArgumentException(
                String.format("Expiration year (%d) cannot be before production year (%d)",
                    expirationYear, productionYear));
        }
        if (expirationYear > currentYear + 10) {
            throw new IllegalArgumentException(
                "Expiration year cannot be more than 10 years in the future");
        }
        return expirationYear;
    }
    
    /**
     * Gets the SKU of this item.
     * @return the SKU
     */
    public String getSku() {
        return sku;
    }
    
    /**
     * Gets the name of this item.
     * @return the item name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the brand of this item.
     * @return the brand name
     */
    public String getBrand() {
        return brand;
    }
    
    /**
     * Gets the price of this item.
     * @return the price in dollars
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets the production year of this item.
     * @return the production year
     */
    public int getProductionYear() {
        return productionYear;
    }
    
    /**
     * Gets the expiration year of this item.
     * @return the expiration year
     */
    public int getExpirationYear() {
        return expirationYear;
    }
    
    /**
     * Checks if the item is expired based on current year.
     * 
     * @return true if current year is greater than expiration year
     */
    public boolean isExpired() {
        int currentYear = Year.now().getValue();
        return currentYear > expirationYear;
    }
    
    /**
     * Gets the shelf life of the item in years.
     * 
     * @return shelf life in years
     */
    public int getShelfLifeYears() {
        return expirationYear - productionYear;
    }
    
    /**
     * Compares this item with another item based on name (alphabetical order).
     * 
     * @param other the item to compare with
     * @return negative if this item comes before, positive if after, 0 if equal
     */
    @Override
    public int compareTo(GroceryItem other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare to null GroceryItem");
        }
        return this.name.compareToIgnoreCase(other.name);
    }
    
    /**
     * Checks if this item is equal to another object.
     * Items are considered equal if they have the same SKU.
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal (same SKU), false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GroceryItem)) return false;
        GroceryItem other = (GroceryItem) obj;
        return sku.equals(other.sku);
    }
    
    /**
     * Generates hash code based on SKU.
     * 
     * @return hash code of the SKU
     */
    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
    
    /**
     * Returns a human-readable string representation of this item.
     * 
     * @return formatted string with item details
     */
    @Override
    public String toString() {
        return String.format("GroceryItem[SKU=%s, Name='%s', Brand='%s', Price=$%.2f, Production=%d, Expiration=%d]",
            sku, name, brand, price, productionYear, expirationYear);
    }
}