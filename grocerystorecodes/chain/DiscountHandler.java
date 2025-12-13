package com.university.grocerystore.chain;

/**
 * Abstract base class for discount approval handlers in the Chain of Responsibility pattern.
 * Defines the interface for handling discount requests and managing the chain.
 * 
 * <p>This class provides the basic structure for the chain of responsibility,
 * allowing handlers to either process requests or pass them to the next handler.</p>
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public abstract class DiscountHandler {
    
    protected DiscountHandler nextHandler;
    
    /**
     * Sets the next handler in the chain.
     * 
     * @param nextHandler the next handler
     */
    public void setNext(DiscountHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    
    /**
     * Gets the next handler in the chain.
     * 
     * @return the next handler
     */
    public DiscountHandler getNext() {
        return nextHandler;
    }
    
    /**
     * Handles a discount request.
     * Subclasses must implement this method to define their approval logic.
     * 
     * @param request the discount request to handle
     */
    public abstract void handleRequest(DiscountRequest request);
    
    /**
     * Passes the request to the next handler in the chain.
     * 
     * @param request the request to pass
     */
    protected void passToNext(DiscountRequest request) {
        if (nextHandler != null) {
            nextHandler.handleRequest(request);
        } else {
            // End of chain - reject if not approved
            if (!request.isApproved()) {
                request.setRejectionReason("No handler could approve this discount");
            }
        }
    }
    
    /**
     * Gets the name of this handler for identification purposes.
     * 
     * @return the handler name
     */
    public abstract String getHandlerName();
    
    /**
     * Gets the maximum discount this handler can approve.
     * 
     * @return the maximum discount rate (0.0 to 1.0)
     */
    public abstract double getMaxDiscount();
    
    /**
     * Checks if this handler can approve the given discount amount.
     * 
     * @param discount the discount rate to check
     * @return true if this handler can approve the discount
     */
    public boolean canApprove(double discount) {
        return discount <= getMaxDiscount();
    }
    
    /**
     * Gets the chain length from this handler to the end.
     * 
     * @return the chain length
     */
    public int getChainLength() {
        int length = 1;
        DiscountHandler current = nextHandler;
        while (current != null) {
            length++;
            current = current.getNext();
        }
        return length;
    }
    
    /**
     * Gets information about this handler and the chain.
     * 
     * @return handler information
     */
    public String getHandlerInfo() {
        return String.format("%s[MaxDiscount=%.1f%%, ChainLength=%d]",
            getHandlerName(), getMaxDiscount() * 100, getChainLength());
    }
    
    @Override
    public String toString() {
        return getHandlerInfo();
    }
}