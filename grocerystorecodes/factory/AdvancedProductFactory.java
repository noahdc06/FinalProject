package com.university.grocerystore.factory;

import java.util.Map;
import java.util.Objects;

import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Perishable;
import com.university.grocerystore.model.Produce;
import com.university.grocerystore.model.Snack;

/**
 * Enhanced factory for creating GroceryProduct instances with advanced validation and type-safe property extraction.
 * Demonstrates improved Factory pattern implementation with better error handling and validation.
 * 
 * <p>This factory extends the basic ProductFactory with enhanced validation,
 * type-safe property extraction, and support for complex product configurations.</p>
 */
public class AdvancedProductFactory {
    
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
        Objects.requireNonNull(type, "Product type cannot be null");
        Objects.requireNonNull(properties, "Properties cannot be null");
        
        String normalizedType = type.trim().toUpperCase();
        
        switch (normalizedType) {
            case "PRODUCE":
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
                throw new IllegalArgumentException("Unknown product type: " + type);
        }
    }
    
    /**
     * Creates a Produce instance with enhanced validation.
     */
    private static Produce createProduce(Map<String, Object> properties) {
        return new Produce(
            getRequiredString(properties, "id"),
            getRequiredString(properties, "name"),
            getRequiredString(properties, "brand"),
            getRequiredDouble(properties, "price"),
            getRequiredInt(properties, "productionYear"),
            getRequiredString(properties, "variety"),
            getRequiredDouble(properties, "weight"),
            getRequiredBoolean(properties, "organic"),
            getRequiredString(properties, "countryOfOrigin"),
            getRequiredEnum(properties, "quality", Perishable.ShelfLifeQuality.class)
        );
    }
    
    /**
     * Creates a FrozenFood instance with enhanced validation.
     */
    private static FrozenFood createFrozenFood(Map<String, Object> properties) {
        return new FrozenFood(
            getRequiredString(properties, "id"),
            getRequiredString(properties, "name"),
            getRequiredString(properties, "brand"),
            getRequiredDouble(properties, "price"),
            getRequiredInt(properties, "productionYear"),
            getRequiredString(properties, "storageType"),
            getRequiredDouble(properties, "netWeight"),
            getRequiredBoolean(properties, "organic"),
            getRequiredInt(properties, "calories"),
            getRequiredEnum(properties, "quality", Perishable.ShelfLifeQuality.class)
        );
    }
    
    /**
     * Creates a CannedGood instance with enhanced validation.
     */
    private static CannedGood createCannedGood(Map<String, Object> properties) {
        return new CannedGood(
            getRequiredString(properties, "id"),
            getRequiredString(properties, "name"),
            getRequiredString(properties, "brand"),
            getRequiredDouble(properties, "price"),
            getRequiredInt(properties, "productionYear"),
            getRequiredString(properties, "canSize"),
            getRequiredBoolean(properties, "recyclable"),
            getRequiredString(properties, "preservationMethod"),
            getRequiredInt(properties, "shelfLifeMonths")
        );
    }
    
    /**
     * Creates a Snack instance with enhanced validation.
     */
    private static Snack createSnack(Map<String, Object> properties) {
        return new Snack(
            getRequiredString(properties, "id"),
            getRequiredString(properties, "name"),
            getRequiredString(properties, "brand"),
            getRequiredDouble(properties, "price"),
            getRequiredInt(properties, "productionYear"),
            getRequiredString(properties, "snackType"),
            getRequiredDouble(properties, "netWeight"),
            getRequiredInt(properties, "calories"),
            getRequiredString(properties, "flavor"),
            getRequiredString(properties, "dietaryInfo")
        );
    }
    
    // Enhanced helper methods for type-safe property extraction
    
    /**
     * Gets a required string property with validation.
     */
    private static String getRequiredString(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property missing: " + key);
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Required property cannot be empty: " + key);
        }
        return str;
    }
    
    /**
     * Gets a required double property with validation.
     */
    private static double getRequiredDouble(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property missing: " + key);
        }
        if (value instanceof Number) {
            double result = ((Number) value).doubleValue();
            if (result < 0) {
                throw new IllegalArgumentException("Property " + key + " must be non-negative: " + result);
            }
            return result;
        }
        throw new IllegalArgumentException("Property " + key + " must be a number");
    }
    
    /**
     * Gets a required integer property with validation.
     */
    private static int getRequiredInt(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property missing: " + key);
        }
        if (value instanceof Number) {
            int result = ((Number) value).intValue();
            if (result < 0) {
                throw new IllegalArgumentException("Property " + key + " must be non-negative: " + result);
            }
            return result;
        }
        throw new IllegalArgumentException("Property " + key + " must be a number");
    }
    
    /**
     * Gets a required boolean property with validation.
     */
    private static boolean getRequiredBoolean(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property missing: " + key);
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new IllegalArgumentException("Property " + key + " must be a boolean");
    }
    
    /**
     * Gets a required enum property with validation.
     */
    private static <T extends Enum<T>> T getRequiredEnum(Map<String, Object> properties, 
                                                       String key, Class<T> enumClass) {
        Object value = properties.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property missing: " + key);
        }
        if (value instanceof String) {
            try {
                return Enum.valueOf(enumClass, value.toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Property " + key + " must be a valid " + 
                                                 enumClass.getSimpleName() + ": " + value);
            }
        }
        throw new IllegalArgumentException("Property " + key + " must be a string");
    }
    
    /**
     * Gets a required string list property with validation.
     */
    @SuppressWarnings("unchecked")
    private static java.util.List<String> getRequiredStringList(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property missing: " + key);
        }
        if (value instanceof java.util.List) {
            return (java.util.List<String>) value;
        }
        if (value instanceof String) {
            String str = value.toString().trim();
            if (str.isEmpty()) {
                return java.util.Arrays.asList();
            }
            return java.util.Arrays.asList(str.split(","));
        }
        throw new IllegalArgumentException("Property " + key + " must be a list or string");
    }
    
    /**
     * Validates that all required properties are present.
     * 
     * @param type the product type to validate
     * @param properties the properties map to validate
     */
    public static void validateRequiredProperties(String type, Map<String, Object> properties) {
        String normalizedType = type.trim().toUpperCase();
        
        switch (normalizedType) {
            case "PRODUCE":
                validateProduceProperties(properties);
                break;
            case "FROZEN_FOOD":
            case "FROZENFOOD":
                validateFrozenFoodProperties(properties);
                break;
            case "CANNED_GOOD":
            case "CANNEDGOOD":
                validateCannedGoodProperties(properties);
                break;
            case "SNACK":
                validateSnackProperties(properties);
                break;
            default:
                throw new IllegalArgumentException("Unknown product type: " + type);
        }
    }
    
    private static void validateProduceProperties(Map<String, Object> properties) {
        String[] required = {"id", "name", "brand", "price", "productionYear", "variety", "weight", "organic", "countryOfOrigin", "quality"};
        validateRequiredKeys(properties, required);
    }
    
    private static void validateFrozenFoodProperties(Map<String, Object> properties) {
        String[] required = {"id", "name", "brand", "price", "productionYear", "storageType", "netWeight", "organic", "calories", "quality"};
        validateRequiredKeys(properties, required);
    }
    
    private static void validateCannedGoodProperties(Map<String, Object> properties) {
        String[] required = {"id", "name", "brand", "price", "productionYear", "canSize", "recyclable", "preservationMethod", "shelfLifeMonths"};
        validateRequiredKeys(properties, required);
    }
    
    private static void validateSnackProperties(Map<String, Object> properties) {
        String[] required = {"id", "name", "brand", "price", "productionYear", "snackType", "netWeight", "calories", "flavor", "dietaryInfo"};
        validateRequiredKeys(properties, required);
    }
    
    private static void validateRequiredKeys(Map<String, Object> properties, String[] requiredKeys) {
        for (String key : requiredKeys) {
            if (!properties.containsKey(key)) {
                throw new IllegalArgumentException("Required property missing: " + key);
            }
        }
    }
}