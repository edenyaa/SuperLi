package Backend.DTO;

import Backend.DomainLayer.DomainLayerHR.TimeSlot;

public class TimeSlotDTO {
    private int dayNumber;
    private int timeAtDay;

    public TimeSlotDTO(TimeSlot timeSlot) {
        this.dayNumber = timeSlot.getDayNumber();
        this.timeAtDay = timeSlot.getTimeAtDay();
    }
    public TimeSlotDTO(int dayNumber, int timeAtDay) {
        this.dayNumber = dayNumber;
        this.timeAtDay = timeAtDay;
    }
    public TimeSlot toDomain() {
        return new TimeSlot(dayNumber, timeAtDay);
    }
    public int getDayNumber() { return dayNumber; }
    public int getTimeAtDay() { return timeAtDay; }
}