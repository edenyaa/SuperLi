package Backend.DataAccessLayer.DAO;

import Backend.DTO.ConstraintsDTO;

import java.time.LocalDate;
import java.util.LinkedList;

public interface ConstraintsDAO extends DAO<ConstraintsDTO, String> {

    ConstraintsDTO get(String id, int timeAtDay, LocalDate date);

    LinkedList<ConstraintsDTO> getEmpCons(String employeeId);
}