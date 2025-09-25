package Backend.DomainLayer.DomainLayerHR;
import java.util.*;

public class WeeklyConstraints {

    private Map<TimeSlot, Constraint> weeklyConstraintsMap;

    public WeeklyConstraints() {
        setNewWeek();
    }

    public Map<TimeSlot, Constraint> getWeeklyConstraintsMap() {
        return weeklyConstraintsMap;
    }

    public void setNewWeek(){
        // Initialize the constraints for the next week
        this.weeklyConstraintsMap = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            for (int j = 1; j <= 2; j++) {
                TimeSlot timeSlot = new TimeSlot(i, j);
                weeklyConstraintsMap.put(timeSlot, new Constraint());
            }
        }
    }

    public WeeklyConstraints copy() {
        WeeklyConstraints copy = new WeeklyConstraints();
        Map<TimeSlot, Constraint> newMap = new HashMap<>();
        // requires copy constructor
        for (Map.Entry<TimeSlot, Constraint> entry : getWeeklyConstraintsMap().entrySet()) {
            TimeSlot timeSlot = entry.getKey();
            Constraint constraint = entry.getValue();
            newMap.put(timeSlot, new Constraint(constraint)); // use copy constructor
        }
        copy.setWeeklyConstraintsMap(newMap);
        return copy;
    }

    private void setWeeklyConstraintsMap(Map<TimeSlot, Constraint> newMap) {
        this.weeklyConstraintsMap = newMap;
    }


    public void addEmpConstraints(String empId, WeeklyConstraints empConstraints) {
        for (Map.Entry<TimeSlot, Constraint> entry : empConstraints.getWeeklyConstraintsMap().entrySet()) {
            if (entry.getValue().contains(empId)) {
                TimeSlot timeSlot = entry.getKey();
                Constraint constraint = getWeeklyConstraintsMap().get(timeSlot);
                if (constraint == null) {
                    constraint = new Constraint();
                    getWeeklyConstraintsMap().put(timeSlot, constraint);
                }
                constraint.addEmployee(empId);
            }
        }
    }

    public String viewWeeklyConstraints() {
        StringBuilder viewWeekly = new StringBuilder();
        viewWeekly.append(" Weekly Constraints:\n");
        List<TimeSlot> sortedMap = new ArrayList<>(weeklyConstraintsMap.keySet());
        Collections.sort(sortedMap);
        for (TimeSlot timeSlot : sortedMap) {
            viewWeekly.append(timeSlot.toString())
                    .append(": ")
                    .append(weeklyConstraintsMap.get(timeSlot))
                    .append("\n");
        }
        return viewWeekly.toString();
    }


    public String toString(){
        return viewWeeklyConstraints();
    }


    public void replaceConstraint(TimeSlot timeSlot, Constraint constraint) {
        if (weeklyConstraintsMap.containsKey(timeSlot)) {
            weeklyConstraintsMap.remove(timeSlot);
            weeklyConstraintsMap.put(timeSlot, constraint);
        } else {
            throw new IllegalArgumentException("Time slot does not exist in the weekly constraints.");
        }
    }
}
