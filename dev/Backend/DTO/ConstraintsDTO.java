package Backend.DTO;

import java.time.LocalDate;
import java.util.List;

public class ConstraintsDTO {
    private List<String> empCanWork;
    private final int timeAtDay;
    private final LocalDate date;

    public ConstraintsDTO(List<String> empCanWork, int timeAtDay, LocalDate date) {
        this.empCanWork = empCanWork;
        this.timeAtDay = timeAtDay;
        this.date = date;
    }

    public List<String> getEmpCanWork() { return empCanWork; }

    public int getTimeAtDay() {
        return timeAtDay;
    }

    public LocalDate getDate() {
        return date;
    }
}