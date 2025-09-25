package Backend;


import Backend.DTO.LocationDTO;
import Backend.DomainLayer.DomainLayerHR.EmployeeDL;
import Backend.DomainLayer.DomainLayerHR.Role;
import Backend.DomainLayer.DomainLayerHR.TimeSlot;
import Backend.ServiceLayer.SuperService;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDLTests {

    private EmployeeDL employee;

    @BeforeEach
    void setUp() {
        SuperService superService = new SuperService();
        superService.deleteData();
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Role(1, "Cashier"));
        LocationDTO location = new LocationDTO(1, "Beer Sheva", "Herzl 10", "050-1234567", "David Levi");

        employee = new EmployeeDL(
                "123456789",
                "1234",
                "John Doe",
                LocalDate.now(),
                roles,
                5000.0,
                "1234567890",
                160,
                0,

                List.of( "B"),
                location
        );
    }

    //================================================================================
    // Full-name validation tests
    //================================================================================

    @Test
    void validateFullName_WithValidName_ShouldReturnTrueAndUpdateName() {
        boolean result = employee.validateFullName("Jane Smith");
        assertTrue(result, "validateFullName() should return true for a valid alphabetic name");
        assertEquals("Jane Smith", employee.getFullName(),
                "After validation, the full name should be updated to the new value");
    }

    @Test
    void validateFullName_WithEmptyString_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employee.validateFullName("")
        );
        assertEquals("Full name cannot be empty.", ex.getMessage(),
                "Empty full name should trigger an IllegalArgumentException with correct message");
    }

    @Test
    void validateFullName_WithNumbersInName_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employee.validateFullName("Jane123")
        );
        assertEquals("Full name must contain only letters and spaces.", ex.getMessage(),
                "A name containing digits should trigger an IllegalArgumentException with correct message");
    }

    //================================================================================
    // Password validation tests
    //================================================================================

    @Test
    void validatePassword_WithValid4CharPassword_ShouldReturnTrueAndUpdatePassword() {
        boolean result = employee.validatePassword("5678");
        assertTrue(result, "validatePassword() should return true for a valid four-character password");
        assertEquals("5678", employee.getPassword(),
                "After validation, the password should be updated to the new value");
    }

    @Test
    void validatePassword_WithEmptyPassword_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employee.validatePassword("")
        );
        assertEquals("Password cannot be empty.", ex.getMessage(),
                "Empty password should trigger an IllegalArgumentException with correct message");
    }

    @Test
    void validatePassword_WithIncorrectLength_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employee.validatePassword("123")
        );
        assertEquals("Password can only be four letters", ex.getMessage(),
                "A password shorter than four characters should trigger IllegalArgumentException with correct message");
    }

    //================================================================================
    // Bank-account validation tests
    //================================================================================

    @Test
    void validateBankAccount_WithValid10Digits_ShouldReturnTrue() {
        boolean result = employee.validateBankAccount("1234567890");
        assertTrue(result, "validateBankAccount() should return true for exactly 10 digits");
    }

    @Test
    void validateBankAccount_WithTooShortAccount_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employee.validateBankAccount("123")
        );
        assertEquals("Bank account must be exactly 10 digits.", ex.getMessage(),
                "A bank account shorter than 10 digits should trigger IllegalArgumentException with correct message");
    }

    @Test
    void validateBankAccount_WithNonDigitCharacters_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employee.validateBankAccount("12345abcd0")
        );
        assertEquals("Bank account must contain only digits.", ex.getMessage(),
                "A bank account containing non-digit characters should trigger IllegalArgumentException with correct message");
    }

    //================================================================================
    // Update-constraints test
    //================================================================================

    @Test
    void updateConstraints_WithValidTimeSlots_ShouldSucceedAndInitializeNextWeekConstraints() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(1, 1));
        Response res = employee.updateConstraints(slots);
        assertNull(res.getErrorMsg(),
                "updateConstraints() with a non-empty list should not produce an error message");
        assertNotNull(employee.getNextWeekConstraints(),
                "After updating with valid slots, next-week constraints should be non-null");
    }

    @Test
    void updateConstraints_WithEmptyList_ShouldReturnErrorMessage() {
        Response res = employee.updateConstraints(Collections.emptyList());
        assertNotNull(res.getErrorMsg(),
                "updateConstraints() with an empty list should produce an error message");
    }

    //================================================================================
    // getDetails() and toString() behavior
    //================================================================================

    @Test
    void getDetails_ShouldReturnNonNullResponseContainingId() {
        Response res = employee.getDetails();
        assertNotNull(res.getReturnValue(),
                "getDetails() should return a non-null object containing employee info");
        String details = res.getReturnValue().toString();
        assertTrue(details.contains("ID: 123456789"),
                "The returned details string should contain the employee ID");
    }

    @Test
    void toString_ShouldIncludeEmployeeNameAndLabel() {
        String output = employee.toString();
        assertTrue(output.contains("Employee Details"),
                "toString() should start with or include 'Employee Details'");
        assertTrue(output.contains("John Doe"),
                "toString() should include the full name of the employee");
    }

    //================================================================================
    // Shift-history and current-week-shift tests
    //================================================================================

    @Test
    void viewCurrentWeekShifts_WhenNoShiftsAssigned_ShouldReturnErrorMessage() {
        Response res = employee.viewCurrentWeekShifts();
        assertNotNull(res.getErrorMsg(),
                "viewCurrentWeekShifts() with no assigned shifts should return an error message");
    }

    @Test
    void viewShiftHistory_AfterArchiving_ShouldReturnHistoryString() {
        employee.archiveCurrentShifts();
        Response res = employee.viewShiftHistory();
        assertNull(res.getErrorMsg(),
                "viewShiftHistory() after archiving should not produce an error message");
        String history = res.getReturnValue().toString().toLowerCase();
        assertTrue(history.contains("shift history"),
                "The return value string should include 'shift history'");
    }

    @Test
    void viewShiftHistory_WhenNoHistoryExists_ShouldReturnErrorMessage() {
        Response res = employee.viewShiftHistory();
        assertNotNull(res.getErrorMsg(),
                "viewShiftHistory() with no history should produce an error message");
    }
    // =================================================================================
    // Task 2: Additional tests
    // =================================================================================
    @Test
    void getId_ReturnsConstructorId() {
        String id = employee.getId();
        assertEquals("123456789", id,
                "getId() should return the same ID that was passed into the constructor");
    }

    @Test
    void getMonthlyHours_ReturnsConstructorValue() {
        int months = employee.getMonthlyHours();
        assertEquals(160, months,
                "getMonthlyHours() should return the value set in the constructor (160)");
    }

    @Test
    void getSalary_ReturnsConstructorSalary() {
        double salary = employee.getSalary();
        assertEquals(5000.0, salary, 0.001,
                "getSalary() should return the salary value set in the constructor (5000.0)");
    }

    @Test
    void getBankAccount_ReturnsConstructorAccount() {
        String bankAcc = employee.getBankAccount();
        assertEquals("1234567890", bankAcc,
                "getBankAccount() should return the bank account value set in the constructor");
    }

    @Test
    void getLicenseTypes_ReturnsExactConstructorList() {
        List<String> licenses = employee.getLicenseType();
        assertEquals(1, licenses.size(),
                "getLicenseTypes() should return exactly the list size passed to the constructor");
        assertEquals("B", licenses.get(0),
                "getLicenseTypes() should return the same license type passed to the constructor");
    }

    @Test
    void getPositions_ReturnsConstructorRoles() {
        List<Role> positions = employee.getPositions();
        assertEquals(1, positions.size(),
                "getPositions() should return exactly one Role as passed in the constructor");
        assertEquals(1, positions.get(0).getRoleId());
        assertEquals("Cashier", positions.get(0).getRoleName(),
                "getPositions() should return a Role object matching the constructor arguments");
    }

    @Test
    void getLocation_ReturnsConstructorLocationObject() {
        LocationDTO loc = employee.getLocation();
        assertNotNull(loc, "getLocation() must not return null");
        assertEquals(1, loc.getId(), "Location ID should match what was passed to the constructor");
        assertEquals("Beer Sheva", loc.getAreaName(),
                "Location area name should match what was passed in the constructor");
    }

    @Test
    void validateFullName_AllowsMultipleWordsAndSpaces() {
        boolean valid = employee.validateFullName("Anna Maria Lopez");
        assertTrue(valid,
                "validateFullName() should accept multi-word names with spaces only");
        assertEquals("Anna Maria Lopez", employee.getFullName(),
                "After validation, the full name should be updated correctly to include spaces");
    }

    @Test
    void validatePassword_WithMixedCharactersOfValidLength_ShouldReturnTrue() {
        boolean valid = employee.validatePassword("12ab");
        assertTrue(valid,
                "validatePassword() should accept any 4-character string, even if it contains digits");
        assertEquals("12ab", employee.getPassword(),
                "After validation, password should be updated to \"12ab\"");
    }

    @Test
    void toString_IncludesLicenseAndLocationInformation() {
        String output = employee.toString();
        assertTrue(output.contains("License Types"),
                "toString() should include a section for license types");
        assertTrue(output.contains("Location"),
                "toString() should include location information");
        assertTrue(output.contains("Beer Sheva"),
                "toString() should include the area-name from the LocationDTO");
    }
}

