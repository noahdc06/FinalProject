package com.university.grocerystore.utils;

import org.slf4j.Logger;

/**
 * Factory for creating SLF4J loggers with consistent configuration.
 * This utility class provides a centralized way to create loggers
 * throughout the application.
 */
public final class LoggerFactory {
    
    private LoggerFactory() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Creates a logger for the specified class.
     * 
     * @param clazz the class for which to create a logger
     * @return the configured logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Creates a logger with the specified name.
     * 
     * @param name the name for the logger
     * @return the configured logger
     */
    public static Logger getLogger(String name) {
        return org.slf4j.LoggerFactory.getLogger(name);
    }
}
