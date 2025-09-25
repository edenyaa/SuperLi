package Backend;

import Backend.DTO.LocationDTO;
import Backend.DTO.TransportationEmployeeDTO;
import Backend.DomainLayer.DomainLayerHR.*;
import Backend.ServiceLayer.SuperService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.DeliveryEmployeeService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.HRInboxService;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import Backend.ServiceLayer.ServiceLayerT.ManagerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryEmployeeServiceTest {

    private DeliveryEmployeeService service;
    private EmployeeFacade realFacade;
    private ManagerService realManager;

    private static class StubHRInboxService extends HRInboxService {
        String lastSender;
        String lastMessage;
        boolean called = false;

        @Override
        public Response sendMessageToHR(String sender, String msg) {
            called = true;
            lastSender = sender;
            lastMessage = msg;
            return new Response((Object) null);
        }
    }

    private StubHRInboxService stubInbox;

    @BeforeEach
    void setUp() throws Exception {
        SuperService superService = new SuperService();
        superService.deleteData();
        service = DeliveryEmployeeService.getInstance();
        realFacade = EmployeeFacade.getInstance();
        realManager = ManagerService.getInstance();

        try {
            Field efField = EmployeeFacade.class.getDeclaredField("EF");
            efField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, EmployeeDL> efMap = (Map<String, EmployeeDL>) efField.get(realFacade);
            efMap.clear();
        } catch (NoSuchFieldException ignored) {
        }

        realManager.getAllAreas().clear();

        stubInbox = new StubHRInboxService();
        Field inboxField = DeliveryEmployeeService.class.getDeclaredField("inboxService");
        inboxField.setAccessible(true);
        inboxField.set(service, stubInbox);
    }

    @AfterEach
    void tearDown() throws Exception {
        try {
            Field efField = EmployeeFacade.class.getDeclaredField("EF");
            efField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, EmployeeDL> efMap = (Map<String, EmployeeDL>) efField.get(realFacade);
            efMap.clear();
        } catch (NoSuchFieldException ignored) {
        }

        realManager.getAllAreas().clear();
    }

    @Test
    void getEmployeesForDelivery_NoEmployees_ReturnsErrorResponse() {
        Response resp = service.getEmployeesForDelivery(3, 1);
        assertNotNull(resp.getErrorMsg());
        assertEquals("No employees found for the requested shift.", resp.getErrorMsg());
        assertNull(resp.getReturnValue());
    }

    @Test
    void getEmployeesForDelivery_WithMatchingEmployee_ReturnsList() throws Exception {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Role(2, "Driver"));
        LocationDTO location = new LocationDTO(5, "AreaX", "StreetY", "0500000000", "OwnerX");

        EmployeeDL employee = new EmployeeDL(
                "123456789",
                "pass",
                "John Doe",
                LocalDate.now(),
                roles,
                4000.0,
                "1234567890",
                160,
                0,
                Collections.singletonList("LicenseA"),
                location
        );

        WeeklyShift weeklyShift = new WeeklyShift(false);
        TimeSlot ts = new TimeSlot(1, 1);

        Field currentWeekField = EmployeeDL.class.getDeclaredField("currentWeekShifts");
        currentWeekField.setAccessible(true);
        currentWeekField.set(employee, weeklyShift);

        Field efField = EmployeeFacade.class.getDeclaredField("EF");
        efField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, EmployeeDL> efMap = (Map<String, EmployeeDL>) efField.get(realFacade);
        efMap.put(employee.getId(), employee);

        Response resp = service.getEmployeesForDelivery(1, 1);
        assertNull(resp.getErrorMsg());
        Object val = resp.getReturnValue();
        assertNotNull(val);
        assertTrue(val instanceof LinkedList);

        @SuppressWarnings("unchecked")
        LinkedList<TransportationEmployeeDTO> resultList = (LinkedList<TransportationEmployeeDTO>) val;
        assertEquals(1, resultList.size());

        TransportationEmployeeDTO dto = resultList.getFirst();
        assertEquals("123456789", dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals(Collections.singletonList("LicenseA"), dto.getLicenseTypes());
        assertTrue(dto.getAvailable());
        assertEquals(location, dto.getBranch());
    }

    @Test
    void reportIssueToHR_ReturnsSuccessResponse_WhenInboxSucceeds() {
        String employeeId = "123456789";
        String reason = "Some issue";

        Response resp = service.reportIssueToHR(employeeId, reason);
        assertNull(resp.getErrorMsg());
        assertNull(resp.getReturnValue());
    }

    @Test
    void reportIssueToHR_Want_To_Fire_Employee_Messagee() {
        String employeeId = "987654321";
        String reason = "I want to fire employee " + employeeId;

        Response resp = service.reportIssueToHR(employeeId, reason);
        assertNull(resp.getErrorMsg());
        assertNull(resp.getReturnValue());
        assertTrue(stubInbox.called);
        assertEquals("Delivery Manager", stubInbox.lastSender);

        String expectedMessage = "Employee ID: " + employeeId + "\nIssue: " + reason;
        assertEquals(expectedMessage, stubInbox.lastMessage);
    }

    @Test
    void reportIssueToHR_EmptyReason_ShouldWork() {
        String employeeId = "123456789";
        String reason = "";

        Response resp = service.reportIssueToHR(employeeId, reason);
        assertNull(resp.getErrorMsg());
        assertNull(resp.getReturnValue());
        assertTrue(stubInbox.called);
        assertEquals("Delivery Manager", stubInbox.lastSender);

        String expectedMessage = "Employee ID: " + employeeId + "\nIssue: " + reason;
        assertEquals(expectedMessage, stubInbox.lastMessage);
    }
}
