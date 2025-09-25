package Backend.ServiceLayer.ServiceLayerHR;

import Backend.DTO.EmployeeDTO;
import Backend.DTO.LocationDTO;
import Backend.DomainLayer.DomainLayerHR.*;
import Backend.DomainLayer.DomainLayerHR.Repos.ControllerFacade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class LoginService {

    private final LoginFacade loginFacade = LoginFacade.getInstance();

    public Response login(String id, String password) {
        loginFacade.validLoginDetails(id, password);
        LoginDetails user = new LoginDetails(id, password);
        int role = loginFacade.login(user);
        if (role == -1) { throw new IllegalArgumentException("Invalid ID or password.");}
        EmployeeDL employee = getEmployee(user);
        EmployeeDTO dto = new EmployeeDTO(employee);
        return new Response(dto, "Login successful");
    }

    public EmployeeDL getEmployeeById(String id) {
        return loginFacade.getEmployeeById(id);
    }

    public boolean isHR(EmployeeDL employee) {
        return employee.hasRole("HrManager");
    }

    public EmployeeDL getEmployee(LoginDetails user) {
        EmployeeDL employeeDL = loginFacade.getEmployee(user);
        if (employeeDL == null) {
            throw new IllegalArgumentException("Employee not found");
        }
        return employeeDL;
    }
    public void createDefaultSystemManager() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Role("SystemManager"));

        EmployeeDL systemManager = new EmployeeDL(
                "999999999",
                "1234",
                "System Manager",
                LocalDate.now(),
                roles,
                15000.0,
                "0000000000",
                182,
                0,
                new ArrayList<>(),
                LocationDTO.GENERAL_LOCATION
        );

        HRDL.getInstance().hireEmployee(systemManager.getId(), systemManager.getPassword(), systemManager.getFullName(),
                systemManager.getStartDate(), systemManager.getPositions(), systemManager.getSalary(),
                systemManager.getBankAccount(), systemManager.getMonthlyHours(), systemManager.getHoursWorked(), systemManager.getLicenseType(), systemManager.getLocation());
    }


    public void loadData() {
        loginFacade.loadData();
    }
    public void deleteAllData() {
        loginFacade.deleteData();
    }

    public void createDefaultManagers() {
        createDefaultHR();
        createDefaultSystemManager();
        RolesFacade.getInstance().insertRolesToDatabase();
    }

    private void createDefaultHR() {
        Role hrRole = new Role(1, "HrManager");
        ArrayList<Role> pos = new ArrayList<>();
        pos.add(hrRole);
        HRDL.getInstance().hireEmployee("123456789", "1234", "Tanos", LocalDate.now(),
                pos, 50000.0, "1234567890", 160, 0, List.of(""), LocationDTO.GENERAL_LOCATION);
    }
}
