package dataAccessLayer.dao;

import dto.WeeklyTemplateDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWeeklyTemplateDAO extends BaseJdbcDAO implements WeeklyTemplateDAO {

    @Override
    public void insert(WeeklyTemplateDTO dto) throws SQLException {
        String sql = "INSERT INTO weekly_templates (templateID, sID, agreementID, reservation_date, next_reservation_date, supply_day) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.templateId());
            ps.setString(2, dto.sID());
            ps.setString(3, dto.agreementID());
            ps.setString(4, dto.reservationDate());
            ps.setString(5, dto.nextReservationDate());
            ps.setString(6, dto.supplyDay());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<WeeklyTemplateDTO> get(String templateId) throws SQLException {
        String sql = "SELECT * FROM weekly_templates WHERE templateID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, templateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new WeeklyTemplateDTO(
                        rs.getString("templateID"),
                        rs.getString("sID"),
                        rs.getString("agreementID"),
                        rs.getString("reservation_date"),
                        rs.getString("next_reservation_date"),
                        rs.getString("supply_day")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<WeeklyTemplateDTO> getAll() throws SQLException {
        String sql = "SELECT * FROM weekly_templates";
        List<WeeklyTemplateDTO> list = new ArrayList<>();
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new WeeklyTemplateDTO(
                    rs.getString("templateID"),
                    rs.getString("sID"),
                    rs.getString("agreementID"),
                    rs.getString("reservation_date"),
                    rs.getString("next_reservation_date"),
                    rs.getString("supply_day")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(WeeklyTemplateDTO dto) throws SQLException {
        String sql = "UPDATE weekly_templates SET sID = ?, agreementID = ?, reservation_date = ?, next_reservation_date = ?, supply_day = ? " +
                     "WHERE templateID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.sID());
            ps.setString(2, dto.agreementID());
            ps.setString(3, dto.reservationDate());
            ps.setString(4, dto.nextReservationDate());
            ps.setString(5, dto.supplyDay());
            ps.setString(6, dto.templateId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String templateId) throws SQLException {
        String sql = "DELETE FROM weekly_templates WHERE templateID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, templateId);
            ps.executeUpdate();
        }
    }
}
