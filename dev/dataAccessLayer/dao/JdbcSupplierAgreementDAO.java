package dataAccessLayer.dao;

import dto.SupplierAgreementDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcSupplierAgreementDAO extends BaseJdbcDAO implements SupplierAgreementDAO {

    @Override
    public void insert(SupplierAgreementDTO dto) throws SQLException {
        String sql = "INSERT INTO supplier_agreements (agreementID, sID, contact_name, has_regular_days) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.agreementID());
            ps.setString(2, dto.sID());
            ps.setString(3, dto.contactName());
            ps.setBoolean(4, dto.hasRegularDays());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<SupplierAgreementDTO> get(String agreementID) throws SQLException {
        String sql = "SELECT * FROM supplier_agreements WHERE agreementID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, agreementID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new SupplierAgreementDTO(
                            rs.getString("agreementID"),
                            rs.getString("sID"),
                            rs.getString("contact_name"),
                            rs.getBoolean("has_regular_days")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<SupplierAgreementDTO> getBySupplier(String sID) throws SQLException {
        List<SupplierAgreementDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier_agreements WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SupplierAgreementDTO(
                            rs.getString("agreementID"),
                            rs.getString("sID"),
                            rs.getString("contact_name"),
                            rs.getBoolean("has_regular_days")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public List<SupplierAgreementDTO> getAll() throws SQLException {
        List<SupplierAgreementDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier_agreements";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SupplierAgreementDTO(
                        rs.getString("agreementID"),
                        rs.getString("sID"),
                        rs.getString("contact_name"),
                        rs.getBoolean("has_regular_days")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(SupplierAgreementDTO dto) throws SQLException {
        String sql = "UPDATE supplier_agreements SET sID = ?, contact_name = ?, has_regular_days = ? WHERE agreementID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.sID());
            ps.setString(2, dto.contactName());
            ps.setBoolean(3, dto.hasRegularDays());
            ps.setString(4, dto.agreementID());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String agreementID) throws SQLException {
        String sql = "DELETE FROM supplier_agreements WHERE agreementID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, agreementID);
            ps.executeUpdate();
        }
    }
}
