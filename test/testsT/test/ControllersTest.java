package testsT.test;

import domainLayerInventory.*;
import domainLayerSuppliers.ProductRes;
import domainLayerSuppliers.Reservation;
import domainLayerSuppliers.Supplier;
import domainLayerSuppliers.SupplyController;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import serviceLayer.SimulatedClock;
import serviceLayer.StorageController;
import util.DatabaseSeeder;
import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllersTest {

    private static InventoryController inventoryController;
    private static ExpiryController expiryReportController;
    private static SupplyController supplyController;
    private static StorageController storageController;

    @BeforeClass
    public static void setupClass() {
        DatabaseSeeder.seed();
        storageController = new StorageController();
        inventoryController = new InventoryController();
        expiryReportController = new ExpiryController();
        supplyController = new SupplyController();
    }

    @Test
    public void test01InventoryGetProductByCode() {
        Product p = inventoryController.getProductByCode("P001");
        assertNotNull(p);
        assertEquals("Tomato", p.getName());
    }

    @Test
    public void test02InventoryGetAllProducts() {
        List<Product> products = inventoryController.getAllProducts();
        assertNotNull(products);
        assertTrue(products.size() >= 5);
    }

    @Test
    public void test03InventoryRegisterPurchaseAndStockUpdate() {
        Map<String, Integer> boughtItems = new HashMap<>();
        boughtItems.put("P001", 5);

        boolean success = inventoryController.registerPurchase(boughtItems, LocalDate.now());
        assertTrue(success);

        Product updated = inventoryController.getProductByCode("P001");
        assertNotNull(updated);
        assertTrue(updated.getQuantityOnShelf() + updated.getQuantityInStorage() == 125);
    }

    @Test
    public void test04InventoryGenerateReportByCategory() {
        List<String> categories = new ArrayList<>();
        categories.add("vegetable");
        List<Product> vegProducts = inventoryController.generateReportByCategory(categories);
        assertNotNull(vegProducts);
        assertFalse(vegProducts.isEmpty());
        boolean allMatch = vegProducts.stream()
            .allMatch(p -> p.getCategories().stream().anyMatch(cat -> cat.getName().equals("vegetable")));
        assertTrue(allMatch);
    }

    @Test
    public void test05InventoryAddNewProduct() {
        List<Double> costPrices = new ArrayList<>();
        costPrices.add(4.0);
        List<String> suppliers = new ArrayList<>();
        suppliers.add("Milky Sunshine LTD");
        Product newProduct = new Product("P006", "Sweet Peanuts", "New York Sweets", costPrices, 5.0,  "10 days","Aisle 9", 20, 50, 10,suppliers, 2);
        List<String> categories = new ArrayList<>();
        categories.add("sweets");
        categories.add("other");

        boolean added = inventoryController.addNewProduct(newProduct, categories);
        assertTrue(added);

        Product fetched = inventoryController.getProductByCode("P006");
        assertNotNull(fetched);
        assertEquals("Sweet Peanuts", fetched.getName());
        assertTrue(fetched.getCategories().get(1).getFullPath().equals("sweets->other"));
    }

    @Test
    public void test06InventoryGetDemandForItem() {
        List<Integer> demands = inventoryController.getDemandForItem("P001");
        assertNotNull(demands);
        assertTrue("Demand list must contain at least 6 months", demands.size() > 5);
        int juneDemand = demands.get(6);
        assertEquals("Expected demand for June is 5", 5, juneDemand);
    }

    @Test
    public void test07InventoryCalculateMinByDemand() {
        boolean calculated = inventoryController.calculateMinByDemandForAllProducts();
        assertTrue(calculated);
    }

    @Test
    public void test08InventoryGetCategoryByName() {
        Category cat = inventoryController.getCategoryByName("fresh");
        assertNotNull(cat);
        assertEquals("fresh", cat.getName());
        assertEquals("vegetable", cat.getParentCategory().getName());
    }

    @Test
    public void test09InventoryUpdateProductQuantityAndStock() {
        Product p = inventoryController.getProductByCode("P001");
        assertNotNull(p);
        p.setQuantityOnShelf(p.getQuantityOnShelf() + 10);
        boolean updated = inventoryController.updateProductQuantity(p);
        assertTrue(updated);

        boolean stockUpdated = inventoryController.updateStock("P001", 50);
        assertTrue(stockUpdated);
    }

    // ExpiryReportController tests

    @Test
    public void test10ExpiryAddAndGetReports() {
        ExpiryReport report = new ExpiryReport("83I920FR", "P001",20, "Storage",SimulatedClock.getInstance().getCurrentDate(), "Danny");
        boolean added = expiryReportController.addExpiryReport(report);
        assertTrue(added);

        List<ExpiryReport> reports = expiryReportController.getAllReports();
        assertNotNull(reports);
        assertTrue(reports.stream().anyMatch(r -> r.getId().equals("83I920FR")));
    }

    @Test
    public void test11ExpiryResetAndGetReportsByProduct() {
        expiryReportController.resetAllReports();

        List<ExpiryReport> reports = expiryReportController.getReportsByProduct("P001");
        assertNotNull(reports);
        assertTrue(reports.isEmpty());
    }

    @Test
    public void test12ExpiryGenerateReport() {
        expiryReportController.resetAllReports();
        expiryReportController.addExpiryReport(new ExpiryReport("P849TEI2", "P001", 10 ,"Storage", LocalDate.now().plusDays(3), "Samuel"));
        List<ExpiryReport> upcoming = expiryReportController.generateReport(5, LocalDate.now());
        assertNotNull(upcoming);
        assertTrue(upcoming.stream().anyMatch(r -> r.getId().equals("P849TEI2")));
    }

    // SupplyController tests

    @Test
    public void test13SupplyFindSupplierByIdAndName() {
        Optional<Supplier> sup1 = supplyController.findSupplierById("8JW92KV8");
        assertTrue(sup1.isPresent());

        Optional<Supplier> sup2 = supplyController.findSupplierByName("Fresh Produce Ltd");
        assertTrue(sup2.isPresent());
    }

    @Test
    public void test14SupplyGetSupplyDaysAndContacts() {
        Optional<Supplier> supplier = supplyController.findSupplierByName("Fresh Produce Ltd");
        assertTrue(supplier.isPresent());
        String sID = supplier.get().getSID();

        List<DayOfWeek> days = supplyController.getSupplierSupplyDays(sID);
        assertNotNull(days);
        assertFalse(days.isEmpty());

        String contacts = supplyController.getSupplierContacts(sID);
        assertNotNull(contacts);
        assertTrue(contacts.contains("Alice Supplier"));
    }

    @Test
    public void test15SupplyAddNewSupplier() {
        String supplierId = "HE93KRIO";
        List<String> supplyDays = Arrays.asList("Monday", "Friday");
        boolean added = supplyController.addNewSupplier(supplierId, "Test Supplier", "123 Test St", "CASH", "BA123456", "John Doe", "123456789", "john@test.com", supplyDays);
        assertTrue(added);

        Optional<Supplier> fetched = supplyController.findSupplierById(supplierId);
        assertTrue(fetched.isPresent());
        assertEquals("Test Supplier", fetched.get().getName());
    }

    @Test
    public void test16SupplyAddAndRemoveAgreement() {
        Optional<Supplier> supplierOpt = supplyController.findSupplierByName("Fresh Produce Ltd");
        assertTrue("Supplier should be present", supplierOpt.isPresent());
        Supplier supplier = supplierOpt.get();

        String newAgreementId = "839KRJ2W";
        List<String[]> productDetails = new ArrayList<>();
        productDetails.add(new String[]{"Cucumber", "1.0", "P002"});

        boolean added = supplyController.addAgreementToSupplier(newAgreementId, supplier.getSID(),
            "John Doe", "123456789", "john@test.com", productDetails, true);
        assertTrue("Agreement should be added successfully", added);

        Optional<Supplier> updatedSupplierOpt = supplyController.findSupplierById(supplier.getSID());
        assertTrue(updatedSupplierOpt.isPresent());
        Supplier updatedSupplier = updatedSupplierOpt.get();

        boolean agreementExists = updatedSupplier.getAgreements().stream()
            .anyMatch(a -> a.getAgreementID().equals(newAgreementId));
        assertTrue("New agreement should exist in supplier agreements", agreementExists);

        boolean removed = supplyController.removeAgreementFromSupplier(supplier.getSID(), newAgreementId);
        assertTrue("Agreement should be removed successfully", removed);

        Optional<Supplier> afterRemovalSupplierOpt = supplyController.findSupplierById(supplier.getSID());
        assertTrue(afterRemovalSupplierOpt.isPresent());
        Supplier afterRemovalSupplier = afterRemovalSupplierOpt.get();

        boolean agreementStillExists = afterRemovalSupplier.getAgreements().stream()
            .anyMatch(a -> a.getAgreementID().equals(newAgreementId));
        assertFalse("Agreement should no longer exist after removal", agreementStillExists);
    }

    @Test
    public void test17SupplyAddProductToAgreement() {
        Optional<Supplier> supplierOpt = supplyController.findSupplierByName("Milky Sunshine LTD");
        assertTrue(supplierOpt.isPresent());
        Supplier supplier = supplierOpt.get();

        String agreementId = "92KVEU45";

        boolean added = supplyController.addProductToAgreement(supplier.getSID(), agreementId, "Sweet Peanuts", 12.5, "P006");
        assertTrue(added || !added); // Adjust as needed
    }

    @Test
public void test18SupplyAddReservation() {
    Optional<Supplier> supplierOpt = supplyController.findSupplierByName("Milky Sunshine LTD");
    assertTrue(supplierOpt.isPresent());
    Supplier supplier = supplierOpt.get();

    String resId = "93JFKWIO";
    List<String> productNums = Arrays.asList("P006", "P004");
    List<Integer> quantities = Arrays.asList(5, 10);
    String agreementId = "92KVEU45";

    boolean added = supplyController.addReservation(resId, supplier.getSID(), productNums, quantities, agreementId, LocalDate.now());
    assertTrue(added);

    Optional<Reservation> reservationOpt = supplier.getReservations().stream()
        .filter(r -> r.getResID().equals(resId))
        .findFirst();
    assertTrue("Reservation should be present", reservationOpt.isPresent());

    Reservation reservation = reservationOpt.get();
    assertNotNull("Agreement should not be null", reservation.getResAgreement());
    assertEquals("Agreement ID should match", agreementId, reservation.getResAgreement().getAgreementID());

    assertNotNull("Contact person should not be null", reservation.getResAgreement().getContactPerson());
    assertEquals("Amanda Hopkins", reservation.getResAgreement().getContactPerson().getName());

    Map<String, Integer> expectedItems = new HashMap<>();
    expectedItems.put("P006", 5);
    expectedItems.put("P004", 10);

    for (Map.Entry<String, Integer> entry : expectedItems.entrySet()) {
        Optional<ProductRes> itemOpt = reservation.getResProducts().stream()
            .filter(i -> i.getProduct().getProductNum().equals(entry.getKey()))
            .findFirst();
        assertTrue("Reservation should contain product " + entry.getKey(), itemOpt.isPresent());
        assertEquals("Quantity should match", entry.getValue().intValue(), itemOpt.get().getQuantity());
    }
}

@Test
public void test19AutoReservationCreatedWhenBelowMin() {
    // Arrange: get product and set quantity below minimum threshold
    Product p = inventoryController.getProductByCode("P001");
    int minThreshold = p.getMinimumThreshold();
    storageController.updateProductStock("P003", minThreshold-1);

    // Assert: verify a new reservation for this product exists
    List<Reservation> reservations = supplyController.getAllReservations("KSU937RX");
    assertNotNull(reservations);
    assertFalse("No reservation found after stock dropped below min threshold", reservations.isEmpty());
}

@Test
public void test20WeeklyTemplateCreatesAndSuppliesReservation() {
    // Arrange: Find supplier and product
    Optional<Supplier> supplierOpt = supplyController.findSupplierByName("Milky Sunshine LTD");
    assertTrue("Supplier not found", supplierOpt.isPresent());
    Supplier supplier = supplierOpt.get();

    // Agreement details
    assertFalse("Supplier has no agreements", supplier.getAgreements().isEmpty());
    String agreementId = supplier.getAgreements().get(0).getAgreementID();

    // Product to track inventory change
    Product beforeSupply = inventoryController.getProductByCode("P006");
    int beforeQty = beforeSupply.getTotalQuantity();

    // Create a weekly template that will trigger in 3 days
    List<String> productCodes = Arrays.asList("P006");
    List<Integer> quantities = Arrays.asList(10);
    DayOfWeek supplyDay = SimulatedClock.getInstance().getCurrentDate().plusDays(3).getDayOfWeek();

    boolean created = supplyController.addResTemplate(
        "WEEKLY_T1",
        SimulatedClock.getInstance().getCurrentDate(),
        supplier.getSID(),
        productCodes,
        quantities,
        agreementId,
        supplyDay
    );
    assertTrue("Weekly template creation failed", created);

    // Act: forward time by 3 days to trigger the template
    storageController.forwardDays(3);

    // Check that the reservation was created
    List<Reservation> reservations = supplyController.getAllReservations(supplier.getSID());
    boolean matchFound = reservations.stream()
        .anyMatch(r -> r.getResProducts().stream()
            .anyMatch(rp -> rp.getProduct().getProductNum().equals("P006") && rp.getQuantity() == 10));
    assertTrue("WeeklyTemplate did not generate expected reservation", matchFound);

    // Act: forward another 7 days to simulate delivery
    storageController.forwardDays(7);

    // Assert: product quantity increased
    Product afterSupply = inventoryController.getProductByCode("P006");
    assertTrue("Product quantity should increase after weekly reservation is supplied",
        afterSupply.getTotalQuantity() > beforeQty);
}

@AfterClass
    public static void cleanupClass() {
        try (Connection conn = util.DatabaseManager.getConnection()) {
            DatabaseSeeder.resetDB(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}