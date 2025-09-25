package domainLayerInventory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> getByBarcode(String barcode) throws SQLException;
    Optional<Product> getByName(String name) throws SQLException;
    List<Product> getAll() throws SQLException;
    void save(Product product) throws SQLException;
    void update(Product product) throws SQLException;
    void delete(String barcode) throws SQLException;
    List<Category> getAllCategories() throws SQLException;
    void updateMinThreshold(String barcode, int newThreshold) throws SQLException;
    void updateProductQuantities(String barcode, int quantityOnShelf, int quantityInStorage) throws SQLException;
    void addDemand(String barcode, int month, int quantity) throws SQLException;
    void updateDemand(String barcode,int month, int quantity) throws SQLException;
    Category getCategoryByNameAndParent(String name, Category parent) throws SQLException;
    void saveCategory(Category category);
    Category getCategoryByName(String name);
}
