package Backend.DTO;

import Backend.DomainLayer.DomainLayerHR.EmployeeDL;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeDTO {
    private final String id;
    private final String fullName;
    private final LocalDate startDate;
    private final ArrayList<RoleDTO> positions;
    private final double salary;
    private final String bankAccount;
    private final int monthlyWorkHours;
    private final int hoursWorked;
    private final List<String> licenseTypes;
    private String password;
    private final LocationDTO location;

    public EmployeeDTO(String id, String password,String fullName, LocalDate startDate,
                       ArrayList<RoleDTO> positions, double salary, String bankAccount,
                       int monthlyWorkHours, int hoursWorked,
                       List<String> licenseTypes, LocationDTO location) {
        this.id = id;
        this.password = password;
        this.fullName = fullName;
        this.startDate = startDate;
        this.positions = positions;
        this.salary = salary;
        this.bankAccount = bankAccount;
        this.monthlyWorkHours = monthlyWorkHours;
        this.hoursWorked = hoursWorked;
        this.licenseTypes = licenseTypes;
        this.location = location;
    }

    public EmployeeDTO(EmployeeDL employee) {
        this.id = employee.getId();
        this.password = employee.getPassword();
        this.fullName = employee.getFullName();
        this.startDate = employee.getStartDate();
        this.salary = employee.getSalary();
        this.bankAccount = employee.getBankAccount();
        this.monthlyWorkHours = employee.getMonthlyHours();
        this.hoursWorked = employee.getHoursWorked();
        this.licenseTypes = employee.getLicenseType();

        this.positions = employee.getPositions().stream()
                .map(RoleDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));
        this.location = employee.getLocation();
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public LocalDate getStartDate() { return startDate; }
    public ArrayList<RoleDTO> getPositions() { return positions; }
    public double getSalary() { return salary; }
    public String getBankAccount() { return bankAccount; }
    public int getMonthlyWorkHours() { return monthlyWorkHours; }
    public int getHoursWorked() { return hoursWorked; }
    public List<String> getLicenseTypes() { return licenseTypes; }
    public String getPassword() { return password; }
    public LocationDTO getLocation() {
        return location;
    }

    public int getMonthlyHours() {
        return monthlyWorkHours;
    }

    public int getUsedHours() {
        return hoursWorked;
    }
}