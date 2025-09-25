package dataAccessLayer.dao;

import dto.ProductDemandDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDemandDAO {
    void insert(ProductDemandDTO dto) throws SQLException;
    Optional<ProductDemandDTO> get(String barcode, int month) throws SQLException;
    List<ProductDemandDTO> getAll() throws SQLException;
    void update(ProductDemandDTO dto) throws SQLException;
    void delete(String barcode, int month) throws SQLException;
    List<ProductDemandDTO> getByBarcode(String barcode) throws SQLException;
    void deleteAllByBarcode(String barcode) throws SQLException;
    void incrementDemand(String barcode, int month, int quantity) throws SQLException;
}
