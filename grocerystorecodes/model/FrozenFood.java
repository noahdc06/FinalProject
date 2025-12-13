package com.university.grocerystore.model;

import java.util.Objects;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a frozen food item in the grocery store system.
 * Extends GroceryProduct and implements Perishable interface to demonstrate
 * multiple inheritance through interfaces.
 * 
 * <p>Frozen foods support various storage types, organic certification, and
 * provide nutritional information and shelf life details.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrozenFood extends GroceryProduct implements Perishable {
    
    private static final Set<String> VALID_STORAGE_TYPES = Set.of("FREEZER", "DEEP_FREEZE", "REFRIGERATED_FROZEN");
    private static final int CALORIES_PER_SERVING_BASIS = 250;
    
    private final String brand;
    private final String storageType;
    private final double netWeight; // in grams
    private final boolean organic;
    private final int calories;
    private final ShelfLifeQuality quality;
    
    /**
     * Constructs a new FrozenFood with validation.
     * 
     * @param id unique identifier
     * @param name the product name
     * @param brand the brand name
     * @param price the price in dollars
     * @param productionYear the production year
     * @param storageType the storage type (FREEZER, DEEP_FREEZE, or REFRIGERATED_FROZEN)
     * @param netWeight the net weight in grams
     * @param organic whether organic certified
     * @param calories the calorie count per serving
     * @param quality the shelf life quality
     * @throws IllegalArgumentException if validation fails
     */
    @JsonCreator
    public FrozenFood(@JsonProperty("id") String id, 
                     @JsonProperty("name") String name, 
                     @JsonProperty("brand") String brand, 
                     @JsonProperty("price") double price, 
                     @JsonProperty("productionYear") int productionYear,
                     @JsonProperty("storageType") String storageType, 
                     @JsonProperty("netWeight") double netWeight, 
                     @JsonProperty("organic") boolean organic, 
                     @JsonProperty("calories") int calories, 
                     @JsonProperty("quality") ShelfLifeQuality quality) {
        super(id, name, price, productionYear, ProductType.FROZEN_FOOD);
        this.brand = validateStringField(brand, "Brand");
        this.storageType = validateStorageType(storageType);
        this.netWeight = validateNetWeight(netWeight);
        this.organic = organic;
        this.calories = validateCalories(calories);
        this.quality = Objects.requireNonNull(quality, "Shelf life quality cannot be null");
    }
    
    /**
     * Validates that the storage type is supported.
     * 
     * @param type the storage type to validate
     * @return the validated storage type
     * @throws IllegalArgumentException if type is not supported
     */
    private String validateStorageType(String type) {
        if (type == null) {
            throw new NullPointerException("Storage type cannot be null");
        }
        String upperType = type.trim().toUpperCase();
        if (!VALID_STORAGE_TYPES.contains(upperType)) {
            throw new IllegalArgumentException(
                String.format("Unsupported storage type: %s. Supported types: %s", 
                    type, VALID_STORAGE_TYPES));
        }
        return upperType;
    }
    
    /**
     * Validates that the net weight is positive.
     * 
     * @param weight the net weight to validate
     * @return the validated weight
     * @throws IllegalArgumentException if weight is not positive
     */
    private double validateNetWeight(double weight) {
        if (weight <= 0.0) {
            throw new IllegalArgumentException(
                String.format("Net weight must be positive. Provided: %.2f g", weight));
        }
        if (Double.isNaN(weight) || Double.isInfinite(weight)) {
            throw new IllegalArgumentException(
                String.format("Net weight must be a valid number. Provided: %.2f g", weight));
        }
        return weight;
    }
    
    /**
     * Validates that the calorie count is non-negative.
     * 
     * @param calories the calorie count to validate
     * @return the validated count
     * @throws IllegalArgumentException if count is negative
     */
    private int validateCalories(int calories) {
        if (calories < 0) {
            throw new IllegalArgumentException(
                String.format("Calorie count cannot be negative. Provided: %d", calories));
        }
        return calories;
    }
    
    @Override
    public String getBrand() {
        return brand;
    }
    
    /**
     * Gets the brand of the frozen food.
     * @return the brand
     */
    public String getBrandName() {
        return brand;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("Frozen Food: %s by %s (%s, %.1f g, %s, %d calories)", 
            getName(), brand, storageType, netWeight, 
            organic ? "Organic" : "Non-Organic", calories);
    }
    
    @Override
    public double getDiscountRate() {
        // Organic products get a 10% discount
        return organic ? 0.10 : 0.05;
    }
    
    /**
     * Calculates estimated servings based on average serving size.
     * 
     * @return estimated number of servings
     */
    public int getEstimatedServings() {
        return (int) Math.ceil(netWeight / CALORIES_PER_SERVING_BASIS);
    }
    
    /**
     * Gets the storage type of the frozen food.
     * 
     * @return the storage type
     */
    public String getStorageType() {
        return storageType;
    }
    
    /**
     * Gets the net weight in grams.
     * 
     * @return the net weight
     */
    public double getNetWeight() {
        return netWeight;
    }
    
    /**
     * Checks if the product is organic.
     * 
     * @return true if organic
     */
    public boolean isOrganic() {
        return organic;
    }
    
    /**
     * Gets the calorie count per serving.
     * 
     * @return the calorie count
     */
    public int getCalories() {
        return calories;
    }
    
    @Override
    public ShelfLifeQuality getShelfLifeQuality() {
        return quality;
    }
    
    public String getDescription() {
        return String.format("Frozen %s food by %s. %s, %.1f grams. Estimated servings: %d. Storage: %s.",
            storageType.toLowerCase().replace("_", " "), brand, 
            organic ? "Organic certified" : "Conventional", 
            netWeight, 
            getEstimatedServings(),
            storageType);
    }
    
    // Perishable interface implementation
    @Override
    public int getShelfLifeDays() {
        return switch (quality) {
            case HIGH -> 365;  // 1 year
            case MEDIUM -> 180; // 6 months
            case LOW -> 90;     // 3 months
        };
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FrozenFood)) return false;
        if (!super.equals(obj)) return false;
        
        FrozenFood other = (FrozenFood) obj;
        return Double.compare(other.netWeight, netWeight) == 0 &&
               organic == other.organic &&
               calories == other.calories &&
               Objects.equals(brand, other.brand) &&
               Objects.equals(storageType, other.storageType) &&
               quality == other.quality;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), brand, storageType, netWeight, 
                           organic, calories, quality);
    }
    
    @Override
    public String toString() {
        return String.format("FrozenFood[ID=%s, Name='%s', Brand='%s', Price=$%.2f, ProductionYear=%d, " +
                           "Storage=%s, Weight=%.1fg, Organic=%s, Calories=%d, Quality=%s]",
            getId(), getName(), brand, getPrice(), getProductionYear(), 
            storageType, netWeight, organic, calories, quality);
    }
}