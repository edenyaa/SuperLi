package dataAccessLayer.dao;

import dto.DiscountItemDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcDiscountItemDAO extends BaseJdbcDAO implements DiscountItemDAO {

    @Override
    public void insert(DiscountItemDTO dto) throws SQLException {
        String sql = "INSERT INTO discount_items(discount_id, barcode) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.discountId());
            ps.setString(2, dto.barcode());
            ps.executeUpdate();
        }
    }

    @Override
    public List<DiscountItemDTO> getByDiscountId(String discountId) throws SQLException {
        List<DiscountItemDTO> list = new ArrayList<>();
        String sql = "SELECT discount_id, barcode FROM discount_items WHERE discount_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, discountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new DiscountItemDTO(
                        rs.getString("discount_id"),
                        rs.getString("barcode")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public void deleteByDiscountId(String discountId) throws SQLException {
        String sql = "DELETE FROM discount_items WHERE discount_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, discountId);
            ps.executeUpdate();
        }
    }
}
