package dataAccessLayer.dao;

import dto.ReservationItemDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationItemDAO extends BaseJdbcDAO implements ReservationItemDAO {

    @Override
    public void insert(ReservationItemDTO dto) throws SQLException {
        String sql = "INSERT INTO reservation_items (resID, product_snum, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.resID());
            ps.setString(2, dto.productSnum());
            ps.setInt(3, dto.quantity());
            ps.executeUpdate();
        }
    }

    @Override
    public List<ReservationItemDTO> getByReservationId(String resID) throws SQLException {
        String sql = "SELECT * FROM reservation_items WHERE resID = ?";
        List<ReservationItemDTO> items = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, resID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new ReservationItemDTO(
                        rs.getString("resID"),
                        rs.getString("product_snum"),
                        rs.getInt("quantity")
                    ));
                }
            }
        }
        return items;
    }

    @Override
    public void update(ReservationItemDTO dto) throws SQLException {
        String sql = "UPDATE reservation_items SET quantity = ? WHERE resID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, dto.quantity());
            ps.setString(2, dto.resID());
            ps.setString(3, dto.productSnum());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String resID, String productSnum) throws SQLException {
        String sql = "DELETE FROM reservation_items WHERE resID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, resID);
            ps.setString(2, productSnum);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteByReservationId(String resID) throws SQLException {
        String sql = "DELETE FROM reservation_items WHERE resID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, resID);
            ps.executeUpdate();
        }
    }
}
