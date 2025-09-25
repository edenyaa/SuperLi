package Backend.DTO;

import java.time.LocalDate;

public class FormerEmployeeDTO {
    private final String id;
    private final String fullName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String reason;
    private final int locationId;

    public FormerEmployeeDTO(String id, String fullName, LocalDate startDate, LocalDate endDate, String reason, int locationId) {
        this.id = id;
        this.fullName = fullName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.locationId = locationId;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public int getLocation() { return locationId; }
}
