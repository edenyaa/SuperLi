package Backend;

import Backend.DomainLayer.DomainLayerHR.ConstraintsFacade;
import Backend.DomainLayer.DomainLayerHR.WeeklyConstraints;
import Backend.ServiceLayer.SuperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
//method we checked:1. addNextWeekConstraints 2. viewPrevWeeklyConstraints 3. setDeadline 4. isBeforeDeadline 5. isAfterDeadline 6. canUploadConstraints 7. resetAfterCollection 8. ConstraintsIsCollected 9. findNextSunday 10. get

class ConstraintsFacadeTests {

    private ConstraintsFacade constraintsFacade;

    @BeforeEach
    void setUp() {
        SuperService superService = new SuperService();
        superService.deleteData();
        constraintsFacade = ConstraintsFacade.getInstance();
        constraintsFacade.setCF(new HashMap<>()); // Clear all constraints
    }

    @Test
    void addNextWeekConstraints_AddsConstraintsForNextSunday() {
        WeeklyConstraints wc = new WeeklyConstraints();
        constraintsFacade.addNextWeekConstraints(wc);
        LocalDate nextSunday = constraintsFacade.findNextSunday();
        assertEquals(wc, constraintsFacade.getCF().get(nextSunday));
    }

    @Test
    void viewPrevWeeklyConstraints_ThrowsWhenNoConstraintsExist() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            constraintsFacade.viewPrevWeeklyConstraints(1);
        });
        assertTrue(thrown.getMessage().contains("No constraints found for the specified week."));
    }

    @Test
    void setDeadline_GetDeadline_ReturnsCorrectFormattedString() {
        constraintsFacade.setDeadline(DayOfWeek.MONDAY, LocalTime.of(10, 30));
        String deadlineStr = constraintsFacade.getDeadline();
        assertTrue(deadlineStr.contains("Deadline is set to"));
        assertTrue(deadlineStr.contains("10:30"));
    }

    @Test
    void isBeforeDeadline_ReturnsTrueWhenCurrentTimeBeforeDeadline() {
        constraintsFacade.setDeadline(DayOfWeek.SUNDAY, LocalTime.of(23, 59));
        assertTrue(constraintsFacade.isBeforeDeadline());
    }

    @Test
    void isAfterDeadline_ReturnsFalseWhenNotDeadlineDayOrHandlesSameDayCorrectly() {
        constraintsFacade.setDeadline(DayOfWeek.MONDAY, LocalTime.of(0, 1));
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            assertTrue(constraintsFacade.isAfterDeadline() || constraintsFacade.isBeforeDeadline());
        } else {
            assertFalse(constraintsFacade.isAfterDeadline());
        }
    }

    @Test
    void canUploadConstraints_ReturnsTrueWhenBeforeDeadline() {
        constraintsFacade.setDeadline(DayOfWeek.SUNDAY, LocalTime.of(23, 59));
        assertTrue(constraintsFacade.canUploadConstraints());
    }

    @Test
    void resetAfterCollection_UpdatesDeadlineToNextCycle() {
        constraintsFacade.resetAfterCollection();
        String oldDeadline = constraintsFacade.getDeadline();
        constraintsFacade.resetAfterCollection();
        String newDeadline = constraintsFacade.getDeadline();
        assertNotEquals(oldDeadline, newDeadline);
    }


    @Test
    void findNextSunday_ReturnsUpcomingSundayAfterToday() {
        LocalDate nextSunday = constraintsFacade.findNextSunday();
        assertEquals(DayOfWeek.SUNDAY, nextSunday.getDayOfWeek());
        assertTrue(nextSunday.isAfter(LocalDate.now()));
    }

    @Test
    void get_ReturnsWeeklyConstraintsWhenExists() {
        WeeklyConstraints wc = new WeeklyConstraints();
        LocalDate nextSunday = constraintsFacade.findNextSunday();
        constraintsFacade.setCF(new HashMap<>());
        constraintsFacade.getCF().put(nextSunday, wc);
        WeeklyConstraints found = constraintsFacade.get(nextSunday);
        assertEquals(wc, found);
    }

    @Test
    void get_ThrowsExceptionWhenNoConstraintForDate() {
        LocalDate randomDate = LocalDate.of(2050, 1, 1);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            constraintsFacade.get(randomDate);
        });
        assertTrue(thrown.getMessage().contains("No constraints found for the specified date."));
    }
    //================================================================================
    // Task 2: Additional Tests
    //================================================================================

    @Test
    void findNextSunday_WhenTodayIsSunday_ReturnsSundayNextWeek() {
        LocalDate today = LocalDate.now();
        if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            LocalDate result = constraintsFacade.findNextSunday();
            assertTrue(result.isAfter(today),
                    "If today is Sunday, findNextSunday() must return next week’s Sunday, not today");
        }
    }

    @Test
    void setDeadline_WithNullDay_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            constraintsFacade.setDeadline(null, LocalTime.NOON);
        }, "setDeadline(null, time) should throw NullPointerException");
    }

    @Test
    void setDeadline_WithNullTime_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            DayOfWeek anyDay = DayOfWeek.MONDAY;
            constraintsFacade.setDeadline(anyDay, null);
        }, "setDeadline(day, null) should throw NullPointerException");
    }

    @Test
    void addNextWeekConstraints_WithNullArgument_DoesNotThrowAndStoresNullValue() {
        // Act
        constraintsFacade.addNextWeekConstraints(null);

        // Assert: internal map contains a null entry under next Sunday
        LocalDate nextSunday = constraintsFacade.findNextSunday();
        assertTrue(constraintsFacade.getCF().containsKey(nextSunday),
                "addNextWeekConstraints(null) should still put an entry (null) under next Sunday");
        assertNull(constraintsFacade.getCF().get(nextSunday),
                "The stored value under next Sunday should be null when passing null argument");
    }

    @Test
    void isBeforeDeadline_WhenDeadlineSetToFutureTime_ReturnsTrue() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime future = LocalTime.now().plusHours(1);
        constraintsFacade.setDeadline(today, future);

        assertTrue(constraintsFacade.isBeforeDeadline(),
                "If deadline is set to a future time on the same day, isBeforeDeadline() should return true");
    }

    @Test
    void isAfterDeadline_WhenDeadlineSetToPastTime_ReturnsTrue() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime past = LocalTime.now().minusHours(2);
        constraintsFacade.setDeadline(today, past);

        assertTrue(constraintsFacade.isAfterDeadline(),
                "If deadline is set to a past time on the same day, isAfterDeadline() should return true");
    }

    @Test
    void canUploadConstraints_TrueExactlyAtDeadlineMoment() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        // Set deadline to one second in the future so this test runs reliably
        LocalTime futurePlusOne = LocalTime.now().plusSeconds(1);
        constraintsFacade.setDeadline(today, futurePlusOne);

        // Immediately call canUploadConstraints: still before or exactly at the moment before moving past it
        assertTrue(constraintsFacade.canUploadConstraints(),
                "canUploadConstraints() should return true if current time is exactly at or before the deadline");
    }
}
