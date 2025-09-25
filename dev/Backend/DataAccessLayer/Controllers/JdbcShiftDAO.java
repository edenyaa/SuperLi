package Backend.DataAccessLayer.Controllers;

import Backend.DTO.RoleDTO;
import Backend.DTO.ShiftDTO;
import Backend.DTO.TimeSlotDTO;
import Backend.DataAccessLayer.DAO.ShiftDAO;
import Backend.DataAccessLayer.DBUtil;
import Backend.DomainLayer.DomainLayerHR.Role;
import Backend.DomainLayer.DomainLayerHR.RolesFacade;
import Backend.DomainLayer.DomainLayerHR.Shift;
import Backend.DomainLayer.DomainLayerHR.TimeSlot;
import Backend.DataAccessLayer.Mappers.RoleMapper;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class JdbcShiftDAO implements ShiftDAO {

    @Override
    public void insert(ShiftDTO shift) {
        String sql = "INSERT INTO Shifts (date, day, time) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
//            stmt.setInt(1, shift.getId());
            stmt.setString(1, shift.getDate().toString());
            stmt.setInt(2, shift.getTimeSlot().getDayNumber());
            stmt.setInt(3, shift.getTimeSlot().getTimeAtDay());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    rs.getInt(1); // Set the generated ID back to the shift object
                } else {
                    throw new SQLException("Failed to retrieve generated key for shift");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        insertAssignedEmployees(shift);
    }

    public void insertAssignedEmployees(ShiftDTO shift) {
        String sql = "INSERT INTO Shift_AssignedEmployees (shiftID, roleName, employeeID) VALUES (?, ?, ?)";
        for (Map.Entry<Role, List<String>> entry : shift.getAssignedEmployeesByRole().entrySet()) {
            Role role = entry.getKey();
            for (String empId : entry.getValue()) {
                try (Connection connection = DBUtil.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, shift.getId());
                    stmt.setString(2, role.getRoleName());
                    stmt.setString(3, empId);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ShiftDTO getBy(Integer id) {
        String sql = "SELECT * FROM Shifts WHERE shiftID = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dateSTR = rs.getString("date");
                    LocalDate date = LocalDate.parse(dateSTR);
                    int day = rs.getInt("day");
                    int time = rs.getInt("time");
                    TimeSlotDTO ts = new TimeSlotDTO(day, time);
                    Map<Role, List<String>> assigned = getAssignedEmployees(id);
                    return new ShiftDTO(id, date, ts, assigned);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getBy(LocalDate date, int timeAtDay) {
        String sql = "SELECT * FROM Shifts WHERE date = ? AND time = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, date.toString());
            stmt.setInt(2, timeAtDay);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("failed retrieving a shift: " + e.getMessage());
        }
        return false;
    }

    public int getId(LocalDate date, int timeAtDay) {
        String sql = "SELECT * FROM Shifts WHERE date = ? AND time = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, date.toString());
            stmt.setInt(2, timeAtDay);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("shiftID");
                }
            }
        } catch (SQLException e) {
            System.out.println("failed retrieving a shift: " + e.getMessage());
        }
        return 0;
    }


    private HashMap<Role, List<String>> getAssignedEmployees(int shiftId) {
        HashMap<Role, List<String>> map = new HashMap<>();
        String sql = "SELECT roleName, employeeID FROM Shift_AssignedEmployees WHERE shiftID = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, shiftId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String roleName = rs.getString("roleName");
                    JdbcRoleDAO roleDAO = new JdbcRoleDAO();
                    RoleDTO roleDTO = roleDAO.getByName(roleName);
                    Role role = RoleMapper.fromDTO(roleDTO);
                    String empId = rs.getString("employeeID");
                    map.computeIfAbsent(role, k -> new ArrayList<>()).add(empId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }


    @Override
    public LinkedList<ShiftDTO> getAll() {
        LinkedList<ShiftDTO> list = new LinkedList<>();
        String sql = "SELECT * FROM Shifts";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(convertRowToShiftDTO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve shifts", e);
        }
        return list;
    }

    private ShiftDTO convertRowToShiftDTO(ResultSet rs) {
        try {
            int id = rs.getInt("shiftID");
            String dateSTR = rs.getString("date");
            LocalDate date = LocalDate.parse(dateSTR);
            int day = rs.getInt("day");
            int time = rs.getInt("time");
            TimeSlotDTO ts = new TimeSlotDTO(day, time);
            HashMap<Role, List<String>> assigned = getAssignedEmployees(id);
            return new ShiftDTO(id, date, ts, assigned);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void assignEmployeeToShift(int shiftId, String employeeId, String roleName) {
        String sql1 = "INSERT INTO Shift_AssignedEmployees (shiftID, roleName, employeeID) VALUES (?, ?, ?)";
        String sql2 = "INSERT INTO Employee_Shifts VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement()) {
            try (PreparedStatement stmt1 = connection.prepareStatement(sql1)) {
                stmt1.setInt(1, shiftId);
                stmt1.setString(2, roleName);
                stmt1.setString(3, employeeId);
                stmt1.executeUpdate();
            }
            try (PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
                stmt2.setInt(1, shiftId);
                stmt2.setString(2, employeeId);
                Role role = RolesFacade.getInstance().getRoleByName(roleName);
                int roleId = role.getRoleId();
                stmt2.setInt(3, roleId);
                stmt2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeEmployeeFromShift(int shiftId, String employeeId) {
        String sql1 = "DELETE FROM Shift_AssignedEmployees WHERE shiftID = ? AND employeeID = ?";
        String sql2 = "DELETE FROM Employee_Shifts WHERE shiftID = ? AND employeeID = ?";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement()) {
            try (PreparedStatement stmt1 = connection.prepareStatement(sql1)) {
                stmt1.setInt(1, shiftId);
                stmt1.setString(2, employeeId);
                stmt1.executeUpdate();
            }
            try (PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
                stmt2.setInt(1, shiftId);
                stmt2.setString(2, employeeId);
                stmt2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(ShiftDTO shiftDTO) {
        int id = shiftDTO.getId();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM Shift_AssignedEmployees WHERE shiftID = ?");
             PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM Shifts WHERE shiftID = ?");
             PreparedStatement stmt3 = connection.prepareStatement("DELETE FROM RequiredRoles WHERE shiftID = ?")) {
            stmt1.setInt(1, id);
            stmt1.executeUpdate();

            stmt2.setInt(1, id);
            stmt2.executeUpdate();

            stmt3.setInt(1, id);
            stmt3.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ShiftDTO shift) {
        String sql = "UPDATE Shifts SET date = ?, day = ?, time = ? WHERE shiftID = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shift.getDate().toString());
            stmt.setInt(2, shift.getTimeSlot().getDayNumber());
            stmt.setInt(3, shift.getTimeSlot().getTimeAtDay());
            stmt.setInt(4, shift.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update shift", e);
        }
    }

    @Override
    public void deleteAll() {
        String deleteShifts = "DELETE FROM Shifts;";
        String deleteAssignEmp = "DELETE FROM Shift_AssignedEmployees;";
        String deleteReqRoles = "DELETE FROM requiredRoles;";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.executeUpdate(deleteReqRoles);
            stmt.executeUpdate(deleteAssignEmp);
            stmt.executeUpdate(deleteShifts);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete all shifts", e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM Shifts";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to count shifts", e);
        }
        return 0;
    }

    public void addRoleToShift(int id, int roleId, int numOfEmployees) {
        if (existsRequiredRole(id, roleId)) {
            return;
        }
        String sql = "INSERT INTO requiredRoles (roleID, shiftID, numberOfEmployees) VALUES (?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            stmt.setInt(2, id);
            stmt.setInt(3, numOfEmployees);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add role to shift", e);
        }
    }

    public void removeRoleFromShift(int id, int roleId) {
        String sql = "DELETE FROM requiredRoles WHERE roleID = ? AND shiftID = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to remove role from shift", e);
        }
    }

    public void removeAssignedEmployeesFromShift(int id, String roleName) {
        String sql = "DELETE FROM Shift_AssignedEmployees WHERE shiftID = ? AND roleName = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, roleName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to remove assigned employees from shift", e);
        }
    }

    public void updateNumOfEmployeesForRole(int id, int roleId, int numOfEmployees) {
        String sql = "UPDATE requiredRoles SET numberOfEmployees = ? WHERE roleID = ? AND shiftID = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numOfEmployees);
            stmt.setInt(2, roleId);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update number of employees for role", e);
        }
    }

    private boolean existsRequiredRole(int shiftId, int roleId) {
        String sql = "SELECT 1 FROM requiredRoles WHERE shiftID = ? AND roleID = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, shiftId);
            stmt.setInt(2, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to check requiredRoles existence", e);
        }
    }

    public int insertAndReturnId(ShiftDTO shiftDTO) {
        String sql = "INSERT INTO Shifts (shiftID, date, day, time) VALUES (?, ?, ?, ?)";
        String findMaxSql = "SELECT COALESCE(MAX(shiftID), 0) + 1 FROM Shifts;";

        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement()) {

            connection.setAutoCommit(false);

            ResultSet rs = stmt.executeQuery(findMaxSql);
            int shiftId;
            if (rs.next()) {
                shiftId = rs.getInt(1);
            } else throw new RuntimeException("Failed to find max shift");
            rs.close();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, shiftId);
                pstmt.setString(2, shiftDTO.getDate().toString());
                pstmt.setInt(3, shiftDTO.getTimeSlot().getDayNumber());
                pstmt.setInt(4, shiftDTO.getTimeSlot().getTimeAtDay());
                pstmt.executeUpdate();
            }
            connection.commit();
            return shiftId;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert shift and return ID", e);
        }
    }

    public Shift findOrCreate(LocalDate date, int timeAtDay) {

        String select = "SELECT * FROM Shifts WHERE date = ? AND time = ?";
        String insertToShifts = "INSERT INTO Shifts (shiftID, date, day, time) VALUES (?,?,?,?)";
        String insertToReqRoles = "INSERT INTO requiredRoles (roleID, shiftID, numberOfEmployees) VALUES (?, ?, ?)";

        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);          // one atomic unit of work

            // ---- 1. try to find the row -------------------------------------------------
            try (PreparedStatement ps = c.prepareStatement(select)) {
                ps.setString(1, date.toString());
                ps.setInt   (2, timeAtDay);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {         // row already exists
                        int id = rs.getInt(1);
                        if (id != 0) {
                            HashMap<Role, List<String>> assignedEmp = getAssignedEmployees(id);
                            HashMap<Role, Integer> reqRoles = getReqRole(id);
                            LocalDate shiftDate = LocalDate.parse(rs.getString(2));
                            int shiftDay = rs.getInt(3);
                            int shiftTimeAtDay = rs.getInt(4);
                            TimeSlot shiftTs = new TimeSlot(shiftDay, shiftTimeAtDay);
                            return new Shift(id, shiftDate, shiftTs ,assignedEmp, reqRoles);
                        }
                        c.commit();        //  <-- will never be 0 once you clean the table
                    }
                }
            }

            // ---- 2. not found → create it ----------------------------------------------
            int newId = nextShiftId(c);      // SAME connection → no race
            try (PreparedStatement ps = c.prepareStatement(insertToShifts)) {
                ps.setInt   (1, newId);
                ps.setString(2, date.toString());
                ps.setInt   (3, getDay(date));   // your helper that turns date → 1-7
                ps.setInt   (4, timeAtDay);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(insertToReqRoles)) {
                ps.setInt(1, 2);
                ps.setInt(2, newId);
                ps.setInt   (3, 1);
                ps.executeUpdate();
            }
            int dayNum = getDay(date);
            TimeSlot ts = new TimeSlot(dayNum, timeAtDay);
            c.commit();
            return new Shift(newId, date, ts);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to find or create shift", ex);
        }
    }

    private HashMap<Role, Integer> getReqRole(int id) {
        String sql = "SELECT roleID, numberOfEmployees FROM requiredRoles WHERE shiftID = ?";
        HashMap<Role, Integer> reqRoles = new HashMap<>();
        try ( Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try ( ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int roleID = rs.getInt(1);
                    JdbcRoleDAO roleDAO = new JdbcRoleDAO();
                    RoleDTO roleDTO = roleDAO.getBy(roleID);
                    Role role;
                    if (roleDTO != null) {
                        role = RoleMapper.fromDTO(roleDTO);

                    }
                    else {
                        role = RolesFacade.getInstance().getRoleById(roleID);
                    }

                    int numOfEmp = rs.getInt(2);
                    if (reqRoles.containsKey(role)) {
                        reqRoles.put(role, reqRoles.get(role) + numOfEmp);
                    }
                    else {
                        reqRoles.put(role, numOfEmp);
                    }
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return reqRoles;
    }


    public int create(LocalDate date, int timeAtDay) {
        int shiftID = getMaxID();
        String sql2 = "INSERT INTO Shifts(shiftID,date,day,time) VALUES(?,?,?,?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql2)) {
            stmt.setInt(1, shiftID);
            stmt.setString(2, date.toString());
            stmt.setInt(3, getDay(date));
            stmt.setInt(4, timeAtDay);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return shiftID;
    }

    // utility – must be called with an *open* connection that
// is part of the current transaction
    private int nextShiftId(Connection c) throws SQLException {
        String sql = "SELECT COALESCE(MAX(shiftID),0) + 1 AS nextId FROM Shifts";
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            rs.next();                 // there is always one row
            return rs.getInt(1);     // the NEXT free id (≥1)
        }
    }



    public int getMaxID() {
        String sql = "SELECT COALESCE(MAX(shiftID), 0) + 1 FROM Shifts;";
        try ( Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            int shiftID;
            if (rs.next()){
                shiftID = rs.getInt(1);
                return shiftID;
            }
            else {
                return 0;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int getDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY:
                return 2;
            case TUESDAY:
                return 3;
            case WEDNESDAY:
                return 4;
            case THURSDAY:
                return 5;
            case FRIDAY:
                return 6;
            case SATURDAY:
                return 7;
            case SUNDAY:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid day of the week.");
        }
    }
}
