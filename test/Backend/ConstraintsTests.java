package Backend;

import Backend.DomainLayer.DomainLayerHR.Constraint;
import Backend.ServiceLayer.SuperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//methods we checked: 1. addEmployee 2. removeEmployee 3. contains 4. toString
class ConstraintsTests {

    private Constraint constraint;

    @BeforeEach
    void setUp() {
        SuperService superService = new SuperService();
        superService.deleteData();
        constraint = new Constraint();
    }
    @Test
    void addEmployee_WhenCalledWithNewId_ShouldAppearInConstraintList() {
        constraint.addEmployee("123456789");
        assertTrue(constraint.contains("123456789"),
                "After adding a new employee ID, contains() should return true for that ID");
    }

    @Test
    void addEmployee_WhenCalledTwiceWithSameId_ShouldThrowDuplicateException() {
        constraint.addEmployee("123456789");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            constraint.addEmployee("123456789");
        });
        assertEquals("Employee ID already exists in the constraint", ex.getMessage(),
                "Adding the same ID twice should throw with the correct message");
    }

    @Test
    void removeEmployee_WhenExists_ShouldRemoveFromConstraintList() {
        constraint.addEmployee("123456789");
        constraint.removeEmployee("123456789");
        assertFalse(constraint.contains("123456789"),
                "After removing an existing employee ID, contains() should return false for that ID");
    }

    @Test
    void removeEmployee_WhenIdNotPresent_ShouldThrowNotFoundException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            constraint.removeEmployee("987654321");
        });
        assertEquals("Employee ID does not exist in the constraint", ex.getMessage(),
                "Removing an ID not present should throw with the correct message");
    }

    @Test
    void contains_WhenCalledOnEmptyConstraint_ShouldReturnFalse() {
        assertFalse(constraint.contains("000000000"),
                "contains() should return false for any ID when constraint is empty");
    }

    @Test
    void contains_WhenCalledAfterAdd_ShouldReturnTrueOnlyForThatId() {
        constraint.addEmployee("123456789");
        assertTrue(constraint.contains("123456789"),
                "contains() should return true for the ID just added");
        assertFalse(constraint.contains("987654321"),
                "contains() should return false for an ID not added");
    }

    @Test
    void toString_WhenConstraintEmpty_ShouldStartWithLabelOnly() {
        String output = constraint.toString();
        assertEquals("Available Employees: ", output,
                "toString() on an empty constraint should produce exactly the label and a space");
    }

    @Test
    void toString_WhenMultipleEmployeesAdded_ShouldListAllIds() {
        constraint.addEmployee("123456789");
        constraint.addEmployee("987654321");
        String output = constraint.toString();
        assertTrue(output.startsWith("Available Employees: "),
                "toString() should begin with the 'Available Employees: ' label");
        assertTrue(output.contains("123456789"),
                "toString() should contain the first added ID");
        assertTrue(output.contains("987654321"),
                "toString() should contain the second added ID");
    }
    //================================================================================
    // Task 2: Additional Tests
    //================================================================================

    @Test
    void constructor_WhenInitializedWithList_ShouldContainThoseIds() {
        List<String> initial = Arrays.asList("A", "B", "C");
        Constraint fromList = new Constraint(initial);
        assertTrue(fromList.contains("A") && fromList.contains("B") && fromList.contains("C"),
                "Constructor taking a List<String> should initialize internal list accordingly");
    }

    @Test
    void copyConstructor_WhenOriginalChanges_ShouldNotAffectCopy() {
        constraint.addEmployee("X");
        Constraint copy = new Constraint(constraint);
        constraint.addEmployee("Y");
        assertTrue(copy.contains("X"),
                "Copy should contain 'X' since it existed at construction time");
        assertFalse(copy.contains("Y"),
                "Copy should not contain 'Y' because it was added to original after copying");
    }
    @Test
    void contains_NullId_ShouldReturnTrueOnceNullAdded() {
        constraint.addEmployee((String) null);
        assertTrue(constraint.contains(null),
                "After adding null, contains(null) should return true");
    }

    @Test
    void getEmpCanWork_AfterAddingMultipleEmployees_ShouldReturnCorrectListSize() {
        constraint.addEmployee("111");
        constraint.addEmployee("222");
        assertEquals(2, constraint.getEmpCanWork().size(),
                "getEmpCanWork() should return a list of size 2 after adding two IDs");
        assertTrue(constraint.getEmpCanWork().containsAll(Arrays.asList("111", "222")),
                "getEmpCanWork() should contain both added IDs");
    }

    @Test
    void removeEmployeeWithNullId_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            constraint.removeEmployee(null);
        });
        assertEquals("Employee ID does not exist in the constraint", ex.getMessage(),
                "Calling removeEmployee(null) should throw IllegalArgumentException with correct message");
    }


    @Test
    void contains_WithNullId_ShouldReturnFalse() {
        assertFalse(constraint.contains(null),
                "contains(null) should not throw and must return false");
    }

    @Test
    void removeEmployeeFromData_WithExistingRecord_ShouldNotThrowAndKeepInMemory() {
        LocalDate date = LocalDate.now();
        constraint.addEmployee("T", date, 2);
        assertDoesNotThrow(() -> constraint.removeEmployeeFromData("T", 2, date),
                "removeEmployeeFromData() should not throw when record exists in DB");
        // In-memory list is not modified by removeEmployeeFromData()
        assertTrue(constraint.contains("T"),
                "After removeEmployeeFromData(), in-memory list should still contain 'T'");
    }

    @Test
    void removeEmployeeFromData_WithNoMatchingRecord_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            constraint.removeEmployeeFromData("NonExist", 5, LocalDate.now());
        }, "removeEmployeeFromData() with no matching DB record should throw NullPointerException");
    }
}