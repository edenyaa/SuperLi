package dataAccessLayer.dao;

import dto.ProductSupplierDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductSupplierDAO {
    void insert(ProductSupplierDTO dto) throws SQLException;
    Optional<ProductSupplierDTO> get(String barcode, String supplierName) throws SQLException;
    List<ProductSupplierDTO> getAll() throws SQLException;
    void update(ProductSupplierDTO dto) throws SQLException;
    void delete(String barcode, String supplierName) throws SQLException;
    List<ProductSupplierDTO> getByBarcode(String barcode) throws SQLException;
    void deleteAllByBarcode(String barcode) throws SQLException;
}
