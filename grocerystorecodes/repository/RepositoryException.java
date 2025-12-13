package com.university.grocerystore.repository;

/**
 * Exception thrown when repository operations fail.
 * 
 * <p>This exception wraps underlying storage-related exceptions and provides
 * a clean abstraction for the domain layer.</p>
 * 
 * @author Navid Mohaghegh
 * @version 3.0
 * @since 2024-09-15
 */
public class RepositoryException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new repository exception with the specified message.
     * 
     * @param message the error message
     */
    public RepositoryException(String message) {
        super(message);
    }
    
    /**
     * Creates a new repository exception with the specified message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates a new repository exception with the specified cause.
     * 
     * @param cause the underlying cause
     */
    public RepositoryException(Throwable cause) {
        super(cause);
    }
}