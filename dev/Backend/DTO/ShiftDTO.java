package Backend.DTO;

import Backend.DomainLayer.DomainLayerHR.Role;
import Backend.DomainLayer.DomainLayerHR.Shift;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ShiftDTO {
    private int id;
    private LocalDate date;
    private TimeSlotDTO timeSlot;
    private Map<Role, List<String>> assignedEmployeesByRole;

    public ShiftDTO(int id, LocalDate date, TimeSlotDTO timeSlot, Map<Role, List<String>> assignedEmployeesByRole) {
        this.id = id;
        this.date = date;
        this.timeSlot = timeSlot;
        this.assignedEmployeesByRole = assignedEmployeesByRole;
    }

    public ShiftDTO(Shift shift) {
        this.id = shift.getId();
        this.date = shift.getDate();
        this.timeSlot = new TimeSlotDTO(shift.getTimeSlot());
        this.assignedEmployeesByRole = new java.util.HashMap<>();
        for (var entry : shift.getReqRolesMap().entrySet()) {
            Role role = entry.getKey();
            List<String> employees = shift.getAssignedEmployees();
            this.assignedEmployeesByRole.put(role, employees);
        }
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public TimeSlotDTO getTimeSlot() { return timeSlot; }
    public Map<Role, List<String>> getAssignedEmployeesByRole() { return assignedEmployeesByRole; }
}