package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.ConstraintsDTO;
import Backend.DomainLayer.DomainLayerHR.Repos.ConstraintsRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.ConstraintsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Constraint {

    private List<String> empCanWork;
    private final ConstraintsRepository conRepo;

    public Constraint() {
        empCanWork = new ArrayList<>();
        this.conRepo = new ConstraintsRepoImpl();
    }

    public Constraint(List<String> empCanWork) {
        this.empCanWork = empCanWork;
        this.conRepo = new ConstraintsRepoImpl();
    }

    public Constraint(Constraint constraint) {
        List<String> copy = new ArrayList<>();
        for (String empId : constraint.getEmpCanWork()) {
            copy.add(empId);
        }
        this.empCanWork = copy;
        this.conRepo = new ConstraintsRepoImpl();
    }

    public List<String> getEmpCanWork() {
        return empCanWork;
    }

    public void addEmployee(String empId) {
        if (empCanWork.contains(empId)) {
            throw new IllegalArgumentException("Employee ID already exists in the constraint");
        }
        empCanWork.add(empId);
    }

    public void addEmployee(String empId, LocalDate date, int timeAtDay) {
        ConstraintsDTO constraintsDTO = new ConstraintsDTO(List.of(empId), timeAtDay, date);
        try {
            conRepo.insert(constraintsDTO);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Constraint insert failed");
        }
        addEmployee(empId);
    }

    public void removeEmployee(String empId) {
        if (!empCanWork.contains(empId)){
            throw new IllegalArgumentException("Employee ID does not exist in the constraint");
        }
        empCanWork.remove(empId);
    }

    @Override
    public String toString() {
        return "Available Employees: " + String.join(", ", empCanWork);
    }

    public boolean contains(String empId) {
        return empCanWork.contains(empId);
    }

    public void removeEmployeeFromData(String id, int timeAtDay, LocalDate date) {
        ConstraintsDTO constraintsDTO = conRepo.findByIdAndDate(id, timeAtDay, date);
        try {
            conRepo.delete(constraintsDTO);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Constraint delete failed");
        }
    }
}
