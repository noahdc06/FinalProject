package com.university.grocerystore.observer;

import java.util.HashMap;
import java.util.Map;

/**
 * Observer that collects analytics data from grocery product events.
 * Tracks event counts, product statistics, and system performance metrics.
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class AnalyticsObserver implements GroceryProductObserver {
    
    private final Map<String, Integer> eventCounts;
    private final Map<String, Double> totalValue;
    private final Map<String, Integer> productTypeCounts;
    private int totalEvents;
    private long firstEventTime;
    private long lastEventTime;
    
    /**
     * Creates a new analytics observer.
     */
    public AnalyticsObserver() {
        this.eventCounts = new HashMap<>();
        this.totalValue = new HashMap<>();
        this.productTypeCounts = new HashMap<>();
        this.totalEvents = 0;
        this.firstEventTime = 0;
        this.lastEventTime = 0;
    }
    
    @Override
    public void onEvent(GroceryProductEvent event) {
        totalEvents++;
        long currentTime = event.getTimestamp();
        
        // Track event timing
        if (firstEventTime == 0) {
            firstEventTime = currentTime;
        }
        lastEventTime = currentTime;
        
        // Track event counts
        String eventType = event.getEventType();
        eventCounts.merge(eventType, 1, Integer::sum);
        
        // Track product statistics
        String productId = event.getProduct().getId();
        String productType = event.getProduct().getType().toString();
        
        if ("PRODUCT_ADDED".equals(eventType)) {
            totalValue.merge(productId, event.getProduct().getPrice(), Double::sum);
            productTypeCounts.merge(productType, 1, Integer::sum);
        }
    }
    
    /**
     * Gets event statistics.
     * 
     * @return map of event type to count
     */
    public Map<String, Integer> getEventStatistics() {
        return new HashMap<>(eventCounts);
    }
    
    /**
     * Gets the count for a specific event type.
     * 
     * @param eventType the event type
     * @return the event count
     */
    public int getEventCount(String eventType) {
        return eventCounts.getOrDefault(eventType, 0);
    }
    
    /**
     * Gets the total number of events processed.
     * 
     * @return the total event count
     */
    public int getTotalEvents() {
        return totalEvents;
    }
    
    /**
     * Gets the total inventory value.
     * 
     * @return the total value
     */
    public double getTotalInventoryValue() {
        return totalValue.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
    
    /**
     * Gets product type statistics.
     * 
     * @return map of product type to count
     */
    public Map<String, Integer> getProductTypeStatistics() {
        return new HashMap<>(productTypeCounts);
    }
    
    /**
     * Gets the count for a specific product type.
     * 
     * @param productType the product type
     * @return the product count
     */
    public int getProductTypeCount(String productType) {
        return productTypeCounts.getOrDefault(productType, 0);
    }
    
    /**
     * Gets the number of unique products.
     * 
     * @return the unique product count
     */
    public int getUniqueProductCount() {
        return totalValue.size();
    }
    
    /**
     * Gets the time of the first event.
     * 
     * @return the first event timestamp
     */
    public long getFirstEventTime() {
        return firstEventTime;
    }
    
    /**
     * Gets the time of the last event.
     * 
     * @return the last event timestamp
     */
    public long getLastEventTime() {
        return lastEventTime;
    }
    
    /**
     * Gets the time span of events.
     * 
     * @return the time span in milliseconds
     */
    public long getEventTimeSpan() {
        if (firstEventTime == 0) {
            return 0;
        }
        return lastEventTime - firstEventTime;
    }
    
    /**
     * Gets the average events per second.
     * 
     * @return the average event rate
     */
    public double getAverageEventRate() {
        long timeSpan = getEventTimeSpan();
        if (timeSpan == 0) {
            return 0.0;
        }
        return (double) totalEvents / (timeSpan / 1000.0);
    }
    
    /**
     * Gets comprehensive analytics data.
     * 
     * @return analytics data object
     */
    public AnalyticsData getAnalyticsData() {
        return new AnalyticsData(
            totalEvents,
            getTotalInventoryValue(),
            getUniqueProductCount(),
            getEventStatistics(),
            getProductTypeStatistics(),
            firstEventTime,
            lastEventTime,
            getEventTimeSpan(),
            getAverageEventRate()
        );
    }
    
    /**
     * Clears all analytics data.
     */
    public void clear() {
        eventCounts.clear();
        totalValue.clear();
        productTypeCounts.clear();
        totalEvents = 0;
        firstEventTime = 0;
        lastEventTime = 0;
    }
    
    @Override
    public String getObserverName() {
        return "AnalyticsObserver";
    }
    
    /**
     * Comprehensive analytics data container.
     */
    public static class AnalyticsData {
        private final int totalEvents;
        private final double totalInventoryValue;
        private final int uniqueProductCount;
        private final Map<String, Integer> eventStatistics;
        private final Map<String, Integer> productTypeStatistics;
        private final long firstEventTime;
        private final long lastEventTime;
        private final long eventTimeSpan;
        private final double averageEventRate;
        
        /**
         * Creates analytics data with comprehensive statistics.
         * 
         * @param totalEvents total number of events
         * @param totalInventoryValue total value of inventory
         * @param uniqueProductCount number of unique products
         * @param eventStatistics map of event type to count
         * @param productTypeStatistics map of product type to count
         * @param firstEventTime timestamp of first event
         * @param lastEventTime timestamp of last event
         * @param eventTimeSpan time span of events
         * @param averageEventRate average rate of events
         */
        public AnalyticsData(int totalEvents, double totalInventoryValue, int uniqueProductCount,
                           Map<String, Integer> eventStatistics, Map<String, Integer> productTypeStatistics,
                           long firstEventTime, long lastEventTime, long eventTimeSpan, double averageEventRate) {
            this.totalEvents = totalEvents;
            this.totalInventoryValue = totalInventoryValue;
            this.uniqueProductCount = uniqueProductCount;
            this.eventStatistics = new HashMap<>(eventStatistics);
            this.productTypeStatistics = new HashMap<>(productTypeStatistics);
            this.firstEventTime = firstEventTime;
            this.lastEventTime = lastEventTime;
            this.eventTimeSpan = eventTimeSpan;
            this.averageEventRate = averageEventRate;
        }
        
        /** Gets the total number of events. @return total number of events */
        public int getTotalEvents() { return totalEvents; }
        /** Gets the total inventory value. @return total inventory value */
        public double getTotalInventoryValue() { return totalInventoryValue; }
        /** Gets the number of unique products. @return number of unique products */
        public int getUniqueProductCount() { return uniqueProductCount; }
        /** Gets the event statistics map. @return map of event type to count */
        public Map<String, Integer> getEventStatistics() { return new HashMap<>(eventStatistics); }
        /** Gets the product type statistics map. @return map of product type to count */
        public Map<String, Integer> getProductTypeStatistics() { return new HashMap<>(productTypeStatistics); }
        /** Gets the timestamp of first event. @return timestamp of first event */
        public long getFirstEventTime() { return firstEventTime; }
        /** Gets the timestamp of last event. @return timestamp of last event */
        public long getLastEventTime() { return lastEventTime; }
        /** Gets the time span of events. @return time span of events */
        public long getEventTimeSpan() { return eventTimeSpan; }
        /** Gets the average rate of events. @return average rate of events */
        public double getAverageEventRate() { return averageEventRate; }
        
        @Override
        public String toString() {
            return String.format("AnalyticsData[Events=%d, Value=$%.2f, Products=%d, Rate=%.2f/s]",
                totalEvents, totalInventoryValue, uniqueProductCount, averageEventRate);
        }
    }
    
    @Override
    public String toString() {
        return String.format("AnalyticsObserver[Events=%d, Products=%d, Value=$%.2f]",
            getTotalEvents(), getUniqueProductCount(), getTotalInventoryValue());
    }
}