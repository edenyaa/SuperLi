package domainLayerSuppliers;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository {
    void saveSupplier(Supplier supplier) throws SQLException;
    void updateSupplier(Supplier supplier) throws SQLException;
    void deleteSupplier(String sID) throws SQLException;
    Optional<Supplier> getSupplier(String sID) throws SQLException;
    List<Supplier> getAllSuppliers() throws SQLException;
    Optional<SupplierAgreement> getSupplierAgreement (String sID, String agreementId) throws SQLException;
    void saveSupplierAgreement(SupplierAgreement agreement, String supplierId) throws SQLException;
    void deleteSupplierAgreement(SupplierAgreement agreement) throws SQLException;
    void saveContactPerson(ContactPerson person, String supplierId) throws SQLException;
    void addProductToAgreement(SupplierAgreement agreement, SupplierProduct product) throws SQLException;
    void removeProductFromAgreement(SupplierAgreement agreement, String productNum) throws SQLException;
}