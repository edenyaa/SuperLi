package Backend.ServiceLayer.ServiceLayerT;

import Backend.DTO.*;
import Backend.DomainLayer.DomainLayerT.*;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import Backend.ServiceLayer.ServiceLayerHR.HRService.DeliveryEmployeeService;
import serviceLayer.StorageController;


import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;



public class ShipmentService {
    // In the next level of the architecture, we will add a logger to log the actions of the manager.
    // For now, we will just use that class to talk to the transport facade.
    // The shipment service will be used by the GUI to talk to the transport facade.
    private static final ShipmentService instance = new ShipmentService();
    private final TransportFacade transportFacade = TransportFacade.getInstance();
    private static final DeliveryEmployeeService deliveryEmployeeService = DeliveryEmployeeService.getInstance();
    private static final StorageController storageController = new StorageController();
    

    private ShipmentService() {}

    public static ShipmentService getInstance() {
        return instance;
    }

    public void addDelivery(DeliveryDTO delivery) {
        for (SimpleEntry<String, Integer> item : delivery.getListOfItems()) {
            if(storageController.checkIfProductExists(item.getKey()) == null){
                throw new IllegalArgumentException("Product " + item.getKey() + " does not exist in the storage.");
            }
        }
        transportFacade.addDelivery(delivery);
    }
    
    public void assignDeliveryToTruck(int deliveryID, int truckID) {
        transportFacade.assignDeliveryToTruck(deliveryID, truckID);
    }
    public LinkedList<DocumentDTO> getTruckDocuments(int truckID) {
        return transportFacade.getTruckDocuments(truckID);
    }
    public DocumentDTO getDocumentByID(int documentID) {
        return transportFacade.getDocumentByID(documentID);
    }
    public void changeDestination(String areaName, int deliveryID, String destination) {
        transportFacade.changeDestination(areaName, deliveryID, destination);
    }
    public void cancelDelivery(int deliveryID) {
        transportFacade.cancelDelivery(deliveryID); 
    }
    public void updateDocument(int documentID, LinkedList<SimpleEntry<String, Integer>> newItems, LinkedList<SimpleEntry<String, Integer>> itemsToRemove) {
        transportFacade.updateDocument(documentID, newItems, itemsToRemove);        
    }
    public void changeTruck(int deliveryID, int newTruck) {
        transportFacade.changeTruck(deliveryID, newTruck);
    }
    public void removeItemsFromTruck(int truckID, int deliveryID, LinkedList<SimpleEntry<String, Integer>> items) {
        if(truckID == -1) {
            transportFacade.removeItemsFromDelivery(deliveryID, items);
        }
        else {
            transportFacade.removeItemsFromTruck(truckID, deliveryID, items);
        }
    }
    public void removeItemsFromDelivery(int deliveryID, LinkedList<SimpleEntry<String, Integer>> items) {
        transportFacade.removeItemsFromDelivery(deliveryID, items);
    }

    public TruckDTO getTruckByID(int id) {
        return transportFacade.getTruckByID(id);
    }    
    
    public LinkedList<TruckDTO> getAvailableTrucks() {
        return transportFacade.getAvailableTrucks();
    }
    
    public DeliveryDTO getDelivery(int deliveryID) {
        return transportFacade.getDeliveryByID(deliveryID);
    }

    public LinkedList<DeliveryDTO> listDeliveriesByTime(int amount) {
        return transportFacade.listDeliveriesByTime(amount);
    }

    public void sendTruckToDistributeDeliveries(int truckID, String day, int hour) {
        int shift = transportFacade.getShift(hour);
        int dayNum = transportFacade.getDayNum(day);
        Response response = deliveryEmployeeService.getEmployeesForDelivery(dayNum, shift);
        if (!response.isSuccess()) {
            throw new IllegalArgumentException(response.getErrorMsg());
        }
        LinkedList<TransportationEmployeeDTO> employees = (LinkedList<TransportationEmployeeDTO>) response.getReturnValue();
        transportFacade.sendTruckToDistributeDeliveries(employees, truckID);
    }

    public DocumentDTO supplierPickUpItems(int deliveryID) {
        return transportFacade.supplierPickUpItems(deliveryID);
    }

    public LinkedList<DeliveryDTO> getAllDeliveries() {
        return transportFacade.getAllDeliveries();
    }

    public LinkedList<DeliveryDTO> getNotAssignedDeliveries() {
        return transportFacade.getNotAssignedDeliveries();
    }

    public LinkedList<DeliveryDTO> getAssignedDeliveries() {
        return transportFacade.getAssignedDeliveries();
    }

    public LinkedList<TruckDTO> getAllTrucks() {
        return transportFacade.getAllTrucks();
    }

    public LinkedList<DocumentDTO> getAllDocuments() {
        return transportFacade.getAllDocuments();
    }
}
