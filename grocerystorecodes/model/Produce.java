package com.university.grocerystore.model;

/**
 * Represents a produce item that implements both GroceryProduct and Perishable interfaces.
 * Demonstrates multiple inheritance through interfaces.
 */
public class Produce extends GroceryProduct implements Perishable {
    
    private final String sku;
    private final String brand;
    private final String variety;
    private final double weight;
    private final boolean organic;
    private final String countryOfOrigin;
    private final int shelfLifeDays;
    private final ShelfLifeQuality quality;
    
    /**
     * Creates a new Produce item with full specifications.
     * 
     * @param sku SKU identifier
     * @param name product name
     * @param brand brand name
     * @param price price in dollars
     * @param productionYear production year
     * @param variety produce variety
     * @param weight weight in grams
     * @param organic true if organic certified
     * @param countryOfOrigin country of origin
     * @param quality shelf life quality level
     */
    public Produce(String sku, String name, String brand,
                   double price, int productionYear, String variety,
                   double weight, boolean organic, String countryOfOrigin,
                   ShelfLifeQuality quality) {
        super(validateSku(sku), name, price, productionYear, ProductType.PRODUCE);
        this.sku = this.id;
        this.brand = validateStringField(brand, "Brand");
        this.variety = validateStringField(variety, "Variety");
        this.weight = validateWeight(weight);
        this.organic = organic;
        this.countryOfOrigin = validateStringField(countryOfOrigin, "Country of Origin");
        this.quality = quality != null ? quality : ShelfLifeQuality.MEDIUM;
        this.shelfLifeDays = calculateShelfLifeDays();
    }
    
    private static String validateSku(String sku) {
        if (sku == null) {
            throw new NullPointerException("SKU cannot be null");
        }
        String cleaned = sku.trim();
        if (cleaned.length() < 3 || cleaned.length() > 20) {
            throw new IllegalArgumentException("Invalid SKU length: " + sku);
        }
        return cleaned;
    }
    
    private double validateWeight(double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException(
                "Weight must be positive. Provided: " + weight);
        }
        return weight;
    }
    
    private int calculateShelfLifeDays() {
        return switch (quality) {
            case HIGH -> 14;  // 2 weeks
            case MEDIUM -> 7;  // 1 week
            case LOW -> 3;     // 3 days
        };
    }
    
    @Override
    public String getBrand() {
        return brand;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s %s - %s, %.2fg, %s, $%.2f",
            brand, name, variety, weight,
            organic ? "Organic" : "Conventional", price);
    }
    
    @Override
    public int getShelfLifeDays() {
        return shelfLifeDays;
    }
    
    @Override
    public ShelfLifeQuality getShelfLifeQuality() {
        return quality;
    }
    
    @Override
    public double getDiscountRate() {
        if (quality == ShelfLifeQuality.LOW) {
            return 0.20;
        }
        return organic ? 0.05 : 0.0;
    }
    
    /**
     * Calculates estimated shelf life based on current date.
     * 
     * @param daysSinceProduction days since production
     * @return remaining shelf life in days
     */
    public int getRemainingShelfLife(int daysSinceProduction) {
        return Math.max(0, shelfLifeDays - daysSinceProduction);
    }
    
    /**
     * Checks if the produce is expired.
     * 
     * @param daysSinceProduction days since production
     * @return true if expired
     */
    public boolean isExpired(int daysSinceProduction) {
        return daysSinceProduction > shelfLifeDays;
    }
    
    /**
     * Calculates recommended storage temperature.
     * 
     * @return recommended temperature in Celsius
     */
    public double getRecommendedStorageTemperature() {
        return switch (quality) {
            case HIGH -> 4.0;   // Refrigerated
            case MEDIUM -> 8.0;  // Cool storage
            case LOW -> 15.0;    // Room temperature
        };
    }
    
    /** Gets the SKU. @return the SKU */
    public String getSku() {
        return sku;
    }
    
    /** Gets the variety. @return the variety */
    public String getVariety() {
        return variety;
    }
    
    /** Gets the weight. @return the weight in grams */
    public double getWeight() {
        return weight;
    }
    
    /** Checks if organic. @return true if organic */
    public boolean isOrganic() {
        return organic;
    }
    
    /** Gets the country of origin. @return the country of origin */
    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }
    
    @Override
    public String toString() {
        return String.format("Produce[SKU=%s, Name='%s', Brand='%s', Variety='%s', Weight=%.2fg, %s, Origin='%s', Quality=%s, Price=$%.2f]",
            sku, name, brand, variety, weight,
            organic ? "Organic" : "Conventional", countryOfOrigin, quality, price);
    }
}