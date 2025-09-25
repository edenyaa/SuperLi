package dataAccessLayer.dao;

import dto.SupplierDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcSupplierDAO extends BaseJdbcDAO implements SupplierDAO {

    @Override
    public void insert(SupplierDTO dto) throws SQLException {
        String sql = "INSERT INTO suppliers(sID, name, address) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.sID());
            ps.setString(2, dto.name());
            ps.setString(3, dto.address());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<SupplierDTO> get(String sID) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new SupplierDTO(
                            rs.getString("sID"),
                            rs.getString("name"),
                            rs.getString("address")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<SupplierDTO> getAll() throws SQLException {
        List<SupplierDTO> list = new ArrayList<>();
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM suppliers")) {
            while (rs.next()) {
                list.add(new SupplierDTO(
                        rs.getString("sID"),
                        rs.getString("name"),
                        rs.getString("address")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(SupplierDTO dto) throws SQLException {
        String sql = "UPDATE suppliers SET name = ?, address = ? WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.name());
            ps.setString(2, dto.address());
            ps.setString(3, dto.sID());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String sID) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            ps.executeUpdate();
        }
    }

}
