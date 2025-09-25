package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.EmployeeDTO;
import Backend.DTO.LocationDTO;
import Backend.DTO.RoleDTO;

import java.util.List;

public interface HRRepsitory extends Repository<EmployeeDTO, String> {
    void updatePassword(String id, String password);
    void updateFullName(String id, String name);

    void updateSalary(String id, double salary);
    void updateBankAccount(String id, String account);
    void updateMonthlyHours(String id, int hours);
    void updateLicenseTypes(String id, List<String> licenseTypes);
    void updateLocation(String id, LocationDTO location);
    void updatePositions(String id, List<RoleDTO> roles);
    void moveToFormerEmployees(String id, EmployeeDTO employee);
}
