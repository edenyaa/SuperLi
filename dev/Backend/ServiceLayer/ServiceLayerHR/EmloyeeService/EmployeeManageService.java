package Backend.ServiceLayer.ServiceLayerHR.EmloyeeService;

import Backend.DTO.EmployeeDTO;
import Backend.DTO.TimeSlotDTO;
import Backend.DomainLayer.DomainLayerHR.Repos.ControllerFacade;
import Backend.DomainLayer.DomainLayerHR.EmployeeDL;
import Backend.DomainLayer.DomainLayerHR.TimeSlot;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import Backend.DomainLayer.DomainLayerHR.LoginFacade;
import Backend.ServiceLayer.SuperService;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeManageService {

    private final EmployeeDL employee;
    private final SuperService superService;

    public EmployeeManageService(EmployeeDTO dto) {
        this.superService = new SuperService();
//        this.employee = controller.getEmpById2(dto.getId());
        this.employee = LoginFacade.getInstance().getEmployeeById2(dto.getId());
        employee.loadData(employee.getId());
    }

    public Response editBankAccount(String bankAccount) {
         try {
             superService.getController().updateBankAccount(employee.getId(), bankAccount);
             employee.validateBankAccount(bankAccount);
             return new Response("Bank account updated successfully");
        } catch (Exception e) {
             return new Response(e.getMessage());
         }
    }

    public Response editName(String fullName) {
        try {
            superService.getController().editFullName(employee.getId(), fullName);
            employee.validateFullName(fullName);
            return new Response("Full name updated successfully");
        } catch (IllegalArgumentException e) {
            return new Response(e.getMessage());
        }
    }

    public Response editPassword(String password) {
        try {
            superService.getController().editPassword(employee.getId(), password);
            employee.validatePassword(password);
            return new Response("Password updated successfully");
        } catch (IllegalArgumentException e) {
            return new Response(e.getMessage());
        }
    }



    public Response updateConstraints(List<TimeSlotDTO>  selectedSlots) {
        List<TimeSlot> timeSlots = selectedSlots.stream()
                .map(TimeSlotDTO::toDomain)
                .collect(Collectors.toList());
        return employee.updateConstraints(timeSlots);
    }

    public Response uploadConstraints() {
        return employee.uploadConstraints();
    }

    public Response showDetails() {
//        return employeeService.showDetails(employee);
        return employee.getDetails();
    }

    public Response showWeeklyShift() {
        return employee.viewCurrentWeekShifts();
    }

    public Response showShiftHistory() {
        return employee.viewShiftHistory();
    }

    public Response showEmployeeDetails(String empId) {
        try {
            EmployeeDL employeeDL = employee.showEmployeeDetails(empId);
            return new Response(employeeDL, null);
        }
        catch (Exception e){
            return new Response("Error displaying employee details: " + e.getMessage());
        }
    }
}
