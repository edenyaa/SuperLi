package dataAccessLayer.dao;

import dto.ContactPersonDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcContactPersonDAO extends BaseJdbcDAO implements ContactPersonDAO {

    @Override
    public void insert(ContactPersonDTO dto) throws SQLException {
        String sql = "INSERT INTO contact_persons(sID, name, phone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.sID());
            ps.setString(2, dto.name());
            ps.setString(3, dto.phoneNumber());
            ps.setString(4, dto.email());
            ps.executeUpdate();
        }
    }

    @Override
    public List<ContactPersonDTO> getBySupplier(String sID) throws SQLException {
        List<ContactPersonDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM contact_persons WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ContactPersonDTO(
                            rs.getString("sID"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public List<ContactPersonDTO> getAll() throws SQLException {
        List<ContactPersonDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM contact_persons";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ContactPersonDTO(
                        rs.getString("sID"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }
        }
        return list;
    }

    @Override
    public void delete(String sID, String name) throws SQLException {
        String sql = "DELETE FROM contact_persons WHERE sID = ? AND name = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            ps.setString(2, name);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteAllForSupplier(String sID) throws SQLException {
        String sql = "DELETE FROM contact_persons WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            ps.executeUpdate();
        }
    }
}
