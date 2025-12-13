package com.university.grocerystore.model;

/**
 * Represents a canned good in the store inventory.
 * Demonstrates inheritance from GroceryProduct base class.
 */
public class CannedGood extends GroceryProduct {
    
    private final String sku;
    private final String brand;
    private final String canSize;
    private final boolean recyclable;
    private final String preservationMethod;
    private final int shelfLifeMonths;
    
    /**
     * Preservation methods for canned goods.
     */
    public enum PreservationMethod {
        CANNING("Canning"),
        RETORT_PROCESSING("Retort Processing"),
        ASEPTIC_PACKAGING("Aseptic Packaging"),
        VACUUM_SEALING("Vacuum Sealing");
        
        private final String label;
        
        PreservationMethod(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    /**
     * Creates a new CannedGood.
     * 
     * @param sku Stock Keeping Unit
     * @param name product name
     * @param brand brand name
     * @param price price per can
     * @param productionYear production year
     * @param canSize can size/dimensions
     * @param recyclable whether can is recyclable
     * @param preservationMethod preservation method used
     * @param shelfLifeMonths shelf life in months
     */
    public CannedGood(String sku, String name, String brand, double price,
                     int productionYear, String canSize, boolean recyclable, 
                     String preservationMethod, int shelfLifeMonths) {
        super(validateSku(sku), name, price, productionYear, ProductType.CANNED_GOOD);
        this.sku = this.id;
        this.brand = validateStringField(brand, "Brand");
        this.canSize = validateStringField(canSize, "Can Size");
        this.recyclable = recyclable;
        this.preservationMethod = validateStringField(preservationMethod, "Preservation Method");
        this.shelfLifeMonths = validateShelfLifeMonths(shelfLifeMonths);
    }
    
    private static String validateSku(String sku) {
        if (sku == null) {
            throw new NullPointerException("SKU cannot be null");
        }
        
        String cleaned = sku.trim().toUpperCase();
        
        if (cleaned.length() < 3 || cleaned.length() > 20) {
            throw new IllegalArgumentException(
                "SKU must be 3-20 characters. Provided: " + sku);
        }
        
        return cleaned;
    }
    
    private int validateShelfLifeMonths(int shelfLifeMonths) {
        if (shelfLifeMonths <= 0) {
            throw new IllegalArgumentException(
                "Shelf life must be positive. Provided: " + shelfLifeMonths);
        }
        if (shelfLifeMonths > 60) { // 5 years maximum
            throw new IllegalArgumentException(
                "Shelf life cannot exceed 60 months. Provided: " + shelfLifeMonths);
        }
        return shelfLifeMonths;
    }
    
    @Override
    public String getBrand() {
        return brand;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s - %s (%s) - %s, $%.2f, Shelf Life: %d months",
            brand, getName(), canSize, preservationMethod, getPrice(), shelfLifeMonths);
    }
    
    @Override
    public double getDiscountRate() {
        int currentYear = java.time.Year.now().getValue();
        int monthsSinceProduction = (currentYear - getProductionYear()) * 12;
        
        if (monthsSinceProduction > shelfLifeMonths * 0.75) {
            return 0.30;
        } else if (monthsSinceProduction > shelfLifeMonths * 0.5) {
            return 0.15;
        }
        return recyclable ? 0.05 : 0.0;
    }
    
    /**
     * Calculates remaining shelf life based on current date.
     * 
     * @return remaining shelf life in months
     */
    public int calculateRemainingShelfLife() {
        int currentYear = java.time.Year.now().getValue();
        int monthsSinceProduction = (currentYear - getProductionYear()) * 12;
        return Math.max(0, shelfLifeMonths - monthsSinceProduction);
    }
    
    /**
     * Checks if the canned good is near expiration.
     * 
     * @return true if less than 3 months of shelf life remain
     */
    public boolean isNearExpiration() {
        return calculateRemainingShelfLife() <= 3;
    }
    
    /**
     * Calculates case discount for bulk purchases.
     * 
     * @param cases number of cases (typically 12-24 cans per case)
     * @return total price with bulk discount
     */
    public double calculateBulkPrice(int cases) {
        if (cases <= 0) {
            return 0.0;
        }
        
        double basePrice = getPrice() * 12 * cases; // Assuming 12 cans per case
        
        if (cases >= 10) {
            return basePrice * 0.80; // 20% discount for 10+ cases
        } else if (cases >= 5) {
            return basePrice * 0.85; // 15% discount for 5+ cases
        } else if (cases >= 2) {
            return basePrice * 0.90; // 10% discount for 2+ cases
        }
        
        return basePrice;
    }
    
    public String getSku() {
        return sku;
    }
    
    public String getBrandName() {
        return brand;
    }
    
    public String getCanSize() {
        return canSize;
    }
    
    public boolean isRecyclable() {
        return recyclable;
    }
    
    public String getPreservationMethod() {
        return preservationMethod;
    }
    
    public int getShelfLifeMonths() {
        return shelfLifeMonths;
    }
    
    @Override
    public String toString() {
        return String.format("CannedGood[SKU=%s, Name='%s', Brand='%s', CanSize='%s', Recyclable=%s, Preservation='%s', ShelfLife=%d months, Price=$%.2f, ProductionYear=%d]",
            sku, getName(), brand, canSize, recyclable, preservationMethod, shelfLifeMonths, getPrice(), getProductionYear());
    }
}