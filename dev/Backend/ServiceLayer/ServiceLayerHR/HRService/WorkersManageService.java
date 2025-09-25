package Backend.ServiceLayer.ServiceLayerHR.HRService;

import Backend.DTO.*;
import Backend.DomainLayer.DomainLayerHR.HRDL;
import Backend.DomainLayer.DomainLayerHR.Role;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class WorkersManageService {

    private final HRDL hr = HRDL.getInstance();

    public Response hireEmployee(String id, String password, String fullName, LocalDate startDate,
                                 ArrayList<RoleDTO> positionsDTO, Double salary, String bankAccount,
                                 int monthlyHours, int hoursWorked, List<String> licenseType, LocationDTO location) {
        try {
            ArrayList<Role> positions = new ArrayList<>();
            for (RoleDTO dto : positionsDTO) {
                positions.add(new Role(dto.getRoleId(), dto.getRoleName()));
            }
            return hr.hireEmployee(id, password, fullName, startDate, positions, salary,
                    bankAccount, monthlyHours, hoursWorked, licenseType, location);
        } catch (Exception e) {
            return new Response("Error hiring employee: " + e.getMessage());
        }
    }


    public Response fireEmployee(String id) {
        return hr.fireEmployee(id);
    }
    public Response updatePassword(String id, String password) {
        return hr.updatePassword(id, password);
    }

    public Response updateFullName(String id, String name) {
        return hr.updateFullName(id, name);
    }

    public Response updatePositions(String id, List<RoleDTO> roleDTOs) {
        List<Role> roles = new ArrayList<>();
        for (RoleDTO dto : roleDTOs) {
            roles.add(new Role(dto.getRoleId(), dto.getRoleName()));
        }
        return hr.updatePositions(id, roles);
    }

    public Response updateSalary(String id, double salary) {
        return hr.updateSalary(id, salary);
    }

    public Response updateBankAccount(String id, String account) {
        return hr.updateBankAccount(id, account);
    }

    public Response updateMonthlyHours(String id, int hours) {
        return hr.updateMonthlyHours(id, hours);
    }

    public Response updateLicenseType(String id, List<String> licenseTypes) {
        return hr.updateLicenseType(id, licenseTypes);
    }
    public Response updateLocation(String id, LocationDTO location) {
        return hr.updateLocation(id, location);
    }

    public Response showEmployee(String id) {
        return hr.showEmployee(id);
    }

    public Response showFormerEmployee(String id) {
        return hr.showFormerEmployee(id);
    }

    public Response showAllEmployees() {
        return hr.showAllEmployees();
    }
}
