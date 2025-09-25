package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.ShiftDTO;
import Backend.DomainLayer.DomainLayerHR.Shift;

import java.time.LocalDate;

public interface ShiftRepository extends Repository<ShiftDTO, Integer> {
    void assignEmployee(int shiftID, String employeeId, String roleName);

    void removeEmployee(int shiftID, String employeeId);

    void insertShiftEmp(ShiftDTO shiftDTO);

    boolean getByDateAndTime(LocalDate date, int timeAtDay);

    void addRole(int shiftId, int roleId, int numOfEmployees);

    void removeRole(int shiftId, int roleId);

    void updateRole(int shiftId, int roleId, int numOfEmployees);

    int insertAndGetID(ShiftDTO shiftDTO);

    Shift findOrCreate(LocalDate date, int timeAtDay);

    void removeRoleAssignedEmployees(int shiftId, String roleName);

}
