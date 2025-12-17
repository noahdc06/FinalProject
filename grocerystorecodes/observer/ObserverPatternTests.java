package com.university.grocerystore.observer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.GroceryProduct.ProductType;

class ObserverPatternTests {
    
    private GroceryProductEventPublisher eventPublisher;
    private InventoryObserver inventoryObserver;
    private AnalyticsObserver analyticsObserver;
    private AuditLogObserver auditLogObserver;
    private GroceryProduct testProduct;
    
    @BeforeEach
    void setUp() {
        eventPublisher = new GroceryProductEventPublisher();
        inventoryObserver = new InventoryObserver();
        analyticsObserver = new AnalyticsObserver();
        auditLogObserver = new AuditLogObserver();
        
        testProduct = new GroceryProduct(
            "P001", 
            "Test Product", 
            "Test Brand", 
            19.99, 
            ProductType.DAIRY, 
            2023
        );
    }
    
    // Test for GroceryProductEventPublisher
    @Test
    void testGroceryProductEventPublisher_BasicObserverManagement() {
        eventPublisher.addObserver(inventoryObserver);
        eventPublisher.addObserver(analyticsObserver);
        
        assertEquals(2, eventPublisher.getObserverCount());
        assertFalse(eventPublisher.hasNoObservers());
        assertTrue(eventPublisher.hasObserver(inventoryObserver));
        assertTrue(eventPublisher.hasObserver(analyticsObserver));
        
        // Test removal
        assertTrue(eventPublisher.removeObserver(analyticsObserver));
        assertEquals(1, eventPublisher.getObserverCount());
        assertFalse(eventPublisher.hasObserver(analyticsObserver));
        
        // Test clearing
        eventPublisher.clearObservers();
        assertTrue(eventPublisher.hasNoObservers());
    }
    
    // Test for ProductAddedEvent
    @Test
    void testProductAddedEvent_CreationAndProperties() {
        ProductAddedEvent event = new ProductAddedEvent(testProduct);
        
        assertEquals(testProduct, event.getProduct());
        assertEquals("PRODUCT_ADDED", event.getEventType());
        assertTrue(event.getTimestamp() > 0);
        assertTrue(event.getDescription().contains("Product added"));
        assertTrue(event.getDescription().contains("Test Product"));
        assertTrue(event.getDescription().contains("P001"));
        assertTrue(event.getDescription().contains("$19.99"));
    }
    
    // Test for PriceChangedEvent
    @Test
    void testPriceChangedEvent_CreationAndProperties() {
        PriceChangedEvent event = new PriceChangedEvent(testProduct, 19.99, 24.99);
        
        assertEquals(testProduct, event.getProduct());
        assertEquals("PRICE_CHANGED", event.getEventType());
        assertEquals(19.99, event.getOldPrice(), 0.001);
        assertEquals(24.99, event.getNewPrice(), 0.001);
        assertEquals(5.00, event.getPriceChange(), 0.001);
        assertTrue(event.isPriceIncrease());
        assertFalse(event.isPriceDecrease());
        assertTrue(event.getDescription().contains("Price changed"));
        assertTrue(event.getDescription().contains("$19.99 -> $24.99"));
    }
    
    // Test for InventoryObserver
    @Test
    void testInventoryObserver_TracksInventoryChanges() {
        // Subscribe observer to events
        eventPublisher.addObserver(inventoryObserver);
        
        // Publish product added event
        eventPublisher.publishProductAdded(testProduct);
        
        assertEquals(1, inventoryObserver.getInventoryCount("P001"));
        assertEquals(19.99, inventoryObserver.getTotalValue("P001"), 0.001);
        assertEquals(1, inventoryObserver.getTotalInventoryCount());
        assertEquals(19.99, inventoryObserver.getTotalInventoryValue(), 0.001);
        assertEquals(1, inventoryObserver.getUniqueProductCount());
        
        // Publish price change
        eventPublisher.publishPriceChanged(testProduct, 19.99, 24.99);
        
        // Inventory value should update with price change
        assertEquals(24.99, inventoryObserver.getTotalValue("P001"), 0.001);
        assertEquals(24.99, inventoryObserver.getTotalInventoryValue(), 0.001);
    }
    
    // Test for AnalyticsObserver
    @Test
    void testAnalyticsObserver_CollectsEventAnalytics() {
        eventPublisher.addObserver(analyticsObserver);
        
        // Publish multiple events
        eventPublisher.publishProductAdded(testProduct);
        eventPublisher.publishPriceChanged(testProduct, 19.99, 24.99);
        
        assertEquals(2, analyticsObserver.getTotalEvents());
        assertEquals(2, analyticsObserver.getEventCount("PRODUCT_ADDED"));
        assertEquals(1, analyticsObserver.getEventCount("PRICE_CHANGED"));
        assertEquals(19.99, analyticsObserver.getTotalInventoryValue(), 0.001);
        assertEquals(1, analyticsObserver.getUniqueProductCount());
        
        Map<String, Integer> eventStats = analyticsObserver.getEventStatistics();
        assertEquals(2, eventStats.get("PRODUCT_ADDED"));
        assertEquals(1, eventStats.get("PRICE_CHANGED"));
        
        AnalyticsObserver.AnalyticsData analyticsData = analyticsObserver.getAnalyticsData();
        assertEquals(2, analyticsData.getTotalEvents());
        assertEquals(19.99, analyticsData.getTotalInventoryValue(), 0.001);
        assertTrue(analyticsData.getAverageEventRate() >= 0);
    }
    
    // Test for AuditLogObserver
    @Test
    void testAuditLogObserver_MaintainsEventHistory() {
        eventPublisher.addObserver(auditLogObserver);
        
        eventPublisher.publishProductAdded(testProduct);
        eventPublisher.publishPriceChanged(testProduct, 19.99, 24.99);
        
        assertEquals(2, auditLogObserver.getLogSize());
        
        List<AuditLogObserver.AuditLogEntry> logEntries = auditLogObserver.getAuditLog();
        assertEquals(2, logEntries.size());
        assertEquals("PRODUCT_ADDED", logEntries.get(0).getEventType());
        assertEquals("PRICE_CHANGED", logEntries.get(1).getEventType());
        assertEquals("P001", logEntries.get(0).getProductId());
        
        // Test filtering
        List<AuditLogObserver.AuditLogEntry> productEntries = 
            auditLogObserver.getAuditLogForProduct("P001");
        assertEquals(2, productEntries.size());
        
        AuditLogObserver.AuditLogStats stats = auditLogObserver.getAuditLogStats();
        assertEquals(2, stats.getTotalEntries());
        assertEquals(1, stats.getProductAddedCount());
        assertEquals(1, stats.getPriceChangedCount());
    }
    
    // Test for GroceryProductSubject interface implementation
    @Test
    void testGroceryProductSubject_InterfaceContract() {
        GroceryProductSubject subject = eventPublisher;
        
        subject.addObserver(inventoryObserver);
        subject.addObserver(analyticsObserver);
        
        assertEquals(2, subject.getObserverCount());
        assertFalse(subject.hasNoObservers());
        
        subject.clearObservers();
        assertTrue(subject.hasNoObservers());
        assertEquals(0, subject.getObserverCount());
    }
    
    // Test for GroceryProductObserver default methods
    @Test
    void testGroceryProductObserver_DefaultMethods() {
        GroceryProductObserver observer = inventoryObserver;
        
        assertEquals("InventoryObserver", observer.getObserverName());
        // Default methods should not throw exceptions
        observer.onAdded(eventPublisher);
        observer.onRemoved(eventPublisher);
    }
    
    // Test for GroceryProductEvent interface
    @Test
    void testGroceryProductEvent_InterfaceContract() {
        GroceryProductEvent event = new ProductAddedEvent(testProduct);
        
        assertEquals(testProduct, event.getProduct());
        assertTrue(event.getTimestamp() > 0);
        assertEquals("PRODUCT_ADDED", event.getEventType());
        assertNotNull(event.getDescription());
        assertTrue(event.getDescription().contains(testProduct.getName()));
    }
    
    // Test for full Observer pattern integration
    @Test
    void testObserverPattern_FullIntegration() {
        // Add all observers
        eventPublisher.addObserver(inventoryObserver);
        eventPublisher.addObserver(analyticsObserver);
        eventPublisher.addObserver(auditLogObserver);
        
        // Create and publish events
        eventPublisher.publishProductAdded(testProduct);
        eventPublisher.publishPriceChanged(testProduct, 19.99, 24.99);
        
        // Verify all observers received events
        assertEquals(1, inventoryObserver.getInventoryCount("P001"));
        assertEquals(2, analyticsObserver.getTotalEvents());
        assertEquals(2, auditLogObserver.getLogSize());
        
        // Verify event propagation
        Map<String, Integer> eventStats = analyticsObserver.getEventStatistics();
        assertEquals(2, eventStats.get("PRODUCT_ADDED"));
        assertEquals(1, eventStats.get("PRICE_CHANGED"));
    }
}