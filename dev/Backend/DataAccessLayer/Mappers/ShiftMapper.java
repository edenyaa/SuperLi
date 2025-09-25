package Backend.DataAccessLayer.Mappers;

import Backend.DTO.ShiftDTO;
import Backend.DTO.TimeSlotDTO;
import Backend.DomainLayer.DomainLayerHR.Role;
import Backend.DomainLayer.DomainLayerHR.RolesFacade;
import Backend.DomainLayer.DomainLayerHR.Shift;
import Backend.DomainLayer.DomainLayerHR.TimeSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftMapper {

    public static ShiftDTO toDTO(Shift shift) {
        Map<Role, List<String>> assigned = new HashMap<>();
        for (Role role : shift.getRequiredRoles()) {
            List<String> employees = new ArrayList<>();
            try {
                employees = getAssignedForRole(shift, role);
            } catch (Exception ignored) {}
            assigned.put(role, employees);
        }

        return new ShiftDTO(
                shift.getId(),
                shift.getDate(),
                new TimeSlotDTO(shift.getTimeSlot()),
                assigned
        );
    }

    public static Shift fromDTO(ShiftDTO dto) {
        TimeSlot timeSlot = dto.getTimeSlot().toDomain();
        Shift shift = new Shift(dto.getId(), dto.getDate(), timeSlot);
        shift.setRolesUnassigned();
        for (Map.Entry<Role, List<String>> entry : dto.getAssignedEmployeesByRole().entrySet()) {
            Role role = entry.getKey();

            List<String> employees = entry.getValue();

            shift.addRole(role, Math.max(1, employees.size()), false);

            for (String empId : employees) {
                try {
                    shift.addEmployee(empId, role, false);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return shift;
    }

    private static List<String> getAssignedForRole(Shift shift, Role role) {
        return shift.getAssignedEmployeesForRole(role);
    }
}