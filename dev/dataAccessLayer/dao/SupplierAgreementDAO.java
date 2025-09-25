package dataAccessLayer.dao;

import dto.SupplierAgreementDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SupplierAgreementDAO {
    void insert(SupplierAgreementDTO dto) throws SQLException;
    Optional<SupplierAgreementDTO> get(String agreementID) throws SQLException;
    List<SupplierAgreementDTO> getBySupplier(String sID) throws SQLException;
    List<SupplierAgreementDTO> getAll() throws SQLException;
    void update(SupplierAgreementDTO dto) throws SQLException;
    void delete(String agreementID) throws SQLException;
}

