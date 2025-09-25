package dataAccessLayer.dao;

import dto.ProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductDAO extends BaseJdbcDAO implements ProductDAO {

    @Override
    public void insert(ProductDTO dto) throws SQLException {
        String sql = "INSERT INTO products (barcode, name, manufacturer, sell_price, expiry_period, " +
                     "quantity_on_shelf, quantity_in_storage, minimum_threshold, location, delivery_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.barcode());
            ps.setString(2, dto.name());
            ps.setString(3, dto.manufacturer());
            ps.setDouble(4, dto.sellPrice());
            ps.setString(5, dto.expiryPeriod());
            ps.setInt(6, dto.quantityOnShelf());
            ps.setInt(7, dto.quantityInStorage());
            ps.setInt(8, dto.minimumThreshold());
            ps.setString(9, dto.location());
            ps.setInt(10, dto.deliveryTime());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<ProductDTO> get(String barcode) throws SQLException {
        String sql = "SELECT * FROM products WHERE barcode = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ProductDTO(
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getString("manufacturer"),
                        rs.getDouble("sell_price"),
                        rs.getString("expiry_period"),
                        rs.getInt("quantity_on_shelf"),
                        rs.getInt("quantity_in_storage"),
                        rs.getInt("minimum_threshold"),
                        rs.getString("location"),
                        rs.getInt("delivery_time")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ProductDTO> getAll() throws SQLException {
        String sql = "SELECT * FROM products";
        List<ProductDTO> list = new ArrayList<>();
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ProductDTO(
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getString("manufacturer"),
                    rs.getDouble("sell_price"),
                    rs.getString("expiry_period"),
                    rs.getInt("quantity_on_shelf"),
                    rs.getInt("quantity_in_storage"),
                    rs.getInt("minimum_threshold"),
                    rs.getString("location"),
                    rs.getInt("delivery_time")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(ProductDTO dto) throws SQLException {
        String sql = "UPDATE products SET name = ?, manufacturer = ?, sell_price = ?, expiry_period = ?, " +
                     "quantity_on_shelf = ?, quantity_in_storage = ?, minimum_threshold = ?, location = ?, " +
                     "delivery_time = ? WHERE barcode = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.name());
            ps.setString(2, dto.manufacturer());
            ps.setDouble(3, dto.sellPrice());
            ps.setString(4, dto.expiryPeriod());
            ps.setInt(5, dto.quantityOnShelf());
            ps.setInt(6, dto.quantityInStorage());
            ps.setInt(7, dto.minimumThreshold());
            ps.setString(8, dto.location());
            ps.setInt(9, dto.deliveryTime());
            ps.setString(10, dto.barcode());
            ps.executeUpdate();
        }
    }

    @Override
public void updateMinimumThreshold(String barcode, int newThreshold) throws SQLException {
    String sql = "UPDATE products SET minimum_threshold = ? WHERE barcode = ?";
    try (PreparedStatement stmt = conn().prepareStatement(sql)){
        stmt.setInt(1, newThreshold);
        stmt.setString(2, barcode);
        stmt.executeUpdate();
    }
}


    @Override
    public void delete(String barcode) throws SQLException {
        String sql = "DELETE FROM products WHERE barcode = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateQuantities(String barcode, int quantityOnShelf, int quantityInStorage) throws SQLException {
        String sql = "UPDATE products SET quantity_on_shelf = ?, quantity_in_storage = ? WHERE barcode = ?";

        try (PreparedStatement stmt = conn().prepareStatement(sql)) {
            stmt.setInt(1, quantityOnShelf);
            stmt.setInt(2, quantityInStorage);
            stmt.setString(3, barcode);
            stmt.executeUpdate();
        }
    }

}
