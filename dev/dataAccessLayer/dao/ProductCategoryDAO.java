package dataAccessLayer.dao;

import dto.ProductCategoryDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductCategoryDAO {
    void insert(ProductCategoryDTO dto) throws SQLException;
    Optional<ProductCategoryDTO> getByBarcode(String barcode) throws SQLException;
    List<ProductCategoryDTO> getAll() throws SQLException;
    void deleteByBarcode(String barcode) throws SQLException;
}
