package dataAccessLayer.dao;

import dto.WeeklyTemplateItemDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWeeklyTemplateItemDAO extends BaseJdbcDAO implements WeeklyTemplateItemDAO {

    @Override
    public void insert(WeeklyTemplateItemDTO dto) throws SQLException {
        String sql = "INSERT INTO weekly_template_items (templateID, product_snum, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.templateId());
            ps.setString(2, dto.productSnum());
            ps.setInt(3, dto.quantity());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<WeeklyTemplateItemDTO> get(String templateId, String productSnum) throws SQLException {
        String sql = "SELECT * FROM weekly_template_items WHERE templateID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, templateId);
            ps.setString(2, productSnum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new WeeklyTemplateItemDTO(
                        rs.getString("templateID"),
                        rs.getString("product_snum"),
                        rs.getInt("quantity")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<WeeklyTemplateItemDTO> getAll() throws SQLException {
        String sql = "SELECT * FROM weekly_template_items";
        List<WeeklyTemplateItemDTO> list = new ArrayList<>();
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new WeeklyTemplateItemDTO(
                    rs.getString("templateID"),
                    rs.getString("product_snum"),
                    rs.getInt("quantity")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(WeeklyTemplateItemDTO dto) throws SQLException {
        String sql = "UPDATE weekly_template_items SET quantity = ? WHERE templateID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, dto.quantity());
            ps.setString(2, dto.templateId());
            ps.setString(3, dto.productSnum());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String templateId, String productSnum) throws SQLException {
        String sql = "DELETE FROM weekly_template_items WHERE templateID = ? AND product_snum = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, templateId);
            ps.setString(2, productSnum);
            ps.executeUpdate();
        }
    }
}
