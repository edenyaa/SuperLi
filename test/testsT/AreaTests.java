package testsT;

import Backend.DTO.AreaDTO;
import Backend.DTO.LocationDTO;
import Backend.ServiceLayer.ServiceLayerT.ManagerService;
import org.junit.jupiter.api.*;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AreaTests {
    
    private static ManagerService manager;
    private static AreaDTO testArea;
    private static LocationDTO testLocation;


    @BeforeEach
    void setup() {
        manager = ManagerService.getInstance();
        
        testLocation = new LocationDTO(
            100, 
            "testArea",
            "test address 123",
            "0501234567",
            "Test Contact");
        LinkedList<LocationDTO> locations = new LinkedList<>();
        locations.add(testLocation);
        testArea = new AreaDTO("testArea", locations);
    }

    @Test
    @Order(1)
    @DisplayName("Add new area - Success")
    void testAddNewArea() {
        assertDoesNotThrow(() -> manager.addArea(testArea));

        assertDoesNotThrow(() -> {
            AreaDTO retrievedArea = manager.getAreaByName("testArea");
                assertEquals("testArea", retrievedArea.getName());
                assertEquals(1, retrievedArea.getLocations().size());
                assertEquals("test address 123", retrievedArea.getLocations().get(0).getAddress());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Add existing area - Failure")
    void testAddExistingArea() {
        AreaDTO existingArea = new AreaDTO("testArea", new LinkedList<>());

        assertThrows(
            Exception.class,
            () -> manager.addArea(existingArea));
    }

    @Test
    @Order(3)
    @DisplayName("Add location to area - Success")
    void testAddLocationToArea() {
        LocationDTO newLocation = new LocationDTO(
            200, 
            "testArea",
            "new address 456",
            "0509876543",
            "New Contact");
        assertDoesNotThrow(() -> manager.addLocation("testArea", newLocation));
        assertDoesNotThrow(() -> {
            AreaDTO retrievedArea = manager.getAreaByName("testArea");
            assertEquals(2, retrievedArea.getLocations().size());
            boolean found = retrievedArea.getLocations().stream().anyMatch(loc -> 
                loc.getAddress().equals("new address 456"));
            assertTrue(found, "New location should be added to the area");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Add location with existing address in same area - Failure")
    void testAddLocationWithExistingAddress() {
        LocationDTO existingLocation = new LocationDTO(
            300, 
            "testArea",
            "test address 123", // Same address as testLocation
            "0501112233",
            "Existing Contact");
        assertThrows(
            Exception.class,
            () -> manager.addLocation("testArea", existingLocation),
            "Should not allow adding location with existing address in the same area"
        );
    }

    @Test
    @Order(5)
    @DisplayName("Add location to non-existing area - Failure")
    void testAddLocationToNonExistingArea() {
        LocationDTO newLocation = new LocationDTO(
            400, 
            "nonExistingArea",
            "some address 789",
            "0502223333",
            "Non Existing Contact");
        
        assertThrows(
            Exception.class,
            () -> manager.addLocation("nonExistingArea", newLocation),
            "Should not allow adding location to a non-existing area"
        );
    }

    @AfterAll
    static void cleanup() {
        // Clean up test data
        try {
            manager.deleteArea("testArea");
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}
