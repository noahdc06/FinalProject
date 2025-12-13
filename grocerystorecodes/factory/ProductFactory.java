package com.university.grocerystore.factory;

import java.util.Map;

import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;
import com.university.grocerystore.model.Produce;
import com.university.grocerystore.model.Snack;

/**
 * Factory class for creating GroceryProduct instances.
 * Demonstrates the Factory pattern by providing a centralized
 * way to create different types of products without exposing
 * their constructors directly.
 * 
 * <p>This factory supports creating all product types including
 * the new FrozenFood class, with proper validation and error handling.</p>
 */
public class ProductFactory {
    
    /**
     * Creates a GroceryProduct instance based on the specified type and properties.
     * 
     * @param type the product type
     * @param properties map of properties required for the product
     * @return the created GroceryProduct instance
     * @throws IllegalArgumentException if type is null or properties are invalid
     * @throws NullPointerException if required properties are missing
     */
    public static GroceryProduct createProduct(String type, Map<String, Object> properties) {
        if (type == null) {
            throw new NullPointerException("Product type cannot be null");
        }
        if (properties == null) {
            throw new NullPointerException("Properties cannot be null");
        }
        
        String normalizedType = type.trim().toUpperCase();
        
        switch (normalizedType) {
            case "PRODUCE":
            case "FRESH_PRODUCE":
                return createProduce(properties);
            case "FROZEN_FOOD":
            case "FROZENFOOD":
                return createFrozenFood(properties);
            case "CANNED_GOOD":
            case "CANNEDGOOD":
                return createCannedGood(properties);
            case "SNACK":
                return createSnack(properties);
            default:
                throw new IllegalArgumentException("Unsupported product type: " + type);
        }
    }
    
    /**
     * Creates a Produce instance.
     */
    private static Produce createProduce(Map<String, Object> properties) {
        String sku = getRequiredString(properties, "sku");
        String name = getRequiredString(properties, "name");
        String brand = getRequiredString(properties, "brand");
        double price = getRequiredDouble(properties, "price");
        int productionYear = getRequiredInteger(properties, "productionYear");
        String variety = getRequiredString(properties, "variety");
        double weight = getRequiredDouble(properties, "weight");
        boolean organic = getRequiredBoolean(properties, "organic");
        String countryOfOrigin = getOptionalString(properties, "countryOfOrigin", "Unknown");
        Perishable.ShelfLifeQuality quality = getRequiredShelfLifeQuality(properties, "quality");
        
        return new Produce(sku, name, brand, price, productionYear, variety, weight, organic, countryOfOrigin, quality);
    }
    
    /**
     * Creates a FrozenFood instance.
     */
    private static FrozenFood createFrozenFood(Map<String, Object> properties) {
        String sku = getRequiredString(properties, "sku");
        String name = getRequiredString(properties, "name");
        String brand = getRequiredString(properties, "brand");
        double price = getRequiredDouble(properties, "price");
        int productionYear = getRequiredInteger(properties, "productionYear");
        String storageType = getRequiredString(properties, "storageType");
        double netWeight = getRequiredDouble(properties, "netWeight");
        boolean organic = getRequiredBoolean(properties, "organic");
        int calories = getRequiredInteger(properties, "calories");
        Perishable.ShelfLifeQuality quality = getRequiredShelfLifeQuality(properties, "quality");
        
        return new FrozenFood(sku, name, brand, price, productionYear, storageType, netWeight, organic, calories, quality);
    }
    
    /**
     * Creates a CannedGood instance.
     */
    private static CannedGood createCannedGood(Map<String, Object> properties) {
        String sku = getRequiredString(properties, "sku");
        String name = getRequiredString(properties, "name");
        String brand = getRequiredString(properties, "brand");
        double price = getRequiredDouble(properties, "price");
        int productionYear = getRequiredInteger(properties, "productionYear");
        String canSize = getRequiredString(properties, "canSize");
        boolean recyclable = getRequiredBoolean(properties, "recyclable");
        String preservationMethod = getOptionalString(properties, "preservationMethod", "Canning");
        int shelfLifeMonths = getRequiredInteger(properties, "shelfLifeMonths");
        
        return new CannedGood(sku, name, brand, price, productionYear, canSize, recyclable, preservationMethod, shelfLifeMonths);
    }
    
    /**
     * Creates a Snack instance.
     */
    private static Snack createSnack(Map<String, Object> properties) {
        String sku = getRequiredString(properties, "sku");
        String name = getRequiredString(properties, "name");
        String brand = getRequiredString(properties, "brand");
        double price = getRequiredDouble(properties, "price");
        int productionYear = getRequiredInteger(properties, "productionYear");
        String snackType = getRequiredString(properties, "snackType");
        double netWeight = getRequiredDouble(properties, "netWeight");
        int calories = getRequiredInteger(properties, "calories");
        String flavor = getOptionalString(properties, "flavor", "Original");
        String dietaryInfo = getOptionalString(properties, "dietaryInfo", "None");
        
        return new Snack(sku, name, brand, price, productionYear, snackType, netWeight, calories, flavor, dietaryInfo);
    }
    
    // Helper methods for extracting and validating properties
    
    private static String getRequiredString(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Property '" + key + "' must be a String, got: " + value.getClass().getSimpleName());
        }
        String str = (String) value;
        if (str.trim().isEmpty()) {
            throw new IllegalArgumentException("Property '" + key + "' cannot be empty");
        }
        return str.trim();
    }
    
    private static double getRequiredDouble(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid number, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a number, got: " + value.getClass().getSimpleName());
    }
    
    private static int getRequiredInteger(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid integer, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be an integer, got: " + value.getClass().getSimpleName());
    }
    
    private static boolean getRequiredBoolean(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            String str = ((String) value).trim().toLowerCase();
            if ("true".equals(str) || "1".equals(str) || "yes".equals(str)) {
                return true;
            }
            if ("false".equals(str) || "0".equals(str) || "no".equals(str)) {
                return false;
            }
            throw new IllegalArgumentException("Property '" + key + "' must be a valid boolean, got: " + value);
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a boolean, got: " + value.getClass().getSimpleName());
    }
    
    private static Perishable.ShelfLifeQuality getRequiredShelfLifeQuality(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof Perishable.ShelfLifeQuality) {
            return (Perishable.ShelfLifeQuality) value;
        }
        if (value instanceof String) {
            try {
                return Perishable.ShelfLifeQuality.valueOf(((String) value).trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid ShelfLifeQuality, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a ShelfLifeQuality, got: " + value.getClass().getSimpleName());
    }
    
    private static String getOptionalString(Map<String, Object> properties, String key, String defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Property '" + key + "' must be a String, got: " + value.getClass().getSimpleName());
        }
        String str = (String) value;
        return str.trim().isEmpty() ? defaultValue : str.trim();
    }
    
    private static boolean getOptionalBoolean(Map<String, Object> properties, String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            String str = ((String) value).trim().toLowerCase();
            if ("true".equals(str) || "1".equals(str) || "yes".equals(str)) {
                return true;
            }
            if ("false".equals(str) || "0".equals(str) || "no".equals(str)) {
                return false;
            }
            throw new IllegalArgumentException("Property '" + key + "' must be a valid boolean, got: " + value);
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a boolean, got: " + value.getClass().getSimpleName());
    }
    
    private static double getOptionalDouble(Map<String, Object> properties, String key, double defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid number, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a number, got: " + value.getClass().getSimpleName());
    }
    
    @SuppressWarnings("unchecked")
    private static java.util.List<String> getOptionalStringList(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            return java.util.Arrays.asList();
        }
        if (value instanceof java.util.List) {
            return (java.util.List<String>) value;
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.trim().isEmpty()) {
                return java.util.Arrays.asList();
            }
            return java.util.Arrays.asList(str.split(","));
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a List<String> or String, got: " + value.getClass().getSimpleName());
    }
}