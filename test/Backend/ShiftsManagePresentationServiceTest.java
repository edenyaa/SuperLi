package Backend;

import Backend.ServiceLayer.SuperService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.ShiftsManageService;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftsManagePresentationServiceTest {

    ShiftsManageService service = new ShiftsManageService();

    @BeforeEach
    void setUp() {
        SuperService superService = new SuperService();
        superService.deleteData();
    }
    @Test
    void removeShift_WhenShiftNotPresent_WarningThenAddShiftSucceeds() {
        Response removeRes = service.removeShift(1, 1);
        if (!removeRes.isSuccess()) {
            System.out.println("Warning: removeShift(1, 1) failed – possibly already removed. Continuing...");
        }
        Response addRes = service.addShift(1, 1);
        assertTrue(addRes.isSuccess(), "Expected addShift(1, 1) to succeed after removal attempt");
    }

    @Test
    void publishWeeklyShift_WhenNoRolesAssigned_ReturnsFailure() {
        Response res = service.publishWeeklyShift();
        assertFalse(res.isSuccess(), "Expected publishWeeklyShift() to fail initially due to unassigned roles");
    }

    @Test
    void viewPublishedWeeklyShifts_WhenCalled_ReturnsNonNull() {
        Response res = service.viewPublishedWeeklyShifts();
        assertNotNull(res.getReturnValue(), "Expected viewPublishedWeeklyShifts() to return a non-null value");
    }
}