package Backend.DataAccessLayer.Controllers;

import Backend.DTO.*;
import Backend.DTO.LocationDTO;
import Backend.DTO.RoleDTO;
import Backend.DataAccessLayer.DAO.EmployeeDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JdbcEmployeeDAO implements EmployeeDAO {

    @Override
    public void insert(EmployeeDTO employee) {
        if (existsById(employee.getId())) {
            return;
        }
        String sql = "INSERT INTO Employees (id, password, fullName, startDate, salary, bankAccount, monthlyHours, usedHours, locationID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, employee.getPassword());
            stmt.setString(3, employee.getFullName());
            stmt.setString(4, employee.getStartDate().toString());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getBankAccount());
            stmt.setInt(7, employee.getMonthlyHours());
            stmt.setInt(8, employee.getUsedHours());
            stmt.setInt(9, employee.getLocation().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        insertRoles(employee);
        insertLicenses(employee);
    }


    private void insertRoles(EmployeeDTO employee) {
        String sql = "INSERT INTO Employees_Positions (employeeID, roleID) VALUES (?, ?)";
        for (RoleDTO role : employee.getPositions()) {
            try (   Connection connection = DBUtil.getConnection();
                    PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, employee.getId());
                stmt.setInt(2, role.getRoleId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertLicenses(EmployeeDTO employee) {
        String sql = "INSERT INTO Employees_License (employeeID, licenseType) VALUES (?, ?)";
        for (String license : employee.getLicenseTypes()) {
            try (   Connection connection = DBUtil.getConnection();
                    PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, employee.getId());
                stmt.setString(2, license);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public EmployeeDTO getBy(String id) {
        String sql = "SELECT * FROM Employees WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LinkedList<EmployeeDTO> getAll() {
        LinkedList<EmployeeDTO> employees = new LinkedList<>();
        String sql = "SELECT * FROM Employees";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(mapRowToEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public void update(EmployeeDTO employee) {
        String sql = "UPDATE Employees SET password = ?, fullName = ?, startDate = ?, salary = ?, bankAccount = ?, monthlyHours = ?, usedHours = ?, locationID = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employee.getPassword());
            stmt.setString(2, employee.getFullName());
            stmt.setString(3, employee.getStartDate().toString());
            stmt.setDouble(4, employee.getSalary());
            stmt.setString(5, employee.getBankAccount());
            stmt.setInt(6, employee.getMonthlyHours());
            stmt.setInt(7, employee.getUsedHours());
            stmt.setInt(8, employee.getLocation().getId());
            stmt.setString(9, employee.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // delete and re-insert roles and licenses
        deleteRoles(employee.getId());
        deleteLicenses(employee.getId());
        insertRoles(employee);
        insertLicenses(employee);
    }
    @Override
    public void updatePassword(String id, String password) {
        String sql = "UPDATE Employees SET password = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void updateFullName(String id, String fullName) {
        String sql = "UPDATE Employees SET fullName = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void updateSalary(String id, double salary) {
        String sql = "UPDATE Employees SET salary = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, salary);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update salary", e);
        }
    }
    @Override
    public void updateMonthlyHours(String id, int hours) {
        String sql = "UPDATE Employees SET monthlyHours = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, hours);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update monthly hours", e);
        }
    }
    @Override
    public void updateLicenseTypes(String id, List<String> licenseTypes) {
        try {
            String deleteSql = "DELETE FROM Employees_License WHERE employeeID = ?";
            try (   Connection connection = DBUtil.getConnection();
                    PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
            }
            String insertSql = "INSERT INTO Employees_License (employeeID, licenseType) VALUES (?, ?)";
            try (   Connection connection = DBUtil.getConnection();
                    PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                for (String license : licenseTypes) {
                    pstmt.setString(1, id);
                    pstmt.setString(2, license);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update license types", e);
        }
    }
    @Override
    public void updateBankAccount(String id, String bankAccount) {
        String sql = "UPDATE Employees SET bankAccount = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bankAccount);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update bank account", e);
        }
    }
    @Override
    public void updateLocation(String id, LocationDTO location) {
        String sql = "UPDATE Employees SET locationID = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, location.getId());
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update location", e);
        }
    }
    @Override
    public void updatePositions(String id, List<RoleDTO> roles) {
        try {
            String deleteSql = "DELETE FROM Employees_Positions WHERE employeeID = ?";
            try (   Connection connection = DBUtil.getConnection();
                    PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
            }
            String insertSql = "INSERT INTO Employees_Positions (employeeID, roleID) VALUES (?, ?)";
            try (   Connection connection = DBUtil.getConnection();
                    PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                for (RoleDTO role : roles) {
                    pstmt.setString(1, id);
                    pstmt.setInt(2, role.getRoleId());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update positions", e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM Employees WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        deleteRoles(id);
        deleteLicenses(id);
    }

    @Override
    public void delete(EmployeeDTO item) {
        String employeeId = item.getId();
        String sql = "DELETE FROM Employees WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        deleteRoles(employeeId);
        deleteLicenses(employeeId);
    }

    @Override
    public void deleteAll() {
        String deleteEmpShifts = "DELETE FROM Employee_Shifts;";
        String deleteLicenses  = "DELETE FROM Employees_License;";
        String deletePositions = "DELETE FROM Employees_Positions;";
        String deleteFormer    = "DELETE FROM FormerEmployees;";
        String deleteEmployees = "DELETE FROM Employees;";

        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");
            connection.setAutoCommit(false);

            stmt.executeUpdate(deleteEmpShifts);
            stmt.executeUpdate(deleteLicenses);
            stmt.executeUpdate(deletePositions);
            stmt.executeUpdate(deleteFormer);
            stmt.executeUpdate(deleteEmployees);

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete all employees", e);
        }
    }

    @Override
    public int count() {
        return 0;
    }


    private void deleteRoles(String employeeId) {
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement("DELETE FROM Employees_Positions WHERE employeeID = ?")) {
            stmt.setString(1, employeeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteLicenses(String employeeId) {
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement("DELETE FROM Employees_License WHERE employeeID = ?")) {
            stmt.setString(1, employeeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private EmployeeDTO mapRowToEmployee(ResultSet rs) throws SQLException {
        String id           = rs.getString("id");
        String password     = rs.getString("password");
        String name         = rs.getString("fullName");
        LocalDate startDate = LocalDate.parse(rs.getString("startDate"));
        double salary       = rs.getDouble("salary");
        String bankAccount  = rs.getString("bankAccount");
        int monthlyHours    = rs.getInt("monthlyHours");
        int usedHours       = rs.getInt("usedHours");

        int locationId = rs.getInt("locationID");
        LocationDTO location = null;
        if (!rs.wasNull()) {
            location = AreaLocationDAOImpl.getInstance().getLocationById(locationId);
        }

        ArrayList<RoleDTO> roles  = getEmployeeRoles(id);
        List<String> licenses     = getEmployeeLicenses(id);

        return new EmployeeDTO(
                id,
                password,
                name,
                startDate,
                roles,
                salary,
                bankAccount,
                monthlyHours,
                usedHours,
                licenses,
                location
        );
    }

    private ArrayList<RoleDTO> getEmployeeRoles(String employeeId) {
        ArrayList<RoleDTO> roles = new ArrayList<>();
        String sql = "SELECT R.id, R.name FROM Employees_Positions ER JOIN Roles R ON ER.roleID = R.id WHERE ER.employeeID = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(new RoleDTO(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    private List<String> getEmployeeLicenses(String employeeId) {
        List<String> licenses = new ArrayList<>();
        String sql = "SELECT licenseType FROM Employees_License WHERE employeeID = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    licenses.add(rs.getString("licenseType"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return licenses;
    }

//    @Override
//    public void moveToFormerEmployees(String id, EmployeeDTO employee) {
//        FormerEmployeeDTO former = new FormerEmployeeDTO(
//                employee.getId(),
//                employee.getFullName(),
//                employee.getStartDate(),
//                LocalDate.now(),
//                "Fired",
//                employee.getLocation().getId()
//        );
//        JdbcFormerEmployeeDAO.getInstance().insert(former);
//        delete(id);
//    }

    public boolean existsById(String id) {
        String sql = "SELECT 1 FROM Employees WHERE id = ?";
        try ( Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
