package util;

import java.sql.SQLException;

import dataAccessLayer.dao.*;
import dataAccessLayer.repository.*;
import domainLayerInventory.DiscountRepository;
import domainLayerInventory.ExpiryReportRepository;
import domainLayerInventory.ProductRepository;
import domainLayerSuppliers.ReservationRepository;
import domainLayerSuppliers.SupplierRepository;
import domainLayerSuppliers.WeeklyTemplateRepository;

public class AppConfig {

    // --- DAOs ---
    private static final ProductDAO productDAO = new JdbcProductDAO();
    private static final ProductCategoryDAO productCategoryDAO = new JdbcProductCategoryDAO();
    private static final ProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
    private static final ProductDemandDAO productDemandDAO = new JdbcProductDemandDAO();

    private static final DiscountDAO discountDAO = new JdbcDiscountDAO();
    private static final DiscountItemDAO discountItemDAO = new JdbcDiscountItemDAO();
    private static final DiscountCategoryDAO discountCategoryDAO = new JdbcDiscountCategoryDAO();

    private static final SupplierDAO supplierDAO = new JdbcSupplierDAO();
    private static final SupplyDayDAO supplyDayDAO = new JdbcSupplyDayDAO();
    private static final PaymentConditionDAO paymentConditionDAO = new JdbcPaymentConditionDAO();
    private static final ContactPersonDAO contactPersonDAO = new JdbcContactPersonDAO();
    private static final SupplierAgreementDAO supplierAgreementDAO = new JdbcSupplierAgreementDAO();
    private static final SupplierAgreementProductDAO agreementProductDAO = new JdbcSupplierAgreementProductDAO();
    private static final ProductAgreementDAO productAgreementDAO = new JdbcProductAgreementDAO();

    private static final ReservationDAO reservationDAO = new JdbcReservationDAO();
    private static final ReservationItemDAO reservationItemDAO = new JdbcReservationItemDAO();

    private static final WeeklyTemplateDAO weeklyTemplateDAO = new JdbcWeeklyTemplateDAO();
    private static final WeeklyTemplateItemDAO weeklyTemplateItemDAO = new JdbcWeeklyTemplateItemDAO();

    private static final ExpiryReportDAO expiryReportDAO = new JdbcExpiryReportDAO();

    // --- Repositories ---
    public static final ProductRepository productRepository;
    public static final DiscountRepository discountRepository;
    public static final SupplierRepository supplierRepository;
    public static final ReservationRepository reservationRepository;
    public static final WeeklyTemplateRepository weeklyTemplateRepository;
    public static final ExpiryReportRepository expiryReportRepository;

    static {
        try {
            discountRepository = new DiscountRepositoryImpl(
                discountDAO, discountItemDAO, discountCategoryDAO);

            productRepository = new ProductRepositoryImpl(
                productDAO, productCategoryDAO, productSupplierDAO, productDemandDAO,discountRepository);

            supplierRepository = new SupplierRepositoryImpl(
                supplierDAO, supplyDayDAO, paymentConditionDAO, contactPersonDAO,
                supplierAgreementDAO, agreementProductDAO, productAgreementDAO);

            reservationRepository = new ReservationRepositoryImpl(reservationDAO, reservationItemDAO);

            weeklyTemplateRepository = new WeeklyTemplateRepositoryImpl(
                weeklyTemplateDAO, weeklyTemplateItemDAO, supplierRepository);

            expiryReportRepository = new ExpiryReportRepositoryImpl(expiryReportDAO);

        } catch (SQLException e) {
            throw new ExceptionInInitializerError("Failed to initialize repositories: " + e);
        }
    }

    // Prevent instantiation
    private AppConfig() {}
}
