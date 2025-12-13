package com.university.grocerystore.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.university.grocerystore.model.GroceryItem;

/**
 * Utility class for array-based operations on GroceryItem objects.
 * 
 * <p>This class provides static methods for manipulating and analyzing
 * arrays of grocery items, demonstrating array operations without using ArrayList.</p>
 * 
 * <p>All methods handle null arrays and null elements gracefully.</p>
 * 
 * @author Navid Mohaghegh
 * @version 1.0
 * @since 2024-09-15
 */
public final class GroceryItemUtils {
    
    /**
     * Private constructor to prevent instantiation.
     */
    private GroceryItemUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Counts grocery items produced before a given year.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @param yearCutoff the cutoff year (exclusive)
     * @return count of items produced before the cutoff year
     */
    public static int countBeforeYear(GroceryItem[] items, int yearCutoff) {
        if (items == null) {
            return 0;
        }
        
        int count = 0;
        for (GroceryItem item : items) {
            if (item != null && item.getProductionYear() < yearCutoff) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Counts grocery items by a specific brand (case-insensitive, exact match).
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @param brand the brand name to search for
     * @return count of items by the specified brand
     */
    public static int countByBrand(GroceryItem[] items, String brand) {
        if (items == null || brand == null) {
            return 0;
        }
        
        int count = 0;
        for (GroceryItem item : items) {
            if (item != null && item.getBrand().equalsIgnoreCase(brand)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Filters grocery items with price at most the specified maximum.
     * Returns a compact array (no nulls, exact size).
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @param maxPrice maximum price (inclusive)
     * @return compact array of items with price less than or equal to maxPrice
     * @throws IllegalArgumentException if maxPrice is negative
     */
    public static GroceryItem[] filterPriceAtMost(GroceryItem[] items, double maxPrice) {
        if (maxPrice < 0) {
            throw new IllegalArgumentException("Max price cannot be negative");
        }
        
        if (items == null) {
            return new GroceryItem[0];
        }
        
        // Count matching items
        int count = 0;
        for (GroceryItem item : items) {
            if (item != null && item.getPrice() <= maxPrice) {
                count++;
            }
        }
        
        // Create compact array
        GroceryItem[] result = new GroceryItem[count];
        int index = 0;
        for (GroceryItem item : items) {
            if (item != null && item.getPrice() <= maxPrice) {
                result[index++] = item;
            }
        }
        
        return result;
    }
    
    /**
     * Filters grocery items produced in a specific year range.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @param startYear the start year (inclusive)
     * @param endYear the end year (inclusive)
     * @return compact array of items from that year range
     */
    public static GroceryItem[] filterByProductionYearRange(GroceryItem[] items, int startYear, int endYear) {
        if (items == null || startYear > endYear) {
            return new GroceryItem[0];
        }
        
        // Count matching items
        int count = 0;
        for (GroceryItem item : items) {
            if (item != null && item.getProductionYear() >= startYear && item.getProductionYear() <= endYear) {
                count++;
            }
        }
        
        // Create compact array
        GroceryItem[] result = new GroceryItem[count];
        int index = 0;
        for (GroceryItem item : items) {
            if (item != null && item.getProductionYear() >= startYear && item.getProductionYear() <= endYear) {
                result[index++] = item;
            }
        }
        
        return result;
    }
    
    /**
     * Filters grocery items expiring in a specific year range.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @param startYear the start year (inclusive)
     * @param endYear the end year (inclusive)
     * @return compact array of items expiring in that year range
     */
    public static GroceryItem[] filterByExpirationYearRange(GroceryItem[] items, int startYear, int endYear) {
        if (items == null || startYear > endYear) {
            return new GroceryItem[0];
        }
        
        // Count matching items
        int count = 0;
        for (GroceryItem item : items) {
            if (item != null && item.getExpirationYear() >= startYear && item.getExpirationYear() <= endYear) {
                count++;
            }
        }
        
        // Create compact array
        GroceryItem[] result = new GroceryItem[count];
        int index = 0;
        for (GroceryItem item : items) {
            if (item != null && item.getExpirationYear() >= startYear && item.getExpirationYear() <= endYear) {
                result[index++] = item;
            }
        }
        
        return result;
    }
    
    /**
     * Sorts grocery items by price in ascending order (in-place).
     * Nulls are moved to the end.
     * 
     * @param items array to sort (modified in-place)
     */
    public static void sortByPrice(GroceryItem[] items) {
        if (items == null || items.length <= 1) {
            return;
        }
        
        Arrays.sort(items, (a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            return Double.compare(a.getPrice(), b.getPrice());
        });
    }
    
    /**
     * Sorts grocery items by production year in ascending order (in-place).
     * Nulls are moved to the end.
     * 
     * @param items array to sort (modified in-place)
     */
    public static void sortByProductionYear(GroceryItem[] items) {
        if (items == null || items.length <= 1) {
            return;
        }
        
        Arrays.sort(items, (a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            return Integer.compare(a.getProductionYear(), b.getProductionYear());
        });
    }
    
    /**
     * Sorts grocery items by expiration year in ascending order (in-place).
     * Nulls are moved to the end.
     * 
     * @param items array to sort (modified in-place)
     */
    public static void sortByExpirationYear(GroceryItem[] items) {
        if (items == null || items.length <= 1) {
            return;
        }
        
        Arrays.sort(items, (a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            return Integer.compare(a.getExpirationYear(), b.getExpirationYear());
        });
    }
    
    /**
     * Calculates the average price of grocery items in the array.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return average price, or 0.0 if array is null or empty
     */
    public static double averagePrice(GroceryItem[] items) {
        if (items == null) {
            return 0.0;
        }
        
        double sum = 0.0;
        int count = 0;
        
        for (GroceryItem item : items) {
            if (item != null) {
                sum += item.getPrice();
                count++;
            }
        }
        
        return count == 0 ? 0.0 : sum / count;
    }
    
    /**
     * Finds the oldest grocery item (earliest production year).
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return the oldest item, or null if array is null/empty
     */
    public static GroceryItem findOldest(GroceryItem[] items) {
        if (items == null) {
            return null;
        }
        
        GroceryItem oldest = null;
        for (GroceryItem item : items) {
            if (item != null) {
                if (oldest == null || item.getProductionYear() < oldest.getProductionYear()) {
                    oldest = item;
                }
            }
        }
        
        return oldest;
    }
    
    /**
     * Finds the item that expires soonest.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return the item expiring soonest, or null if array is null/empty
     */
    public static GroceryItem findExpiringSoonest(GroceryItem[] items) {
        if (items == null) {
            return null;
        }
        
        GroceryItem soonest = null;
        for (GroceryItem item : items) {
            if (item != null) {
                if (soonest == null || item.getExpirationYear() < soonest.getExpirationYear()) {
                    soonest = item;
                }
            }
        }
        
        return soonest;
    }
    
    /**
     * Merges two grocery item arrays into one, preserving all elements.
     * 
     * @param arr1 first array (may be null)
     * @param arr2 second array (may be null)
     * @return merged array containing all items from both arrays
     */
    public static GroceryItem[] merge(GroceryItem[] arr1, GroceryItem[] arr2) {
        int len1 = (arr1 == null) ? 0 : arr1.length;
        int len2 = (arr2 == null) ? 0 : arr2.length;
        
        GroceryItem[] result = new GroceryItem[len1 + len2];
        
        if (arr1 != null) {
            System.arraycopy(arr1, 0, result, 0, len1);
        }
        if (arr2 != null) {
            System.arraycopy(arr2, 0, result, len1, len2);
        }
        
        return result;
    }
    
    /**
     * Removes duplicate grocery items based on SKU.
     * Returns a compact array with unique items only.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return compact array with duplicates removed
     */
    public static GroceryItem[] removeDuplicates(GroceryItem[] items) {
        if (items == null) {
            return new GroceryItem[0];
        }
        
        Set<String> seenSkus = new HashSet<>();
        List<GroceryItem> unique = new ArrayList<>();
        
        for (GroceryItem item : items) {
            if (item != null && seenSkus.add(item.getSku())) {
                unique.add(item);
            }
        }
        
        return unique.toArray(new GroceryItem[0]);
    }
    
    /**
     * Filters expired grocery items.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return compact array of expired items
     */
    public static GroceryItem[] filterExpired(GroceryItem[] items) {
        if (items == null) {
            return new GroceryItem[0];
        }
        
        return Stream.of(items)
            .filter(item -> item != null && item.isExpired())
            .toArray(GroceryItem[]::new);
    }
    
    /**
     * Filters non-expired grocery items.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return compact array of non-expired items
     */
    public static GroceryItem[] filterNonExpired(GroceryItem[] items) {
        if (items == null) {
            return new GroceryItem[0];
        }
        
        return Stream.of(items)
            .filter(item -> item != null && !item.isExpired())
            .toArray(GroceryItem[]::new);
    }
    
    /**
     * Groups grocery items by brand and returns a summary.
     * 
     * @param items array of grocery items
     * @return map with brand as key and count as value
     */
    public static Map<String, Integer> countByBrand(GroceryItem[] items) {
        Map<String, Integer> brandCounts = new TreeMap<>();
        
        if (items != null) {
            for (GroceryItem item : items) {
                if (item != null) {
                    String brand = item.getBrand();
                    brandCounts.merge(brand, 1, Integer::sum);
                }
            }
        }
        
        return brandCounts;
    }
    
    /**
     * Groups grocery items by production year and returns a summary.
     * 
     * @param items array of grocery items
     * @return map with production year as key and count as value
     */
    public static Map<Integer, Integer> countByProductionYear(GroceryItem[] items) {
        Map<Integer, Integer> yearCounts = new TreeMap<>();
        
        if (items != null) {
            for (GroceryItem item : items) {
                if (item != null) {
                    int year = item.getProductionYear();
                    yearCounts.merge(year, 1, Integer::sum);
                }
            }
        }
        
        return yearCounts;
    }
    
    /**
     * Groups grocery items by expiration year and returns a summary.
     * 
     * @param items array of grocery items
     * @return map with expiration year as key and count as value
     */
    public static Map<Integer, Integer> countByExpirationYear(GroceryItem[] items) {
        Map<Integer, Integer> yearCounts = new TreeMap<>();
        
        if (items != null) {
            for (GroceryItem item : items) {
                if (item != null) {
                    int year = item.getExpirationYear();
                    yearCounts.merge(year, 1, Integer::sum);
                }
            }
        }
        
        return yearCounts;
    }
    
    /**
     * Finds the grocery item with the longest name.
     * 
     * @param items array of grocery items
     * @return grocery item with longest name, null if array is null/empty
     */
    public static GroceryItem findLongestName(GroceryItem[] items) {
        if (items == null) {
            return null;
        }
        
        GroceryItem longest = null;
        int maxLength = 0;
        
        for (GroceryItem item : items) {
            if (item != null && item.getName().length() > maxLength) {
                maxLength = item.getName().length();
                longest = item;
            }
        }
        
        return longest;
    }
    
    /**
     * Calculates the average shelf life of grocery items in the array.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return average shelf life in years, or 0.0 if array is null or empty
     */
    public static double averageShelfLife(GroceryItem[] items) {
        if (items == null) {
            return 0.0;
        }
        
        double total = 0.0;
        int count = 0;
        
        for (GroceryItem item : items) {
            if (item != null) {
                total += item.getShelfLifeYears();
                count++;
            }
        }
        
        return count == 0 ? 0.0 : total / count;
    }
    
    /**
     * Finds the grocery item with the longest shelf life.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return item with longest shelf life, null if array is null/empty
     */
    public static GroceryItem findLongestShelfLife(GroceryItem[] items) {
        if (items == null) {
            return null;
        }
        
        GroceryItem longest = null;
        int maxShelfLife = 0;
        
        for (GroceryItem item : items) {
            if (item != null && item.getShelfLifeYears() > maxShelfLife) {
                maxShelfLife = item.getShelfLifeYears();
                longest = item;
            }
        }
        
        return longest;
    }
    
    /**
     * Finds the most expensive grocery item.
     * 
     * @param items array of grocery items (may be null or contain nulls)
     * @return the most expensive item, null if array is null/empty
     */
    public static GroceryItem findMostExpensive(GroceryItem[] items) {
        if (items == null) {
            return null;
        }
        
        GroceryItem mostExpensive = null;
        for (GroceryItem item : items) {
            if (item != null) {
                if (mostExpensive == null || item.getPrice() > mostExpensive.getPrice()) {
                    mostExpensive = item;
                }
            }
        }
        
        return mostExpensive;
    }
}