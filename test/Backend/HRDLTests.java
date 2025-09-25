package Backend;

import Backend.DTO.LocationDTO;
import Backend.DomainLayer.DomainLayerHR.HRDL;
import Backend.DomainLayer.DomainLayerHR.Role;
import Backend.ServiceLayer.SuperService;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//methods we checked :1Hire/fire Employee 2. Add/Remove Shift 3. View Weekly Shifts 4. Publish Weekly Shift 5. View Weekly Constraints 6. Set Next Week Constraints 7. Show Employee 8. Show Shift Details
class HRDLTests {

    private final HRDL hr = HRDL.getInstance();

    @BeforeEach
    void setUp() {
        SuperService superService = new SuperService();
        superService.deleteData();
    }

    @Test
    void hireEmployee_WithValidData_ShouldSucceed() {
        LocationDTO loc = new LocationDTO(1, "Test Location", "123 Test St", "Test City", "12345");
        Response res = hr.hireEmployee(
                "123456782", "1234", "Test Employee",
                LocalDate.now(), new ArrayList<>(), 15000.0, "1234567890", 160, 0, List.of("B"), loc
        );
        assertNull(res.getErrorMsg());
        assertEquals("Employee hired successfully.", res.getReturnValue());
    }

    @Test
    void fireEmployee_ExistingEmployee_ShouldSucceed() {
        hr.hireEmployee(
                "123456783", "1234", "Employee To Fire",
                LocalDate.now(), new ArrayList<>(), 15000.0, "1234567891", 160, 0, List.of("B"),
                new LocationDTO(1, "Test Location", "123 Test St", "Test City", "12345")
        );
        Response res = hr.fireEmployee("123456783");
        assertNull(res.getErrorMsg());
        assertEquals("Employee fired successfully.", res.getReturnValue());
    }

    @Test
    void fireEmployee_NonExistingEmployee_ShouldReturnError() {
        Response res = hr.fireEmployee("000000000");
        assertNotNull(res.getErrorMsg());
    }

    @Test
    void addShift_NewTimeSlot_ShouldSucceed() {
        hr.removeShift(6, 2);
        Response res = hr.addShift(6, 2);
        assertNull(res.getErrorMsg());
        assertNotNull(res.getReturnValue());
    }

    @Test
    void addShift_ExistingTimeSlot_ShouldReturnError() {
        hr.addShift(5, 1);
        Response res = hr.addShift(5, 1);
        assertNotNull(res.getErrorMsg());
        assertTrue(res.getErrorMsg().toLowerCase().contains("already exists"));
    }

    @Test
    void removeShift_ExistingShift_ShouldSucceed() {
        hr.addShift(6, 1);
        Response res = hr.removeShift(6, 1);
        assertNull(res.getErrorMsg());
        assertNotNull(res.getReturnValue());
    }

    @Test
    void removeShift_NonExistingShift_ShouldReturnError() {
        Response res = hr.removeShift(7, 2);
        assertNotNull(res.getErrorMsg());
    }

    @Test
    void viewPublishedWeeklyShifts_ShouldReturnShifts() {
        Response res = hr.viewPublishedWeeklyShifts();
        assertNull(res.getErrorMsg());
        assertNotNull(res.getReturnValue());
    }

    @Test
    void viewWeeklyConstraints_WhenCalled_ShouldReturnValueOrError() {
        Response res = hr.viewWeeklyConstraints();
        assertNotNull(res.getReturnValue());
    }

    @Test
    void setNextWeekConstraints_BeforeDeadline_ShouldReturnError() {
        Response res = hr.setNextWeekConstraints();
        assertNull(res.getReturnValue(), "Expected null return value when failing before deadline");
        assertNotNull(res.getErrorMsg(), "Expected an error message when failing before deadline");
        assertEquals("Cannot collect constraints before the deadline.", res.getErrorMsg());
    }

    @Test
    void showEmployee_ExistingEmployee_ShouldReturnEmployee() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Role(1, "Cashier"));

        LocationDTO location = new LocationDTO(
                1, "Beer Sheva", "Herzl 10", "050-1234567", "David Levi"
        );

        hr.hireEmployee(
                "123456785", "1234", "Employee To Show",
                LocalDate.now(), roles, 15000.0, "1234567893", 160, 0, List.of("B"), location
        );

        Response res = hr.showEmployee("123456785");
        assertNull(res.getErrorMsg());
        assertNotNull(res.getReturnValue());
    }

    @Test
    void showShiftDetails_NonExistingShift_ShouldReturnError() {
        hr.removeShift(7, 2);
        Response res = hr.showShiftDetails(7, 2, false);
        if (res.getReturnValue() != null) {
            fail("Expected failure when shift not found, but got a shift.");
        }
        assertNotNull(res.getErrorMsg(), "Expected error message when shift not found.");
        assertTrue(res.getErrorMsg().toLowerCase().contains("shift not found"));
    }

    @Test
    void showShiftDetails_ExistingShift_ShouldReturnShift() {
        hr.addShift(5, 2);
        Response res = hr.showShiftDetails(5, 2, false);
        assertNull(res.getErrorMsg());
        assertNotNull(res.getReturnValue());
    }
}