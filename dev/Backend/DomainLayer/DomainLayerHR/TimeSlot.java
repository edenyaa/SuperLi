package Backend.DomainLayer.DomainLayerHR;

import java.util.Objects;

public class TimeSlot implements Comparable<TimeSlot>{
    private int dayNumber;
    private int timeAtDay;

    public TimeSlot(int dayNumber, int timeAtDay) {
        this.dayNumber = dayNumber;
        // dayNumber = 1 for Sunday
        // dayNumber = 2 for Monday
        // ......
        this.timeAtDay = timeAtDay;
        // time at day = 1 for morning shift
        // time at day = 2 for evening shift
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public int getTimeAtDay() {
        return timeAtDay;
    }
    public String getDayName() {
        switch (dayNumber) {
            case 1: return "Sunday";
            case 2: return "Monday";
            case 3: return "Tuesday";
            case 4: return "Wednesday";
            case 5: return "Thursday";
            case 6: return "Friday";
            case 7: return "Saturday";
            default: return "Invalid day";
        }
    }

    @Override
    public String toString() {
        return "Day " + dayNumber + " - " + (timeAtDay == 1 ? "Morning" : "Evening");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return dayNumber == timeSlot.dayNumber &&
                timeAtDay == timeSlot.timeAtDay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayNumber, timeAtDay);
    }

    @Override
    public int compareTo(TimeSlot other) {
        if (this.dayNumber != other.dayNumber) {
            return this.dayNumber - other.dayNumber;
        }
        return this.timeAtDay - other.timeAtDay;
    }

}
