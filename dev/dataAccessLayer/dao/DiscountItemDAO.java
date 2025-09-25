package dataAccessLayer.dao;

import dto.DiscountItemDTO;
import java.sql.SQLException;
import java.util.List;

public interface DiscountItemDAO {
    void insert(DiscountItemDTO dto) throws SQLException;
    List<DiscountItemDTO> getByDiscountId(String discountId) throws SQLException;
    void deleteByDiscountId(String discountId) throws SQLException;
}
