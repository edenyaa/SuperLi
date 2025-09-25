package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.*;
import Backend.DTO.RoleDTO;
import Backend.DomainLayer.DomainLayerHR.Repos.FormerRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.FormerRepository;
import Backend.DomainLayer.DomainLayerHR.Repos.HRRepImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.HRRepsitory;
import Backend.DataAccessLayer.Mappers.EmployeeMapper;
import Backend.DataAccessLayer.Mappers.RoleMapper;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeFacade {
    private Map<String, EmployeeDL> EF;
    private Map<String, EmployeeDL> formerEmployee;

    private final HRRepsitory empReop;
    private final FormerRepository formerRepo;
    private static final EmployeeFacade instance = new EmployeeFacade();
    private final String DefaultHrID = "123456789";
    private final String DefaultSystemManID = "999999999";

    public static EmployeeFacade getInstance() {
        return instance;
    }

    private EmployeeFacade() {
        // private constructor to prevent instantiation
        EF = new HashMap<>();
        formerEmployee = new HashMap<>();
        this.empReop = new HRRepImpl();
        this.formerRepo = new FormerRepoImpl();
    }

    public void hireEmployee(String id, String password, String fullName, LocalDate startDate, ArrayList<Role> positions,
                             Double salary, String bankAccount, int monthlyHours, int hoursWorked, List<String> licenseType,
                             LocationDTO location) {
        EmployeeDL newEmployee = new EmployeeDL(id, password, fullName, startDate, positions, salary, bankAccount,
                monthlyHours, hoursWorked, licenseType, location);
        if (EF.containsKey(id) && !id.equals(DefaultHrID) && !id.equals(DefaultSystemManID)) {
            throw new IllegalArgumentException("Employee with this ID already exists.");
        }
        EF.putIfAbsent(id, newEmployee);
        EmployeeDTO dto = EmployeeMapper.toDTO(newEmployee);
        try {
            if (empReop.select(id) == null) {
                empReop.insert(dto);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void fireEmployee(String id) {
        if (!EF.containsKey(id)) {
            throw new IllegalArgumentException("Employee with this ID does not exist.");
        }
        EmployeeDL employee = EF.get(id);
        EmployeeDTO dto = EmployeeMapper.toDTO(employee);
//        EmployeeDAO dao = JdbcEmployeeDAO.getInstance();
//        dao.moveToFormerEmployees(id, dto);
        empReop.moveToFormerEmployees(id, dto);
        formerEmployee.put(id, EF.get(id));
        EF.remove(id);
    }

    public Response updatePassword(String id, String newPassword) {
        if (!EF.containsKey(id))
            return new Response("Employee with this ID does not exist.");
        if (newPassword.length() != 4)
            return new Response("Password must be exactly 4 characters.");
        try {
            empReop.updatePassword(id, newPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EF.get(id).setPassword(newPassword);
//        dao.updatePassword(id, newPassword);
        return new Response("Password updated successfully.",null);
    }

    public Response updateFullName(String id, String fullName) {
        if (!EF.containsKey(id))
            return new Response("Employee with this ID does not exist.");
        // Update the Db
//        EmployeeDAO dao = JdbcEmployeeDAO.getInstance();
//        dao.updateFullName(id, fullName);
        try {
            empReop.updateFullName(id, fullName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EF.get(id).setFullName(fullName);

        return new Response("Full name updated successfully.",null);
    }

    public Response updatePositions(String id, List<Role> roles) {
        if (!EF.containsKey(id))
            return new Response("Employee with this ID does not exist.");
        ArrayList<Role> positions = new ArrayList<>();
        for (Role role : roles) {
            positions.add(role);
        }
        List<RoleDTO> roleDTOs = roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
//        EmployeeDAO dao = JdbcEmployeeDAO.getInstance();
//        dao.updatePositions(id, roleDTOs);
        try {
            empReop.updatePositions(id, roleDTOs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EF.get(id).setPositions(positions);
        return new Response("Positions updated successfully.",null);
    }

    public Response updateSalary(String id, double salary) {
        if (!EF.containsKey(id))
            return new Response("Employee with this ID does not exist.");
        if (salary < 0)
            return new Response("Salary must be non-negative.");
//        EmployeeDAO dao = JdbcEmployeeDAO.getInstance();
//        dao.updateSalary(id, salary);
        try {
            empReop.updateSalary(id, salary);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EF.get(id).setSalary(salary);
        return new Response("Salary updated successfully.",null);
    }

    public Response updateBankAccount(String id, String account) {
        if (!EF.containsKey(id))
            return new Response("Employee with this ID does not exist.");
        if (!account.matches("\\d{10}"))
            return new Response("Bank account must be exactly 10 digits.");
//        EmployeeDAO dao = JdbcEmployeeDAO.getInstance();
//        dao.updateBankAccount(id, account);
        try {
            empReop.updateBankAccount(id, account);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EF.get(id).setBankAccount(account);
        return new Response("Bank account updated successfully.",null);
    }

    public Response updateMonthlyHours(String id, int hours) {
        if (!EF.containsKey(id))
            return new Response("Employee with this ID does not exist.");
        if (hours < 0 || hours > 360)
            return new Response("Monthly hours must be between 0 and 360.");
//        EmployeeDAO dao = JdbcEmployeeDAO.getInstance();
//        dao.updateMonthlyHours(id, hours);
        try {
            empReop.updateMonthlyHours(id, hours);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EF.get(id).setMonthlyHours(hours);
        return new Response("Monthly hours updated successfully.",null);
    }

    public Response updateLicenseType(String id, List<String> licenseTypes) {
        if (!EF.containsKey(id))
            return new Response("Employee with this ID does not exist.");
//        EmployeeDAO dao = JdbcEmployeeDAO.getInstance();
//        dao.updateLicenseTypes(id, licenseTypes);
        try {
            empReop.updateLicenseTypes(id, licenseTypes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EF.get(id).setLicenseType(licenseTypes);
        return new Response("License type updated successfully.",null);
    }
    public Response updateLocation(String id, LocationDTO location) {
        if (!EF.containsKey(id))
            return new Response("Employee with this ID does not exist.");
//        EmployeeDAO dao = JdbcEmployeeDAO.getInstance();
//        dao.updateLocation(id, location);
        try {
            empReop.updateLocation(id, location);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EF.get(id).setLocation(location);
        return new Response("Location updated successfully.",null);
    }

    public EmployeeDL showEmployee(String id){
        if (!EF.containsKey(id)) {
            throw new IllegalArgumentException("Employee with this ID does not exist.");
        }
        return EF.get(id);
    }

    public EmployeeDL showFormerEmployee(String id){
        if (!formerEmployee.containsKey(id)) {
            throw new IllegalArgumentException("Employee with this ID does not exist.");
        }
        return formerEmployee.get(id);
    }
    //public FormerEmployeeDTO showFormerEmployee(String id) {
    //    if (formerEmployee.containsKey(id)) {
    //        return formerEmployee.get(id);
    //    }
    //
    //    // אם לא בזיכרון - ננסה מה-DB
    //    FormerEmployeeDTO fromDB = JdbcFormerEmployeeDAO.getInstance().getById(id);
    //    if (fromDB != null) {
    //        formerEmployee.put(id, fromDB); // נטען אותו לזיכרון לשימוש עתידי
    //        return fromDB;
    //    }
    //
    //    throw new IllegalArgumentException("Former employee with this ID does not exist.");
    //}

    public List<EmployeeDL> getAllEmployees() {
        List<EmployeeDL> employees = new ArrayList<>();
        for (EmployeeDL emp : EF.values()) {
            employees.add(emp);
        }
        return employees;
    }

    public void loadData() {
        LinkedList<EmployeeDTO> employeesDTOs = new LinkedList<>();
        LinkedList<FormerEmployeeDTO> formerEmployeesDTOs = new LinkedList<>();
        try {
            employeesDTOs = empReop.selectAll();
            for (EmployeeDTO dto : employeesDTOs) {
                EmployeeDL emp = new EmployeeDL(dto, dto.getPassword());
                emp.loadData(emp.getId());
                EF.putIfAbsent(emp.getId(), emp);
            }
            formerEmployeesDTOs = formerRepo.selectAll();
            for (FormerEmployeeDTO dto : formerEmployeesDTOs) {
                if (!formerEmployee.containsKey(dto.getId())) {
                    ArrayList<Role> positions = new ArrayList<>();
                    EmployeeDL formerEmp = new EmployeeDL(dto.getId(), "1234", dto.getFullName(), dto.getStartDate(), positions, 8.0,
                            "0000000000", 182, 0, new ArrayList<>(), new LocationDTO(dto.getLocation(), "", "", "", ""));
                    formerEmployee.put(dto.getId(), formerEmp);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteData() {
        try{
            empReop.deleteAll();
            formerRepo.deleteAll();
            this.EF.clear();
            this.formerEmployee.clear();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        }
}
