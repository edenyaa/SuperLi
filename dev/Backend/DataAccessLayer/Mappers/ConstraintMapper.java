package Backend.DataAccessLayer.Mappers;

import Backend.DTO.ConstraintsDTO;
import Backend.DomainLayer.DomainLayerHR.Constraint;
import java.util.ArrayList;

public class ConstraintMapper {


//    public static ConstraintsDTO toDTO(Constraint constraint) {
//        return new ConstraintsDTO(new ArrayList<>(constraint.getEmpCanWork()));
//    }

    public static Constraint fromDTO(ConstraintsDTO dto) {
        return new Constraint(new ArrayList<>(dto.getEmpCanWork()));
    }
}
