package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.*;
import Backend.DomainLayer.DomainLayerHR.Repos.ConstraintsRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.ConstraintsRepository;
import Backend.DomainLayer.DomainLayerHR.Repos.ShiftRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.ShiftRepository;
import Backend.DataAccessLayer.Mappers.RoleMapper;
import Backend.DataAccessLayer.Mappers.ShiftMapper;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;


public class EmployeeDL {
    private String id;
    private String password;
    private String fullName;
    private LocalDate startDate;
    private ArrayList<Role> positions;
    private Double salary;
    private String bankAccount;
    private int monthlyHours;
    private int hoursWorked;
    private List<String> licenseType;
    private WeeklyConstraints NextWeekConstraints; ////////
    private WeeklyShift currentWeekShifts;
    private final List<WeeklyShift> shiftHistory = new ArrayList<>();
    protected boolean uploadedConstraints = false;
    private boolean canUploadConstraints = true;
    private LocationDTO location;
    private final ConstraintsRepository conRepo;
    private final ShiftRepository shiftRepo;
    boolean loaded;


    public EmployeeDL(String id, String password, String fullName, LocalDate startDate, ArrayList<Role> positions,
                      Double salary, String bankAccount, int monthlyHours, int hoursWorked, List<String> licenseType,LocationDTO location) {
        validateID(id);
        validatePassword(password);
        validateFullName(fullName);
        validateBankAccount(bankAccount);
        validateMonthlyHours(monthlyHours);
        validateSalary(salary);
        this.startDate = startDate;
        this.positions = positions;
        this.hoursWorked = hoursWorked;
        this.licenseType = licenseType;
        this.NextWeekConstraints = new WeeklyConstraints();
        this.currentWeekShifts = new WeeklyShift(false);
        boolean isHighRole = hasRole("HrManager") || hasRole("SystemManager");
        this.location = isHighRole ? LocationDTO.GENERAL_LOCATION : location;
        this.conRepo = new ConstraintsRepoImpl();
        this.shiftRepo = new ShiftRepoImpl();
        this.loaded = false;
    }

    public EmployeeDL(EmployeeDTO dto, String password) {
        this.id = dto.getId();
        this.password = password;
        this.fullName = dto.getFullName();
        this.startDate = dto.getStartDate();
        this.salary = dto.getSalary();
        this.bankAccount = dto.getBankAccount();
        this.monthlyHours = dto.getMonthlyWorkHours();
        this.hoursWorked = dto.getHoursWorked();
        this.licenseType = dto.getLicenseTypes();
        this.location = dto.getLocation();
        this.positions = getPositionsFromDTO(dto.getPositions());
        this.NextWeekConstraints = new WeeklyConstraints();
        this.conRepo = new ConstraintsRepoImpl();
        this.shiftRepo = new ShiftRepoImpl();
    }

    private ArrayList<Role> getPositionsFromDTO(ArrayList<RoleDTO> positions) {
        ArrayList<Role> roles = new ArrayList<>();
        for (RoleDTO roleDTO : positions) {
            roles.add(RoleMapper.fromDTO(roleDTO));
        }
        return roles;
    }

    private void validateMonthlyHours(int monthlyHours) {
        if (monthlyHours <= 0) {
            throw new IllegalArgumentException("Monthly hours must be a positive number.");
        }
        setMonthlyHours(monthlyHours);
    }

    private void validateSalary(Double salary) {
        if (salary == null || salary <= 0) {
            throw new IllegalArgumentException("Salary must be a positive number.");
        }
        setSalary(salary);
    }

    private boolean validateID(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty.");
        }
        if (id.length() != 9) {
            throw new IllegalArgumentException("ID must be exactly 9 characters long.");
        }
        if (!id.matches("[0-9]+")) {
            throw new IllegalArgumentException("ID must contain only digits.");
        }
        setID(id);
        return true;
    }

    private void setID(String id) {
        this.id = id;
    }

    public boolean validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if(!fullName.matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("Full name must contain only letters and spaces.");
        }
        setFullName(fullName);
        return true;
    }

    public boolean validatePassword(String password)
    {
        if(password==null||password.isEmpty())
        {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if(password.length()!=4)
        {
            throw new IllegalArgumentException("Password can only be four letters");
        }
        setPassword(password);
        return true;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean validateBankAccount(String bankAccount) {
        if (bankAccount == null || bankAccount.length() != 10) {
            throw new IllegalArgumentException("Bank account must be exactly 10 digits.");
        }
        if (!bankAccount.matches("\\d+")) {
            throw new IllegalArgumentException("Bank account must contain only digits.");
        }
        setBankAccount(bankAccount);
        return true;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public Response updateConstraints(List<TimeSlot> selectedSlots) {
        if (selectedSlots == null || selectedSlots.isEmpty()) {
            return new Response("Selected slots cannot be null or empty");
        }
        WeeklyConstraints MyConstraints = new WeeklyConstraints();
        try {
            for (Map.Entry<TimeSlot, Constraint> entry : getNextWeekConstraints().getWeeklyConstraintsMap().entrySet()) {
                TimeSlot ts = entry.getKey();
                int timeAtDay = ts.getTimeAtDay();
                Constraint con = entry.getValue();
                LocalDate date = getTimeSlotDate(ts);
                if (con.contains(getId())) con.removeEmployeeFromData(getId(), ts.getTimeAtDay(), date);
            }
            for (TimeSlot slot : selectedSlots) {
                Constraint constraint = MyConstraints.getWeeklyConstraintsMap().get(slot);
                if (constraint != null) {
                    int timeAtDay = slot.getTimeAtDay();
                    LocalDate date = getTimeSlotDate(slot);
                    constraint.addEmployee(getId(), date, timeAtDay);
                }
            }
            this.setConstraints(MyConstraints);
            return new Response("your" + MyConstraints, null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Response("Failed to update constraints: " + e.getMessage());
        }
    }

    public LocalDate getTimeSlotDate(TimeSlot slot) {
        int dayNumber = slot.getDayNumber();
        LocalDate today = LocalDate.now();
        int todayDay = today.getDayOfWeek().getValue();
        int todayIso = (todayDay == 7) ? 1 : todayDay + 1;
        int daysToAdd = (dayNumber - todayDay) + 6;
        return LocalDate.now().plusDays(daysToAdd);
    }

    public Response uploadConstraints() {
        ConstraintsFacade CF = ConstraintsFacade.getInstance();
        CF.updateCollectionStateIfNeeded();
        if(!CF.canUploadConstraints()){
            this.setCanUploadConstraints(false);
            return new Response("Constraints can not be uploaded after the deadline.");
        }
        if (this.NextWeekConstraints == null || this.NextWeekConstraints.getWeeklyConstraintsMap().isEmpty()) {
            return new Response("This worker has no constraints to upload.");
        }
        this.uploadedConstraints = true;
        return new Response("Constraints uploaded successfully.", null);
    }


    public Response getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(id).append("\n");
        details.append("Full Name: ").append(fullName).append("\n");
        details.append("Start Date: ").append(startDate).append("\n");
        details.append("Bank Account: ").append(bankAccount).append("\n");
        details.append("Salary: ").append(salary).append("\n");
        details.append("Monthly Hours: ").append(monthlyHours).append("\n");
        details.append("Hours Worked: ").append(hoursWorked).append("\n");
        details.append("Roles: ");
        for (Role r : positions) {
            details.append(r.getRoleName()).append(" ");
        }
        details.append("\n");
        details.append("License Types: ");
        for (String license : licenseType) {
            details.append(license).append(" ");
        }
        details.append("\n");
        details.append("Location: ").append(location != null ? location.toString() : "None").append("\n");
        return new Response(details.toString(), null);
    }

    public void setConstraints(WeeklyConstraints constraints) {
        if (!canUploadConstraints){
            throw new IllegalStateException("Next Constraints can not been uploaded yet.");
        }
        this.NextWeekConstraints = constraints;
    }

    @Override
    public String toString() {
        return "Employee Details:\n" +
                "ID: " + id + "\n" +
                "Full Name: " + fullName + "\n" +
                "Start Date: " + startDate + "\n" +
                "Bank Account: " + bankAccount + "\n" +
                "Salary: " + salary + "\n" +
                "Monthly Hours: " + monthlyHours + "\n" +
                "Hours Worked: " + hoursWorked + "\n" +
                "Roles: " + positions + "\n" +
                "License Types: " + licenseType +"\n" +
                "Location: " + (location != null ? location.toString() : "None");
    }

    public WeeklyConstraints getNextWeekConstraints() {
        return NextWeekConstraints;
    }

    public WeeklyShift getCurrentWeekShifts() {
        return currentWeekShifts;
    }

    public List<WeeklyShift> getShiftHistory() {
        return shiftHistory;
    }

    public void archiveCurrentShifts() {
        shiftHistory.add(currentWeekShifts);
        currentWeekShifts = new WeeklyShift(false);
    }

    public Response viewCurrentWeekShifts() {
        try {
            if (!(this.currentWeekShifts.allShiftsAreAssigned())) {
                throw new NullPointerException(" no shifts this week yet.");
            }
            return new Response(currentWeekShifts.toString(),null);
        } catch (NullPointerException e) {
            return new Response(e.getMessage());
        }
    }

    public Response viewShiftHistory() {
        try {
            if (shiftHistory.isEmpty())
                throw new NullPointerException(" no shifts history exist.");
            StringBuilder sb = new StringBuilder("shift History:\n");
            for (WeeklyShift s : shiftHistory) {
                sb.append(s.toString()).append("\n--------------------\n");
            }
            return new Response(sb.toString(),null);
        } catch (NullPointerException e) {
            return new Response(e.getMessage());
        }
    }

        public ArrayList<Role> getPositions() {
        return positions;
    }
    public void setPositions(ArrayList<Role> positions) {
        this.positions = positions;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public Double getSalary() {
        return salary;
    }
    public void setSalary(Double salary) {
        this.salary = salary;
    }
    public String getBankAccount() {
        return bankAccount;
    }

    public int getMonthlyHours() {
        return monthlyHours;
    }

    public void setMonthlyHours(int monthlyHours) {
        this.monthlyHours = monthlyHours;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public boolean hasUploadedConstraints() {
        return uploadedConstraints;
    }

    protected void setCanUploadConstraints(boolean b) {
        this.canUploadConstraints = b;
    }

    public boolean hasRole(String roleName) {
        for (Role role : this.getPositions()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(List<String> licenseType) {
        this.licenseType = licenseType;
    }
    public void addLicenseType(String license) {
        if (licenseType == null) {
            licenseType = new ArrayList<>();
        }
        licenseType.add(license);
    }
    public EmployeeDTO toDTO() {
        ArrayList<RoleDTO> roleDTOs = this.positions.stream()
                .map(RoleDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));

        return new EmployeeDTO(
                this.id,
                this.password,
                this.fullName,
                this.startDate,
                roleDTOs,
                this.salary,
                this.bankAccount,
                this.monthlyHours,
                this.hoursWorked,
                this.licenseType,
                this.location
        );
    }


    protected void setWeeklyShift(WeeklyShift nextWeek) {
        this.currentWeekShifts = nextWeek;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public EmployeeDL showEmployeeDetails(String empId) {
        Role smRole = new Role("ShiftManager");
        Role hrRole = new Role("HrManager");
        if (!(this.getPositions().contains(smRole)) && !(this.getPositions().contains(hrRole))) {
            throw new IllegalArgumentException("Only Shift Managers can view employee details.");
        }
        if (empId == null || empId.isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty.");
        }
        EmployeeDL emp = EmployeeFacade.getInstance().showEmployee(empId);
        return emp;
    }

    public void loadData(String empId) {
        try {
            if (loaded) {
                return; // Data already loaded
            }
            LinkedList<ConstraintsDTO> empConstraints = conRepo.findEmpCons(empId);
            WeeklyConstraints weeklyConstraints = new WeeklyConstraints();
            for (ConstraintsDTO cons : empConstraints) {
                if (cons.getDate().isAfter(findNextSunday()) || cons.getDate().isEqual(findNextSunday())) {
                    int dayNumber = getDay(cons.getDate());
                    int timeAtDay = cons.getTimeAtDay();
                    TimeSlot timeSlot = new TimeSlot(dayNumber, timeAtDay);
                    weeklyConstraints.getWeeklyConstraintsMap().get(timeSlot).addEmployee(empId);
                }
            }
            this.NextWeekConstraints = weeklyConstraints;
            WeeklyShift currentWeekShifts = new WeeklyShift(false);
            HashMap<LocalDate, WeeklyShift> shiftHistory = new HashMap<>();
            LinkedList<ShiftDTO> shiftDTOS = shiftRepo.selectAll();
            for (ShiftDTO shiftDTO : shiftDTOS) {
                LocalDate shiftPrevSunday = findPreviousSunday(shiftDTO.getDate());
                if (shiftDTO.getDate().isBefore(findPreviousSunday(LocalDate.now()))) {
                    try {
                        WeeklyShiftFacade.getInstance().get(shiftPrevSunday);
                        if (!shiftHistory.containsKey(shiftPrevSunday)) {
                            shiftHistory.put(shiftPrevSunday, new WeeklyShift(false));
                        }
                        WeeklyShift weeklyShift = shiftHistory.get(shiftPrevSunday);
                        TimeSlot timeSlot = new TimeSlot(shiftDTO.getTimeSlot().getDayNumber(), shiftDTO.getTimeSlot().getTimeAtDay());
                        weeklyShift.replaceShift(timeSlot, ShiftMapper.fromDTO(shiftDTO));
                    } catch (Exception e) {
                        System.out.println("exception was occoured retreving Weekly Shift starting at: " + shiftPrevSunday);
                    }
                } else {
                    int timeAtDay = shiftDTO.getTimeSlot().getTimeAtDay();
                    int dayNumber = getDay(shiftDTO.getDate());
                    TimeSlot timeSlot = new TimeSlot(dayNumber, timeAtDay);
                    currentWeekShifts.replaceShift(timeSlot, ShiftMapper.fromDTO(shiftDTO));
                }
            }
            setWeeklyShift(currentWeekShifts);
            this.shiftHistory.addAll(shiftHistory.values());
            this.loaded = true;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private LocalDate findPreviousSunday(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
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

    public LocalDate findNextSunday(){
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
    }


}
