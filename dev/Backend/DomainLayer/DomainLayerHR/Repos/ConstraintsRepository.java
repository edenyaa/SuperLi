package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.ConstraintsDTO;

import java.time.LocalDate;
import java.util.LinkedList;

public interface ConstraintsRepository extends Repository<ConstraintsDTO, String> {
    ConstraintsDTO findByIdAndDate(String id, int timeAtDay, LocalDate date);

    LinkedList<ConstraintsDTO> findEmpCons(String employeeId);
}
