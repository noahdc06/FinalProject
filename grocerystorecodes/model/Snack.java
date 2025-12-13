package com.university.grocerystore.model;

import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a snack food item in the grocery store inventory.
 * Extends GroceryProduct class to demonstrate inheritance.
 * 
 * <p>This class maintains consistent structure with the product hierarchy
 * while providing snack-specific functionality.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Snack extends GroceryProduct {
    
    private static final Pattern UPC_PATTERN = Pattern.compile("^\\d{12}$");
    private static final int CALORIES_PER_SERVING_BASIS = 150;
    
    private final String upc;
    private final String brand;
    private final String snackType;
    private final double netWeight;
    private final int calories;
    private final String flavor;
    private final String dietaryInfo;
    
    /**
     * Creates a new Snack with validation.
     * 
     * @param upc the Universal Product Code
     * @param name the snack name
     * @param brand the brand name
     * @param price the price in dollars
     * @param productionYear the production year
     * @param snackType type of snack (chips, cookies, etc.)
     * @param netWeight net weight in grams
     * @param calories calorie count per serving
     * @param flavor snack flavor
     * @param dietaryInfo dietary information
     */
    @JsonCreator
    public Snack(@JsonProperty("id") String upc, 
                @JsonProperty("name") String name, 
                @JsonProperty("brand") String brand, 
                @JsonProperty("price") double price, 
                @JsonProperty("productionYear") int productionYear, 
                @JsonProperty("snackType") String snackType, 
                @JsonProperty("netWeight") double netWeight, 
                @JsonProperty("calories") int calories, 
                @JsonProperty("flavor") String flavor, 
                @JsonProperty("dietaryInfo") String dietaryInfo) {
        super(validateUpc(upc), name, price, productionYear, ProductType.SNACK);
        this.upc = this.id;
        this.brand = validateStringField(brand, "Brand");
        this.snackType = validateStringField(snackType, "Snack Type");
        this.netWeight = validateNetWeight(netWeight);
        this.calories = validateCalories(calories);
        this.flavor = validateStringField(flavor, "Flavor");
        this.dietaryInfo = dietaryInfo != null ? dietaryInfo : "No dietary restrictions";
    }
    
    /**
     * Convenience constructor for basic snack creation.
     */
    public Snack(String upc, String name, String brand, double price, int productionYear) {
        this(upc, name, brand, price, productionYear, "Snack", 0, 0, "Original", "No dietary restrictions");
    }
    
    private static String validateUpc(String upc) {
        if (upc == null) {
            throw new NullPointerException("UPC cannot be null");
        }
        
        String cleaned = upc.replaceAll("-", "").trim();
        
        if (!UPC_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException(
                "UPC must be 12 digits. Provided: " + upc);
        }
        
        return cleaned;
    }
    
    private double validateNetWeight(double netWeight) {
        if (netWeight < 0) {
            throw new IllegalArgumentException("Net weight cannot be negative");
        }
        return netWeight;
    }
    
    private int validateCalories(int calories) {
        if (calories < 0) {
            throw new IllegalArgumentException("Calories cannot be negative");
        }
        return calories;
    }
    
    @Override
    public String getBrand() {
        return brand;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s %s - %s flavor, %.2fg, %d calories, $%.2f",
            brand, getName(), flavor, netWeight, calories, getPrice());
    }
    
    @Override
    public double getDiscountRate() {
        int currentYear = java.time.Year.now().getValue();
        int monthsSinceProduction = (currentYear - getProductionYear()) * 12;
        
        if (monthsSinceProduction > 3) {
            return 0.20;
        } else if (monthsSinceProduction > 1) {
            return 0.10;
        }
        return dietaryInfo.toLowerCase().contains("organic") ? 0.05 : 0.0;
    }
    
    public String getUpc() {
        return upc;
    }
    
    public String getBrandName() {
        return brand;
    }
    
    public String getSnackType() {
        return snackType;
    }
    
    public double getNetWeight() {
        return netWeight;
    }
    
    public int getCalories() {
        return calories;
    }
    
    public String getFlavor() {
        return flavor;
    }
    
    public String getDietaryInfo() {
        return dietaryInfo;
    }
    
    /**
     * Calculates estimated servings based on standard serving sizes.
     * 
     * @return estimated number of servings per package
     */
    public int estimateServings() {
        if (netWeight == 0) {
            return 1;
        }
        int standardServingSize = 30; // 30g is a typical snack serving
        return (int) Math.max(1, Math.ceil(netWeight / standardServingSize));
    }
    
    /**
     * Calculates calories per gram for nutritional comparison.
     * 
     * @return calories per gram
     */
    public double getCaloriesPerGram() {
        if (netWeight == 0) {
            return 0.0;
        }
        return (double) calories / netWeight;
    }
    
    /**
     * Checks if snack meets specific dietary requirements.
     * 
     * @param requirement dietary requirement (e.g., "gluten-free", "vegan")
     * @return true if snack meets the requirement
     */
    public boolean meetsDietaryRequirement(String requirement) {
        if (requirement == null || requirement.trim().isEmpty()) {
            return true;
        }
        return dietaryInfo.toLowerCase().contains(requirement.toLowerCase().trim());
    }
    
    @Override
    public String toString() {
        return String.format("Snack[UPC=%s, Name='%s', Brand='%s', Price=$%.2f, ProductionYear=%d, Type='%s', Weight=%.2fg, Calories=%d, Flavor='%s', Dietary='%s']",
            upc, getName(), brand, getPrice(), getProductionYear(), snackType, netWeight, calories, flavor, dietaryInfo);
    }
}