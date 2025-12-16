package com.university.grocerystore.builder;

import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;

/**
 * Builder for creating FrozenFood instances with a fluent interface.
 * Demonstrates the Builder pattern for complex object construction.
 */
public class FrozenFoodBuilder implements ProductBuilder<FrozenFood> {
    
    private String id;
    private String name;
    private String brand;
    private double price;
    private int productionYear;
    private String storageType;
    private double netWeight;
    private boolean organic = false;
    private int calories;
    private Perishable.ShelfLifeQuality quality = Perishable.ShelfLifeQuality.MEDIUM;
    
    /**
     * Sets the frozen food ID.
     * 
     * @param id the frozen food ID
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setId(String id) {
        this.id = id;
        return this;
    }
    
    /**
     * Sets the frozen food name.
     * 
     * @param name the frozen food name
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * Sets the frozen food brand.
     * 
     * @param brand the frozen food brand
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setBrand(String brand) {
        this.brand = brand;
        return this;
    }
    
    /**
     * Sets the frozen food price.
     * 
     * @param price the frozen food price
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setPrice(double price) {
        this.price = price;
        return this;
    }
    
    /**
     * Sets the frozen food production year.
     * 
     * @param productionYear the production year
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setProductionYear(int productionYear) {
        this.productionYear = productionYear;
        return this;
    }
    
    /**
     * Sets the frozen food storage type.
     * 
     * @param storageType the storage type (e.g., "Freezer", "Refrigerated")
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setStorageType(String storageType) {
        this.storageType = storageType;
        return this;
    }
    
    /**
     * Sets the frozen food net weight.
     * 
     * @param netWeight the net weight in grams
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setNetWeight(double netWeight) {
        this.netWeight = netWeight;
        return this;
    }
    
    /**
     * Sets the organic status.
     * 
     * @param organic true if organic
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setOrganic(boolean organic) {
        this.organic = organic;
        return this;
    }
    
    /**
     * Sets the frozen food calorie count.
     * 
     * @param calories the calorie count
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setCalories(int calories) {
        this.calories = calories;
        return this;
    }
    
    /**
     * Sets the frozen food quality.
     * 
     * @param quality the shelf life quality
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setQuality(Perishable.ShelfLifeQuality quality) {
        this.quality = quality;
        return this;
    }
    
    /**
     * Sets organic to true.
     * 
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder makeOrganic() {
        this.organic = true;
        return this;
    }
    
    /**
     * Sets organic to false.
     * 
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder makeNonOrganic() {
        this.organic = false;
        return this;
    }
    
    /**
     * Sets the quality to high.
     * 
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setHighQuality() {
        this.quality = Perishable.ShelfLifeQuality.HIGH;
        return this;
    }
    
    /**
     * Sets the quality to low.
     * 
     * @return this builder for method chaining
     */
    public FrozenFoodBuilder setLowQuality() {
        this.quality = Perishable.ShelfLifeQuality.LOW;
        return this;
    }
    
    @Override
    public FrozenFood build() {
        validate();
        return new FrozenFood(id, name, brand, price, productionYear, storageType, 
                        netWeight, organic, calories, quality);
    }
    
    @Override
    public void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalStateException("ID is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Name is required");
        }
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalStateException("Brand is required");
        }
        if (price < 0) {
            throw new IllegalStateException("Price must be non-negative: " + price);
        }
        if (productionYear < 2000 || productionYear > 2100) {
            throw new IllegalStateException("Production year must be between 2000 and 2100: " + productionYear);
        }
        if (storageType == null || storageType.trim().isEmpty()) {
            throw new IllegalStateException("Storage type is required");
        }
        if (netWeight < 0) {
            throw new IllegalStateException("Net weight must be non-negative: " + netWeight);
        }
        if (calories < 0) {
            throw new IllegalStateException("Calorie count must be non-negative: " + calories);
        }
        if (quality == null) {
            throw new IllegalStateException("Quality is required");
        }
    }
    
    @Override
    public void reset() {
        this.id = null;
        this.name = null;
        this.brand = null;
        this.price = 0.0;
        this.productionYear = 0;
        this.storageType = null;
        this.netWeight = 0.0;
        this.organic = false;
        this.calories = 0;
        this.quality = Perishable.ShelfLifeQuality.MEDIUM;
    }
    
    /**
     * Gets the current ID.
     * 
     * @return the current ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the current name.
     * 
     * @return the current name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the current brand.
     * 
     * @return the current brand
     */
    public String getBrand() {
        return brand;
    }
    
    /**
     * Gets the current price.
     * 
     * @return the current price
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets the current production year.
     * 
     * @return the current production year
     */
    public int getProductionYear() {
        return productionYear;
    }
    
    /**
     * Gets the current storage type.
     * 
     * @return the current storage type
     */
    public String getStorageType() {
        return storageType;
    }
    
    /**
     * Gets the current net weight.
     * 
     * @return the current net weight
     */
    public double getNetWeight() {
        return netWeight;
    }
    
    /**
     * Gets the current organic status.
     * 
     * @return the current organic status
     */
    public boolean isOrganic() {
        return organic;
    }
    
    /**
     * Gets the current calorie count.
     * 
     * @return the current calorie count
     */
    public int getCalories() {
        return calories;
    }
    
    /**
     * Gets the current quality.
     * 
     * @return the current quality
     */
    public Perishable.ShelfLifeQuality getQuality() {
        return quality;
    }
    
    @Override
    public String toString() {
        return String.format("FrozenFoodBuilder[ID=%s, Name=%s, Brand=%s, Price=$%.2f, Year=%d, Storage=%s, Weight=%.2fg, Organic=%s, Calories=%d, Quality=%s]",
            id, name, brand, price, productionYear, storageType, netWeight, organic, calories, quality);
    }
}
