package com.university.grocerystore.decorator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.GroceryProduct.ProductType;

@ExtendWith(MockitoExtension.class)
class DecoratorPatternTests {
    
    private GroceryProduct baseProduct;
    private FrozenFood frozenProduct;
    private ProductEnhancementService enhancementService;
    
    @BeforeEach
    void setUp() {
        baseProduct = new GroceryProduct(
            "P001", 
            "Test Product", 
            "Test Brand", 
            29.99, 
            ProductType.DAIRY, 
            2023
        );
        
        frozenProduct = new FrozenFood(
            "F001", 
            "Frozen Vegetables", 
            "Organic Farms", 
            9.99, 
            ProductType.FROZEN_FOOD, 
            2024, 
            -18.0
        );
        
        enhancementService = new ProductEnhancementService();
    }
    
    // Test for ProductEnhancementService
    @Test
    void testProductEnhancementService_AddSingleEnhancement() {
        GroceryProduct giftWrapped = enhancementService.addGiftWrapping(baseProduct, "Premium");
        
        assertTrue(giftWrapped instanceof GiftWrappingDecorator);
        assertEquals(29.99 + 4.99, giftWrapped.getPrice(), 0.001);
        assertTrue(giftWrapped.getDisplayInfo().contains("Gift Wrapped"));
        
        assertTrue(enhancementService.hasEnhancements(giftWrapped));
        assertEquals(1, enhancementService.getEnhancementCount(giftWrapped));
        assertEquals(baseProduct, enhancementService.getBaseProduct(giftWrapped));
    }
    
    // Test for GiftWrappingDecorator
    @Test
    void testGiftWrappingDecorator_CreationAndProperties() {
        GiftWrappingDecorator decorator = new GiftWrappingDecorator(baseProduct, "Holiday");
        
        assertEquals(baseProduct, decorator.getDecoratedProduct());
        assertEquals("Holiday", decorator.getWrappingStyle());
        assertEquals(4.99, decorator.getGiftWrappingCost(), 0.001);
        assertEquals(29.99 + 4.99, decorator.getPrice(), 0.001);
        assertTrue(decorator.getDisplayInfo().contains("Gift Wrapped"));
        assertTrue(decorator.getDisplayInfo().contains("Holiday"));
    }
    
    // Test for ExpeditedDeliveryDecorator
    @Test
    void testExpeditedDeliveryDecorator_CreationAndProperties() {
        ExpeditedDeliveryDecorator decorator = new ExpeditedDeliveryDecorator(baseProduct, 24);
        
        assertEquals(baseProduct, decorator.getDecoratedProduct());
        assertEquals(24, decorator.getDeliveryHours());
        assertEquals(9.99, decorator.getExpeditedDeliveryCost(), 0.001);
        assertEquals(29.99 + 9.99, decorator.getPrice(), 0.001);
        assertTrue(decorator.getDisplayInfo().contains("Expedited Delivery"));
        assertTrue(decorator.getDisplayInfo().contains("24 hours"));
    }
    
    // Test for OrganicCertificationDecorator
    @Test
    void testOrganicCertificationDecorator_CreationAndProperties() {
        OrganicCertificationDecorator decorator = new OrganicCertificationDecorator(baseProduct);
        
        assertEquals(baseProduct, decorator.getDecoratedProduct());
        assertEquals(1.99, decorator.getOrganicCertificationCost(), 0.001);
        assertEquals(29.99 + 1.99, decorator.getPrice(), 0.001);
        assertTrue(decorator.getDisplayInfo().contains("Organic Certified"));
        assertEquals(1, decorator.getCertificationCount());
        assertTrue(decorator.getCertifications().contains("USDA Organic Certified"));
        
        decorator.addCertification("EU Organic Certified");
        assertEquals(2, decorator.getCertificationCount());
        assertTrue(decorator.hasAdditionalCertifications());
    }
    
    // Test for ProductDecorator base class
    @Test
    void testProductDecorator_BaseFunctionality() {
        ProductDecorator decorator = new GiftWrappingDecorator(baseProduct, "Simple");
        
        assertEquals(baseProduct, decorator.getDecoratedProduct());
        assertEquals(baseProduct, decorator.getBaseProduct());
        assertEquals(1, decorator.getDecoratorCount());
        assertTrue(decorator.hasDecorators());
        
        // Test delegation to wrapped product
        assertEquals(baseProduct.getBrand(), decorator.getBrand());
        assertEquals(baseProduct.getDiscountRate(), decorator.getDiscountRate(), 0.001);
    }
    
    // Test for multiple decorators (chain of decorators)
    @Test
    void testMultipleDecorators_ChainEnhancements() {
        GroceryProduct enhanced = baseProduct;
        
        // Add gift wrapping
        enhanced = new GiftWrappingDecorator(enhanced, "Luxury");
        assertEquals(29.99 + 4.99, enhanced.getPrice(), 0.001);
        
        // Add expedited delivery
        enhanced = new ExpeditedDeliveryDecorator(enhanced, 12);
        assertEquals(29.99 + 4.99 + 9.99, enhanced.getPrice(), 0.001);
        
        // Verify multiple enhancements
        assertTrue(enhanced.getDisplayInfo().contains("Gift Wrapped"));
        assertTrue(enhanced.getDisplayInfo().contains("Expedited Delivery"));
        assertEquals(2, enhancementService.getEnhancementCount(enhanced));
    }
    
    // Test for ProductEnhancementService enhancement packages
    @Test
    void testProductEnhancementService_PremiumPackage() {
        GroceryProduct premiumPackage = enhancementService.createPremiumPackage(baseProduct, "Elegant", 24);
        
        assertTrue(premiumPackage instanceof ExpeditedDeliveryDecorator);
        assertTrue(enhancementService.hasEnhancement(premiumPackage, GiftWrappingDecorator.class));
        assertTrue(enhancementService.hasEnhancement(premiumPackage, ExpeditedDeliveryDecorator.class));
        assertEquals(29.99 + 4.99 + 9.99, premiumPackage.getPrice(), 0.001);
        
        // For frozen food, should also have organic certification
        GroceryProduct frozenPremium = enhancementService.createPremiumPackage(frozenProduct, "Simple", 48);
        assertTrue(frozenPremium.getDisplayInfo().contains("Organic Certified"));
        assertEquals(9.99 + 4.99 + 9.99 + 1.99, frozenPremium.getPrice(), 0.001);
    }
    
    // Test for enhancement cost calculation
    @Test
    void testProductEnhancementService_CalculateEnhancementCost() {
        GroceryProduct enhanced = enhancementService.addGiftWrapping(baseProduct, "Standard");
        enhanced = enhancementService.addExpeditedDelivery(enhanced, 24);
        
        double enhancementCost = enhancementService.calculateEnhancementCost(baseProduct, enhanced);
        assertEquals(4.99 + 9.99, enhancementCost, 0.001);
        assertEquals(29.99 + 4.99 + 9.99, enhanced.getPrice(), 0.001);
    }
}