package Backend.DataAccessLayer.Mappers;

import Backend.DTO.FormerEmployeeDTO;
import Backend.DomainLayer.DomainLayerHR.EmployeeDL;

import java.time.LocalDate;

public class FormerEmployeeMapper {

    public static FormerEmployeeDTO toDTO(EmployeeDL formerEmployee, LocalDate endDate, String reason) {
        return new FormerEmployeeDTO(
                formerEmployee.getId(),
                formerEmployee.getFullName(),
                formerEmployee.getStartDate(),
                endDate,
                reason,
                formerEmployee.getLocation().getId()
        );
    }
}
