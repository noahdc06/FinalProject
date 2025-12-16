package com.university.grocerystore.observer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer that maintains an audit log of all grocery product events.
 * Provides a complete history of system activities for compliance and debugging.
 */
public class AuditLogObserver implements GroceryProductObserver {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogObserver.class);
    
    private final List<AuditLogEntry> auditLog;
    private final int maxLogSize;
    
    /**
     * Creates a new audit log observer with unlimited log size.
     */
    public AuditLogObserver() {
        this(Integer.MAX_VALUE);
    }
    
    /**
     * Creates a new audit log observer with the specified maximum log size.
     * 
     * @param maxLogSize the maximum number of log entries to keep
     */
    public AuditLogObserver(int maxLogSize) {
        this.auditLog = new ArrayList<>();
        this.maxLogSize = maxLogSize;
    }
    
    @Override
    public void onEvent(GroceryProductEvent event) {
        if (event == null) {
            return;
        }
        
        AuditLogEntry entry = new AuditLogEntry(
            event.getTimestamp(),
            event.getEventType(),
            event.getProduct().getId(),
            event.getProduct().getName(),
            event.getDescription()
        );
        
        auditLog.add(entry);
        
        // Maintain log size limit
        if (auditLog.size() > maxLogSize) {
            auditLog.remove(0); // Remove oldest entry
        }
    }
    
    /**
     * Gets all audit log entries.
     * 
     * @return list of audit log entries
     */
    public List<AuditLogEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
    
    /**
     * Gets audit log entries for a specific product.
     * 
     * @param productId the product ID to filter by
     * @return list of audit log entries for the product
     */
    public List<AuditLogEntry> getAuditLogForProduct(String productId) {
        return auditLog.stream()
            .filter(entry -> Objects.equals(entry.getProductId(), productId))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets audit log entries for a specific event type.
     * 
     * @param eventType the event type to filter by
     * @return list of audit log entries for the event type
     */
    public List<AuditLogEntry> getAuditLogForEventType(String eventType) {
        return auditLog.stream()
            .filter(entry -> Objects.equals(entry.getEventType(), eventType))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets audit log entries within a time range.
     * 
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @return list of audit log entries within the time range
     */
    public List<AuditLogEntry> getAuditLogForTimeRange(long startTime, long endTime) {
        return auditLog.stream()
            .filter(entry -> entry.getTimestamp() >= startTime && entry.getTimestamp() <= endTime)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Prints the audit log to the console.
     */
    public void printAuditLog() {
        auditLog.forEach(entry -> LOGGER.info("Audit: {}", entry.toString()));
    }
    
    /**
     * Gets the number of audit log entries.
     * 
     * @return the log size
     */
    public int getLogSize() {
        return auditLog.size();
    }
    
    /**
     * Clears the audit log.
     */
    public void clearAuditLog() {
        auditLog.clear();
    }
    
    /**
     * Gets audit log statistics.
     * 
     * @return audit log statistics
     */
    public AuditLogStats getAuditLogStats() {
        int totalEntries = auditLog.size();
        int productAddedCount = (int) auditLog.stream()
            .filter(entry -> "PRODUCT_ADDED".equals(entry.getEventType()))
            .count();
        int priceChangedCount = (int) auditLog.stream()
            .filter(entry -> "PRICE_CHANGED".equals(entry.getEventType()))
            .count();
        
        long oldestTimestamp = auditLog.stream()
            .mapToLong(AuditLogEntry::getTimestamp)
            .min()
            .orElse(0L);
        
        long newestTimestamp = auditLog.stream()
            .mapToLong(AuditLogEntry::getTimestamp)
            .max()
            .orElse(0L);
        
        return new AuditLogStats(
            totalEntries,
            productAddedCount,
            priceChangedCount,
            oldestTimestamp,
            newestTimestamp
        );
    }
    
    @Override
    public String getObserverName() {
        return "AuditLogObserver";
    }
    
    /**
     * Represents a single audit log entry.
     */
    public static class AuditLogEntry {
        private final long timestamp;
        private final String eventType;
        private final String productId;
        private final String productName;
        private final String description;
        
        /**
         * Creates an audit log entry.
         * 
         * @param timestamp when the event occurred
         * @param eventType type of event
         * @param productId ID of the product
         * @param productName name of the product
         * @param description event description
         */
        public AuditLogEntry(long timestamp, String eventType, String productId, 
                           String productName, String description) {
            this.timestamp = timestamp;
            this.eventType = eventType;
            this.productId = productId;
            this.productName = productName;
            this.description = description;
        }
        
        /** Gets the timestamp of the event. @return timestamp of the event */
        public long getTimestamp() { return timestamp; }
        /** Gets the type of event. @return type of event */
        public String getEventType() { return eventType; }
        /** Gets the ID of the product. @return ID of the product */
        public String getProductId() { return productId; }
        /** Gets the name of the product. @return name of the product */
        public String getProductName() { return productName; }
        /** Gets the event description. @return event description */
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s (ID: %s) - %s",
                new Date(timestamp),
                eventType,
                productName,
                productId,
                description);
        }
    }
    
    /**
     * Statistics for the audit log.
     */
    public static class AuditLogStats {
        private final int totalEntries;
        private final int productAddedCount;
        private final int priceChangedCount;
        private final long oldestTimestamp;
        private final long newestTimestamp;
        
        /**
         * Creates audit log statistics.
         * 
         * @param totalEntries total number of entries
         * @param productAddedCount number of product added events
         * @param priceChangedCount number of price changed events
         * @param oldestTimestamp timestamp of oldest entry
         * @param newestTimestamp timestamp of newest entry
         */
        public AuditLogStats(int totalEntries, int productAddedCount, int priceChangedCount,
                           long oldestTimestamp, long newestTimestamp) {
            this.totalEntries = totalEntries;
            this.productAddedCount = productAddedCount;
            this.priceChangedCount = priceChangedCount;
            this.oldestTimestamp = oldestTimestamp;
            this.newestTimestamp = newestTimestamp;
        }
        
        /** Gets the total number of entries. @return total number of entries */
        public int getTotalEntries() { return totalEntries; }
        /** Gets the number of product added events. @return number of product added events */
        public int getProductAddedCount() { return productAddedCount; }
        /** Gets the number of price changed events. @return number of price changed events */
        public int getPriceChangedCount() { return priceChangedCount; }
        /** Gets the timestamp of oldest entry. @return timestamp of oldest entry */
        public long getOldestTimestamp() { return oldestTimestamp; }
        /** Gets the timestamp of newest entry. @return timestamp of newest entry */
        public long getNewestTimestamp() { return newestTimestamp; }
        
        @Override
        public String toString() {
            return String.format("AuditLogStats[Total=%d, Added=%d, PriceChanged=%d, Oldest=%s, Newest=%s]",
                totalEntries, productAddedCount, priceChangedCount,
                new Date(oldestTimestamp), new Date(newestTimestamp));
        }
    }
    
    @Override
    public String toString() {
        return String.format("AuditLogObserver[LogSize=%d, MaxSize=%d]", getLogSize(), maxLogSize);
    }
}
