//package testsT;
//
//import Backend.DTO.*;
//import Backend.DomainLayer.DomainLayerT.*;
//import Backend.PresentationLayerHR.IdGenrator;
//import Backend.ServiceLayer.ServiceLayerT.ManagerService;
//import Backend.ServiceLayer.ServiceLayerT.ShipmentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//import java.util.AbstractMap.SimpleEntry;
//import java.util.LinkedList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TransportSystemIntegrationTest {
//
//    private ManagerService manager;
//    private ShipmentService shipment;
//
//    @BeforeEach
//    void setup() {
//        manager = ManagerService.getInstance();
//        shipment = ShipmentService.getInstance();
//        IdGenrator.resetIDs();
//    }
//
//    @Test
//    void fullFlow_assignDeliveryToTruck_generatesDocument() {
//        // Setup locations
//        LocationDTO origin = new LocationDTO(IdGenrator.generateLocationID(), "CentralArea", "Origin St", "111-111", "Alice");
//        LocationDTO destination = new LocationDTO(IdGenrator.generateLocationID(), "CentralArea", "Destination Ave", "222-222", "Bob");
//        AreaDTO area = new AreaDTO("CentralArea", new LinkedList<>(List.of(origin, destination)));
//        manager.addArea(area);
//
//        LocationDTO origin2 = new LocationDTO(IdGenrator.generateLocationID(), "CentralArea2", "Origin St2", "111-111", "Alice");
//        LocationDTO destination2 = new LocationDTO(IdGenrator.generateLocationID(), "CentralArea2", "Destination Ave2", "222-222", "Bob");
//        AreaDTO area2 = new AreaDTO("CentralArea2", new LinkedList<>(List.of(origin2, destination2)));
//        manager.addArea(area2);
//
//        // Setup truck
//        int truckId = IdGenrator.generateTruckID();
//        TruckDTO truck = new TruckDTO(new TruckDL(truckId, "Volvo", 2000, 10000, "C"));
//        manager.addTruck(truck);
//
//        // Setup driver
//        int driverId = IdGenrator.generateDriverID();
//        DriverDL driver = new DriverDL("driverUser", "pass", driverId, "Charlie", new LinkedList<>(List.of("C")));
//        manager.addDriver(driver);
//
//        // Create delivery
//        int deliveryId = IdGenrator.generateDeliveryID();
//        LinkedList<SimpleEntry<String, Integer>> items = new LinkedList<>();
//        items.add(new SimpleEntry<>("Boxes", 3000));
//        DeliveryDTO delivery = new DeliveryDTO(deliveryId, LocalDate.now().plusDays(1), origin, destination, items);
//        shipment.addDelivery(delivery);
//
//        int deliveryId2 = IdGenrator.generateDeliveryID();
//        LinkedList<SimpleEntry<String, Integer>> items2 = new LinkedList<>();
//        items2.add(new SimpleEntry<>("Boxes", 5000));
//        DeliveryDTO delivery2 = new DeliveryDTO(deliveryId2, LocalDate.now().plusDays(1), destination, origin, items2);
//        shipment.addDelivery(delivery2);
//
//        // Assign delivery to truck
//        shipment.assignDeliveryToTruck(deliveryId, truck);
//        TruckDTO updatedTruck = shipment.getTruckByID(truckId);
//        assertEquals(5000, updatedTruck.getWeight()); // 2000 dry + 3000 load
//        assertTrue(updatedTruck.getAvailable()); //check that truck is still available for next delivery
//
//        shipment.assignDeliveryToTruck(deliveryId2, truck);
//        TruckDTO updatedTruck2 = shipment.getTruckByID(truckId);
//        assertEquals(10000, updatedTruck2.getWeight()); // 2000 dry + 3000 load
//        assertFalse(updatedTruck2.getAvailable()); //check that truck is not available for next delivery because its full
//
//        UserDTO user1 = new UserDTO("user1", "pass1", 0);
//        manager.addUser(user1);
//        assertEquals(user1, manager.getUserByUserName("user1"));
//
//        manager.changeUserPermission(user1.getUserName(), -1);
//        // Validate truck is updated
//        assertEquals(-1, user1.getPermissionLevel());
//
//
//
//
//        // Validate document is created
//        DocumentDTO document = shipment.getDocumentByID(deliveryId);
//        assertEquals("Origin St", document.getOrigin().getAddress());
//        assertEquals("Destination Ave", document.getDestination().getAddress());
//        assertEquals(3000, document.getWeight());
//        assertEquals(truckId, document.getTruckID());
//
//        DocumentDTO document2 = shipment.getDocumentByID(deliveryId2);
//        assertEquals("Destination Ave", document2.getOrigin().getAddress());
//        assertEquals("Origin St", document2.getDestination().getAddress());
//        assertEquals(5000, document2.getWeight());
//        assertEquals(truckId, document2.getTruckID());
//
//        manager.changeDocuementOrigin(document.getId(), area2.getName(), origin2.getAddress());
//        manager.changeDocuementDestination(document.getId(), area2.getName(), destination2.getAddress());
//
//        assertEquals("Origin St2", document.getOrigin().getAddress());
//        assertEquals("Destination Ave2", document.getDestination().getAddress());
//
//        LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();
//        itemsToRemove.add(new SimpleEntry<>("Boxes", 3000));
//        LinkedList<SimpleEntry<String, Integer>> newItems = new LinkedList<>();
//        newItems.add(new SimpleEntry<>("Boxes1", 2000));
//        newItems.add(new SimpleEntry<>("Boxes2", 1000));
//        shipment.updateDocument(document.getId(), newItems, itemsToRemove);
//        DocumentDTO updatedDocument = shipment.getDocumentByID(document.getId());
//        assertEquals(3000, updatedDocument.getWeight());
//
//        //supplier picks up delivery2
//        shipment.supplierPickUpItems(deliveryId2);
//        assertThrows(IllegalArgumentException.class, () -> {
//            shipment.getDeliveryDL(deliveryId2);
//        });
//
//        System.out.println(shipment.getTruckDocuments(truckId));
//
//        //truck is delivering delivery1
//        shipment.sendTruckToDistributeDeliveries(truckId);
//        assertThrows(IllegalArgumentException.class, () -> {
//            shipment.getDeliveryDL(deliveryId);
//        });
//    }
//}
