package com.university.grocerystore.model;

/**
 * Interface for perishable grocery products.
 * Demonstrates interface segregation principle and multiple inheritance through interfaces.
 * 
 * <p>This interface defines behavior for perishable products,
 * including shelf life, expiration handling, and storage recommendations.</p>
 */
public interface Perishable {
    
    /**
     * Gets the shelf life of the product in days.
     * 
     * @return shelf life in days
     */
    int getShelfLifeDays();
    
    /**
     * Gets the shelf life quality rating.
     * 
     * @return quality rating
     */
    ShelfLifeQuality getShelfLifeQuality();
    
    /**
     * Gets storage recommendation based on product type.
     * 
     * @return storage instructions
     */
    default String getStorageRecommendation() {
        return switch (getShelfLifeQuality()) {
            case HIGH -> "Refrigerate below 4°C";
            case MEDIUM -> "Store in cool, dry place (8-15°C)";
            case LOW -> "Consume within a few days of purchase";
        };
    }
    
    /**
     * Calculates expiration date based on purchase date.
     * 
     * @param purchaseDate date of purchase
     * @return estimated expiration date
     */
    default java.time.LocalDate calculateExpirationDate(java.time.LocalDate purchaseDate) {
        return purchaseDate.plusDays(getShelfLifeDays());
    }
    
    /**
     * Checks if product is expired based on production date.
     * 
     * @param daysSinceProduction days since production
     * @return true if expired
     */
    default boolean isExpired(int daysSinceProduction) {
        return daysSinceProduction > getShelfLifeDays();
    }
    
    /**
     * Gets remaining shelf life percentage.
     * 
     * @param daysSinceProduction days since production
     * @return percentage of shelf life remaining (0-100)
     */
    default double getRemainingShelfLifePercentage(int daysSinceProduction) {
        if (daysSinceProduction < 0) {
            return 100.0;
        }
        double remaining = getShelfLifeDays() - daysSinceProduction;
        return Math.max(0.0, Math.min(100.0, (remaining / getShelfLifeDays()) * 100.0));
    }
    
    /**
     * Quality levels for perishable products.
     */
    enum ShelfLifeQuality {
        LOW("Limited Shelf Life", "Use within a few days"),
        MEDIUM("Moderate Shelf Life", "Store properly for extended freshness"),
        HIGH("Extended Shelf Life", "Can be stored for longer periods");
        
        private final String description;
        private final String recommendation;
        
        ShelfLifeQuality(String description, String recommendation) {
            this.description = description;
            this.recommendation = recommendation;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getRecommendation() {
            return recommendation;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
}