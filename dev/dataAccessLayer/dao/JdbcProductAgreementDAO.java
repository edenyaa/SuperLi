package dataAccessLayer.dao;

import dto.ProductAgreementDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductAgreementDAO extends BaseJdbcDAO implements ProductAgreementDAO {

    @Override
    public void insert(ProductAgreementDTO dto) throws SQLException {
        String sql = "INSERT INTO product_agreements (agreementID, product_snum, min_quantity, discount_percent) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.agreementID());
            ps.setString(2, dto.productSnum());
            ps.setInt(3, dto.minQuantity());
            ps.setDouble(4, dto.discountPercent());
            ps.executeUpdate();
        }
    }

    @Override
    public ProductAgreementDTO get(String agreementID, String productSnum) throws SQLException {
        String sql = "SELECT * FROM product_agreements WHERE agreementID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, agreementID);
            ps.setString(2, productSnum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProductAgreementDTO(
                            rs.getString("agreementID"),
                            rs.getString("product_snum"),
                            rs.getInt("min_quantity"),
                            rs.getDouble("discount_percent")
                    );
                } else {
                    throw new SQLException("No product agreement found for given ID and product.");
                }
            }
        }
    }

    @Override
    public List<ProductAgreementDTO> getByAgreement(String agreementID) throws SQLException {
        List<ProductAgreementDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM product_agreements WHERE agreementID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, agreementID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProductAgreementDTO(
                            rs.getString("agreementID"),
                            rs.getString("product_snum"),
                            rs.getInt("min_quantity"),
                            rs.getDouble("discount_percent")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public List<ProductAgreementDTO> getAll() throws SQLException {
        List<ProductAgreementDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM product_agreements";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ProductAgreementDTO(
                        rs.getString("agreementID"),
                        rs.getString("product_snum"),
                        rs.getInt("min_quantity"),
                        rs.getDouble("discount_percent")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(ProductAgreementDTO dto) throws SQLException {
        String sql = "UPDATE product_agreements SET min_quantity = ?, discount_percent = ? WHERE agreementID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, dto.minQuantity());
            ps.setDouble(2, dto.discountPercent());
            ps.setString(3, dto.agreementID());
            ps.setString(4, dto.productSnum());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String agreementID, String productSnum) throws SQLException {
        String sql = "DELETE FROM product_agreements WHERE agreementID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, agreementID);
            ps.setString(2, productSnum);
            ps.executeUpdate();
        }
    }
}
