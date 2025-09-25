package serviceLayer;

import java.time.LocalDate;

public class SimulatedClock {
    private static final SimulatedClock instance = new SimulatedClock(); // eager singleton
    private LocalDate currentDate;

    private SimulatedClock() {
        this.currentDate = LocalDate.now();
    }

    public static SimulatedClock getInstance() {
        return instance;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void advanceDay(){
        currentDate = currentDate.plusDays(1);
    }

    public void reset() {
        currentDate = LocalDate.now();
    }
}
