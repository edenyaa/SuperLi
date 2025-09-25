package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.ShiftDTO;
import Backend.DomainLayer.DomainLayerHR.Repos.ShiftRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.ShiftRepository;
import Backend.DataAccessLayer.Mappers.ShiftMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class WeeklyShiftFacade {
    private static final WeeklyShiftFacade instance = new WeeklyShiftFacade();
    private Map<LocalDate, WeeklyShift> WS;
    private LocalDate publishedWSDate;
    private WeeklyShift unPublishedWeeklyShift;
    private WeeklyShift publishedWeeklyShift;
//    final JdbcShiftDAO dao = JdbcShiftDAO.getInstance();
    private final ShiftRepository shiftRepo;

    public static WeeklyShiftFacade getInstance() {
        return instance;
    }

    private WeeklyShiftFacade() {
        // private constructor to prevent instantiation
        WS = new HashMap<>();
        unPublishedWeeklyShift = new WeeklyShift(false);
        shiftRepo = new ShiftRepoImpl();
    }

    public Shift addShift(int day, int timeAtDay) {
        TimeSlot ts = new TimeSlot(day, timeAtDay);
        WeeklyShift unpub = getNextWeeklyShift();
        return unpub.addShift(ts);
    }

    public WeeklyShift getNextWeeklyShift() {
        if (unPublishedWeeklyShift == null) {
            unPublishedWeeklyShift = new WeeklyShift(false);
        }
        return unPublishedWeeklyShift;
    }

    public WeeklyShift getPublishedWeeklyShift() {
        if (publishedWeeklyShift == null) {
            publishedWeeklyShift = new WeeklyShift(false);
        }
        return publishedWeeklyShift;
    }

    public Shift getShift(int day, int time, boolean published) {
        TimeSlot ts = new TimeSlot(day, time);
        if (published) {
            WeeklyShift pub = getPublishedWeeklyShift();
            return pub.getShift(ts);
        }
        WeeklyShift unpub = getNextWeeklyShift();
        return unpub.getShift(ts);
    }

    public String viewPublishedWeeklyShifts() {
        if (publishedWeeklyShift == null) {
            return "published weekly shift not been initialized yet";
        }
        return publishedWeeklyShift.toString();
    }

    public String viewUnPublishedWeeklyShifts() {
        return unPublishedWeeklyShift.toString();
    }

    public Shift removeShift(int day, int time) {
        TimeSlot ts = new TimeSlot(day, time);
        WeeklyShift unpub = getNextWeeklyShift();
        return unpub.removeShift(ts);
    }

    public void setNewWeek() {
        LocalDate nextSunday = findNextSunday();
        if (!(WS.containsKey(nextSunday))) WS.put(nextSunday, unPublishedWeeklyShift);
        publishedWSDate = nextSunday;
        publishedWeeklyShift = unPublishedWeeklyShift;
        LocalDate nextNextSunday = nextSunday.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        unPublishedWeeklyShift = new WeeklyShift(true);
        if (!(WS.containsKey(nextNextSunday))) WS.put(nextNextSunday, unPublishedWeeklyShift);
    }

    public LocalDate findNextSunday() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
    }

    public LocalDate findPreviousSunday(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    public LocalDate getPublishedWeekStart() {
        if (publishedWSDate == null) {;
            return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        }
        return publishedWSDate;
    }

    public void loadData() {
        LinkedList<ShiftDTO> allShifts = new LinkedList<>();
        try {
            allShifts = shiftRepo.selectAll();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (ShiftDTO shiftDTO : allShifts) {
            LocalDate startWeek = findPreviousSunday(shiftDTO.getDate());
            if (!WS.containsKey(startWeek)) {
                WeeklyShift ws = new WeeklyShift(false);
                WS.put(startWeek, ws);
                if (startWeek.isEqual(findPreviousSunday(LocalDate.now()))) {
                    publishedWeeklyShift = ws;
                }
                else if (startWeek.isEqual(findNextSunday())) {
                    unPublishedWeeklyShift = ws;
                }
            }
            WeeklyShift weeklyShift = WS.get(startWeek);
            TimeSlot timeSlot = new TimeSlot(shiftDTO.getTimeSlot().getDayNumber(), shiftDTO.getTimeSlot().getTimeAtDay());
            weeklyShift.replaceShift(timeSlot, ShiftMapper.fromDTO(shiftDTO));
        }

    }

    public void deleteData() {
        try{
            shiftRepo.deleteAll();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void get(LocalDate date) {
        if (WS.containsKey(date)) {
            WS.get(date);
        } else {
            throw new IllegalArgumentException("No weekly shift found for the specified date.");
        }
    }
}
