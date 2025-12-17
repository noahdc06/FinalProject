package com.university.grocerystore.chain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.grocerystore.model.GroceryProduct;

@ExtendWith(MockitoExtension.class)
class ChainOfResponsibilityTests {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainOfResponsibilityTests.class);
    
    @Mock
    private GroceryProduct mockProduct;
    
    private DiscountApprovalService approvalService;
    
    @BeforeEach
    void setUp() {
        when(mockProduct.getName()).thenReturn("Test Product");
        when(mockProduct.getPrice()).thenReturn(100.0);
        approvalService = new DiscountApprovalService();
    }
    
    // Test for DiscountApprovalService
    @Test
    void testDiscountApprovalService_DefaultChainCreation() {
        String chainInfo = approvalService.getChainInfo();
        assertTrue(chainInfo.contains("SupervisorHandler"));
        assertTrue(chainInfo.contains("DepartmentManagerHandler"));
        assertTrue(chainInfo.contains("StoreManagerHandler"));
        LOGGER.info("Default chain created: {}", chainInfo);
    }
    
    // Test for DiscountHandler
    @Test
    void testDiscountHandler_ChainSetupAndPassing() {
        DiscountHandler supervisor = new SupervisorHandler();
        DiscountHandler deptManager = new DepartmentManagerHandler();
        DiscountHandler storeManager = new StoreManagerHandler();
        
        supervisor.setNext(deptManager);
        deptManager.setNext(storeManager);
        
        assertEquals(deptManager, supervisor.getNext());
        assertEquals(storeManager, deptManager.getNext());
        assertEquals(3, supervisor.getChainLength());
        LOGGER.info("Chain setup correctly with length: {}", supervisor.getChainLength());
    }
    
    // Test for SupervisorHandler
    @Test
    void testSupervisorHandler_Approve15PercentDiscount() {
        SupervisorHandler supervisor = new SupervisorHandler();
        DiscountRequest request = new DiscountRequest(mockProduct, 0.15, "CUST001", "Test");
        
        supervisor.handleRequest(request);
        
        assertTrue(request.isApproved());
        assertEquals("Supervisor", request.getApprovedBy());
        assertEquals(85.0, request.getDiscountedPrice(), 0.001);
        LOGGER.info("Supervisor approved 15% discount");
    }
    
    // Test for DepartmentManagerHandler
    @Test
    void testDepartmentManagerHandler_Approve25PercentDiscount() {
        DepartmentManagerHandler deptManager = new DepartmentManagerHandler();
        DiscountRequest request = new DiscountRequest(mockProduct, 0.25, "CUST001", "Test");
        
        deptManager.handleRequest(request);
        
        assertTrue(request.isApproved());
        assertEquals("Department Manager", request.getApprovedBy());
        assertEquals(75.0, request.getDiscountedPrice(), 0.001);
        LOGGER.info("Department Manager approved 25% discount");
    }
    
    // Test for StoreManagerHandler
    @Test
    void testStoreManagerHandler_Approve40PercentDiscount() {
        StoreManagerHandler storeManager = new StoreManagerHandler();
        DiscountRequest request = new DiscountRequest(mockProduct, 0.40, "CUST001", "Test");
        
        storeManager.handleRequest(request);
        
        assertTrue(request.isApproved());
        assertEquals("Store Manager", request.getApprovedBy());
        assertEquals(60.0, request.getDiscountedPrice(), 0.001);
        LOGGER.info("Store Manager approved 40% discount");
    }
    
    // Test for DiscountRequest
    @Test
    void testDiscountRequest_ObjectCreationAndValidation() {
        DiscountRequest request = new DiscountRequest(mockProduct, 0.30, "CUST001", "Bulk purchase");
        
        assertEquals(mockProduct, request.getProduct());
        assertEquals(0.30, request.getRequestedDiscount(), 0.001);
        assertEquals(30.0, request.getRequestedDiscountPercentage(), 0.001);
        assertEquals("CUST001", request.getCustomerId());
        assertEquals("Bulk purchase", request.getReason());
        assertFalse(request.isApproved());
        assertNotNull(request.getTimestamp());
        
        LOGGER.info("DiscountRequest created: {}", request);
    }
    
    // Test chain flow: Supervisor -> Department Manager
    @Test
    void testChainFlow_SupervisorPassesToDepartmentManager() {
        DiscountHandler supervisor = new SupervisorHandler();
        DiscountHandler deptManager = new DepartmentManagerHandler();
        supervisor.setNext(deptManager);
        
        DiscountRequest request = new DiscountRequest(mockProduct, 0.20, "CUST001", "Chain test");
        
        supervisor.handleRequest(request);
        
        assertTrue(request.isApproved());
        assertEquals("Department Manager", request.getApprovedBy());
        LOGGER.info("Chain flow: Supervisor passed 20% discount to Department Manager for approval");
    }
    
    // Test chain flow: Department Manager -> Store Manager
    @Test
    void testChainFlow_DepartmentManagerPassesToStoreManager() {
        DiscountHandler deptManager = new DepartmentManagerHandler();
        DiscountHandler storeManager = new StoreManagerHandler();
        deptManager.setNext(storeManager);
        
        DiscountRequest request = new DiscountRequest(mockProduct, 0.35, "CUST001", "Chain test");
        
        deptManager.handleRequest(request);
        
        assertTrue(request.isApproved());
        assertEquals("Store Manager", request.getApprovedBy());
        LOGGER.info("Chain flow: Department Manager passed 35% discount to Store Manager for approval");
    }
    
    // Test rejection: Discount too high
    @Test
    void testChainRejection_DiscountTooHigh() {
        DiscountRequest request = approvalService.requestDiscount(
            mockProduct, 50.0, "CUST001", "Too high discount"
        );
        
        assertFalse(request.isApproved());
        assertNotNull(request.getRejectionReason());
        assertTrue(request.getRejectionReason().contains("exceeds Store Manager approval limit"));
        LOGGER.info("Discount rejected as expected: {}", request.getRejectionReason());
    }
    
    // Test DiscountApprovalService statistics
    @Test
    void testDiscountApprovalService_Statistics() {
        approvalService.requestDiscount(mockProduct, 10.0, "CUST001", "Test 1");
        approvalService.requestDiscount(mockProduct, 20.0, "CUST002", "Test 2");
        approvalService.requestDiscount(mockProduct, 30.0, "CUST003", "Test 3");
        approvalService.requestDiscount(mockProduct, 45.0, "CUST004", "Test 4");
        
        DiscountApprovalService.ApprovalStats stats = approvalService.getApprovalStats();
        
        assertEquals(4, stats.getTotalRequests());
        assertEquals(3, stats.getApprovedRequests());
        assertEquals(1, stats.getRejectedRequests());
        assertTrue(stats.getTotalSavings() > 0);
        
        LOGGER.info("Statistics: {}", stats);
    }
    
    // Test DiscountRequest calculations
    @Test
    void testDiscountRequest_Calculations() {
        DiscountRequest request = new DiscountRequest(mockProduct, 0.25, "CUST001", "Test");
        request.setApproved(true);
        
        assertEquals(75.0, request.getDiscountedPrice(), 0.001);
        assertEquals(25.0, request.getSavingsAmount(), 0.001);
        LOGGER.info("Calculations: Price=${}, Savings=${}", 
                   request.getDiscountedPrice(), request.getSavingsAmount());
    }
    
    // Test end of chain scenario
    @Test
    void testEndOfChain_NoHandlerCanApprove() {
        SupervisorHandler supervisor = new SupervisorHandler();
        // No next handler set
        
        DiscountRequest request = new DiscountRequest(mockProduct, 0.20, "CUST001", "End of chain test");
        
        supervisor.handleRequest(request); // Can't approve, passes to next but none exists
        
        assertFalse(request.isApproved());
        assertEquals("No handler could approve this discount", request.getRejectionReason());
        LOGGER.info("End of chain handled correctly");
    }
    
    // Test DiscountRequest string representations
    @Test
    void testDiscountRequest_StringRepresentations() {
        DiscountRequest request = new DiscountRequest(mockProduct, 0.15, "CUST001", "String test");
        
        String summary = request.getSummary();
        String toString = request.toString();
        
        assertTrue(summary.contains("Discount Request"));
        assertTrue(summary.contains("CUST001"));
        assertTrue(toString.contains("DiscountRequest["));
        assertTrue(toString.contains("15.0%"));
        
        LOGGER.info("Summary: {}", summary);
        LOGGER.info("toString: {}", toString);
    }
    
    // Test DiscountHandler information methods
    @Test
    void testDiscountHandler_InformationMethods() {
        SupervisorHandler supervisor = new SupervisorHandler();
        
        assertEquals("SupervisorHandler", supervisor.getHandlerName());
        assertEquals(0.15, supervisor.getMaxDiscount(), 0.001);
        assertTrue(supervisor.canApprove(0.10));
        assertFalse(supervisor.canApprove(0.20));
        
        LOGGER.info("Supervisor info: {}", supervisor.getHandlerInfo());
    }
}