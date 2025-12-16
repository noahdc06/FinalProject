package com.university.grocerystore.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Service for managing discount approval using the Chain of Responsibility pattern.
 * Builds and manages the approval chain and processes discount requests.
 * 
 * <p>This service demonstrates the Chain of Responsibility pattern by creating
 * a hierarchy of approval handlers and processing requests through the chain.</p>
 */
public class DiscountApprovalService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountApprovalService.class);
    private final DiscountHandler chain;
    private final List<DiscountRequest> processedRequests;
    
    /**
     * Creates a new discount approval service with the default chain.
     */
    public DiscountApprovalService() {
        this.chain = buildDefaultChain();
        this.processedRequests = new ArrayList<>();
    }
    
    /**
     * Creates a new discount approval service with a custom chain.
     * 
     * @param chain the approval chain to use
     */
    public DiscountApprovalService(DiscountHandler chain) {
        this.chain = Objects.requireNonNull(chain, "Chain cannot be null");
        this.processedRequests = new ArrayList<>();
    }
    
    /**
     * Processes a discount request through the approval chain.
     * 
     * @param product the product for which discount is requested
     * @param discountPercent the discount percentage (0.0 to 100.0)
     * @param customerId the customer requesting the discount
     * @param reason the reason for the discount request
     * @return the processed discount request
     */
    public DiscountRequest requestDiscount(GroceryProduct product, double discountPercent, 
                                         String customerId, String reason) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be null or empty");
        }
        
        // Convert percentage to decimal
        double discountRate = discountPercent / 100.0;
        
        DiscountRequest request = new DiscountRequest(product, discountRate, customerId, reason);
        
        LOGGER.info("Processing discount request:");
        LOGGER.info("Product: {}", product.getName());
        LOGGER.info("Requested discount: {}%", discountPercent);
        LOGGER.info("Customer: {}", customerId);
        LOGGER.info("Reason: {}", reason);
        LOGGER.info("---");
        
        // Process through the chain
        chain.handleRequest(request);
        
        // Store the processed request
        processedRequests.add(request);
        
        // Print result
        if (request.isApproved()) {
            LOGGER.info("// [OK] APPROVED by {}", request.getApprovedBy());
            LOGGER.info("Final price: ${}", String.format("%.2f", request.getDiscountedPrice()));
            LOGGER.info("Savings: ${}", String.format("%.2f", request.getSavingsAmount()));
        } else {
            LOGGER.info("[X] REJECTED: {}", request.getRejectionReason());
        }
        
        return request;
    }
    
    /**
     * Calculates the final price for an approved discount request.
     * 
     * @param request the discount request
     * @return the final price
     */
    public double calculateFinalPrice(DiscountRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        
        return request.getDiscountedPrice();
    }
    
    /**
     * Gets all processed discount requests.
     * 
     * @return list of processed requests
     */
    public List<DiscountRequest> getProcessedRequests() {
        return new ArrayList<>(processedRequests);
    }
    
    /**
     * Gets approved discount requests.
     * 
     * @return list of approved requests
     */
    public List<DiscountRequest> getApprovedRequests() {
        return processedRequests.stream()
            .filter(DiscountRequest::isApproved)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets rejected discount requests.
     * 
     * @return list of rejected requests
     */
    public List<DiscountRequest> getRejectedRequests() {
        return processedRequests.stream()
            .filter(request -> !request.isApproved())
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets discount requests for a specific customer.
     * 
     * @param customerId the customer ID
     * @return list of requests for the customer
     */
    public List<DiscountRequest> getRequestsForCustomer(String customerId) {
        return processedRequests.stream()
            .filter(request -> Objects.equals(request.getCustomerId(), customerId))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets discount requests for a specific product.
     * 
     * @param product the product
     * @return list of requests for the product
     */
    public List<DiscountRequest> getRequestsForProduct(GroceryProduct product) {
        return processedRequests.stream()
            .filter(request -> Objects.equals(request.getProduct(), product))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets approval statistics.
     * 
     * @return approval statistics
     */
    public ApprovalStats getApprovalStats() {
        int totalRequests = processedRequests.size();
        int approvedRequests = (int) processedRequests.stream()
            .filter(DiscountRequest::isApproved)
            .count();
        int rejectedRequests = totalRequests - approvedRequests;
        
        double approvalRate = totalRequests > 0 ? (double) approvedRequests / totalRequests : 0.0;
        
        double totalSavings = processedRequests.stream()
            .filter(DiscountRequest::isApproved)
            .mapToDouble(DiscountRequest::getSavingsAmount)
            .sum();
        
        return new ApprovalStats(
            totalRequests,
            approvedRequests,
            rejectedRequests,
            approvalRate,
            totalSavings
        );
    }
    
    /**
     * Clears all processed requests.
     */
    public void clearRequests() {
        processedRequests.clear();
    }
    
    /**
     * Gets the approval chain information.
     * 
     * @return chain information
     */
    public String getChainInfo() {
        return chain.getHandlerInfo();
    }
    
    /**
     * Builds the default approval chain: Supervisor -> Department Manager -> Store Manager.
     * 
     * @return the default approval chain
     */
    private DiscountHandler buildDefaultChain() {
        DiscountHandler supervisor = new SupervisorHandler();
        DiscountHandler deptManager = new DepartmentManagerHandler();
        DiscountHandler storeManager = new StoreManagerHandler();
        
        // Set up the chain
        supervisor.setNext(deptManager);
        deptManager.setNext(storeManager);
        
        return supervisor;
    }
    
    /**
     * Statistics class for approval analysis.
     */
    public static class ApprovalStats {
        private final int totalRequests;
        private final int approvedRequests;
        private final int rejectedRequests;
        private final double approvalRate;
        private final double totalSavings;
        
        public ApprovalStats(int totalRequests, int approvedRequests, int rejectedRequests,
                           double approvalRate, double totalSavings) {
            this.totalRequests = totalRequests;
            this.approvedRequests = approvedRequests;
            this.rejectedRequests = rejectedRequests;
            this.approvalRate = approvalRate;
            this.totalSavings = totalSavings;
        }
        
        public int getTotalRequests() { return totalRequests; }
        public int getApprovedRequests() { return approvedRequests; }
        public int getRejectedRequests() { return rejectedRequests; }
        public double getApprovalRate() { return approvalRate; }
        public double getTotalSavings() { return totalSavings; }
        
        @Override
        public String toString() {
            return String.format("ApprovalStats[Total=%d, Approved=%d, Rejected=%d, Rate=%.1f%%, Savings=$%.2f]",
                totalRequests, approvedRequests, rejectedRequests, approvalRate * 100, totalSavings);
        }
    }
    
    @Override
    public String toString() {
        return String.format("DiscountApprovalService[Chain=%s, Requests=%d]",
            chain.getHandlerName(), processedRequests.size());
    }
}
