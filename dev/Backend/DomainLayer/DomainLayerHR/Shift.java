package Backend.DomainLayer.DomainLayerHR;

import Backend.DomainLayer.DomainLayerHR.Repos.ShiftRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.ShiftRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shift implements Comparable<Shift> {

//    private static int nextId = 1;
    private int id;
    private LocalDate date;
    private TimeSlot timeSlot;
    private HashMap<Role, List<String>> assignedEmployees;
    private HashMap<Role, Integer> requiredRoles;
    private boolean rolesUnassigned;
    private final ShiftRepository shiftRepo;

    public Shift(TimeSlot timeSlot) {
        this.id = 0;
        this.timeSlot = timeSlot;
        this.date = calculateDateFromTimeSlot(timeSlot);
        this.assignedEmployees = new HashMap<>();
        this.requiredRoles = new HashMap<>();
        this.rolesUnassigned = true;
        this.requiredRoles.put(new Role("ShiftManager"), 1);
        this.assignedEmployees.put(new Role("ShiftManager"), new ArrayList<String>());
        this.shiftRepo = new ShiftRepoImpl();
    }

    public Shift(int id, LocalDate date, TimeSlot timeSlot, HashMap<Role, List<String>> assignedEmp, HashMap<Role, Integer> reqRoles){
        this.id = id;
        this.date = date;
        this.timeSlot = timeSlot;
        if (assignedEmp != null) {
            this.assignedEmployees = assignedEmp;
        }
        else assignedEmployees = new HashMap<>();
        if (reqRoles != null) {
            requiredRoles = reqRoles;
        }
        else requiredRoles = new HashMap<>();
        this.rolesUnassigned = true;
        this.shiftRepo = new ShiftRepoImpl();
        setRolesUnassigned();
    }

    public Shift(int id, LocalDate date, TimeSlot timeSlot){
        this.id = id;
        this.date = date;
        this.timeSlot = timeSlot;
        this.assignedEmployees = new HashMap<>();
        this.requiredRoles = new HashMap<>();
        requiredRoles.put(new Role("ShiftManager"), 1);
        this.rolesUnassigned = false;
        this.shiftRepo = new ShiftRepoImpl();
        setRolesUnassigned();
    }

    public void addEmployee(String empId, Role role, boolean saveToDb) {
        if (!(requiredRoles.containsKey(role))) {
            throw new IllegalArgumentException("role does not exist in shift");
        }
        if (assignedEmployees.get(role) != null) {
            if (requiredRoles.get(role) == assignedEmployees.get(role).size()) {
                throw new IllegalArgumentException("this role is already filled");
            }
        }
        else assignedEmployees.put(role, new ArrayList<>());
        checkIfEmployeeAlreadyAssigned(empId);
        if (saveToDb) {
            if (getId() == 0) {
                throw new IllegalStateException("Shifts must be saved before assigned");
            }
            shiftRepo.assignEmployee(getId(), empId, role.getRoleName());
        }
        assignedEmployees.get(role).add(empId);
        setRolesUnassigned();
    }

    private void checkIfEmployeeAlreadyAssigned(String empId) {
        for (Map.Entry<Role, List<String>> entry : assignedEmployees.entrySet()) {
            if (entry.getValue().contains(empId))
                throw new IllegalArgumentException("Employee already exist in this shift");
        }
    }

    public void removeEmployee(String empId) {
        try {
            for (Map.Entry<Role, List<String>> entry : assignedEmployees.entrySet()) {
                List<String> empList = entry.getValue();
                if (empList.contains(empId)) {
                    empList.remove(empId);
                    shiftRepo.removeEmployee(getId(), empId);
                    setRolesUnassigned();
                    return;
                }
            }
            throw new IllegalArgumentException("employee does not exist in the shift");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addRole(Role role, int numOfEmployees, boolean saveToDb) {
        if (requiredRoles.containsKey(role) && saveToDb) throw new IllegalArgumentException("shift already contains this role");
        if (numOfEmployees <= 0) throw new IllegalArgumentException("number of employees must be greater than 0");
        try {
            if (saveToDb) shiftRepo.addRole(getId(), role.getRoleId(), numOfEmployees);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        requiredRoles.put(role, numOfEmployees);
        assignedEmployees.put(role, new ArrayList<String>());
        setRolesUnassigned();
    }

    public void removeRole(Role role) {
        if (role.getRoleName().equalsIgnoreCase("ShiftManager")) {
            throw new IllegalArgumentException("cannot remove shift manager role");
        }
        if (!requiredRoles.containsKey(role)) {
            throw new IllegalArgumentException("role does not exist in shift");
        }
        try {
            shiftRepo.removeRole(getId(), role.getRoleId());
            shiftRepo.removeRoleAssignedEmployees(getId(), role.getRoleName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        requiredRoles.remove(role);
        assignedEmployees.remove(role);
        setRolesUnassigned();
    }

    private LocalDate calculateDateFromTimeSlot(TimeSlot ts) {
        LocalDate nextSunday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        return nextSunday.plusDays(ts.getDayNumber() - 1);
    }

    //getters and setters
    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public List<String> getAssignedEmployees() {
        if (assignedEmployees.isEmpty()) {
            throw new IllegalArgumentException("no employees assigned to this shift");
        }
        List<String> empList = new ArrayList<>();
        for (Map.Entry<Role, List<String>> entry : assignedEmployees.entrySet()) {
            List<String> empIds = entry.getValue();
            empList.addAll(empIds);
        }
        return empList;
    }

    public Map<Role, Integer> getReqRolesMap() {
        return requiredRoles;
    }

    public List<Role> getRequiredRoles() {
        List<Role> shiftRoles = new ArrayList<>();
        for (Role role : requiredRoles.keySet()) {
            shiftRoles.add(role);
        }
        return shiftRoles;
    }

    public boolean isRolesUnassigned() {
        return rolesUnassigned;
    }

    public void setRolesUnassigned() {
        for (Map.Entry<Role, Integer> entry : requiredRoles.entrySet()) {
            Role role = entry.getKey();
            if (!(assignedEmployees.containsKey(role))) {
                this.rolesUnassigned = true;
                return;
            }
            List<String> empList = assignedEmployees.get(role);
            if (empList.size() < entry.getValue()) {
                this.rolesUnassigned = true;
                return;
            }
        }
        this.rolesUnassigned = false;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", date=" + date +
                ", timeSlot=" + timeSlot +
                ", assignedEmployees=" + assignedEmployees +
                '}';
    }

    @Override
    public int compareTo(Shift other) {
        if (this.date != other.date) {
            return this.date.compareTo(other.date);
        }
        return this.timeSlot.compareTo(other.timeSlot);
    }

    public boolean hasShiftManager() {
        LoginFacade loginFacade = LoginFacade.getInstance();
        if (assignedEmployees.containsKey(new Role("ShiftManager"))) {
            List<String> empIds = assignedEmployees.get(new Role("ShiftManager"));
            for (String empId : empIds) {
                EmployeeDL employee = loginFacade.getEmployeeById(empId);
                if (employee != null && employee.hasRole("ShiftManager")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateNumOfEmployeesForRole(Role role, int numOfEmployees) {
        if (numOfEmployees <= 0) {
            throw new IllegalArgumentException("number of employees must be greater than 0");
        }
        if (!requiredRoles.containsKey(role)) {
            throw new IllegalArgumentException("role does not exist in shift");
        }
        if (assignedEmployees.get(role) != null) {
            if (numOfEmployees < assignedEmployees.get(role).size()) {
                throw new IllegalArgumentException("cannot reduce number of employees for this role because there are more assigned employees, remove employees first");
            }
        }
        if (role.getRoleName().equalsIgnoreCase("ShiftManager")) {
            if (numOfEmployees < 1) throw new IllegalArgumentException("shift must have at leats one shift manager");
        }
        try {
            shiftRepo.updateRole(getId(), role.getRoleId(), numOfEmployees);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        requiredRoles.put(role, numOfEmployees);
    }

    public List<String> getAssignedEmployeesForRole(Role role) {
        return assignedEmployees.getOrDefault(role, new ArrayList<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Shift shift = (Shift) obj;
        return shift.timeSlot.equals(timeSlot);
    }

    public void setId(int shiftId) {
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive integer.");
        }
        this.id = shiftId;
    }

    public void setDate(LocalDate localDate) {
        if (localDate == null) {
            return;
        }
        this.date = localDate;
    }

    public void setReqRoles(HashMap<Role, Integer> reqRoles) {
        this.requiredRoles = reqRoles;
    }

    public void setAssignedEmp(HashMap<Role, List<String>> assignedEmployees) {
        this.assignedEmployees = assignedEmployees;
    }
}

