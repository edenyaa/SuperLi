package dataAccessLayer.dao;

import dto.SupplierAgreementProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSupplierAgreementProductDAO extends BaseJdbcDAO implements SupplierAgreementProductDAO {

    @Override
    public void insert(SupplierAgreementProductDTO dto) throws SQLException {
        String sql = "INSERT INTO supplier_agreement_products (agreementID, product_name, product_snum, base_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.agreementID());
            ps.setString(2, dto.productName());
            ps.setString(3, dto.productSnum());
            ps.setDouble(4, dto.basePrice());
            ps.executeUpdate();
        }
    }

    @Override
    public SupplierAgreementProductDTO get(String agreementID, String productSnum) throws SQLException {
        String sql = "SELECT * FROM supplier_agreement_products WHERE agreementID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, agreementID);
            ps.setString(2, productSnum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SupplierAgreementProductDTO(
                            rs.getString("agreementID"),
                            rs.getString("product_name"),
                            rs.getString("product_snum"),
                            rs.getDouble("base_price")
                    );
                } else {
                    throw new SQLException("No SupplierAgreementProduct found for given ID and product.");
                }
            }
        }
    }

    @Override
    public List<SupplierAgreementProductDTO> getByAgreement(String agreementID) throws SQLException {
        List<SupplierAgreementProductDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier_agreement_products WHERE agreementID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, agreementID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SupplierAgreementProductDTO(
                            rs.getString("agreementID"),
                            rs.getString("product_name"),
                            rs.getString("product_snum"),
                            rs.getDouble("base_price")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public List<SupplierAgreementProductDTO> getAll() throws SQLException {
        List<SupplierAgreementProductDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier_agreement_products";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SupplierAgreementProductDTO(
                        rs.getString("agreementID"),
                        rs.getString("product_name"),
                        rs.getString("product_snum"),
                        rs.getDouble("base_price")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(SupplierAgreementProductDTO dto) throws SQLException {
        String sql = "UPDATE supplier_agreement_products SET product_name = ?, base_price = ? WHERE agreementID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.productName());
            ps.setDouble(2, dto.basePrice());
            ps.setString(3, dto.agreementID());
            ps.setString(4, dto.productSnum());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String agreementID, String productSnum) throws SQLException {
        String sql = "DELETE FROM supplier_agreement_products WHERE agreementID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, agreementID);
            ps.setString(2, productSnum);
            ps.executeUpdate();
        }
    }

}
