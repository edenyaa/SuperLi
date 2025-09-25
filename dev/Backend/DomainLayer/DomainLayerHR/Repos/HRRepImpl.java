package Backend.DomainLayer.DomainLayerHR.Repos;


import Backend.DTO.EmployeeDTO;
import Backend.DTO.FormerEmployeeDTO;
import Backend.DTO.LocationDTO;
import Backend.DTO.RoleDTO;
import Backend.DataAccessLayer.DAO.EmployeeDAO;
import Backend.DataAccessLayer.DAO.FormerEmployeeDAO;
import Backend.DataAccessLayer.Controllers.JdbcEmployeeDAO;
import Backend.DataAccessLayer.Controllers.JdbcFormerEmployeeDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class HRRepImpl implements HRRepsitory {
    private final EmployeeDAO empDAO;
    private final FormerEmployeeDAO formerEmpDAO;

    public HRRepImpl() {
        this.empDAO = new JdbcEmployeeDAO();
        this.formerEmpDAO = new JdbcFormerEmployeeDAO();
    }

    @Override
    public void updatePassword(String id, String password) {
        empDAO.updatePassword(id, password);
    }

    @Override
    public void updateFullName(String id, String name) {
        empDAO.updateFullName(id, name);
    }

    @Override
    public void updateSalary(String id, double salary) {
        empDAO.updateSalary(id, salary);
    }

    @Override
    public void updateBankAccount(String id, String account) {
        empDAO.updateBankAccount(id, account);
    }

    @Override
    public void updateMonthlyHours(String id, int hours) {
        empDAO.updateMonthlyHours(id, hours);
    }

    @Override
    public void updateLicenseTypes(String id, List<String> licenseTypes) {
        empDAO.updateLicenseTypes(id, licenseTypes);
    }

    @Override
    public void updateLocation(String id, LocationDTO location) {
        empDAO.updateLocation(id, location);
    }

    @Override
    public void updatePositions(String id, List<RoleDTO> roles) {
        empDAO.updatePositions(id, roles);
    }

    @Override
    public void moveToFormerEmployees(String id, EmployeeDTO employee) {
        FormerEmployeeDTO former = new FormerEmployeeDTO(
                employee.getId(),
                employee.getFullName(),
                employee.getStartDate(),
                LocalDate.now(),
                "Fired",
                employee.getLocation().getId()
        );
        empDAO.delete(employee);
        formerEmpDAO.insert(former);
    }

    @Override
    public LinkedList<EmployeeDTO> selectAll() throws SQLException {
        return empDAO.getAll();
    }

    @Override
    public void deleteAll() throws SQLException {
        empDAO.deleteAll();
    }

    @Override
    public void insert(EmployeeDTO item) throws SQLException {
        empDAO.insert(item);
    }

    @Override
    public void update(EmployeeDTO item) throws SQLException {
        empDAO.update(item);
    }

    @Override
    public void delete(EmployeeDTO item) throws SQLException {
        empDAO.delete(item);
    }

    @Override
    public EmployeeDTO select(String empId) throws SQLException {
        return empDAO.getBy(empId);
    }
}
