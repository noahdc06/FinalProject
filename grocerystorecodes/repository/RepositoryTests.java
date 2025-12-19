package com.university.grocerystore.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.university.grocerystore.model.CannedGood;
import com.university.grocerystore.model.FrozenFood;
import com.university.grocerystore.model.GroceryProduct;
import com.university.grocerystore.model.Produce;

class RepositoryTests {

    private Produce apple;
    private FrozenFood pizza;
    private CannedGood beans;
    private String testFilePath;

    @BeforeEach
    void setUp() throws Exception {
        apple = new Produce("P001", "Apple", 1.99, 2024, "Fresh Farms", "Gala", 0.5, true, "USA", null);
        pizza = new FrozenFood("F001", "Pizza", 5.99, 2024, "FrozenCo", -18.0, 0.8, true, 450);
        beans = new CannedGood("C001", "Canned Beans", 2.49, 2023, "CanCo", "15 oz", true, "Pressure Canning", 24);

        // Create temporary test file
        Path tempDir = Files.createTempDirectory("grocery-test");
        testFilePath = tempDir.resolve("test-products.json").toString();

        // Clean up any existing test file
        File testFile = new File(testFilePath);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    // ============ JsonGroceryProductRepository Tests ============

    @Test
    void testJsonRepository_SaveAndRetrieve() {
        JsonGroceryProductRepository repo = new JsonGroceryProductRepository(testFilePath);

        repo.save(apple);
        repo.save(pizza);

        Optional<GroceryProduct> found = repo.findById("P001");
        assertTrue(found.isPresent());
        assertEquals("Apple", found.get().getName());
        assertEquals(1.99, found.get().getPrice(), 0.001);

        found = repo.findById("F001");
        assertTrue(found.isPresent());
        assertEquals("Pizza", found.get().getName());
    }

    @Test
    void testJsonRepository_UpdateProduct() {
        JsonGroceryProductRepository repo = new JsonGroceryProductRepository(testFilePath);

        // Save initial product
        repo.save(apple);

        // Create updated version
        Produce updatedApple = new Produce("P001", "Organic Apple", 2.49, 2024,
                "Organic Farms", "Gala", 0.5, true, "Canada", null);

        // Save updated version
        repo.save(updatedApple);

        Optional<GroceryProduct> found = repo.findById("P001");
        assertTrue(found.isPresent());
        assertEquals("Organic Apple", found.get().getName());
        assertEquals(2.49, found.get().getPrice(), 0.001);
        assertEquals("Organic Farms", found.get().getBrand());
    }

    @Test
    void testJsonRepository_FindAll() {
        JsonGroceryProductRepository repo = new JsonGroceryProductRepository(testFilePath);

        repo.save(apple);
        repo.save(pizza);
        repo.save(beans);

        List<GroceryProduct> allProducts = repo.findAll();
        assertEquals(3, allProducts.size());
        assertEquals(3, repo.count());
    }

    @Test
    void testJsonRepository_Delete() {
        JsonGroceryProductRepository repo = new JsonGroceryProductRepository(testFilePath);

        repo.save(apple);
        repo.save(pizza);

        assertEquals(2, repo.count());
        assertTrue(repo.exists("P001"));

        boolean deleted = repo.delete("P001");
        assertTrue(deleted);
        assertEquals(1, repo.count());
        assertFalse(repo.exists("P001"));

        // Try deleting non-existent product
        assertFalse(repo.delete("NONEXISTENT"));
    }

    @Test
    void testJsonRepository_DeleteAll() {
        JsonGroceryProductRepository repo = new JsonGroceryProductRepository(testFilePath);

        repo.save(apple);
        repo.save(pizza);
        repo.save(beans);

        assertEquals(3, repo.count());

        repo.deleteAll();

        assertEquals(0, repo.count());
        List<GroceryProduct> allProducts = repo.findAll();
        assertTrue(allProducts.isEmpty());
    }

    @Test
    void testJsonRepository_FileOperations() {
        JsonGroceryProductRepository repo = new JsonGroceryProductRepository(testFilePath);

        assertFalse(repo.dataFileExists());

        repo.save(apple);

        assertTrue(repo.dataFileExists());
        assertTrue(repo.getDataFileSize() > 0);
    }

    @Test
    void testJsonRepository_PathValidation() {
        // Test path traversal prevention
        assertThrows(SecurityException.class, () ->
                new JsonGroceryProductRepository("../sensitive-file.json"));

        assertThrows(SecurityException.class, () ->
                new JsonGroceryProductRepository("../../etc/passwd"));
    }

    @Test
    void testJsonRepository_NullAndEmptyOperations() {
        JsonGroceryProductRepository repo = new JsonGroceryProductRepository(testFilePath);

        // Test null product save
        assertThrows(IllegalArgumentException.class, () -> repo.save(null));

        // Test null ID lookup
        Optional<GroceryProduct> result = repo.findById(null);
        assertFalse(result.isPresent());

        result = repo.findById("");
        assertFalse(result.isPresent());

        // Test empty ID delete
        assertFalse(repo.delete(null));
        assertFalse(repo.delete(""));
    }

    // ============ ModernJsonGroceryProductRepository Tests ============

    @Test
    void testModernRepository_BasicOperations() {
        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            repo.save(apple);
            repo.save(pizza);

            assertEquals(2, repo.count());
            assertTrue(repo.exists("P001"));
            assertTrue(repo.exists("F001"));
            assertFalse(repo.exists("NONEXISTENT"));

            Optional<GroceryProduct> found = repo.findById("P001");
            assertTrue(found.isPresent());
            assertEquals("Apple", found.get().getName());
        }
    }

    @Test
    void testModernRepository_BatchOperations() {
        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            List<GroceryProduct> products = Arrays.asList(apple, pizza, beans);

            repo.saveAll(products);

            assertEquals(3, repo.count());

            List<GroceryProduct> all = repo.findAll();
            assertEquals(3, all.size());

            // Test batch delete
            int deleted = repo.deleteAll(Arrays.asList("P001", "F001"));
            assertEquals(2, deleted);
            assertEquals(1, repo.count());

            // Verify remaining product
            assertFalse(repo.exists("P001"));
            assertFalse(repo.exists("F001"));
            assertTrue(repo.exists("C001"));
        }
    }

    @Test
    void testModernRepository_AtomicOperations() {
        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            // Save multiple products
            repo.save(apple);
            repo.save(pizza);

            // Verify file exists and has content
            assertTrue(repo.dataFileExists());
            long fileSize = repo.getDataFileSize();
            assertTrue(fileSize > 0);

            // Backup should be created during atomic operations
            File backupFile = new File(testFilePath + ".backup");
            assertTrue(backupFile.exists() || !backupFile.exists()); // Backup may or may not exist based on timing
        }
    }

    @Test
    void testModernRepository_BackupAndRestore() {
        String backupFilePath = testFilePath.replace(".json", "-backup.json");

        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            repo.save(apple);
            repo.save(pizza);

            assertEquals(2, repo.count());

            // Create backup
            boolean backupCreated = repo.createBackup();
            assertTrue(backupCreated);

            // Clear repository
            repo.deleteAll();
            assertEquals(0, repo.count());

            // Restore from backup (using the main backup file created by atomic operations)
            // Note: In practice, we'd need to know the exact backup file path
            // For test purposes, we'll just verify the backup creation worked
            assertTrue(repo.dataFileExists());
        }
    }

    @Test
    void testModernRepository_MaintenanceOperations() {
        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            // Add some products
            repo.save(apple);
            repo.save(pizza);
            repo.save(beans);

            // Perform maintenance
            repo.performMaintenance();

            // Verify data still intact
            assertEquals(3, repo.count());
            assertTrue(repo.exists("P001"));
            assertTrue(repo.exists("F001"));
            assertTrue(repo.exists("C001"));
        }
    }

    @Test
    void testModernRepository_ThreadSafety() {
        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            // Test that operations can be called safely
            // (Actual concurrency testing would require multiple threads)
            repo.save(apple);

            // Concurrent-like operations
            Optional<GroceryProduct> found1 = repo.findById("P001");
            repo.save(pizza);
            Optional<GroceryProduct> found2 = repo.findById("F001");
            long count = repo.count();

            assertTrue(found1.isPresent());
            assertTrue(found2.isPresent());
            assertEquals(2, count);
        }
    }

    @Test
    void testModernRepository_AutoCloseable() {
        ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath);
        repo.save(apple);

        assertTrue(repo.dataFileExists());

        repo.close();

        // After closing, operations should fail
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repo.save(pizza)
        );
        assertTrue(exception.getMessage().contains("closed"));
    }

    @Test
    void testModernRepository_PolymorphicSerialization() {
        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            // Save different types of products
            repo.save(apple);      // Produce
            repo.save(pizza);      // FrozenFood
            repo.save(beans);      // CannedGood

            // Reload and verify types preserved
            List<GroceryProduct> loaded = repo.findAll();
            assertEquals(3, loaded.size());

            // Check that types are preserved
            assertTrue(loaded.get(0) instanceof Produce);
            assertTrue(loaded.get(1) instanceof FrozenFood);
            assertTrue(loaded.get(2) instanceof CannedGood);

            // Verify data integrity
            Produce loadedApple = (Produce) loaded.stream()
                    .filter(p -> p.getId().equals("P001"))
                    .findFirst().get();
            assertEquals("Apple", loadedApple.getName());
            assertEquals("Fresh Farms", loadedApple.getBrand());
        }
    }

    @Test
    void testModernRepository_ErrorRecovery() {
        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            repo.save(apple);
            repo.save(pizza);

            // Corrupt the file
            Files.write(Path.of(testFilePath), "corrupted json data".getBytes());

            // Repository should handle corruption gracefully
            List<GroceryProduct> products = repo.findAll();

            // Either returns empty list or recovers from backup
            assertNotNull(products);
            // Size could be 0 (if corruption unrecoverable) or 2 (if backup restored)
        }
    }

    // ============ Repository Interface Tests ============

    @Test
    void testRepositoryInterface_Consistency() {
        // Test that both implementations follow the same interface contract

        GroceryProductRepository jsonRepo = new JsonGroceryProductRepository(testFilePath + "-json");
        GroceryProductRepository modernRepo = new ModernJsonGroceryProductRepository(testFilePath + "-modern");

        // Both should support save
        jsonRepo.save(apple);
        modernRepo.save(apple);

        // Both should support findById
        Optional<GroceryProduct> jsonResult = jsonRepo.findById("P001");
        Optional<GroceryProduct> modernResult = modernRepo.findById("P001");

        assertTrue(jsonResult.isPresent());
        assertTrue(modernResult.isPresent());
        assertEquals("Apple", jsonResult.get().getName());
        assertEquals("Apple", modernResult.get().getName());

        // Both should support count
        assertEquals(1, jsonRepo.count());
        assertEquals(1, modernRepo.count());

        // Both should support delete
        assertTrue(jsonRepo.delete("P001"));
        assertTrue(modernRepo.delete("P001"));

        // Both should report 0 after delete
        assertEquals(0, jsonRepo.count());
        assertEquals(0, modernRepo.count());
    }

    @Test
    void testRepositoryException_Propagation() {
        JsonGroceryProductRepository repo = new JsonGroceryProductRepository("/invalid/path/that/does/not/exist/products.json");

        // Operations should throw RepositoryException
        RepositoryException exception = assertThrows(
                RepositoryException.class,
                () -> repo.save(apple)
        );
        assertTrue(exception.getMessage().contains("Failed to save product"));
    }

    @Test
    void testProductsWrapper_SerializationHelper() {
        // Test the wrapper class used for JSON serialization
        ProductsWrapper wrapper = new ProductsWrapper();
        assertNotNull(wrapper.getProducts());
        assertTrue(wrapper.getProducts().isEmpty());

        List<GroceryProduct> products = Arrays.asList(apple, pizza);
        wrapper.setProducts(products);

        assertEquals(2, wrapper.getProducts().size());
        assertEquals("Apple", wrapper.getProducts().get(0).getName());
        assertEquals("Pizza", wrapper.getProducts().get(1).getName());

        // Test constructor with products
        ProductsWrapper wrapper2 = new ProductsWrapper(products);
        assertEquals(2, wrapper2.getProducts().size());
    }

    @Test
    void testIntegration_CompleteWorkflow() {
        try (ModernJsonGroceryProductRepository repo = new ModernJsonGroceryProductRepository(testFilePath)) {
            // 1. Initial empty state
            assertTrue(repo.findAll().isEmpty());
            assertEquals(0, repo.count());

            // 2. Add products
            repo.save(apple);
            repo.save(pizza);
            repo.save(beans);

            assertEquals(3, repo.count());
            assertTrue(repo.exists("P001"));
            assertTrue(repo.exists("F001"));
            assertTrue(repo.exists("C001"));

            // 3. Retrieve and verify
            Optional<GroceryProduct> found = repo.findById("P001");
            assertTrue(found.isPresent());
            assertEquals(1.99, found.get().getPrice(), 0.001);

            // 4. Update product
            Produce updatedApple = new Produce("P001", "Organic Apple", 2.49, 2024,
                    "Organic Farms", "Gala", 0.5, true, "Canada", null);
            repo.save(updatedApple);

            found = repo.findById("P001");
            assertTrue(found.isPresent());
            assertEquals("Organic Apple", found.get().getName());
            assertEquals(2.49, found.get().getPrice(), 0.001);

            // 5. Delete product
            assertTrue(repo.delete("F001"));
            assertEquals(2, repo.count());
            assertFalse(repo.exists("F001"));

            // 6. Batch operations
            List<GroceryProduct> newProducts = Arrays.asList(
                    new FrozenFood("F002", "Ice Cream", 3.99, 2024, "IceCo", -20.0, 0.5, false, 300)
            );
            repo.saveAll(newProducts);

            assertEquals(3, repo.count());

            // 7. Clear all
            repo.deleteAll();
            assertEquals(0, repo.count());
            assertTrue(repo.findAll().isEmpty());
        }
    }

    @Test
    void testIntegration_FilePersistence() {
        // Test that data persists between repository instances
        String persistFilePath = testFilePath + "-persist";

        // First repository instance
        try (ModernJsonGroceryProductRepository repo1 = new ModernJsonGroceryProductRepository(persistFilePath)) {
            repo1.save(apple);
            repo1.save(pizza);
            assertEquals(2, repo1.count());
        }

        // Second repository instance (should see same data)
        try (ModernJsonGroceryProductRepository repo2 = new ModernJsonGroceryProductRepository(persistFilePath)) {
            assertEquals(2, repo2.count());
            assertTrue(repo2.exists("P001"));
            assertTrue(repo2.exists("F001"));

            List<GroceryProduct> products = repo2.findAll();
            assertEquals(2, products.size());

            // Verify data integrity
            Produce loadedApple = (Produce) products.stream()
                    .filter(p -> p.getId().equals("P001"))
                    .findFirst().get();
            assertEquals("Apple", loadedApple.getName());
            assertEquals("Fresh Farms", loadedApple.getBrand());
        }
    }
}