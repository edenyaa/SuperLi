package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.ShiftDTO;
import Backend.DataAccessLayer.Controllers.JdbcShiftDAO;
import Backend.DataAccessLayer.DAO.ShiftDAO;
import Backend.DomainLayer.DomainLayerHR.Shift;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

public class ShiftRepoImpl implements ShiftRepository {
    private final ShiftDAO shiftDAO;

    public ShiftRepoImpl() {
        this.shiftDAO = new JdbcShiftDAO();
    }

    @Override
    public void assignEmployee(int shiftID, String employeeId, String roleName) {
        shiftDAO.assignEmployeeToShift(shiftID, employeeId, roleName);
    }

    @Override
    public void removeEmployee(int shiftID, String employeeId) {
        shiftDAO.removeEmployeeFromShift(shiftID, employeeId);
    }

    @Override
    public void insertShiftEmp(ShiftDTO shiftDTO) {
        shiftDAO.insertAssignedEmployees(shiftDTO);
    }

    @Override
    public boolean getByDateAndTime(LocalDate date, int timeAtDay) {
        return shiftDAO.getBy(date, timeAtDay);
    }

    @Override
    public void addRole(int shiftId, int roleId, int numOfEmployees) {
        shiftDAO.addRoleToShift(shiftId, roleId, numOfEmployees);
    }

    @Override
    public void removeRole(int shiftId, int roleId) {
        shiftDAO.removeRoleFromShift(shiftId, roleId);
    }

    @Override
    public void updateRole(int shiftId, int roleId, int numOfEmployees) {
        shiftDAO.updateNumOfEmployeesForRole(shiftId, roleId, numOfEmployees);
    }

    @Override
    public int insertAndGetID(ShiftDTO shiftDTO) {
        return shiftDAO.insertAndReturnId(shiftDTO);
    }

    @Override
    public Shift findOrCreate(LocalDate date, int timeAtDay) {
        return shiftDAO.findOrCreate(date, timeAtDay);
    }

    @Override
    public LinkedList<ShiftDTO> selectAll() throws SQLException {
        return shiftDAO.getAll();
    }

    @Override
    public void deleteAll() throws SQLException {
        shiftDAO.deleteAll();
    }

    @Override
    public void insert(ShiftDTO item) throws SQLException {
        shiftDAO.insert(item);
    }

    @Override
    public void update(ShiftDTO item) throws SQLException {
        shiftDAO.update(item);
    }

    @Override
    public void delete(ShiftDTO item) throws SQLException {
        shiftDAO.delete(item);
    }

    @Override
    public ShiftDTO select(Integer shiftId) throws SQLException {
        return shiftDAO.getBy(shiftId);
    }

    @Override
    public void removeRoleAssignedEmployees(int shiftId, String roleName) {
        shiftDAO.removeAssignedEmployeesFromShift(shiftId, roleName);
    }
}
