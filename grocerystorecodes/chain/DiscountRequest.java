package com.university.grocerystore.chain;

import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Request object for discount approval in the Chain of Responsibility pattern.
 * Contains all information needed to process a discount request.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class DiscountRequest {
    
    private final GroceryProduct product;
    private final double requestedDiscount;
    private final String customerId;
    private final String reason;
    private boolean approved;
    private String approvedBy;
    private String rejectionReason;
    private final long timestamp;
    
    /**
     * Creates a new discount request.
     * 
     * @param product the product for which discount is requested
     * @param requestedDiscount the requested discount rate (0.0 to 1.0)
     * @param customerId the customer requesting the discount
     * @param reason the reason for the discount request
     * @throws IllegalArgumentException if parameters are invalid
     */
    public DiscountRequest(GroceryProduct product, double requestedDiscount, 
                          String customerId, String reason) {
        this.product = Objects.requireNonNull(product, "Product cannot be null");
        this.requestedDiscount = Math.max(0.0, Math.min(1.0, requestedDiscount));
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
        this.approved = false;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Gets the product for which discount is requested.
     * 
     * @return the product
     */
    public GroceryProduct getProduct() {
        return product;
    }
    
    /**
     * Gets the requested discount rate.
     * 
     * @return the discount rate (0.0 to 1.0)
     */
    public double getRequestedDiscount() {
        return requestedDiscount;
    }
    
    /**
     * Gets the requested discount percentage.
     * 
     * @return the discount percentage (0.0 to 100.0)
     */
    public double getRequestedDiscountPercentage() {
        return requestedDiscount * 100.0;
    }
    
    /**
     * Gets the customer ID.
     * 
     * @return the customer ID
     */
    public String getCustomerId() {
        return customerId;
    }
    
    /**
     * Gets the reason for the discount request.
     * 
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
    
    /**
     * Checks if the request is approved.
     * 
     * @return true if approved
     */
    public boolean isApproved() {
        return approved;
    }
    
    /**
     * Sets the approval status.
     * 
     * @param approved the approval status
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    
    /**
     * Sets who approved the request.
     * 
     * @param approvedBy the approver name
     */
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    /**
     * Gets who approved the request.
     * 
     * @return the approver name
     */
    public String getApprovedBy() {
        return approvedBy;
    }
    
    /**
     * Gets the rejection reason.
     * 
     * @return the rejection reason
     */
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    /**
     * Sets the rejection reason.
     * 
     * @param rejectionReason the rejection reason
     */
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    /**
     * Gets the request timestamp.
     * 
     * @return the timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Calculates the discounted price if approved.
     * 
     * @return the discounted price
     */
    public double getDiscountedPrice() {
        if (approved) {
            return product.getPrice() * (1.0 - requestedDiscount);
        }
        return product.getPrice();
    }
    
    /**
     * Calculates the savings amount if approved.
     * 
     * @return the savings amount
     */
    public double getSavingsAmount() {
        if (approved) {
            return product.getPrice() * requestedDiscount;
        }
        return 0.0;
    }
    
    /**
     * Gets a summary of the discount request.
     * 
     * @return the request summary
     */
    public String getSummary() {
        return String.format("Discount Request: %s for %s (%.1f%%) - %s",
            product.getName(), customerId, getRequestedDiscountPercentage(), reason);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        DiscountRequest that = (DiscountRequest) obj;
        return Double.compare(that.requestedDiscount, requestedDiscount) == 0 &&
               approved == that.approved &&
               timestamp == that.timestamp &&
               Objects.equals(product, that.product) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(reason, that.reason) &&
               Objects.equals(approvedBy, that.approvedBy) &&
               Objects.equals(rejectionReason, that.rejectionReason);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(product, requestedDiscount, customerId, reason, 
                          approved, approvedBy, rejectionReason, timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("DiscountRequest[%s, %.1f%%, %s, %s, Approved=%s]",
            product.getName(), getRequestedDiscountPercentage(), customerId, reason, approved);
    }
}