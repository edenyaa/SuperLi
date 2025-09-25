package Backend.DataAccessLayer.DAO;

import Backend.DTO.ShiftDTO;
import Backend.DomainLayer.DomainLayerHR.Shift;

import java.time.LocalDate;

public interface ShiftDAO extends DAO<ShiftDTO, Integer> {
    void assignEmployeeToShift(int shiftId, String employeeId, String roleName);
    void removeEmployeeFromShift(int shiftId, String employeeId);
    void insertAssignedEmployees(ShiftDTO shift);
    boolean getBy(LocalDate date, int timeAtDay);
    void addRoleToShift(int shiftId, int roleId, int numOfEmployees);
    void removeRoleFromShift(int id, int roleId);
    void updateNumOfEmployeesForRole(int id, int roleId, int numOfEmployees);
    int insertAndReturnId(ShiftDTO shiftDTO);
    Shift findOrCreate(LocalDate date, int timeAtDay);
    void removeAssignedEmployeesFromShift(int id, String roleName);
}