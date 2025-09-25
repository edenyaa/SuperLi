
package Backend.ServiceLayer.ServiceLayerHR.HRService;

import Backend.DTO.*;
import Backend.DTO.LocationDTO;
import Backend.DomainLayer.DomainLayerHR.*;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import Backend.ServiceLayer.ServiceLayerT.ManagerService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeliveryEmployeeService {
    private static final DeliveryEmployeeService instance = new DeliveryEmployeeService();
    private final EmployeeFacade employeeFacade = EmployeeFacade.getInstance();
    private final HRInboxService inboxService = new HRInboxService();

    private DeliveryEmployeeService() {}

    public static DeliveryEmployeeService getInstance() {
        return instance;
    }



    public Response getEmployeesForDelivery(int day, int shiftTime) {
        try {
            LinkedList<TransportationEmployeeDTO> result = new LinkedList<>();

            for (EmployeeDL employee : employeeFacade.getAllEmployees()) {
                WeeklyShift empWeeklyShift = employee.getCurrentWeekShifts();
                Map<TimeSlot, Shift> empWeeklyShifts = empWeeklyShift.getWeeklyShiftMap();

                for (Shift shift : empWeeklyShifts.values()) {
                    TimeSlot ts = shift.getTimeSlot();
                    if (ts.getDayNumber() == day && ts.getTimeAtDay() == shiftTime) {
                        for (Role role : employee.getPositions()) {
                            String roleName = role.getRoleName();
                            if (roleName.equalsIgnoreCase("Driver") || roleName.equalsIgnoreCase("WareHouse Worker")) {
                                LinkedList<String> licenseTypes = new LinkedList<>(employee.getLicenseType());

                                TransportationEmployeeDTO dto = new TransportationEmployeeDTO(
                                        employee.getId(),
                                        employee.getFullName(),
                                        licenseTypes,
                                        true,
                                        employee.getLocation()
                                );
                                result.add(dto);
                                break;
                            }
                        }
                    }
                }
            }

            if (result.isEmpty()) {
                return new Response("No employees found for the requested shift.");
            }

            return new Response(result);

        } catch (Exception e) {
            return new Response("Internal error while retrieving employees: " + e.getMessage());
        }
    }
    public List<LocationDTO> getAllAvailableLocations() {
        List<LocationDTO> locations = new ArrayList<>();
        LinkedList<AreaDTO> areas = ManagerService.getInstance().getAllAreas();
        for (AreaDTO area : areas) {
            locations.addAll(area.getLocations());
        }
        return locations;
    }
    public Response reportIssueToHR(String employeeId, String reason) {
        String msg = "Employee ID: " + employeeId + "\nIssue: " + reason;
        return inboxService.sendMessageToHR("Delivery Manager", msg);
    }
}
