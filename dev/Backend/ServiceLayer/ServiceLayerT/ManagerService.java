package Backend.ServiceLayer.ServiceLayerT;

import Backend.DomainLayer.DomainLayerT.*;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import Backend.ServiceLayer.ServiceLayerHR.HRService.DeliveryEmployeeService;
import Backend.DTO.*;
import java.util.LinkedList;

public class ManagerService {
    // In the next level of the architecture, we will add a logger to log the actions of the manager.
    // For now, we will just use that class to talk to the transport facade.
    // The manager service will be used by the GUI to talk to the transport facade.
    private static final ManagerService instance = new ManagerService();
    private final TransportFacade transportFacade = TransportFacade.getInstance();
    private static final DeliveryEmployeeService deliveryEmployeeService = DeliveryEmployeeService.getInstance();
    
    private ManagerService() {}

    public static ManagerService getInstance() {
        return instance;
    }

    public void addArea(AreaDTO area){
        transportFacade.addArea(area);
    }

    public AreaDTO getAreaByName(String name){
        return transportFacade.getAreaByName(name);
    }

    public void addLocation(String areaName, LocationDTO location){
        transportFacade.addLocation(areaName, location);
    }

    public LocationDTO getLocationByAddress(String areaName, String address){
        return transportFacade.getLocationByAddress(areaName, address);
    }

    public void addTruck(TruckDTO truck){
        transportFacade.addTruck(truck);
    }

    public TruckDTO getTruckById(int id){
        return transportFacade.getTruckByID(id);
    }

    public void complainOnDriver(String driverID, String complaint){ 
        deliveryEmployeeService.reportIssueToHR(driverID, complaint);
    }

    public boolean isValidDay(String day) {
        return transportFacade.isValidDay(day);
    }

    public LinkedList<LinkedList<TransportationEmployeeDTO>> getAllShiftAssignmentsForTheDay(String dayName) {
        int day = transportFacade.getDayNum(dayName);
        Response response1 = deliveryEmployeeService.getEmployeesForDelivery(day, 1);
        Response response2 = deliveryEmployeeService.getEmployeesForDelivery(day, 2);
        if (!response1.isSuccess()) {
            throw new IllegalArgumentException(response1.getErrorMsg());
        }
        else if (!response2.isSuccess()) {
            throw new IllegalArgumentException(response2.getErrorMsg());
        }
        LinkedList<TransportationEmployeeDTO> morningTransportationEmployees = (LinkedList<TransportationEmployeeDTO>) response1.getReturnValue();
        LinkedList<TransportationEmployeeDTO> eveningTransportationEmployees = (LinkedList<TransportationEmployeeDTO>) response2.getReturnValue();
        LinkedList<LinkedList<TransportationEmployeeDTO>> allShiftAssignments = new LinkedList<>();
        allShiftAssignments.add(morningTransportationEmployees);
        allShiftAssignments.addLast(eveningTransportationEmployees);
        return allShiftAssignments;
    }

    public void deleteTruck(int truckID) {
        transportFacade.deleteTruck(truckID);
    }

    public void deleteArea(String areaName) {
        transportFacade.deleteArea(areaName);
    }

    public void deleteLocation(String areaName, String locationAddress) {
        transportFacade.deleteLocation(areaName, locationAddress);
    }

    public void changeDocuementOrigin(int documentID, String areaName, String locationName) {
        transportFacade.changeDocuementOrigin(documentID, areaName, locationName);
    }

    public void changeDocuementDestination(int documentID, String areaName, String locationName) {
        transportFacade.changeDocuementDestination(documentID, areaName, locationName);
    }

    public LinkedList<AreaDTO> getAllAreas() {
        return transportFacade.getAllAreas();
    }

    public boolean isValidShift(int shift) {
        return transportFacade.isValidShift(shift);
    }

    public int getMaxDeliveryID() {
        return transportFacade.getMaxDeliveryID();
    }

    public int getMaxTruckID() {
        return transportFacade.getMaxTruckID();
    }

    public int getMaxLocationID() {
        return transportFacade.getMaxLocationID();
    }

    public int getMaxDocumentID() {
        return transportFacade.getMaxDocumentID();
    }
}
