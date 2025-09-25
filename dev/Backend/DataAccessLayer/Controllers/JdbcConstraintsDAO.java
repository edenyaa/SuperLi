package Backend.DataAccessLayer.Controllers;

import Backend.DTO.ConstraintsDTO;
import Backend.DataAccessLayer.DAO.ConstraintsDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class JdbcConstraintsDAO implements ConstraintsDAO {

    @Override
    public LinkedList<ConstraintsDTO> getAll() {
        HashMap<LocalDate, HashMap<Integer, ConstraintsDTO>> constraintsMap = new HashMap<>();
        String sql = "SELECT * FROM Constraints";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ConstraintsDTO con = mapRowToConstraintsDTO(rs);
                    LocalDate date = con.getDate();
                    if (!(constraintsMap.containsKey(date))) {
                        HashMap<Integer, ConstraintsDTO> dayConstraints = new HashMap<>();
                        dayConstraints.put(con.getTimeAtDay(), con);
                        constraintsMap.put(date, dayConstraints);
                    } else {
                        HashMap<Integer, ConstraintsDTO> dayConstraints = constraintsMap.get(date);
                        if (!dayConstraints.containsKey(con.getTimeAtDay())) {
                            dayConstraints.put(con.getTimeAtDay(), con);
                        } else {
                            ConstraintsDTO existing = dayConstraints.get(con.getTimeAtDay());
                            List<String> empCanWork = con.getEmpCanWork();
                            for (String id : empCanWork) {
                                if (!(existing.getEmpCanWork().contains(id))) {
                                    existing.getEmpCanWork().add(id);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve constraints", e);
        }
        return convertMapToList(constraintsMap);
    }

    private LinkedList<ConstraintsDTO> convertMapToList(HashMap<LocalDate, HashMap<Integer, ConstraintsDTO>> constraintsMap) {
        LinkedList<ConstraintsDTO> constraintsList = new LinkedList<>();
        for (HashMap<Integer, ConstraintsDTO> dayConstraints : constraintsMap.values()) {
            for (ConstraintsDTO constraint : dayConstraints.values()) {
                constraintsList.add(constraint);
            }
        }
        return constraintsList;
    }

    private ConstraintsDTO mapRowToConstraintsDTO(ResultSet rs) {
        try {
            String employeeId = rs.getString("employeeID");
            int timeAtDay = rs.getInt("timeAtDay");
            String dateText = rs.getString("date");
            LocalDate date = LocalDate.parse(dateText);
            List<String> empList = new ArrayList<>();
            empList.add(employeeId);
            return new ConstraintsDTO(empList, timeAtDay, date);
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // or throw an exception
        }
    }

    @Override
    public void insert(ConstraintsDTO constraints) {
        String sql = "INSERT INTO Constraints (employeeID, timeAtDay, date) VALUES (?, ?, ?)";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String empId : constraints.getEmpCanWork()) {
                stmt.setString(1, empId);
                stmt.setInt(2, constraints.getTimeAtDay());
                stmt.setString(3, constraints.getDate().toString());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert constraints", e);
        }
    }

    @Override
    public void update(ConstraintsDTO constraints) {
        String sql = "UPDATE Constraints SET employeeID = ?, timeAtDay = ?, date = ? WHERE employeeID = ? AND timeAtDay = ? AND date = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String empId : constraints.getEmpCanWork()) {
                stmt.setString(1, empId);
                stmt.setInt(2, constraints.getTimeAtDay());
                stmt.setString(3, constraints.getDate().toString());
                stmt.setString(4, empId); // Assuming you want to update the same employee
                stmt.setInt(5, constraints.getTimeAtDay());
                stmt.setString(6, constraints.getDate().toString());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update constraints", e);
        }
    }

    @Override
    public void delete(ConstraintsDTO constraints) {
        String sql = "DELETE FROM Constraints WHERE employeeID = ? AND timeAtDay = ? AND date = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, constraints.getEmpCanWork().get(0)); // Assuming you want to delete by the first employee ID
            stmt.setInt(2, constraints.getTimeAtDay());
            stmt.setString(3, constraints.getDate().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete constraints", e);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM Constraints";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete all constraints", e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM Constraints";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to count constraints", e);
        }
        return 0;
    }

    @Override
    public ConstraintsDTO getBy(String id){
        return null;
    }

    public ConstraintsDTO get(String empId, int timeAtDay, LocalDate date) {
        String sql = "SELECT * FROM Constraints WHERE employeeID = ? AND timeAtDay = ? AND date = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, empId);
            stmt.setInt(2, timeAtDay);
            stmt.setString(3, date.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToConstraintsDTO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve constraints by employee ID", e);
        }
        return null; // or throw an exception if not found
    }

    public LinkedList<ConstraintsDTO> getEmpCons(String empId) {
        LinkedList<ConstraintsDTO> constraintsList = new LinkedList<>();
        String sql = "SELECT * FROM Constraints WHERE employeeID = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ConstraintsDTO constraint = mapRowToConstraintsDTO(rs);
                    if (constraint != null) {
                        constraintsList.add(constraint);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve employee constraints", e);
        }
        return constraintsList;
    }

}
