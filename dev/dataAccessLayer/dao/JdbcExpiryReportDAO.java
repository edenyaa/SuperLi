package dataAccessLayer.dao;

import dto.ExpiryReportDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExpiryReportDAO extends BaseJdbcDAO implements ExpiryReportDAO {

    @Override
    public void insert(ExpiryReportDTO dto) throws SQLException {
        String sql = "INSERT INTO expiry_reports(id, barcode, quantity_expired, location, reported_at, reported_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.id());
            ps.setString(2, dto.barcode());
            ps.setInt(3, dto.quantityExpired());
            ps.setString(4, dto.location());
            ps.setString(5, dto.reportedAt());
            ps.setString(6, dto.reportedBy());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<ExpiryReportDTO> getById(String id) throws SQLException {
        String sql = "SELECT * FROM expiry_reports WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ExpiryReportDTO(
                        rs.getString("id"),
                        rs.getString("barcode"),
                        rs.getInt("quantity_expired"),
                        rs.getString("location"),
                        rs.getString("reported_at"),
                        rs.getString("reported_by")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ExpiryReportDTO> getAll() throws SQLException {
        List<ExpiryReportDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM expiry_reports";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ExpiryReportDTO(
                    rs.getString("id"),
                    rs.getString("barcode"),
                    rs.getInt("quantity_expired"),
                    rs.getString("location"),
                    rs.getString("reported_at"),
                    rs.getString("reported_by")
                ));
            }
        }
        return list;
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM expiry_reports WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}
