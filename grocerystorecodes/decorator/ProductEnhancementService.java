package com.university.grocerystore.decorator;

import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;

/**
 * Service for managing product enhancements using the Decorator pattern.
 * Provides methods to apply various decorators to products and create enhancement packages.
 * 
 * <p>This service demonstrates the Decorator pattern in action by providing
 * a high-level interface for dynamically adding features to products.</p>
 */
public class ProductEnhancementService {
    
    /**
     * Adds gift wrapping to a product.
     * 
     * @param product the product to enhance
     * @param style the wrapping style
     * @return the enhanced product with gift wrapping
     * @throws IllegalArgumentException if product or style is null
     */
    public GroceryProduct addGiftWrapping(GroceryProduct product, String style) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (style == null || style.trim().isEmpty()) {
            throw new IllegalArgumentException("Wrapping style cannot be null or empty");
        }
        
        return new GiftWrappingDecorator(product, style);
    }
    
    /**
     * Adds expedited delivery to a product.
     * 
     * @param product the product to enhance
     * @param deliveryHours the number of hours for delivery
     * @return the enhanced product with expedited delivery
     * @throws IllegalArgumentException if product is null or deliveryHours is invalid
     */
    public GroceryProduct addExpeditedDelivery(GroceryProduct product, int deliveryHours) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (deliveryHours < 1) {
            throw new IllegalArgumentException("Delivery hours must be at least 1: " + deliveryHours);
        }
        
        return new ExpeditedDeliveryDecorator(product, deliveryHours);
    }
    
    /**
     * Adds organic certification to a product.
     * 
     * @param product the product to enhance
     * @return the enhanced product with organic certification
     * @throws IllegalArgumentException if product is null
     */
    public GroceryProduct addOrganicCertification(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        return new OrganicCertificationDecorator(product);
    }
    
    /**
     * Creates a premium package with multiple enhancements.
     * 
     * @param product the product to enhance
     * @param giftStyle the gift wrapping style
     * @param deliveryHours the number of hours for delivery
     * @return the enhanced product with premium package
     * @throws IllegalArgumentException if product is null or parameters are invalid
     */
    public GroceryProduct createPremiumPackage(GroceryProduct product, String giftStyle, int deliveryHours) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        GroceryProduct enhanced = product;
        enhanced = addGiftWrapping(enhanced, giftStyle);
        enhanced = addExpeditedDelivery(enhanced, deliveryHours);
        
        // Add organic certification if it's a frozen food
        if (product instanceof FrozenFood) {
            enhanced = addOrganicCertification(enhanced);
        }
        
        return enhanced;
    }
    
    /**
     * Creates a gift package with gift wrapping and expedited delivery.
     * 
     * @param product the product to enhance
     * @param giftStyle the gift wrapping style
     * @param deliveryHours the number of hours for delivery
     * @return the enhanced product with gift package
     */
    public GroceryProduct createGiftPackage(GroceryProduct product, String giftStyle, int deliveryHours) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        GroceryProduct enhanced = product;
        enhanced = addGiftWrapping(enhanced, giftStyle);
        enhanced = addExpeditedDelivery(enhanced, deliveryHours);
        
        return enhanced;
    }
    
    /**
     * Creates an organic package with organic certification (for frozen foods only).
     * 
     * @param product the product to enhance
     * @return the enhanced product with organic package, or original if not a frozen food
     */
    public GroceryProduct createOrganicPackage(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (product instanceof FrozenFood) {
            return addOrganicCertification(product);
        }
        
        return product; // Return original if not a frozen food
    }
    
    /**
     * Calculates the total enhancement cost for a product.
     * 
     * @param original the original product
     * @param enhanced the enhanced product
     * @return the total enhancement cost
     */
    public double calculateEnhancementCost(GroceryProduct original, GroceryProduct enhanced) {
        if (original == null || enhanced == null) {
            throw new IllegalArgumentException("Products cannot be null");
        }
        
        return enhanced.getPrice() - original.getPrice();
    }
    
    /**
     * Gets a summary of enhancements applied to a product.
     * 
     * @param product the product to analyze
     * @return enhancement summary
     */
    public String getEnhancementSummary(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        StringBuilder summary = new StringBuilder();
        
        // Check if there are any enhancements
        if (!hasEnhancements(product)) {
            summary.append("No enhancements applied");
            return summary.toString();
        }
        
        summary.append("Enhancements:\n");
        
        // Check for decorators by examining the display info
        String displayInfo = product.getDisplayInfo();
        if (displayInfo.contains("Gift Wrapped")) {
            summary.append("// [OK] Gift Wrapping\n");
        }
        if (displayInfo.contains("Expedited Delivery")) {
            summary.append("// [OK] Expedited Delivery\n");
        }
        if (displayInfo.contains("Organic Certified")) {
            summary.append("// [OK] Organic Certification\n");
        }
        
        double enhancementCost = product.getPrice() - getBasePrice(product);
        summary.append("Total Cost: $").append(String.format("%.2f", product.getPrice()));
        
        return summary.toString();
    }
    
    /**
     * Gets the base price of a product (without decorators).
     * 
     * @param product the product to analyze
     * @return the base price
     */
    public double getBasePrice(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        GroceryProduct current = product;
        while (current instanceof ProductDecorator) {
            current = ((ProductDecorator) current).getDecoratedProduct();
        }
        
        return current.getPrice();
    }
    
    /**
     * Checks if a product has any enhancements applied.
     * 
     * @param product the product to check
     * @return true if enhancements are applied
     */
    public boolean hasEnhancements(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        return product instanceof ProductDecorator;
    }
    
    /**
     * Gets the number of decorators applied to a product.
     * 
     * @param product the product to analyze
     * @return the decorator count
     */
    public int getEnhancementCount(GroceryProduct product) {
        if (product == null) {
            return 0;
        }
        
        int count = 0;
        GroceryProduct current = product;
        while (current instanceof ProductDecorator) {
            count++;
            current = ((ProductDecorator) current).getDecoratedProduct();
        }
        
        return count;
    }
    
    /**
     * Gets the base product (unwraps all decorators).
     * 
     * @param product the product to unwrap
     * @return the base product
     */
    public GroceryProduct getBaseProduct(GroceryProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        GroceryProduct current = product;
        while (current instanceof ProductDecorator) {
            current = ((ProductDecorator) current).getDecoratedProduct();
        }
        
        return current;
    }
    
    /**
     * Checks if a product has a specific type of enhancement.
     * 
     * @param product the product to check
     * @param enhancementType the type of enhancement to look for
     * @return true if the enhancement is applied
     */
    public boolean hasEnhancement(GroceryProduct product, Class<? extends ProductDecorator> enhancementType) {
        if (product == null || enhancementType == null) {
            return false;
        }
        
        GroceryProduct current = product;
        while (current instanceof ProductDecorator) {
            if (enhancementType.isInstance(current)) {
                return true;
            }
            current = ((ProductDecorator) current).getDecoratedProduct();
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return "ProductEnhancementService[Available enhancements: Gift Wrapping, Expedited Delivery, Organic Certification]";
    }
}
