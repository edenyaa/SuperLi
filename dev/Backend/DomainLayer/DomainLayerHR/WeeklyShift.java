package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.ShiftDTO;
import Backend.DomainLayer.DomainLayerHR.Repos.ShiftRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.ShiftRepository;
import Backend.DataAccessLayer.Mappers.ShiftMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


public class WeeklyShift {
    private Map<TimeSlot, Shift> weeklyShiftMap;
    private Map<String, List<Integer>> employeeWorkDaysCount;
    private final ShiftRepository shiftRepo;

    public WeeklyShift(boolean saveToDB) {
        this.weeklyShiftMap = new HashMap<>();
        this.employeeWorkDaysCount = new HashMap<>();
        this.shiftRepo = new ShiftRepoImpl();
        setNewWeek(saveToDB);
    }

    public void setNewWeek(boolean saveToDB) {
        // Reset the weekly shift map for a new week
        try {
            employeeWorkDaysCount.clear();
            weeklyShiftMap.clear();
            LocalDate sunday = findNextSunday();
            LocalDate nextSunday = sunday.plusDays(7);
            for (int i = 1; i <= 7; i++) {
                for (int j = 1; j <= 2; j++) {
                    TimeSlot timeSlot = new TimeSlot(i, j);
                    Shift shift = shiftRepo.findOrCreate(nextSunday.plusDays(i - 1), timeSlot.getTimeAtDay());
                    if (saveToDB) {
                        shift = shiftRepo.findOrCreate(nextSunday.plusDays(i - 1), timeSlot.getTimeAtDay());
                        HashMap<Role, Integer> reqRoles = new HashMap<>();
                        reqRoles.put(new Role("ShiftManager"), 1);
                        HashMap<Role, List<String>> assignedEmp = new HashMap<>();
                        shift.setReqRoles(reqRoles);
                        shift.setAssignedEmp(assignedEmp);
                    }
                    weeklyShiftMap.putIfAbsent(timeSlot, shift);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDate findNextSunday() {
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }


    private LocalDate findNextSaturday() {
        return LocalDate.now().plusDays(7).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }

    public void addEmployeeWorkDay(String empId, int day) {
        if (!(checkIfEmployeeExists(empId, day)))
            return;
        else {
            List<Integer> empDays = employeeWorkDaysCount.get(empId);
            if (empDays.contains(day)) return;
            else {
                empDays.add(day);
            }
        }
    }

    private boolean checkIfEmployeeExists(String empId, int day) {
        if (!(employeeWorkDaysCount).containsKey(empId)) {
            ArrayList<Integer> empDays = new ArrayList<>();
            empDays.add(day);
            employeeWorkDaysCount.put(empId, empDays);
            return false;
        }
        return true;
    }

    public Shift addShift(TimeSlot ts) {
        if (weeklyShiftMap.containsKey(ts)) {
            throw new IllegalArgumentException("Shift already exists for this time slot.");
        }
        Shift newShift = new Shift(ts);
        ShiftDTO shiftDTO = ShiftMapper.toDTO(newShift);
        int shiftId = shiftRepo.insertAndGetID(shiftDTO);
        newShift.setId(shiftId);
        ShiftDTO shiftDTO2 = ShiftMapper.toDTO(newShift);
        shiftRepo.insertShiftEmp(shiftDTO2);
        Map<Role, Integer> reqRolesMap = newShift.getReqRolesMap();
        for (Map.Entry<Role, Integer> entry : reqRolesMap.entrySet()) {
            shiftRepo.addRole(shiftDTO2.getId(), entry.getKey().getRoleId(), entry.getValue());
        }
        weeklyShiftMap.put(ts, newShift);
        return newShift;
    }

    public Shift addShiftFromData(TimeSlot ts, Shift shift) {
        if (weeklyShiftMap.containsKey(ts)) {
            throw new IllegalArgumentException("Shift already exists for this time slot.");
        }
        weeklyShiftMap.put(ts, shift);
        return shift;
    }

    public Shift removeShift(TimeSlot ts) {
        if (!weeklyShiftMap.containsKey(ts)) {
            throw new IllegalArgumentException("Shift does not exist for time slot: " + ts);
        }
        Shift shift = weeklyShiftMap.get(ts);
        ShiftDTO shiftDTO = ShiftMapper.toDTO(shift);
        try {
            shiftRepo.delete(shiftDTO);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return weeklyShiftMap.remove(ts);
    }


    public String viewWeeklyShift(){
        if (weeklyShiftMap.isEmpty()){
            throw new IllegalArgumentException("No shifts available for this week.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Weekly Shifts ===\n");
        sb.append("\n");
        List<TimeSlot> sortedTimeSlots = new ArrayList<>(weeklyShiftMap.keySet());
        Collections.sort(sortedTimeSlots);
        for (TimeSlot ts : sortedTimeSlots) {
            sb.append(ts.toString())
                    .append(": ")
                    .append(weeklyShiftMap.get(ts))
                    .append("\n")
                    .append("\n");
        }
        return sb.toString();
    }


    public boolean allShiftsAreAssigned() {
        for (Map.Entry<TimeSlot, Shift> entry : weeklyShiftMap.entrySet()) {
            TimeSlot ts = entry.getKey();
            Shift shift = entry.getValue();
            if (shift.isRolesUnassigned()) return false;
        }
        return true;
    }

    public Map<TimeSlot, Shift> getWeeklyShiftMap(){
        return weeklyShiftMap;
    }

    public Shift getShift(TimeSlot ts) {
        return weeklyShiftMap.get(ts);
    }

    public String toString(){
        return viewWeeklyShift();
    }


    public void contains(Shift shift) {
        TimeSlot ts = shift.getTimeSlot();
        if (!weeklyShiftMap.containsKey(ts)) {
            throw new IllegalArgumentException("Shift does not exist for this time slot.");
        }
        Shift existingShift = weeklyShiftMap.get(ts);
        if (!(existingShift.equals(shift))) {
            throw new IllegalArgumentException("Shift does not exist for this time slot.");
        }
    }

    public void checkIfCanAssignEmployee(String empId) {
        if (employeeWorkDaysCount.containsKey(empId)) {
            List<Integer> empDays = employeeWorkDaysCount.get(empId);
            if (empDays.size() >= 6) {
                throw new IllegalArgumentException("This Employee has reached max work days for this week");
            }
        }
    }

    public void replaceShift(TimeSlot ts, Shift newShift) {
        if (!weeklyShiftMap.containsKey(ts)) {
            throw new IllegalArgumentException("Shift does not exist for time slot: " + ts);
        }
        weeklyShiftMap.remove(ts);
        weeklyShiftMap.put(ts, newShift);
    }
}
