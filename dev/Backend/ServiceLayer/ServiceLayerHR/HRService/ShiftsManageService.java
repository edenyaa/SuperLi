package Backend.ServiceLayer.ServiceLayerHR.HRService;

import Backend.DTO.RoleDTO;
import Backend.DomainLayer.DomainLayerHR.ConstraintsFacade;
import Backend.DomainLayer.DomainLayerHR.HRDL;
import Backend.DomainLayer.DomainLayerHR.Role;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class ShiftsManageService {

    private final HRDL hr = HRDL.getInstance();

    public Response viewPublishedWeeklyShifts() {
        return hr.viewPublishedWeeklyShifts();
    }

    public Response publishWeeklyShift() {
        return hr.publishWeeklyShift();
    }

    public Response viewWeeklyConstraints() {
        return hr.viewWeeklyConstraints();
    }

    public Response viewUnpublishedWeeklyShifts() {
        String result = hr.viewUnpublishedWeeklyShifts();
        if (result == null) {
            return new Response("No unpublished shifts available.");
        }
        return new Response(result);
    }

    public Response setNextWeekConstraints() {
        return hr.setNextWeekConstraints();
    }

    public Response changeConstraintsDeadline(DayOfWeek day, LocalTime time) {
        try {

            ConstraintsFacade.getInstance().setDeadline(day, time);
            return new Response("Deadline updated to: " + day + " at " + time);
        } catch (Exception e) {
            return new Response("Error updating deadline: " + e.getMessage());
        }
    }

    public Response addShift(int day, int time) {
        return hr.addShift(day, time);
    }

    public Response removeShift(int day, int time) {
        return hr.removeShift(day, time);
    }

    public Response showShiftDetails(int day, int time, boolean published) {
        return hr.showShiftDetails(day, time, published);
    }

    public Response assignEmployeeToShift(int day, int time, String empId, RoleDTO roleDTO, boolean published) {
        Role role = new Role(roleDTO.getRoleId(), roleDTO.getRoleName());
        return hr.assignEmployeeToShift(day, time, empId, role, published);
    }

    public Response removeEmployeeFromShift(int day, int time, String empId, boolean published) {
        return hr.removeEmployeeFromShift(day, time, empId, published);
    }

    public Response addRoleToShift(int day, int time, RoleDTO roleDTO, int numOfEmployees, boolean published) {
        Role role = new Role(roleDTO.getRoleId(), roleDTO.getRoleName());
        return hr.addRoleToShift(day, time, role, numOfEmployees, published);
    }

    public Response removeRoleFromShift(int day, int time, RoleDTO roleDTO, boolean published) {
        Role role = new Role(roleDTO.getRoleId(), roleDTO.getRoleName());
        return hr.removeRoleFromShift(day, time, role, published);
    }

    public Response updateNumOfEmployeesForRole(int day, int time, RoleDTO roleDTO, int numOfEmployees, boolean published) {
        Role role = new Role(roleDTO.getRoleId(), roleDTO.getRoleName());
        return hr.updateNumOfEmployeesForRole(day, time, role, numOfEmployees, published);
    }

    public LocalDate getPublishedWeekStart() {
        return hr.getPublishedWeekStart();
    }

    public void checkIfEmployeeExists(String empId) {
        hr.checkIfEmployeeExists(empId);
    }
}