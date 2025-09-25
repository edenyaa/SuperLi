package dataAccessLayer.dao;

import dto.ProductCategoryDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductCategoryDAO extends BaseJdbcDAO implements ProductCategoryDAO {

    @Override
    public void insert(ProductCategoryDTO dto) throws SQLException {
        String sql = "INSERT INTO product_categories(barcode, category_path) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.barcode());
            ps.setString(2, dto.categoryPath());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<ProductCategoryDTO> getByBarcode(String barcode) throws SQLException {
        String sql = "SELECT * FROM product_categories WHERE barcode = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ProductCategoryDTO(
                        rs.getString("barcode"),
                        rs.getString("category_path")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public List<ProductCategoryDTO> getAllCategories() throws SQLException {
        String sql = "SELECT DISTINCT category_path FROM product_categories";
        List<ProductCategoryDTO> result = new ArrayList<>();

        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String categoryPath = rs.getString("category_path");
                // We don't have barcode here, so pass null or an empty string (as agreed in repository logic)
                result.add(new ProductCategoryDTO(null, categoryPath));
            }
        }
 
        return result;
    }

    

    @Override
    public List<ProductCategoryDTO> getAll() throws SQLException {
        String sql = "SELECT * FROM product_categories";
        List<ProductCategoryDTO> list = new ArrayList<>();
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ProductCategoryDTO(
                    rs.getString("barcode"),
                    rs.getString("category_path")
                ));
            }
        }
        return list;
    }

    @Override
    public void deleteByBarcode(String barcode) throws SQLException {
        String sql = "DELETE FROM product_categories WHERE barcode = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.executeUpdate();
        }
    }
}
