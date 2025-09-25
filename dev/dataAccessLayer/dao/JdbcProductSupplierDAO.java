package dataAccessLayer.dao;

import dto.ProductSupplierDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductSupplierDAO extends BaseJdbcDAO implements ProductSupplierDAO {

    @Override
    public void insert(ProductSupplierDTO dto) throws SQLException {
        String sql = "INSERT INTO product_suppliers(barcode, supplier_name, cost_price) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.barcode());
            ps.setString(2, dto.supplierName());
            ps.setDouble(3, dto.costPrice());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<ProductSupplierDTO> get(String barcode, String supplierName) throws SQLException {
        String sql = "SELECT * FROM product_suppliers WHERE barcode = ? AND supplier_name = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.setString(2, supplierName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ProductSupplierDTO(
                        rs.getString("barcode"),
                        rs.getString("supplier_name"),
                        rs.getDouble("cost_price")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ProductSupplierDTO> getAll() throws SQLException {
        List<ProductSupplierDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM product_suppliers";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ProductSupplierDTO(
                    rs.getString("barcode"),
                    rs.getString("supplier_name"),
                    rs.getDouble("cost_price")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(ProductSupplierDTO dto) throws SQLException {
        String sql = "UPDATE product_suppliers SET cost_price = ? WHERE barcode = ? AND supplier_name = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, dto.costPrice());
            ps.setString(2, dto.barcode());
            ps.setString(3, dto.supplierName());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String barcode, String supplierName) throws SQLException {
        String sql = "DELETE FROM product_suppliers WHERE barcode = ? AND supplier_name = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.setString(2, supplierName);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ProductSupplierDTO> getByBarcode(String barcode) throws SQLException {
        String sql = "SELECT * FROM product_suppliers WHERE barcode = ?";
        List<ProductSupplierDTO> result = new ArrayList<>();
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, barcode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new ProductSupplierDTO(
                            rs.getString("barcode"),
                            rs.getString("supplier_name"),
                            rs.getDouble("cost_price")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public void deleteAllByBarcode(String barcode) throws SQLException {
        String sql = "DELETE FROM product_suppliers WHERE barcode = ?";
        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setString(1, barcode);
            stmt.executeUpdate();
        }
    }
}
