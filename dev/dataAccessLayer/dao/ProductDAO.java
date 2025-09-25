package dataAccessLayer.dao;

import dto.ProductDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    void insert(ProductDTO dto) throws SQLException;
    Optional<ProductDTO> get(String barcode) throws SQLException;
    List<ProductDTO> getAll() throws SQLException;
    void update(ProductDTO dto) throws SQLException;
    void delete(String barcode) throws SQLException;
    void updateMinimumThreshold(String barcode, int newThreshold) throws SQLException;
    void updateQuantities(String barcode, int quantityOnShelf, int quantityInStorage) throws SQLException;
}
