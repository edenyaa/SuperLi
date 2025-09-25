package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import Backend.DataAccessLayer.DBUtil;

public class DatabaseManager {

    static {
        System.out.println("DatabaseManager working!");
        try {
            Class.forName("org.sqlite.JDBC");
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement()){
                // Suppliers
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS suppliers (
                        sID TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        address TEXT
                    );
                """);
                //Supplier Payments
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS payment_conditions (
                        sID TEXT PRIMARY KEY,
                        payment_type TEXT CHECK (payment_type IN ('TRANSFER', 'CASH', 'CHECK')) NOT NULL,
                        bank_account TEXT,
                        FOREIGN KEY (sID) REFERENCES suppliers(sID) ON DELETE CASCADE
                    );
                """);
                // Supply Days
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS supply_days (
                        sID TEXT,
                        day TEXT,
                        PRIMARY KEY (sID, day),
                        FOREIGN KEY (sID) REFERENCES suppliers(sID) ON DELETE CASCADE
                    );
                """);
                // Contact Persons
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS contact_persons (
                        name TEXT PRIMARY KEY,
                        sID TEXT,
                        phone TEXT,
                        email TEXT,
                        FOREIGN KEY (sID) REFERENCES suppliers(sID) ON DELETE CASCADE
                    );
                """);
                // Supplier Agreements
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS supplier_agreements (
                        agreementID TEXT PRIMARY KEY,
                        sID TEXT,
                        contact_name TEXT,
                        has_regular_days INTEGER,
                        FOREIGN KEY (sID) REFERENCES suppliers(sID),
                        FOREIGN KEY (contact_name) REFERENCES contact_persons(name)
                    );
                """);
                // Product Agreements
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS product_agreements (
                        agreementID TEXT,
                        product_snum TEXT,
                        min_quantity INTEGER,
                        discount_percent REAL,
                        PRIMARY KEY (agreementID, product_snum),
                        FOREIGN KEY (agreementID, product_snum) REFERENCES supplier_agreement_products(agreementID, product_snum)
                    );
                """);
                // Supplier Products
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS supplier_agreement_products (
                        agreementID TEXT,
                        product_name TEXT,
                        product_snum TEXT,
                        base_price REAL,
                        PRIMARY KEY (agreementID, product_snum),
                        FOREIGN KEY (agreementID) REFERENCES supplier_agreements(agreementID)
                    );
                """);
                // Reservations
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS reservations (
                        resID TEXT PRIMARY KEY,
                        sID TEXT,
                        agreementID TEXT,
                        reservation_date TEXT,
                        delivery_date TEXT,
                        total_price REAL,
                        FOREIGN KEY (sID) REFERENCES suppliers(sID)
                        FOREIGN KEY (agreementID) REFERENCES supplier_agreements(agreementID)
                    );
                """);
                // Reservation items
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS reservation_items (
                        resID TEXT,
                        product_snum TEXT,
                        quantity INTEGER,
                        PRIMARY KEY (resID, product_snum),
                        FOREIGN KEY (resID) REFERENCES reservations(resID)
                    );
                """);
                // Weekly template
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS weekly_templates (
                        templateID TEXT PRIMARY KEY,
                        sID TEXT,
                        agreementID TEXT,
                        reservation_date TEXT,
                        next_reservation_date TEXT,
                        supply_day TEXT,
                        FOREIGN KEY (sID) REFERENCES suppliers(sID)
                    );
                """);
                // Weekly template items
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS weekly_template_items (
                        templateID TEXT,
                        product_snum TEXT,
                        quantity INTEGER,
                        PRIMARY KEY (templateID, product_snum),
                        FOREIGN KEY (templateID) REFERENCES weekly_templates(templateID)
                    );
                """);
                // Products
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS products (
                        barcode TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        manufacturer TEXT,
                        sell_price REAL,
                        expiry_period TEXT,
                        quantity_on_shelf INTEGER,
                        quantity_in_storage INTEGER,
                        minimum_threshold INTEGER,
                        location TEXT,
                        delivery_time INTEGER
                    );
                """);
                //Product categories
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS product_categories (
                        barcode TEXT,
                        category_path TEXT,
                        PRIMARY KEY (barcode, category_path),
                        FOREIGN KEY (barcode) REFERENCES products(barcode)
                    );
                """);
                //Product suppliers
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS product_suppliers (
                        barcode TEXT,
                        supplier_name TEXT,
                        cost_price REAL,
                        PRIMARY KEY (barcode, supplier_name),
                        FOREIGN KEY (barcode) REFERENCES products(barcode)
                    );
                """);
                //Product demands
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS product_demands (
                        barcode TEXT,
                        month INTEGER,  
                        demand INTEGER,
                        PRIMARY KEY (barcode, month),
                        FOREIGN KEY (barcode) REFERENCES products(barcode)
                    );
                """);
                //Store discounts
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS discounts (
                        discount_id TEXT PRIMARY KEY,
                        start_date TEXT NOT NULL,
                        end_date TEXT NOT NULL,
                        percentage INTEGER NOT NULL,
                        discount_set_price REAL,
                        applies_to TEXT CHECK (applies_to IN ('ITEM', 'CATEGORY')) NOT NULL
                    );
                """);
                //Item-based discounts
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS discount_items (
                        discount_id TEXT,
                        barcode TEXT,
                        PRIMARY KEY (discount_id, barcode),
                        FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE CASCADE,
                        FOREIGN KEY (barcode) REFERENCES products(barcode)
                    );
                """);
                //Category-based discounts
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS discount_categories (
                        discount_id TEXT PRIMARY KEY,
                        category_name TEXT,
                        FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE CASCADE
                    );
                """);
                // Expiry Reports
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS expiry_reports (
                        id TEXT PRIMARY KEY,
                        barcode TEXT NOT NULL,
                        quantity_expired INTEGER NOT NULL,
                        location TEXT NOT NULL,
                        reported_at TEXT NOT NULL,
                        reported_by TEXT NOT NULL,
                        FOREIGN KEY (barcode) REFERENCES products(barcode) ON DELETE CASCADE
                        );
                """);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }
}
