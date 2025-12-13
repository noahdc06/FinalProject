package com.university.grocerystore.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Department Manager-level discount approval handler.
 * Can approve discounts up to 25%.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class DepartmentManagerHandler extends DiscountHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentManagerHandler.class);
    private static final double MAX_DISCOUNT = 0.25; // 25%
    
    @Override
    public void handleRequest(DiscountRequest request) {
        if (request.getRequestedDiscount() <= MAX_DISCOUNT) {
            // Department Manager can approve this discount
            request.setApproved(true);
            request.setApprovedBy("Department Manager");
            LOGGER.info("Department Manager approved {}% discount for {}", 
                       request.getRequestedDiscountPercentage(), request.getProduct().getName());
        } else {
            // Department Manager cannot approve, pass to next handler
            LOGGER.info("Department Manager cannot approve discount > {}%. Passing to Store Manager.", 
                       (MAX_DISCOUNT * 100));
            passToNext(request);
        }
    }
    
    @Override
    public String getHandlerName() {
        return "DepartmentManagerHandler";
    }
    
    @Override
    public double getMaxDiscount() {
        return MAX_DISCOUNT;
    }
}