package dataAccessLayer.dao;

import dto.DiscountCategoryDTO;
import java.sql.SQLException;
import java.util.List;

public interface DiscountCategoryDAO {
    void insert(DiscountCategoryDTO dto) throws SQLException;
    List<DiscountCategoryDTO> getByDiscountId(String discountId) throws SQLException;
    void deleteByDiscountId(String discountId) throws SQLException;
}
