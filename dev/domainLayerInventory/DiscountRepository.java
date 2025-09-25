package domainLayerInventory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository {
    void saveDiscount(Discount discount) throws SQLException;
    void updateDiscount(Discount discount) throws SQLException;
    void deleteDiscount(String discountId) throws SQLException;
    Optional<Discount> getDiscount(String discountId) throws SQLException;
    List<Discount> getDiscountsByProduct(String barcode) throws SQLException;
    List<Discount> getAllDiscounts() throws SQLException;
}
