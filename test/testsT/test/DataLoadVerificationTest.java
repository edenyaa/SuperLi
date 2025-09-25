package testsT.test;

import domainLayerInventory.ExpiryController;
import domainLayerInventory.ExpiryReport;
import domainLayerInventory.InventoryController;
import domainLayerInventory.Product;
import domainLayerSuppliers.Supplier;
import domainLayerSuppliers.SupplyController;
import org.junit.BeforeClass;
import org.junit.Test;
import util.DatabaseSeeder;

import java.util.List;

import static org.junit.Assert.*;

public class DataLoadVerificationTest {

    static InventoryController inventoryController;
    static SupplyController supplyController;
    static ExpiryController expiryController;

    @BeforeClass
    public static void setup() {
        // 1. Seed the database
        DatabaseSeeder.seed();

        // 2. Initialize controllers (they should load from the DB via repositories)
        inventoryController = new InventoryController();
        supplyController = new SupplyController();
        expiryController = new ExpiryController();
    }

    @Test
    public void testProductsLoadedCorrectly() {
        List<Product> products = inventoryController.getAllProducts();
        assertEquals(5, products.size());

        Product tomato = inventoryController.getProductByCode("P001");
        assertNotNull(tomato);
        assertEquals("Tomato", tomato.getName());
        assertEquals("FreshGrow", tomato.getManufacturer());
        assertEquals(1.5, tomato.getSellPrice(), 0.01);
        assertEquals("Aisle 3", tomato.getLocation());
    }

    @Test
    public void testSuppliersAndAgreementsLoaded() {
        List<Supplier> suppliers = supplyController.getAllSuppliers();
        assertEquals(4, suppliers.size());

        Supplier freshProduce = supplyController.findSupplierById("8JW92KV8").get();
        assertNotNull(freshProduce);
        assertEquals("Fresh Produce Ltd", freshProduce.getName());
        assertEquals("123 Farm Lane", freshProduce.getAddress());
        assertEquals("TRANSFER", freshProduce.getPaymentCondition().getPaymentType().name());
        assertEquals("Alice Supplier", freshProduce.getContactPersons().get(0).getName());
        assertEquals("MONDAY", freshProduce.getSupplyDays().get(0).name());

        assertEquals(1, freshProduce.getAgreements().size());
        assertTrue(freshProduce.getAgreements().get(0).hasRegularDays());
    }

    @Test
    public void testProductsLinkedToAgreementsAndSuppliers() {
        Supplier supplier = supplyController.findSupplierById("8JW92KV8").get();
        assertNotNull(supplier);

        var agreement = supplier.getAgreements().get(0);
        assertTrue(agreement.getProducts().get(0).getProductNum().equals("P001"));
        assertEquals(0.9, agreement.getProducts().get(0).getPrice(), 0.01);
    }

    @Test
    public void testExpiryReportsInitiallyEmpty() {
        List<ExpiryReport> reports = expiryController.getAllReports();
        assertTrue(reports.isEmpty());
    }

}
