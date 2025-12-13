package com.university.grocerystore.decorator;

import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Decorator that adds expedited delivery functionality to products.
 * Increases the price and provides delivery time information.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class ExpeditedDeliveryDecorator extends ProductDecorator {
    
    private static final double EXPEDITED_COST = 9.99;
    private final int deliveryHours;
    
    /**
     * Creates a new expedited delivery decorator.
     * 
     * @param product the product to add expedited delivery to
     * @param deliveryHours the number of hours for delivery (minimum 1)
     * @throws IllegalArgumentException if deliveryHours is less than 1
     */
    public ExpeditedDeliveryDecorator(GroceryProduct product, int deliveryHours) {
        super(product);
        if (deliveryHours < 1) {
            throw new IllegalArgumentException("Delivery hours must be at least 1: " + deliveryHours);
        }
        this.deliveryHours = deliveryHours;
    }
    
    @Override
    public double getPrice() {
        return decoratedProduct.getPrice() + EXPEDITED_COST;
    }
    
    @Override
    public String getDisplayInfo() {
        return decoratedProduct.getDisplayInfo() + 
               String.format(" [Expedited Delivery: %d hours (+$%.2f)]", 
                           deliveryHours, EXPEDITED_COST);
    }
    
    /**
     * Gets the delivery time in hours.
     * 
     * @return the delivery hours
     */
    public int getDeliveryHours() {
        return deliveryHours;
    }
    
    /**
     * Gets the expedited delivery cost.
     * 
     * @return the expedited delivery cost
     */
    public double getExpeditedDeliveryCost() {
        return EXPEDITED_COST;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        
        ExpeditedDeliveryDecorator that = (ExpeditedDeliveryDecorator) obj;
        return deliveryHours == that.deliveryHours;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deliveryHours);
    }
}