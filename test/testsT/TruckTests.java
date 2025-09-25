package testsT;

import Backend.DTO.TruckDTO;
import Backend.ServiceLayer.ServiceLayerT.ManagerService;
import Backend.ServiceLayer.ServiceLayerT.ShipmentService;
import org.junit.jupiter.api.*;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TruckTests {

    private static ManagerService manager;
    private static ShipmentService shipmentService;
    private static TruckDTO testTruck;

    @BeforeEach
    void setup() {
        manager = ManagerService.getInstance();
        shipmentService = ShipmentService.getInstance();

        testTruck = new TruckDTO(
            999,
            "TestModel",
            150,
            150,
            800,
            true,
            "B",
            new LinkedList<>());
    }

    @Test
    @Order(1)
    @DisplayName("Add Truck Test - Success")
    void addTruckTest() {
        assertDoesNotThrow(() -> manager.addTruck(testTruck));

        assertDoesNotThrow(() -> {
            TruckDTO retrievedTruck = manager.getTruckById(999);
            assertEquals(999, retrievedTruck.getId());
            assertEquals("TestModel", retrievedTruck.getModel());
            assertEquals(150, retrievedTruck.getDryWeight());
            assertEquals(150, retrievedTruck.getWeight());
            assertEquals(800, retrievedTruck.getMaxLoad());
            assertTrue(retrievedTruck.getAvailable());
            assertEquals("B", retrievedTruck.getLicense());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Add truck with invalid weight - Failure")
    void addTruckWithInvalidWeightTest() {
        TruckDTO invalidTruck = new TruckDTO(
            998,
            "InvalidModel",
            500,
            500,
            300,
            true,
            "A",
            new LinkedList<>());

        assertThrows(
            Exception.class,
            () -> manager.addTruck(invalidTruck),
            "Expected an exception for invalid truck weight"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Get Available Trucks Test - Success")
    void getAvailableTrucksTest() {
        assertDoesNotThrow(() -> {
            LinkedList<TruckDTO> availableTrucks = shipmentService.getAvailableTrucks();
            assertTrue(availableTrucks.stream().anyMatch(truck -> truck.getId() == 999));
        });
    }

    @Test
    @Order(4)
    @DisplayName("Truck Weight Calculation Test - Success")
    void truckWeightCalculationTest() {
        assertDoesNotThrow(() -> {
            TruckDTO retrievedTruck = manager.getTruckById(999);
            assertEquals(150, retrievedTruck.getWeight(), "Truck weight should match the initial weight");
        });
    }

    @Test
    @Order(5)
    @DisplayName("Remove Truck Test - Success")
    void removeTruckTest() {
        assertDoesNotThrow(() -> manager.deleteTruck(999));
        assertThrows(
            Exception.class,
            () -> manager.getTruckById(999),
            "Expected an exception when trying to retrieve a deleted truck"
        );
    }

    @AfterAll
    static void cleanup() {
        // Clean up any resources or reset states if necessary
        try {
            manager.deleteTruck(999);
        } catch (Exception e) {
            // Ignore if the truck was already deleted
        }
        try{
            manager.deleteTruck(998);
        } catch (Exception e) {
            // Ignore if the truck was already deleted
        }
    }
}
