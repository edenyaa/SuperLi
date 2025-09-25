package Backend.DataAccessLayer.Mappers;

import Backend.DomainLayer.DomainLayerHR.HRDL;
import Backend.DTO.HRDTO;
import Backend.DTO.RoleDTO;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HRMapper {

    public static HRDTO toDTO(HRDL hr) {
        if (hr == null) return null;

        ArrayList<RoleDTO> roleDTOs = hr.getPositions().stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toCollection(ArrayList::new));

        return new HRDTO(
                hr.getId(),
                hr.getFullName(),
                hr.getPassword(),
                hr.getStartDate(),
                roleDTOs,
                hr.getSalary(),
                hr.getBankAccount(),
                hr.getMonthlyWorkHours(),
                hr.getHoursWorked(),
                hr.getLicenseTypes(),
                hr.getUnreadMessagesCount(),
                hr.getReadMessagesCount()
        );
    }

}
