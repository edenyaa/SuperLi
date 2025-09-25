package dataAccessLayer.dao;

import dto.ReservationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcReservationDAO extends BaseJdbcDAO implements ReservationDAO {

    @Override
    public void insert(ReservationDTO dto) throws SQLException {
        String sql = "INSERT INTO reservations (resID, sID, agreementID, reservation_date, delivery_date, total_price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.resID());
            ps.setString(2, dto.sID());
            ps.setString(3, dto.agreementID());
            ps.setString(4, dto.reservationDate());
            ps.setString(5, dto.deliveryDate());
            ps.setDouble(6, dto.totalPrice());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<ReservationDTO> get(String resID) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE resID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, resID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ReservationDTO(
                        rs.getString("resID"),
                        rs.getString("sID"),
                        rs.getString("agreementID"),
                        rs.getString("reservation_date"),
                        rs.getString("delivery_date"),
                        rs.getDouble("total_price")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ReservationDTO> getAll() throws SQLException {
        List<ReservationDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ReservationDTO(
                    rs.getString("resID"),
                    rs.getString("sID"),
                    rs.getString("agreementID"),
                    rs.getString("reservation_date"),
                    rs.getString("delivery_date"),
                    rs.getDouble("total_price")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(ReservationDTO dto) throws SQLException {
        String sql = "UPDATE reservations SET sID = ?, agreementID = ?, reservation_date = ?, delivery_date = ?, total_price = ? WHERE resID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.sID());
            ps.setString(2, dto.agreementID());
            ps.setString(3, dto.reservationDate());
            ps.setString(4, dto.deliveryDate());
            ps.setDouble(5, dto.totalPrice());
            ps.setString(6, dto.resID());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String resID) throws SQLException {
        String sql = "DELETE FROM reservations WHERE resID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, resID);
            ps.executeUpdate();
        }
    }
}
