package dataAccessLayer.dao;

import dto.ProductAgreementDTO;

import java.sql.SQLException;
import java.util.List;

public interface ProductAgreementDAO {
    void insert(ProductAgreementDTO dto) throws SQLException;
    ProductAgreementDTO get(String agreementID, String productSnum) throws SQLException;
    List<ProductAgreementDTO> getByAgreement(String agreementID) throws SQLException;
    List<ProductAgreementDTO> getAll() throws SQLException;
    void update(ProductAgreementDTO dto) throws SQLException;
    void delete(String agreementID, String productSnum) throws SQLException;
}
