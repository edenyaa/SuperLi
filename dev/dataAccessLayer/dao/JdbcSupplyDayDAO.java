package dataAccessLayer.dao;

import dto.SupplyDayDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSupplyDayDAO extends BaseJdbcDAO implements SupplyDayDAO {

    @Override
    public void insert(SupplyDayDTO dto) throws SQLException {
        String sql = "INSERT INTO supply_days (sID, day) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.sID());
            ps.setString(2, dto.day());
            ps.executeUpdate();
        }
    }

    @Override
    public List<SupplyDayDTO> getBySupplier(String sID) throws SQLException {
        List<SupplyDayDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supply_days WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SupplyDayDTO(
                            rs.getString("sID"),
                            rs.getString("day")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public List<SupplyDayDTO> getAll() throws SQLException {
        List<SupplyDayDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supply_days";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SupplyDayDTO(
                        rs.getString("sID"),
                        rs.getString("day")
                ));
            }
        }
        return list;
    }

    @Override
    public void delete(String sID, String day) throws SQLException {
        String sql = "DELETE FROM supply_days WHERE sID = ? AND day = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            ps.setString(2, day);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteAllForSupplier(String sID) throws SQLException {
        String sql = "DELETE FROM supply_days WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            ps.executeUpdate();
        }
    }
}
