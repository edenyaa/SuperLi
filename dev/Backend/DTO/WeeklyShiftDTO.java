package Backend.DTO;

import java.util.List;

public class WeeklyShiftDTO {
    private List<ShiftDTO> shifts;

    public WeeklyShiftDTO(List<ShiftDTO> shifts) {
        this.shifts = shifts;
    }

    public List<ShiftDTO> getShifts() { return shifts; }
}