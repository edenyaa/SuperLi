package dataAccessLayer.dao;

import dto.DiscountDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DiscountDAO {
    void insert(DiscountDTO dto) throws SQLException;
    Optional<DiscountDTO> get(String discountId) throws SQLException;
    List<DiscountDTO> getAll() throws SQLException;
    void update(DiscountDTO dto) throws SQLException;
    void delete(String discountId) throws SQLException;
}
