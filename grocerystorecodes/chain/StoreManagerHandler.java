package com.university.grocerystore.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store Manager-level discount approval handler.
 * Can approve discounts up to 40%.
 */
public class StoreManagerHandler extends DiscountHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreManagerHandler.class);
    private static final double MAX_DISCOUNT = 0.40; // 40%
    
    @Override
    public void handleRequest(DiscountRequest request) {
        if (request.getRequestedDiscount() <= MAX_DISCOUNT) {
            // Store Manager can approve this discount
            request.setApproved(true);
            request.setApprovedBy("Store Manager");
            LOGGER.info("Store Manager approved {}% discount for {}", 
                       request.getRequestedDiscountPercentage(), request.getProduct().getName());
        } else {
            // Store Manager cannot approve, reject the request
            request.setRejectionReason("Discount too high - exceeds Store Manager approval limit");
            LOGGER.info("Store Manager rejected discount > {}%", (MAX_DISCOUNT * 100));
        }
    }
    
    @Override
    public String getHandlerName() {
        return "StoreManagerHandler";
    }
    
    @Override
    public double getMaxDiscount() {
        return MAX_DISCOUNT;
    }
}
