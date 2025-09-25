package Backend.DataAccessLayer.Mappers;

import Backend.DTO.EmployeeDTO;
import Backend.DTO.RoleDTO;
import Backend.DomainLayer.DomainLayerHR.EmployeeDL;
import Backend.DomainLayer.DomainLayerHR.Role;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EmployeeMapper {

    public static EmployeeDTO toDTO(EmployeeDL employee) {
        ArrayList<RoleDTO> roleDTOs = employee.getPositions().stream()
                .map(RoleDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));

        return new EmployeeDTO(
                employee.getId(),
                employee.getPassword(),
                employee.getFullName(),
                employee.getStartDate(),
                roleDTOs,
                employee.getSalary(),
                employee.getBankAccount(),
                employee.getMonthlyHours(),
                employee.getHoursWorked(),
                employee.getLicenseType(),
                employee.getLocation()
        );
    }

    public static EmployeeDL fromDTO(EmployeeDTO dto) {
        ArrayList<Role> roles = dto.getPositions().stream()
                .map(r -> new Role(r.getRoleId(), r.getRoleName()))
                .collect(Collectors.toCollection(ArrayList::new));

        return new EmployeeDL(
                dto.getId(),
                dto.getPassword(),
                dto.getFullName(),
                dto.getStartDate(),
                roles,
                dto.getSalary(),
                dto.getBankAccount(),
                dto.getMonthlyHours(),
                dto.getUsedHours(),
                dto.getLicenseTypes(),
                dto.getLocation()
        );
    }
}