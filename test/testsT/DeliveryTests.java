package testsT;

import Backend.DTO.DeliveryDTO;
import Backend.DTO.DocumentDTO;
import Backend.DTO.LocationDTO;
import Backend.DTO.TruckDTO;
import Backend.ServiceLayer.ServiceLayerT.ManagerService;
import Backend.ServiceLayer.ServiceLayerT.ShipmentService;
import Exceptions.OverWeightException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeliveryTests {

    private static ManagerService manager;
    private static ShipmentService shipmentService;
    private static DeliveryDTO testDelivery;
    private static LocationDTO sourceLocation;
    private static LocationDTO destinationLocation;

    @BeforeEach
    void setup() {

        manager = ManagerService.getInstance();
        shipmentService = ShipmentService.getInstance();

        sourceLocation = new LocationDTO(0, "center", "hruv 9 kiryat ekron", "523788177", "eden");
        destinationLocation = new LocationDTO(1, "center", "hertzel 7 Ramat Gan", "8772687", "Itay");

        LinkedList<SimpleEntry<String, Integer>> items = new LinkedList<>();
        items.add(new SimpleEntry<>("Test Item 1", 50));
        items.add(new SimpleEntry<>("Test Item 2", 30));

        testDelivery = new DeliveryDTO(
            9999,
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            sourceLocation,
            destinationLocation,
            items);
    }

    @Test
    @Order(1)
    @DisplayName("Add Delivery Test - Success")
    void addDeliveryTest() {
        assertDoesNotThrow(() -> shipmentService.addDelivery(testDelivery));

        assertDoesNotThrow(() -> {
            DeliveryDTO retrievedDelivery = shipmentService.getDelivery(9999);
            assertEquals(9999, retrievedDelivery.getId());
            assertEquals("hruv 9 kiryat ekron", retrievedDelivery.getOriginLoc().getAddress());
            assertEquals("hertzel 7 Ramat Gan", retrievedDelivery.getDestinationLoc().getAddress());
            assertEquals(2, retrievedDelivery.getListOfItems().size());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Assign Delivery Test - Success")
    void assignDeliveryTest() {
        TruckDTO truck = new TruckDTO(
                99999, "Test Truck", 200, 200, 1000, true, "C", new LinkedList<>()
        );

        assertDoesNotThrow(() -> manager.addTruck(truck));
        assertDoesNotThrow(() -> shipmentService.assignDeliveryToTruck(9999, truck.getId()));
        assertDoesNotThrow(() -> {
            DocumentDTO assignedDelivery = shipmentService.getDocumentByID(9999);
            assertEquals(9999, assignedDelivery.getId());
            assertEquals(99999, assignedDelivery.getTruckID());
            assertEquals("hruv 9 kiryat ekron", assignedDelivery.getOrigin().getAddress());
            assertEquals("hertzel 7 Ramat Gan", assignedDelivery.getDestination().getAddress());
        });
    }

    @Test
    @Order(3)
    @DisplayName("Assign Delivery to truck with insufficient capacity - Failure")
    void assignDeliveryToTruckWithInsufficientCapacityTest(){
        LinkedList<SimpleEntry<String, Integer>> largeItems = new LinkedList<>();
        largeItems.add(new SimpleEntry<>("Large Item 1", 500));

        DeliveryDTO largeDelivery = new DeliveryDTO(
                9996,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                sourceLocation,
                destinationLocation,
                largeItems
        );

        assertDoesNotThrow(() -> shipmentService.addDelivery(largeDelivery));
        TruckDTO truck = new TruckDTO(
                99998, "Test Truck", 200, 200, 300, true, "C", new LinkedList<>()
        );
        assertDoesNotThrow(() -> manager.addTruck(truck));
        assertThrows(OverWeightException.class, () -> {
            shipmentService.assignDeliveryToTruck(9996, truck.getId());
        });
    }

    @Test
    @Order(4)
    @DisplayName("Supplier pick up Test - Success")
    void supplierPickUpTest() {
        assertDoesNotThrow(() -> {
            DocumentDTO pickedUpDelivery = shipmentService.supplierPickUpItems(9999);
            assertNotNull(pickedUpDelivery);
            assertEquals(9999, pickedUpDelivery.getId());
        });
    }

    @AfterAll
   static void tearDown() {
        // Clean up after tests
        try {
            shipmentService.cancelDelivery(9999);
        } catch (Exception e) {
            // Handle any exceptions that may occur during cleanup
            e.printStackTrace();
        }
    }

}
