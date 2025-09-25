package dataAccessLayer.dao;

import dto.ProductDemandDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductDemandDAO extends BaseJdbcDAO implements ProductDemandDAO {

    @Override
    public void insert(ProductDemandDTO dto) throws SQLException {
        String sql = "INSERT INTO product_demands(barcode, month, demand) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.barcode());
            ps.setInt(2, dto.month());
            ps.setInt(3, dto.demand());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<ProductDemandDTO> get(String barcode, int month) throws SQLException {
        String sql = "SELECT * FROM product_demands WHERE barcode = ? AND month = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ProductDemandDTO(
                        rs.getString("barcode"),
                        rs.getInt("month"),
                        rs.getInt("demand")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ProductDemandDTO> getAll() throws SQLException {
        List<ProductDemandDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM product_demands";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ProductDemandDTO(
                    rs.getString("barcode"),
                    rs.getInt("month"),
                    rs.getInt("demand")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(ProductDemandDTO dto) throws SQLException {
        String sql = "UPDATE product_demands SET demand = ? WHERE barcode = ? AND month = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, dto.demand());
            ps.setString(2, dto.barcode());
            ps.setInt(3, dto.month());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String barcode, int month) throws SQLException {
        String sql = "DELETE FROM product_demands WHERE barcode = ? AND month = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.setInt(2, month);
            ps.executeUpdate();
        }
    }

    @Override
public List<ProductDemandDTO> getByBarcode(String barcode) throws SQLException {
    String sql = "SELECT * FROM Product_demands WHERE barcode = ?";
    List<ProductDemandDTO> result = new ArrayList<>();
    try (PreparedStatement stmt = conn().prepareStatement(sql)) {
        stmt.setString(1, barcode);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(new ProductDemandDTO(
                        rs.getString("barcode"),
                        rs.getInt("month"),
                        rs.getInt("demand")
                ));
            }
        }
    }
    return result;
}

@Override
public void deleteAllByBarcode(String barcode) throws SQLException {
    String sql = "DELETE FROM Product_demands WHERE barcode = ?";
    try (PreparedStatement stmt = conn().prepareStatement(sql)) {
        stmt.setString(1, barcode);
        stmt.executeUpdate();
    }
}

@Override
public void incrementDemand(String barcode, int month, int quantity) throws SQLException {
    String selectSQL = "SELECT demand FROM product_demands WHERE barcode = ? AND month = ?";
    String insertSQL = "INSERT INTO product_demands (barcode, month, demand) VALUES (?, ?, ?)";
    String updateSQL = "UPDATE product_demands SET demand = ? WHERE barcode = ? AND month = ?";

    try (PreparedStatement selectStmt = conn().prepareStatement(selectSQL)) {
        selectStmt.setString(1, barcode);
        selectStmt.setInt(2, month);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            int current = rs.getInt("demand");
            try (PreparedStatement updateStmt = conn().prepareStatement(updateSQL)) {
                updateStmt.setInt(1, current + quantity);
                updateStmt.setString(2, barcode);
                updateStmt.setInt(3, month);
                updateStmt.executeUpdate();
            }
        } else {
            try (PreparedStatement insertStmt = conn().prepareStatement(insertSQL)) {
                insertStmt.setString(1, barcode);
                insertStmt.setInt(2, month);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
            }
        }
    }
}


}
