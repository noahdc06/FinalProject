package com.university.grocerystore.decorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.university.grocerystore.model.GroceryProduct;

/**
 * Decorator that adds organic certification functionality to products.
 * Increases the price and provides certification management capabilities.
 */
public class OrganicCertificationDecorator extends ProductDecorator {
    
    private static final double ORGANIC_CERTIFICATION_COST = 1.99;
    private final List<String> certifications;
    
    /**
     * Creates a new organic certification decorator.
     * 
     * @param product the product to add organic certification to
     */
    public OrganicCertificationDecorator(GroceryProduct product) {
        super(product);
        this.certifications = new ArrayList<>();
        // Add default organic certification
        this.certifications.add("USDA Organic Certified");
    }
    
    @Override
    public double getPrice() {
        return decoratedProduct.getPrice() + ORGANIC_CERTIFICATION_COST;
    }
    
    @Override
    public String getDisplayInfo() {
        return decoratedProduct.getDisplayInfo() + 
               String.format(" [Organic Certified: %d certifications (+$%.2f)]", 
                           certifications.size(), ORGANIC_CERTIFICATION_COST);
    }
    
    /**
     * Adds an additional organic certification to the product.
     * 
     * @param certification the certification text
     * @throws IllegalArgumentException if certification is null or empty
     */
    public void addCertification(String certification) {
        if (certification == null || certification.trim().isEmpty()) {
            throw new IllegalArgumentException("Certification cannot be null or empty");
        }
        certifications.add(certification.trim());
    }
    
    /**
     * Removes a certification by index.
     * 
     * @param index the index of the certification to remove
     * @return the removed certification
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public String removeCertification(int index) {
        if (index < 0 || index >= certifications.size()) {
            throw new IndexOutOfBoundsException("Invalid certification index: " + index);
        }
        return certifications.remove(index);
    }
    
    /**
     * Gets all certifications.
     * 
     * @return unmodifiable list of certifications
     */
    public List<String> getCertifications() {
        return Collections.unmodifiableList(certifications);
    }
    
    /**
     * Gets the number of certifications.
     * 
     * @return the certification count
     */
    public int getCertificationCount() {
        return certifications.size();
    }
    
    /**
     * Clears all additional certifications (keeps the default USDA certification).
     */
    public void clearAdditionalCertifications() {
        String defaultCert = "USDA Organic Certified";
        certifications.clear();
        certifications.add(defaultCert);
    }
    
    /**
     * Gets the organic certification cost.
     * 
     * @return the certification cost
     */
    public double getOrganicCertificationCost() {
        return ORGANIC_CERTIFICATION_COST;
    }
    
    /**
     * Checks if the product has any additional certifications beyond the default.
     * 
     * @return true if additional certifications exist
     */
    public boolean hasAdditionalCertifications() {
        return certifications.size() > 1;
    }
    
    /**
     * Gets the default USDA organic certification.
     * 
     * @return the default certification
     */
    public String getDefaultCertification() {
        return "USDA Organic Certified";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        
        OrganicCertificationDecorator that = (OrganicCertificationDecorator) obj;
        return Objects.equals(certifications, that.certifications);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), certifications);
    }
}
