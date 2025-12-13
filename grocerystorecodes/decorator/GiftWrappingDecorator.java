package com.university.grocerystore.decorator;

import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Decorator that adds gift wrapping functionality to products.
 * Increases the price and provides gift wrapping information.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class GiftWrappingDecorator extends ProductDecorator {
    
    private static final double GIFT_WRAPPING_COST = 4.99;
    private final String wrappingStyle;
    
    /**
     * Creates a new gift wrapping decorator.
     * 
     * @param product the product to wrap
     * @param wrappingStyle the style of gift wrapping
     * @throws IllegalArgumentException if wrappingStyle is null or empty
     */
    public GiftWrappingDecorator(GroceryProduct product, String wrappingStyle) {
        super(product);
        if (wrappingStyle == null || wrappingStyle.trim().isEmpty()) {
            throw new IllegalArgumentException("Wrapping style cannot be null or empty");
        }
        this.wrappingStyle = wrappingStyle.trim();
    }
    
    @Override
    public double getPrice() {
        return decoratedProduct.getPrice() + GIFT_WRAPPING_COST;
    }
    
    @Override
    public String getDisplayInfo() {
        return decoratedProduct.getDisplayInfo() + 
               String.format(" [Gift Wrapped: %s (+$%.2f)]", 
                           wrappingStyle, GIFT_WRAPPING_COST);
    }
    
    /**
     * Gets the wrapping style.
     * 
     * @return the wrapping style
     */
    public String getWrappingStyle() {
        return wrappingStyle;
    }
    
    /**
     * Gets the gift wrapping cost.
     * 
     * @return the gift wrapping cost
     */
    public double getGiftWrappingCost() {
        return GIFT_WRAPPING_COST;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        
        GiftWrappingDecorator that = (GiftWrappingDecorator) obj;
        return Objects.equals(wrappingStyle, that.wrappingStyle);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), wrappingStyle);
    }
}