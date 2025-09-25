package Backend.DataAccessLayer.Controllers;

import Backend.DTO.RoleDTO;
import Backend.DataAccessLayer.DAO.RoleDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.util.LinkedList;

public class JdbcRoleDAO implements RoleDAO {

    @Override
    public void insert(RoleDTO role) {
        String sql = "INSERT INTO Roles (id, name) VALUES (?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, role.getRoleId());
            stmt.setString(2, role.getRoleName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RoleDTO getBy(Integer id) {
        String sql = "SELECT * FROM Roles WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new RoleDTO(rs.getInt("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RoleDTO getByName(String name) {
        String sql = "SELECT * FROM Roles WHERE name = ?";
        try ( Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new RoleDTO(rs.getInt(1), rs.getString(2));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LinkedList<RoleDTO> getAll() {
        LinkedList<RoleDTO> roles = new LinkedList<>();
        String sql = "SELECT * FROM Roles";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new RoleDTO(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve roles", e);
        }
        return roles;
    }

    @Override
    public void update(RoleDTO role) {
        String sql = "UPDATE Roles SET name = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role.getRoleName());
            stmt.setInt(2, role.getRoleId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(RoleDTO role) {
        int id = role.getRoleId();
        String sql = "DELETE FROM Roles WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM Roles";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete all roles", e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM Roles";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to count roles", e);
        }
        return 0;
    }
}
