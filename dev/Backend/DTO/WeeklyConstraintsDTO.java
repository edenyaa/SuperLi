package Backend.DTO;

import java.util.Map;

public class WeeklyConstraintsDTO {
    private Map<TimeSlotDTO, ConstraintsDTO> constraints;

    public WeeklyConstraintsDTO(Map<TimeSlotDTO, ConstraintsDTO> constraints) {
        this.constraints = constraints;
    }

    public Map<TimeSlotDTO, ConstraintsDTO> getConstraints() { return constraints; }
}