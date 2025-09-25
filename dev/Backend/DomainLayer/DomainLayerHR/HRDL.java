package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.ConstraintsDTO;
import Backend.DTO.EmployeeDTO;
import Backend.DTO.InboxMessageDTO;
import Backend.DTO.LocationDTO;
//import Backend.DataAccessLayer.JdbcConstraintsDAO;
//import Backend.DataAccessLayer.JdbcHRInboxDAO;
import Backend.DataAccessLayer.Mappers.EmployeeMapper;
import Backend.DomainLayer.DomainLayerHR.Repos.ConstraintsRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.ConstraintsRepository;
import Backend.DomainLayer.DomainLayerHR.Repos.HRInboxRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.HRInboxRepository;
import Backend.DataAccessLayer.Mappers.ConstraintMapper;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class HRDL extends EmployeeDL {
    EmployeeFacade employeeFacade = EmployeeFacade.getInstance();
    WeeklyShiftFacade weeklyShiftFacade = WeeklyShiftFacade.getInstance();
    ConstraintsFacade constraintsFacade = ConstraintsFacade.getInstance();
    WeeklyConstraints WC;
    private final List<InboxMessage> inbox = new ArrayList<>();
    private int readMessagesCount = 0;
    private int unreadMessagesCount = 0;
    private final ConstraintsRepository conRepo;
    private final HRInboxRepository hrInboxRepo;


    // singleton instance of HR
    private static final HRDL instance = new HRDL("123456789", "1234", "Tanos", LocalDate.now(),
           new ArrayList<Role>(), 50000.0, "1234567890", 160, 0, List.of(""));


    public static HRDL getInstance() {
        return instance;
    }


    private HRDL(String id, String password, String fullName, LocalDate startDate, ArrayList<Role> positions,
                 Double salary, String bankAccount, int monthlyHours, int hoursWorked, List<String> licenseType) {
        super(id, password, fullName, startDate, positions, salary, bankAccount, monthlyHours, hoursWorked, licenseType, LocationDTO.GENERAL_LOCATION);
        ArrayList<Role> hrRole = new ArrayList<>();
        hrRole.add(new Role("HrManager"));
        setPositions(hrRole);
        WC = new WeeklyConstraints();
        this.conRepo = new ConstraintsRepoImpl();
        this.hrInboxRepo = new HRInboxRepoImpl();
        hireEmployee(id, password, fullName, startDate, this.getPositions(), salary, bankAccount,
                monthlyHours, hoursWorked, licenseType, LocationDTO.GENERAL_LOCATION);
    }

    public Response hireEmployee(String id, String password, String fullName, LocalDate startDate, ArrayList<Role> positions,
                                 Double salary, String bankAccount, int monthlyHours, int hoursWorked, List<String> licenseType,
                                 LocationDTO location) {
        try {
            employeeFacade.hireEmployee(id, password, fullName, startDate, positions, salary, bankAccount,
                    monthlyHours, hoursWorked, licenseType, location);
            EmployeeDL actualEmployee = employeeFacade.showEmployee(id);
            LoginDetails loginDetails = new LoginDetails(id, password);
//            int roleId = getLowestRoleId(positions);
            if (LoginFacade.getInstance().getEmployeeById(loginDetails.getId()) == null) {
                LoginFacade.getInstance().register(loginDetails, actualEmployee);
            }
            return new Response("Employee hired successfully.", null);
        } catch (Exception e) {
            return new Response("Error hiring employee: " + e.getMessage());
        }
    }



    private int getLowestRoleId(ArrayList<Role> positions) {
        int maxRoleId = Integer.MAX_VALUE;
        for (Role role : positions) {
            if (role.getRoleId() < maxRoleId) {
                maxRoleId = role.getRoleId();
            }
        }
        return maxRoleId;
    }

    public Response fireEmployee(String id) {
        try {
            employeeFacade.fireEmployee(id);
            return new Response("Employee fired successfully.", null);
        } catch (Exception e) {
            return new Response("Error firing employee: " + e.getMessage());
        }
    }
    public Response updatePassword(String id, String password) {
        try {
            return employeeFacade.updatePassword(id, password);
        } catch (Exception e) {
            return new Response("Error updating password: " + e.getMessage());
        }
    }

    public Response updateFullName(String id, String name) {
        try {
            return employeeFacade.updateFullName(id, name);
        } catch (Exception e) {
            return new Response("Error updating full name: " + e.getMessage());
        }
    }

    public Response updatePositions(String id, List<Role> roles) {
        try {
            return employeeFacade.updatePositions(id, roles);
        } catch (Exception e) {
            return new Response("Error updating positions: " + e.getMessage());
        }
    }

    public Response updateSalary(String id, double salary) {
        try {
            return employeeFacade.updateSalary(id, salary);
        } catch (Exception e) {
            return new Response("Error updating salary: " + e.getMessage());
        }
    }

    public Response updateBankAccount(String id, String account) {
        try {
            return employeeFacade.updateBankAccount(id, account);
        } catch (Exception e) {
            return new Response("Error updating bank account: " + e.getMessage());
        }
    }


    public Response updateMonthlyHours(String id, int hours) {
        try {
            return employeeFacade.updateMonthlyHours(id, hours);
        } catch (Exception e) {
            return new Response("Error updating monthly hours: " + e.getMessage());
        }
    }

    public Response updateLicenseType(String id, List<String> licenseTypes) {
        try {
            return employeeFacade.updateLicenseType(id, licenseTypes);
        } catch (Exception e) {
            return new Response("Error updating license type: " + e.getMessage());
        }
    }
    public Response updateLocation(String id, LocationDTO location) {
        try {
            return employeeFacade.updateLocation(id, location);
        } catch (Exception e) {
            return new Response("Error updating location: " + e.getMessage());
        }
    }

    public Response showEmployee(String id) {
        try {
            EmployeeDL emp = employeeFacade.showEmployee(id);
            return new Response(emp);
        } catch (Exception e) {
            return new Response("Error showing employee: " + e.getMessage());
        }
    }
    public Response showFormerEmployee(String id){
        try {
            EmployeeDL emp = employeeFacade.showFormerEmployee(id);
            if (emp == null) {
                throw new IllegalArgumentException("Employee not found.");
            }
            return new Response(emp);
        } catch (Exception e) {
            return new Response("Error showing former employee: " + e.getMessage());
        }

    }


    public Response viewPublishedWeeklyShifts() {
        try {
            String result = weeklyShiftFacade.viewPublishedWeeklyShifts();
            return new Response(result, null);
        } catch (Exception e) {
            return new Response("Failed to view weekly shifts: " + e.getMessage());
        }
    }

    public Response publishWeeklyShift() {
        WeeklyShift nextWeek = weeklyShiftFacade.getNextWeeklyShift();
        if (!nextWeek.allShiftsAreAssigned()) {
            return new Response("Cannot publish: Some required roles are unassigned in one or more shifts.");
        }
        //  Archive current shifts for each employee
        for (EmployeeDL emp : employeeFacade.getAllEmployees()) {
            emp.archiveCurrentShifts(); // move current → history
            emp.setWeeklyShift(nextWeek);
            showEmployee(emp.getId());
        }
        // Step 3: Reset constraints for the upcoming week
        WC.setNewWeek();
        weeklyShiftFacade.setNewWeek();
        return new Response(nextWeek,null);
    }

    public Response assignEmployeeToShift(int day, int time, String empId, Role role, boolean published) {
        try {
            Shift shift = weeklyShiftFacade.getShift(day, time, published);
            if (!canEmpWork(day, time, empId)) {
                System.out.println("Employee cannot work on this shift due to constraints.");
            }
            WeeklyShift nextWeek = weeklyShiftFacade.getNextWeeklyShift();
            nextWeek.checkIfCanAssignEmployee(empId);
            checkIfEmployeeHasRole(empId, role);
            shift.addEmployee(empId, role, true);
            nextWeek.addEmployeeWorkDay(empId, day);
            return new Response(shift,null);
        } catch (Exception e) {
            return new Response("Error assigning employee: " + e.getMessage());
        }
    }

    private void checkIfEmployeeHasRole(String empId, Role role) {
        EmployeeDL emp = employeeFacade.showEmployee(empId);
        if (emp == null) {
            throw new IllegalArgumentException("Employee not found.");
        }
        if (!emp.getPositions().contains(role)) {
            throw new IllegalArgumentException("Employee does not have the required role.");
        }
    }

    private boolean canEmpWork(int day, int time, String empId) {
        TimeSlot ts = new TimeSlot(day, time);
        Constraint constraint = WC.getWeeklyConstraintsMap().get(ts);
        return constraint.contains(empId);
    }

    public Response removeEmployeeFromShift(int day, int time, String empId, boolean published) {
        try {
            Shift shift = weeklyShiftFacade.getShift(day, time, published);
            shift.removeEmployee(empId);
            return new Response(shift, null);
        } catch (Exception e) {
            return new Response("Error removing employee: " + e.getMessage());
        }
    }

    public Response addRoleToShift(int day, int time, Role role, int numOfEmployees, boolean published) {
        try {
            Shift shift = weeklyShiftFacade.getShift(day, time, published);
            shift.addRole(role, numOfEmployees, true);
            return new Response(shift,null);
        } catch (Exception e) {
            return new Response("Error adding role: " + e.getMessage());
        }
    }

    public Response removeRoleFromShift(int day, int time, Role role, boolean published) {
        try {
            Shift shift = weeklyShiftFacade.getShift(day, time, published);
            shift.removeRole(role);
            return new Response(shift,null);
        } catch (Exception e) {
            return new Response("Error removing role: " + e.getMessage());
        }
    }

    public Response showShiftDetails(int day, int time, boolean published) {
        try {
            Shift shift = weeklyShiftFacade.getShift(day, time, published);
            if (shift == null) {
                throw new IllegalArgumentException("Shift not found for the given day and time.");
            }
            return new Response(shift, null);
        } catch (Exception e) {
            return new Response("Error showing shift details: " + e.getMessage());
        }
    }

    public Response addShift(int dayToAssign, int shiftTimeToAssign) {
        try {
            Shift shift = weeklyShiftFacade.addShift(dayToAssign, shiftTimeToAssign);
            return new Response(shift, null);
        } catch (Exception e) {
            return new Response("Error adding shift: " + e.getMessage());
        }
    }

    public Response viewWeeklyConstraints() {
        try {
            if (WC.getWeeklyConstraintsMap().isEmpty()) {
                throw new IllegalStateException("No weekly constraints available.");
            }
            String result = WC.viewWeeklyConstraints();
            return new Response(result, null);
        } catch (Exception e) {
            return new Response("Error viewing weekly constraints: " + e.getMessage());
        }
    }


    public Response removeShift(int day, int time) {
        try {
            Shift removed = weeklyShiftFacade.removeShift(day, time);
            return new Response(removed,null);
        } catch (Exception e) {
            return new Response("Failed to remove shift: " + e.getMessage());
        }
    }


    public Response setNextWeekConstraints() {
        try {
            if (constraintsFacade.isBeforeDeadline()) {
                return new Response("Cannot collect constraints before the deadline.");
            }
            boolean addedAny = false;
            System.out.println("===== Starting to collect constraints from employees =====");
            for (EmployeeDL employeeDL : employeeFacade.getAllEmployees()) {
                if (employeeDL.getNextWeekConstraints() != null && employeeDL.hasUploadedConstraints()) {
                    System.out.println("Adding constraints for employee: " + employeeDL.getId());
                    WC.addEmpConstraints(employeeDL.getId(), employeeDL.getNextWeekConstraints());
                    employeeDL.setCanUploadConstraints(true);
                    resetEmpConstraints(employeeDL);
                    addedAny = true;
                }
            }
            if (!addedAny) {
                return new Response("No constraints were uploaded by employees.");
            }
            constraintsFacade.addNextWeekConstraints(WC.copy());
            constraintsFacade.ConstraintsIsCollected();
            constraintsFacade.resetAfterCollection();
            return new Response("The " + WC.copy(), null);
        } catch (Exception e) {
            return new Response("Error setting weekly constraints: " + e.getMessage());
        }
    }

    private Response resetEmpConstraints(EmployeeDL employeeDL) {
        try {
            WeeklyConstraints newConstraints = new WeeklyConstraints();
            employeeDL.setConstraints(newConstraints);
            return new Response(newConstraints,null);
        } catch (Exception e) {
            return new Response("Error resetting constraints for " + employeeDL.getId() + ": " + e.getMessage());
        }
    }
    public WeeklyConstraints getWeeklyConstraints() {
        return WC;
    }


    public String viewUnpublishedWeeklyShifts() {
       return weeklyShiftFacade.viewUnPublishedWeeklyShifts();
    }

    public ConstraintsFacade getConstraintsFacade() {
        return constraintsFacade;
    }


    public WeeklyShiftFacade getWeeklyShiftFacade() {
        return weeklyShiftFacade;
    }

    public Response updateNumOfEmployeesForRole(int day, int time, Role role, int numOfEmployees, boolean published) {
        try {
            Shift shift = weeklyShiftFacade.getShift(day, time, published);
            if (shift == null) {
                throw new IllegalArgumentException("Shift not found for the given day and time.");
            }
            shift.updateNumOfEmployeesForRole(role, numOfEmployees);
            return new Response(shift, null);
        } catch (Exception e) {
            return new Response("Error updating number of employees for role: " + e.getMessage());
        }
    }

    public LocalDate getPublishedWeekStart() {
        try {
            return weeklyShiftFacade.getPublishedWeekStart();
        } catch (Exception e) {
            return null;
        }
    }

    public void checkIfEmployeeExists(String empId) {
        employeeFacade.showEmployee(empId);
    }

    public int getMonthlyWorkHours() {
        return this.getMonthlyHours();
    }
    public int getHoursWorked() {
        return this.getHoursWorked();
    }
    public List<String> getLicenseTypes() {
        return this.getLicenseType();
    }

    public Response showAllEmployees() {
        try {
            List<EmployeeDL> employees = employeeFacade.getAllEmployees();
            List<EmployeeDTO> employeesToShow = new ArrayList<>();
            for (EmployeeDL emp : employees) {
                employeesToShow.add(EmployeeMapper.toDTO(emp));
            }
            return new Response(employeesToShow, null);
        }
        catch (Exception e) {
            return new Response("Error showing all employees: " + e.getMessage());
        }
    }

    public void loadData(){
        LinkedList<InboxMessageDTO> allMessages = new  LinkedList<>();
        try {
            allMessages = hrInboxRepo.selectAll();
            for (InboxMessageDTO dto : allMessages) {
                InboxMessage msg = new InboxMessage(dto.getId(), dto.getSender(), dto.getMessage());
                if (dto.isRead()) {
                    msg.markAsRead();
                    readMessagesCount++;
                } else {
                    unreadMessagesCount++;
                }
                inbox.add(msg);
            }
            LinkedList<ConstraintsDTO> allConstraints = conRepo.selectAll();
            WeeklyConstraints ws = new WeeklyConstraints();
            for (ConstraintsDTO cons : allConstraints) {
                if (cons.getDate().isAfter(findNextSunday()) || cons.getDate().isEqual(findNextSunday())) {
                    int timeAtDay = cons.getTimeAtDay();
                    int dayNumber = getDay(cons.getDate());
                    TimeSlot timeSlot = new TimeSlot(dayNumber, timeAtDay);
                    ws.replaceConstraint(timeSlot, ConstraintMapper.fromDTO(cons));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading inbox data: " + e.getMessage());
            throw new RuntimeException("Error loading inbox data: " + e.getMessage());
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

    public void deleteData(){
        try{
            hrInboxRepo.deleteAll();
            this.inbox.clear();
            readMessagesCount = 0;
            unreadMessagesCount = 0;
            WC = new  WeeklyConstraints();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting data: " + e.getMessage());
        }
    }


    //INNER CLASS OF HRDL OF INBOX
    private static class InboxMessage {
        private final int id;
        private final String sender;
        private final String content;
        private boolean read;

        public InboxMessage(int id, String sender, String content) {
            this.id = id;
            this.sender = sender;
            this.content = content;
            this.read = false;
        }
        public int getId() {
            return id;
        }
        public void markAsRead() {
            this.read = true;
        }

        public boolean isRead() {
            return read;
        }

        @Override
        public String toString() {
            return "From: " + sender + "\nMessage: " + content + "\nStatus: " + (read ? "Read" : "Unread") + "\n";
        }
    }

    public void addMessageFromModule(String sender, String message) {
        InboxMessageDTO dto = new InboxMessageDTO(0, sender, message, false);
        int generatedId;
        try {
            generatedId = hrInboxRepo.insertAndGetId(dto);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting inbox message: " + e.getMessage());
        }
        inbox.add(new InboxMessage(generatedId, sender, message));
        unreadMessagesCount++;
    }

    public void clearAllMessages() {
        try {
            hrInboxRepo.deleteAll();
            inbox.clear();
            readMessagesCount = 0;
            unreadMessagesCount = 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error clearing all messages: " + e.getMessage());
        }
    }

    public Response viewMessages(boolean onlyUnread, boolean onlyRead, int howMany) {
        try {
            if (howMany <= 0) {
                throw new IllegalArgumentException("You must choose to read at least one message.");
            }
            int availableMessages;
            if (onlyUnread) {
                availableMessages = unreadMessagesCount;
            } else if (onlyRead) {
                availableMessages = readMessagesCount;
            } else {
                availableMessages = readMessagesCount + unreadMessagesCount;
            }
            if (availableMessages == 0) {
                return new Response("No messages found for the selected filter.");
            }
            if (howMany > availableMessages) {
                return new Response("You asked to read " + howMany +
                        " messages, but only " + availableMessages +
                        " are available in this category.");
            }
            StringBuilder result = new StringBuilder();
            int count = 0;
            for (InboxMessage msg : inbox) {
                if (onlyUnread && msg.isRead()) continue;
                if (onlyRead && !msg.isRead()) continue;
                result.append("Message ").append(count + 1).append(":\n");
                result.append(msg.toString()).append("---\n");
                if (!msg.isRead()) {
                    msg.markAsRead();
                    unreadMessagesCount--;
                    readMessagesCount++;
                    hrInboxRepo.markAsRead(msg.getId());
                }
                count++;
                if (count >= howMany) break;
            }
            return new Response(result.toString(), null);
        } catch (IllegalArgumentException e) {
            return new Response("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return new Response("Error while reading messages: " + e.getMessage());
        }
    }

    public Response getInboxStats() {
        try {
            int total = readMessagesCount + unreadMessagesCount;
            String result = "Total Messages: " + total +
                    "\nUnread Messages: " + unreadMessagesCount +
                    "\nRead Messages: " + readMessagesCount;
            return new Response(result, null);
        } catch (Exception e) {
            return new Response("Failed to retrieve inbox statistics: " + e.getMessage());
        }
    }
    public List<InboxMessage> getInbox() {
        return inbox;
    }
    public int getReadMessagesCount() {
        return readMessagesCount;
    }
    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }
    public int getTotalMessagesCount() {
        return readMessagesCount + unreadMessagesCount;
    }
}
