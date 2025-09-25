package util;

import java.sql.Statement;
import Backend.DataAccessLayer.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DatabaseSeeder {

    public static void seed() {

        try (Connection conn = DBUtil.getConnection()) {
            
            // === Supplier 1 ===
            String supplier1ID = "8JW92KV8";
            String supplier1Name = "Fresh Produce Ltd";
            insertSupplier(conn, supplier1ID, supplier1Name, "123 Farm Lane", "TRANSFER", "DE12345678901234567890", "Alice Supplier", "555-1234", "alice@freshproduce.com", "Monday");

            String agreement1ID = "KNC932K1";
            insertAgreement(conn, agreement1ID, supplier1ID, "Alice Supplier", true);

            // === Supplier 2 ===
            String supplier2ID = "MDO029C4";
            String supplier2Name = "Green Valley Foods";
            insertSupplier(conn, supplier2ID, supplier2Name, "456 Market Road", "CASH", "FS12345668901233567319", "Bob Greens", "555-5678", "bob@greenvalley.com", "Sunday");

            String agreement2ID = "038PLM12";
            insertAgreement(conn, agreement2ID, supplier2ID, "Bob Greens", true);

            // === Supplier 3 ===
            String supplier3ID = "T20MNF78";
            String supplier3Name = "Super Sweet Foods";
            insertSupplier(conn, supplier3ID, supplier3Name, "56 Archer Valley", "CHECK", "NY12345675901244562894", "Rachel Adams", "555-3779", "rachel@supersweet.com", "Wednesday");

            String agreement3ID = "192TRUW8";
            insertAgreement(conn, agreement3ID, supplier3ID, "Rachel Adams", false);

            // === Supplier 4 ===
            String supplier4ID = "KSU937RX";
            String supplier4Name = "Milky Sunshine LTD";
            insertSupplier(conn, supplier4ID, supplier4Name, "898 Rainbow Boulevard", "TRANSFER", "RN12335677901234577870", "Amanda Hopkins", "555-2073", "amanda@milkysunshine.com", "Wednesday");

            String agreement4ID = "92KVEU45";
            insertAgreement(conn, agreement4ID, supplier4ID, "Amanda Hopkins", true);

            // === Products ===
            String tomatoBarcode = "P001";
            insertProduct(conn, tomatoBarcode, "Tomato", "FreshGrow", 1.5, "7 days", 30, 100, 20, "Aisle 3", 2);
            insertCategory(conn, tomatoBarcode, "vegetable->fresh");
            insertAgreementProduct(conn, agreement1ID, "Tomato", tomatoBarcode, 0.9);
            insertProductAgreement(conn, agreement1ID, tomatoBarcode, 50, 0.10);
            insertProductSupplier(conn, tomatoBarcode, supplier1Name, 0.9);
            insertAgreementProduct(conn, agreement2ID, "Tomato", tomatoBarcode, 1.1);
            insertProductAgreement(conn, agreement2ID, tomatoBarcode, 30, 0.05);
            insertProductSupplier(conn, tomatoBarcode, supplier2Name, 1.1);

            String cucumberBarcode = "P002";
            insertProduct(conn, cucumberBarcode, "Cucumber", "GreenHouse", 2.0, "5 days", 20, 50, 10, "Aisle 3", 3);
            insertCategory(conn, cucumberBarcode, "vegetable->fresh");
            insertAgreementProduct(conn, agreement1ID, "Cucumber", cucumberBarcode, 1.4);
            insertProductAgreement(conn, agreement1ID, cucumberBarcode, 40, 0.08);
            insertProductSupplier(conn, cucumberBarcode, supplier1Name, 1.4);

            String chocolaterBarcode = "P003";
            insertProduct(conn, chocolaterBarcode, "Milky Chocolate", "Swiss Sweets", 10.0, "30 days", 20, 50, 10, "Aisle 12", 1);
            insertCategory(conn, chocolaterBarcode, "sweets->chocolate");
            insertAgreementProduct(conn, agreement3ID, "Milky Chocolate", chocolaterBarcode, 7.0);
            insertProductAgreement(conn, agreement3ID, chocolaterBarcode, 100, 0.15);
            insertProductSupplier(conn, chocolaterBarcode, supplier3Name, 7.0);
            insertAgreementProduct(conn, agreement4ID, "Milky Chocolate", chocolaterBarcode, 5.0);
            insertProductAgreement(conn, agreement4ID, chocolaterBarcode, 10, 0.05);
            insertProductSupplier(conn, chocolaterBarcode, supplier4Name, 5.0);

            String marshmellowBarcode = "P004";
            insertProduct(conn, marshmellowBarcode, "Pink Marshmellow", "New York Sweets", 8.5, "21 days", 20, 50, 10, "Aisle 12", 2);
            insertCategory(conn, marshmellowBarcode, "sweets->other");
            insertAgreementProduct(conn, agreement4ID, "Pink Marshmellow", marshmellowBarcode, 6.5);
            insertProductAgreement(conn, agreement4ID, marshmellowBarcode, 40, 0.15);
            insertProductSupplier(conn, marshmellowBarcode, supplier4Name, 6.5);

            String gummyBearsBarcode = "P005";
            insertProduct(conn, gummyBearsBarcode, "Colorful Gummy Bears", "New York Sweets", 5.5, "21 days", 20, 50, 10, "Aisle 12", 2);
            insertCategory(conn, gummyBearsBarcode, "sweets->other");
            insertAgreementProduct(conn, agreement4ID, "Colorful Gummy Bears", gummyBearsBarcode, 4.5);
            insertProductAgreement(conn, agreement4ID, gummyBearsBarcode, 40, 0.20);
            insertProductSupplier(conn, gummyBearsBarcode, supplier4Name, 4.5);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertSupplier(Connection conn, String sID, String name, String address, String paymentType, String bankAccount, String contactName, String phone, String email, String supplyDay) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO suppliers (sID, name, address) VALUES (?, ?, ?)")) {
            stmt.setString(1, sID);
            stmt.setString(2, name);
            stmt.setString(3, address);
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO payment_conditions (sID, payment_type, bank_account) VALUES (?, ?, ?)")) {
            stmt.setString(1, sID);
            stmt.setString(2, paymentType);
            stmt.setString(3, bankAccount);
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO contact_persons (name, sID, phone, email) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, contactName);
            stmt.setString(2, sID);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO supply_days (sID, day) VALUES (?, ?)")) {
            stmt.setString(1, sID);
            stmt.setString(2, supplyDay);
            stmt.executeUpdate();
        }
    }

    private static void insertAgreement(Connection conn, String agreementID, String sID, String contactName, boolean hasRegularDays) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO supplier_agreements (agreementID, sID, contact_name, has_regular_days) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, agreementID);
            stmt.setString(2, sID);
            stmt.setString(3, contactName);
            stmt.setInt(4, hasRegularDays ? 1 : 0);
            stmt.executeUpdate();
        }
    }

    private static void insertProduct(Connection conn, String barcode, String name, String manufacturer, double sellPrice, String expiryPeriod, int shelfQty, int storageQty, int threshold, String location, int deliveryTime) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO products (barcode, name, manufacturer, sell_price, expiry_period, quantity_on_shelf, quantity_in_storage, minimum_threshold, location, delivery_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, barcode);
            stmt.setString(2, name);
            stmt.setString(3, manufacturer);
            stmt.setDouble(4, sellPrice);
            stmt.setString(5, expiryPeriod);
            stmt.setInt(6, shelfQty);
            stmt.setInt(7, storageQty);
            stmt.setInt(8, threshold);
            stmt.setString(9, location);
            stmt.setInt(10, deliveryTime);
            stmt.executeUpdate();
        }
    }

    private static void insertAgreementProduct(Connection conn, String agreementID, String productName, String productSNum, double basePrice) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO supplier_agreement_products (agreementID, product_name, product_snum, base_price) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, agreementID);
            stmt.setString(2, productName);
            stmt.setString(3, productSNum);
            stmt.setDouble(4, basePrice);
            stmt.executeUpdate();
        }
    }

    private static void insertProductAgreement(Connection conn, String agreementID, String productSNum, int minQty, double discountPercent) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO product_agreements (agreementID, product_snum, min_quantity, discount_percent) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, agreementID);
            stmt.setString(2, productSNum);
            stmt.setInt(3, minQty);
            stmt.setDouble(4, discountPercent);
            stmt.executeUpdate();
        }
    }

    private static void insertProductSupplier(Connection conn, String barcode, String supplierName, double costPrice) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO product_suppliers (barcode, supplier_name, cost_price) VALUES (?, ?, ?)")) {
            stmt.setString(1, barcode);
            stmt.setString(2, supplierName);
            stmt.setDouble(3, costPrice);
            stmt.executeUpdate();
        }
    }

    private static void insertCategory(Connection conn, String barcode, String categoryPath) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT OR IGNORE INTO product_categories (barcode, category_path) VALUES (?, ?)")) {
            stmt.setString(1, barcode);
            stmt.setString(2, categoryPath);
            stmt.executeUpdate();
        }
    }

    public static void resetDB(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("PRAGMA foreign_keys = OFF"); // Temporarily disable foreign key checks

            stmt.executeUpdate("DELETE FROM supply_days");
            stmt.executeUpdate("DELETE FROM contact_persons");
            stmt.executeUpdate("DELETE FROM payment_conditions");
            stmt.executeUpdate("DELETE FROM supplier_agreement_products");
            stmt.executeUpdate("DELETE FROM product_agreements");
            stmt.executeUpdate("DELETE FROM product_suppliers");
            stmt.executeUpdate("DELETE FROM supplier_agreements");
            stmt.executeUpdate("DELETE FROM product_categories");
            stmt.executeUpdate("DELETE FROM products");
            stmt.executeUpdate("DELETE FROM suppliers");
            stmt.executeUpdate("DELETE FROM reservations");
            stmt.executeUpdate("DELETE FROM product_demands");
            stmt.executeUpdate("DELETE FROM discounts");
            stmt.executeUpdate("DELETE FROM discount_items");
            stmt.executeUpdate("DELETE FROM discount_categories");
            stmt.executeUpdate("DELETE FROM expiry_reports");
            stmt.executeUpdate("DELETE FROM weekly_templates");
            stmt.executeUpdate("DELETE FROM weekly_template_items");
            stmt.executeUpdate("DELETE FROM reservation_items");

            stmt.executeUpdate("PRAGMA foreign_keys = ON"); // Re-enable foreign key checks
        }
    }
} 