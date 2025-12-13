package com.university.grocerystore.visitor;

import com.university.grocerystore.model.Produce;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.Snack;

/**
 * Visitor interface for implementing the Visitor pattern.
 * Allows adding new operations to the GroceryProduct hierarchy without
 * modifying existing classes.
 * 
 * <p>This pattern is particularly useful for operations that need
 * to behave differently based on the concrete type of GroceryProduct.</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public interface GroceryProductVisitor {
    
    /**
     * Visits a Produce item.
     * 
     * @param produce the produce item to visit
     */
    void visit(Produce produce);
    
    /**
     * Visits a FrozenFood item.
     * 
     * @param frozenFood the frozen food item to visit
     */
    void visit(FrozenFood frozenFood);
    
    /**
     * Visits a CannedGood item.
     * 
     * @param cannedGood the canned good item to visit
     */
    void visit(CannedGood cannedGood);
    
    /**
     * Visits a Snack item.
     * 
     * @param snack the snack item to visit
     */
    void visit(Snack snack);
}