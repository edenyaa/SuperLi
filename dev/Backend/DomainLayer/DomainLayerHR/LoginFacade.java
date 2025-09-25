package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.EmployeeDTO;
import Backend.DomainLayer.DomainLayerHR.Repos.HRRepImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.HRRepsitory;
import Backend.DataAccessLayer.Mappers.EmployeeMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LoginFacade {
    private static final LoginFacade instance = new LoginFacade();
    private final HRRepsitory loginRepo;

    public static LoginFacade getInstance() {
        return instance;
    }

    private Map<LoginDetails, EmployeeDL> employeeMap;

    private LoginFacade() {
        this.employeeMap = new HashMap<>();
        this.loginRepo = new HRRepImpl();

    }

    public int register(LoginDetails user, EmployeeDL employee) {
        //if the worker already in thw system
        if (employeeMap.containsKey(user)) {
            System.out.println("Registration failed: User already exists.");
            return -1;
        }
        employeeMap.put(user, employee);
        return 1;
    }


    //if the connection failed return -1
    public int login(LoginDetails user) {
        EmployeeDL em = getEmployeeById(user.getId());
        if (em == null) {
            System.out.println("Login failed: User not found.");
            return -1;
        } else {
            if (em.getPassword() == null) {
                System.out.println("Login failed: Password is null.");
                return -1;
            }
            if (user.getPassword().equals(em.getPassword())) {
                System.out.println("Login successful: " + user.getId());
                if (isHr(user)) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                System.out.println("Login failed: Incorrect password.");
                return -1;
            }
        }

    }

    private boolean isHr(LoginDetails user) {
        EmployeeDL em = getEmployeeById(user.getId());
        ArrayList<Role> roles = em.getPositions();
        for (Role role : roles) {
            if (role.getRoleName().equalsIgnoreCase("HrManager")) {
                return true;
            }
        }
        return false;
    }


    public EmployeeDL getEmployee(LoginDetails user) {
        return getEmployeeById(user.getId());
    }

    public EmployeeDL getEmployeeById(String id) {
        for (Map.Entry<LoginDetails, EmployeeDL> entry : employeeMap.entrySet()) {
            if (entry.getValue().getId().equals(id)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public EmployeeDL getEmployeeById2(String id) {
        return EmployeeFacade.getInstance().showEmployee(id);
    }

    public void validLoginDetails(String id, String password) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (id.length() != 9) {
            throw new IllegalArgumentException("ID must be exactly 9 characters long");
        }
        if (password.length() != 4) {
            throw new IllegalArgumentException("Password must be exactly 4 characters long");
        }
        Integer.parseInt(password); // Check if password is a valid integer
    }

    public void loadData() {
        LinkedList<EmployeeDTO> allEmp = new  LinkedList<>();
        try {
            allEmp = loginRepo.selectAll();
            for (EmployeeDTO empDTO : allEmp) {
                LoginDetails loginDetails = new LoginDetails(empDTO.getId(), empDTO.getPassword());
                EmployeeDL employee = EmployeeMapper.fromDTO(empDTO);
                register(loginDetails, employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load registration data from database", e);
        }
    }
    public void deleteData() {
        try {
            loginRepo.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete registration data from database", e);
        }
    }

        public void emptyData() {
            this.employeeMap = new HashMap<>();
        }

}