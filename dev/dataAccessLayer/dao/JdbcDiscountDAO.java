package dataAccessLayer.dao;

import dto.DiscountDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcDiscountDAO extends BaseJdbcDAO implements DiscountDAO {

    @Override
    public void insert(DiscountDTO dto) throws SQLException {
        String sql = "INSERT INTO discounts(discount_id, start_date, end_date, percentage, discount_set_price, applies_to) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.discountId());
            ps.setString(2, dto.startDate());
            ps.setString(3, dto.endDate());
            ps.setInt(4, dto.percentage());
            if (dto.discountSetPrice() == null) {
                ps.setNull(5, Types.DOUBLE);
            } else {
                ps.setDouble(5, dto.discountSetPrice());
            }
            ps.setString(6, dto.appliesTo());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<DiscountDTO> get(String discountId) throws SQLException {
        String sql = "SELECT * FROM discounts WHERE discount_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, discountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new DiscountDTO(
                        rs.getString("discount_id"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getInt("percentage"),
                        rs.getObject("discount_set_price") != null ? rs.getDouble("discount_set_price") : null,
                        rs.getString("applies_to")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<DiscountDTO> getAll() throws SQLException {
        List<DiscountDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM discounts";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DiscountDTO(
                    rs.getString("discount_id"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getInt("percentage"),
                    rs.getObject("discount_set_price") != null ? rs.getDouble("discount_set_price") : null,
                    rs.getString("applies_to")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(DiscountDTO dto) throws SQLException {
        String sql = "UPDATE discounts SET start_date = ?, end_date = ?, percentage = ?, discount_set_price = ?, applies_to = ? WHERE discount_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.startDate());
            ps.setString(2, dto.endDate());
            ps.setInt(3, dto.percentage());
            if (dto.discountSetPrice() == null) {
                ps.setNull(4, Types.DOUBLE);
            } else {
                ps.setDouble(4, dto.discountSetPrice());
            }
            ps.setString(5, dto.appliesTo());
            ps.setString(6, dto.discountId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String discountId) throws SQLException {
        String sql = "DELETE FROM discounts WHERE discount_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, discountId);
            ps.executeUpdate();
        }
    }
}
