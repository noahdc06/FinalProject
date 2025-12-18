package com.university.grocerystore.visitor;

import com.university.grocerystore.model.Produce;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.Snack;
import com.university.grocerystore.model.GroceryProduct;

/**
 * Concrete visitor implementation for calculating storage costs.
 * Demonstrates the Visitor pattern by providing different storage
 * cost calculations based on grocery product type.
 * 
 * <p>Storage costs vary by product type:
 * - Perishable items: $0.10 per day of shelf life
 * - Frozen items: $0.15 per day of shelf life
 * - Canned goods: $0.05 per day of shelf life
 * - Snacks: $0.08 per day of shelf life</p>
 */
public class StorageCostCalculator implements GroceryProductVisitor {
    
    private static final double PERISHABLE_RATE = 0.10; // per day of shelf life
    private static final double FROZEN_RATE = 0.15; // per day of shelf life
    private static final double CANNED_RATE = 0.05; // per day of shelf life
    private static final double SNACK_RATE = 0.08; // per day of shelf life
    
    private double totalStorageCost = 0.0;
    
    /**
     * Calculates storage cost for produce items.
     * 
     * @param produce the produce item
     */
    @Override
    public void visit(Produce produce) {
        double shelfLifeDays = produce.getShelfLifeDays();
        double cost = shelfLifeDays * PERISHABLE_RATE;
        totalStorageCost += cost;
    }
    
    /**
     * Calculates storage cost for frozen food items.
     * 
     * @param frozenFood the frozen food item
     */
    @Override
    public void visit(FrozenFood frozenFood) {
        double shelfLifeDays = frozenFood.getShelfLifeDays();
        double cost = shelfLifeDays * FROZEN_RATE;
        totalStorageCost += cost;
    }
    
    /**
     * Calculates storage cost for canned goods.
     * 
     * @param cannedGood the canned good item
     */
    @Override
    public void visit(CannedGood cannedGood) {
        double shelfLifeDays = cannedGood.getShelfLifeDays();
        double cost = shelfLifeDays * CANNED_RATE;
        totalStorageCost += cost;
    }
    
    /**
     * Calculates storage cost for snack items.
     * 
     * @param snack the snack item
     */
    @Override
    public void visit(Snack snack) {
        double shelfLifeDays = snack.getShelfLifeDays();
        double cost = shelfLifeDays * SNACK_RATE;
        totalStorageCost += cost;
    }
    
    /**
     * Gets the total storage cost calculated so far.
     * 
     * @return the total storage cost
     */
    public double getTotalStorageCost() {
        return totalStorageCost;
    }
    
    /**
     * Resets the storage cost calculator for a new calculation.
     */
    public void reset() {
        totalStorageCost = 0.0;
    }
    
    /**
     * Calculates storage cost for a single grocery product.
     * 
     * @param product the grocery product to calculate storage for
     * @return the storage cost for this product
     */
    public double calculateStorageCost(GroceryProduct product) {
        reset();
        
        // Use pattern matching or instanceof to determine the correct visit method
        if (product instanceof Produce) {
            visit((Produce) product);
        } else if (product instanceof FrozenFood) {
            visit((FrozenFood) product);
        } else if (product instanceof CannedGood) {
            visit((CannedGood) product);
        } else if (product instanceof Snack) {
            visit((Snack) product);
        } else {
            throw new IllegalArgumentException("Unknown product type: " + product.getClass().getSimpleName());
        }
        
        return totalStorageCost;
    }
    
    /**
     * Calculates storage cost for a collection of grocery products.
     * 
     * @param products array of grocery products
     * @return total storage cost for all products
     */
    public double calculateTotalStorageCost(GroceryProduct[] products) {
        reset();
        
        if (products != null) {
            for (GroceryProduct product : products) {
                if (product instanceof Produce) {
                    visit((Produce) product);
                } else if (product instanceof FrozenFood) {
                    visit((FrozenFood) product);
                } else if (product instanceof CannedGood) {
                    visit((CannedGood) product);
                } else if (product instanceof Snack) {
                    visit((Snack) product);
                }
            }
        }
        
        return totalStorageCost;
    }
}
