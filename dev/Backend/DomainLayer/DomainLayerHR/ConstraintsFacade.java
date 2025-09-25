package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.ConstraintsDTO;
import Backend.DomainLayer.DomainLayerHR.Repos.ConstraintsRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.ConstraintsRepository;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.time.LocalTime;

public class ConstraintsFacade {

    private final Map<LocalDate, WeeklyConstraints> CF;
    private LocalDateTime deadlineDateTime;
    private boolean constraintsCollected = true;

    private static ConstraintsFacade instance = new ConstraintsFacade();

    public static ConstraintsFacade getInstance() { return instance; }

    private final ConstraintsRepository conRepo;

    private ConstraintsFacade() {
        // private constructor to prevent instantiation
        this.CF = new HashMap<>();
        this.deadlineDateTime = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.THURSDAY))
                .atTime(16, 0);
        this.conRepo = new ConstraintsRepoImpl();
    }

    public void addNextWeekConstraints(WeeklyConstraints constraints) {
        LocalDate nextSunday = findNextSunday();
        if (!CF.containsKey(nextSunday)) {
            CF.put(nextSunday, constraints);
        } else {
            System.out.println("Constraints for the next week already exist. Please update them instead.");
        }
    }

    public WeeklyConstraints viewPrevWeeklyConstraints(int weeksBack) {
        LocalDate thisSunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate targetWeek = thisSunday.minusWeeks(weeksBack);
        if (CF.containsKey(targetWeek)) {
            return CF.get(targetWeek);
        } else {
            throw new IllegalArgumentException("No constraints found for the specified week.");
        }
    }


    public void setDeadline(DayOfWeek day, LocalTime time) {
        LocalDate now = LocalDate.now();
        LocalDate nextDay = now.with(TemporalAdjusters.nextOrSame(day));
        this.deadlineDateTime = LocalDateTime.of(nextDay, time);
    }

    public boolean canUploadConstraints() {
        return !isAfterDeadline();
    }
    public void ConstraintsIsCollected() {
        this.constraintsCollected = true;
    }
    public void resetAfterCollection() {
        this.deadlineDateTime = this.deadlineDateTime.plusWeeks(1);
        System.out.println("New deadline set to " + this.deadlineDateTime.getDayOfMonth() + " at month " + this.deadlineDateTime.getMonth() + " at " + this.deadlineDateTime.getHour() + ":" + this.deadlineDateTime.getMinute() + this.deadlineDateTime.getSecond());
    }


    public String getDeadline() {
        return "Deadline is set to " + this.deadlineDateTime;
    }

    public boolean isAfterDeadline() {
        return LocalDateTime.now().isAfter(deadlineDateTime);
    }

    public boolean isBeforeDeadline() {
        return LocalDateTime.now().isBefore(deadlineDateTime);
    }

    public void updateCollectionStateIfNeeded() {
        if (isAfterDeadline()) {
            this.constraintsCollected = false;
        }
    }

    public Map<LocalDate, WeeklyConstraints> getCF() {
        return CF;
    }

    public void setCF(Map<LocalDate, WeeklyConstraints> CF) {
        this.CF.clear();
        this.CF.putAll(CF);
    }
    public LocalDate findNextSunday(){
        LocalDate today = LocalDate.now();
        LocalDate nextSunday = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        return nextSunday;
    }

    public WeeklyConstraints get(LocalDate date){
        if (CF.containsKey(date)) {
            return CF.get(date);
        } else {
            throw new IllegalArgumentException("No constraints found for the specified date.");
        }
    }

    public void loadData() {
        LinkedList<ConstraintsDTO> cons = new LinkedList<>();
        try {
            cons = conRepo.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
        for (ConstraintsDTO dto : cons) {
            if (dto.getDate().isAfter(findNextSunday()) || dto.getDate().isEqual(findNextSunday())) {
                LocalDate sunday = findPreviousSunday(dto.getDate());
                if (!(CF.containsKey(sunday))) {
                    CF.put(sunday, new WeeklyConstraints());
                }
                WeeklyConstraints weekCons = CF.get(sunday);
                int dayNumber = getDay(dto.getDate());
                int timaAtDay = dto.getTimeAtDay();
                TimeSlot timeSlot = new TimeSlot(dayNumber, timaAtDay);
                Constraint constraint = new Constraint(dto.getEmpCanWork());
                if (!weekCons.getWeeklyConstraintsMap().containsKey(timeSlot)) {
                    weekCons.getWeeklyConstraintsMap().put(timeSlot, constraint);
                } else {
                    for (String empId : dto.getEmpCanWork()) {
                        weekCons.getWeeklyConstraintsMap().get(timeSlot).addEmployee(empId);
                    }
                }
            }
        }
    }

    private int getDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY:
                return 2;
            case TUESDAY:
                return 3;
            case WEDNESDAY:
                return 4;
            case THURSDAY:
                return 5;
            case FRIDAY:
                return 6;
            case SATURDAY:
                return 7;
            case SUNDAY:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid day of the week.");
        }
    }

    public LocalDate findPreviousSunday(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    public void deleteData() {
        try{
            conRepo.deleteAll();
            this.CF.clear();
            this.deadlineDateTime = LocalDate.now()
                    .with(TemporalAdjusters.next(DayOfWeek.THURSDAY))
                    .atTime(16, 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
