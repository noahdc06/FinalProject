package com.university.grocerystore.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supervisor-level discount approval handler.
 * Can approve discounts up to 15%.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class SupervisorHandler extends DiscountHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SupervisorHandler.class);
    private static final double MAX_DISCOUNT = 0.15; // 15%
    
    @Override
    public void handleRequest(DiscountRequest request) {
        if (request.getRequestedDiscount() <= MAX_DISCOUNT) {
            // Supervisor can approve this discount
            request.setApproved(true);
            request.setApprovedBy("Supervisor");
            LOGGER.info("Supervisor approved {}% discount for {}", 
                       request.getRequestedDiscountPercentage(), request.getProduct().getName());
        } else {
            // Supervisor cannot approve, pass to next handler
            LOGGER.info("Supervisor cannot approve discount > {}%. Passing to Department Manager.", 
                       (MAX_DISCOUNT * 100));
            passToNext(request);
        }
    }
    
    @Override
    public String getHandlerName() {
        return "SupervisorHandler";
    }
    
    @Override
    public double getMaxDiscount() {
        return MAX_DISCOUNT;
    }
}