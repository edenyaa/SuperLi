package dataAccessLayer.dao;

import dto.SupplierAgreementProductDTO;
import java.sql.SQLException;
import java.util.List;

public interface SupplierAgreementProductDAO {
    void insert(SupplierAgreementProductDTO dto) throws SQLException;
    SupplierAgreementProductDTO get(String agreementID, String productSnum) throws SQLException;
    List<SupplierAgreementProductDTO> getByAgreement(String agreementID) throws SQLException;
    List<SupplierAgreementProductDTO> getAll() throws SQLException;
    void update(SupplierAgreementProductDTO dto) throws SQLException;
    void delete(String agreementID, String productSnum) throws SQLException;
}
