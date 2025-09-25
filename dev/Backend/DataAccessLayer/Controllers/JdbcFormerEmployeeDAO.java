package Backend.DataAccessLayer.Controllers;

import Backend.DTO.FormerEmployeeDTO;
import Backend.DataAccessLayer.DAO.FormerEmployeeDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class JdbcFormerEmployeeDAO implements FormerEmployeeDAO {

    @Override
    public void insert(FormerEmployeeDTO employee) {
        String sql = "INSERT INTO FormerEmployees (id, fullName, startDate, endDate, reason, locationID) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, employee.getFullName());
            stmt.setString(3, employee.getStartDate().toString());
            stmt.setString(4, employee.getEndDate().toString());
            stmt.setString(5, employee.getReason());
            stmt.setInt(6, employee.getLocation());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void update(FormerEmployeeDTO item) {
        String sql = "UPDATE FormerEmployees SET fullName = ?, startDate = ?, endDate = ?, reason = ?, locationID = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, item.getId());
            stmt.setString(2, item.getFullName());
            stmt.setString(3, item.getStartDate().toString());
            stmt.setString(4, item.getEndDate().toString());
            stmt.setString(5, item.getReason());
            stmt.setInt(6, item.getLocation());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update former employee with id: " + item.getId(), e);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM FormerEmployees";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete all former employees", e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM FormerEmployees";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to count former employees", e);
        }
        return 0;
    }

    @Override
    public FormerEmployeeDTO getBy(String id) {
        String sql = "SELECT * FROM FormerEmployees WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFormerEmployee(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LinkedList<FormerEmployeeDTO> getAll() {
        LinkedList<FormerEmployeeDTO> employees = new LinkedList<>();
        String sql = "SELECT * FROM FormerEmployees";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(mapRowToFormerEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve former employees", e);
        }
        return employees;
    }

    @Override
    public void delete(FormerEmployeeDTO formerEmployeeDTO) {
        String id = formerEmployeeDTO.getId();
        String sql = "DELETE FROM FormerEmployees WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete former employee with id: " + id, e);
        }
    }

    private FormerEmployeeDTO mapRowToFormerEmployee(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("fullName");
        String startDateSTR = rs.getString("startDate");
        LocalDate startDate = LocalDate.parse(startDateSTR);
        String endDateSTR = rs.getString("endDate");
        LocalDate endDate = LocalDate.parse(endDateSTR);
        String reason = rs.getString("reason");
        int locationId = rs.getInt("locationID");

        return new FormerEmployeeDTO(id, name, startDate, endDate, reason, locationId);
    }
}
