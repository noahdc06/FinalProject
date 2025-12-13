package com.university.grocerystore.builder;

import java.util.List;

import com.university.grocerystore.composite.GroceryBundle;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;

/**
 * Director class that orchestrates the construction of complex GroceryProduct objects
 * using various builders. Demonstrates the Director pattern in conjunction
 * with the Builder pattern for creating predefined configurations.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class GroceryDirector {
    
    private final FrozenFoodBuilder frozenFoodBuilder;
    private final GroceryBundleBuilder bundleBuilder;
    
    /**
     * Constructs a GroceryDirector with the specified builders.
     * 
     * @param frozenFoodBuilder the FrozenFood builder
     * @param bundleBuilder the GroceryBundle builder
     */
    public GroceryDirector(FrozenFoodBuilder frozenFoodBuilder, GroceryBundleBuilder bundleBuilder) {
        this.frozenFoodBuilder = frozenFoodBuilder;
        this.bundleBuilder = bundleBuilder;
    }
    
    /**
     * Constructs a premium FrozenFood with all features enabled.
     * 
     * @param id the FrozenFood ID
     * @param name the FrozenFood name
     * @param brand the FrozenFood brand
     * @param price the FrozenFood price
     * @return a premium FrozenFood
     */
    public GroceryProduct buildPremiumFrozenFood(String id, String name, String brand, double price) {
        frozenFoodBuilder.reset();
        return frozenFoodBuilder
            .setId(id)
            .setName(name)
            .setBrand(brand)
            .setPrice(price)
            .setStorageType("Freezer")
            .setNetWeight(500) // 500g
            .makeOrganic()
            .setCalories(250)
            .setQuality(Perishable.ShelfLifeQuality.HIGH)
            .build();
    }
    
    /**
     * Constructs a basic FrozenFood with minimal features.
     * 
     * @param id the FrozenFood ID
     * @param name the FrozenFood name
     * @param brand the FrozenFood brand
     * @param price the FrozenFood price
     * @return a basic FrozenFood
     */
    public GroceryProduct buildBasicFrozenFood(String id, String name, String brand, double price) {
        frozenFoodBuilder.reset();
        return frozenFoodBuilder
            .setId(id)
            .setName(name)
            .setBrand(brand)
            .setPrice(price)
            .setStorageType("Freezer")
            .setNetWeight(300) // 300g
            .makeNonOrganic()
            .setCalories(350)
            .setQuality(Perishable.ShelfLifeQuality.MEDIUM)
            .build();
    }
    
    /**
     * Constructs a healthy FrozenFood with health-focused features.
     * 
     * @param id the FrozenFood ID
     * @param name the FrozenFood name
     * @param brand the FrozenFood brand
     * @param price the FrozenFood price
     * @return a healthy FrozenFood
     */
    public GroceryProduct buildHealthyFrozenFood(String id, String name, String brand, double price) {
        frozenFoodBuilder.reset();
        return frozenFoodBuilder
            .setId(id)
            .setName(name)
            .setBrand(brand)
            .setPrice(price)
            .setStorageType("Freezer")
            .setNetWeight(400) // 400g
            .makeOrganic()
            .setCalories(200)
            .setQuality(Perishable.ShelfLifeQuality.HIGH)
            .build();
    }
    
    /**
     * Constructs a meal deal bundle with a 20% discount.
     * 
     * @param bundleName the bundle name
     * @param products the products to include in the bundle
     * @return a meal deal bundle
     */
    public GroceryBundle buildMealDealBundle(String bundleName, List<GroceryProduct> products) {
        bundleBuilder.reset();
        return bundleBuilder
            .setBundleName(bundleName)
            .setMediumDiscount() // 20% discount
            .addProducts(products)
            .build();
    }
    
    /**
     * Constructs a family bundle with a 25% discount.
     * 
     * @param bundleName the bundle name
     * @param products the products to include in the bundle
     * @return a family bundle
     */
    public GroceryBundle buildFamilyBundle(String bundleName, List<GroceryProduct> products) {
        bundleBuilder.reset();
        return bundleBuilder
            .setBundleName(bundleName)
            .setBundleDiscount(0.25) // 25% discount
            .addProducts(products)
            .build();
    }
    
    /**
     * Constructs a premium bundle with a 30% discount.
     * 
     * @param bundleName the bundle name
     * @param products the products to include in the bundle
     * @return a premium bundle
     */
    public GroceryBundle buildPremiumBundle(String bundleName, List<GroceryProduct> products) {
        bundleBuilder.reset();
        return bundleBuilder
            .setBundleName(bundleName)
            .setLargeDiscount() // 30% discount
            .addProducts(products)
            .build();
    }
    
    /**
     * Constructs a starter bundle with a 10% discount.
     * 
     * @param bundleName the bundle name
     * @param products the products to include in the bundle
     * @return a starter bundle
     */
    public GroceryBundle buildStarterBundle(String bundleName, List<GroceryProduct> products) {
        bundleBuilder.reset();
        return bundleBuilder
            .setBundleName(bundleName)
            .setSmallDiscount() // 10% discount
            .addProducts(products)
            .build();
    }
    
    /**
     * Constructs a nested bundle (bundle containing other bundles).
     * 
     * @param bundleName the bundle name
     * @param bundles the bundles to include
     * @param discount the discount rate
     * @return a nested bundle
     */
    public GroceryBundle buildNestedBundle(String bundleName, List<GroceryBundle> bundles, double discount) {
        bundleBuilder.reset();
        GroceryBundleBuilder builder = bundleBuilder
            .setBundleName(bundleName)
            .setBundleDiscount(discount);
        
        for (GroceryBundle bundle : bundles) {
            builder.addBundle(bundle);
        }
        
        return builder.build();
    }
    
    /**
     * Constructs a custom bundle with specified parameters.
     * 
     * @param bundleName the bundle name
     * @param products the products to include
     * @param discount the discount rate
     * @return a custom bundle
     */
    public GroceryBundle buildCustomBundle(String bundleName, List<GroceryProduct> products, double discount) {
        bundleBuilder.reset();
        return bundleBuilder
            .setBundleName(bundleName)
            .setBundleDiscount(discount)
            .addProducts(products)
            .build();
    }
    
    /**
     * Gets the FrozenFood builder for custom configurations.
     * 
     * @return the FrozenFood builder
     */
    public FrozenFoodBuilder getFrozenFoodBuilder() {
        return frozenFoodBuilder;
    }
    
    /**
     * Gets the GroceryBundle builder for custom configurations.
     * 
     * @return the GroceryBundle builder
     */
    public GroceryBundleBuilder getBundleBuilder() {
        return bundleBuilder;
    }
    
    /**
     * Creates a new FrozenFood builder instance.
     * 
     * @return a new FrozenFood builder
     */
    public FrozenFoodBuilder createFrozenFoodBuilder() {
        return new FrozenFoodBuilder();
    }
    
    /**
     * Creates a new GroceryBundle builder instance.
     * 
     * @return a new GroceryBundle builder
     */
    public GroceryBundleBuilder createBundleBuilder() {
        return new GroceryBundleBuilder();
    }
}