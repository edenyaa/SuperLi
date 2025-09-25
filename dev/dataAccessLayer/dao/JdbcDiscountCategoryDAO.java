package dataAccessLayer.dao;

import dto.DiscountCategoryDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcDiscountCategoryDAO extends BaseJdbcDAO implements DiscountCategoryDAO {

    @Override
    public void insert(DiscountCategoryDTO dto) throws SQLException {
        String sql = "INSERT INTO discount_categories(discount_id, category_name) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.discountId());
            ps.setString(2, dto.categoryName());
            ps.executeUpdate();
        }
    }

    @Override
    public List<DiscountCategoryDTO> getByDiscountId(String discountId) throws SQLException {
        List<DiscountCategoryDTO> list = new ArrayList<>();
        String sql = "SELECT discount_id, category_name FROM discount_categories WHERE discount_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, discountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new DiscountCategoryDTO(
                        rs.getString("discount_id"),
                        rs.getString("category_name")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public void deleteByDiscountId(String discountId) throws SQLException {
        String sql = "DELETE FROM discount_categories WHERE discount_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, discountId);
            ps.executeUpdate();
        }
    }
}
