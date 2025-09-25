package Backend.DomainLayer.DomainLayerT;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.time.LocalDate;
import Backend.DTO.DocumentDTO;

public class DocumentDL {
    
    private int id;
    private LinkedList<SimpleEntry<String, Integer>> listOfItems;
    private LocationDL origin;
    private LocationDL destination;
    private Date exitedTime;
    private LocalDate createdDate;
    private int truckID;
    private String driverName;
    private int weight;

    public DocumentDL(int id, LinkedList<SimpleEntry<String, Integer>> listOfItems, LocationDL origin, LocationDL destination, LocalDate creatDate, int truckID, String driverName) {
        this.id = id;
        this.listOfItems = listOfItems;
        this.origin = origin;
        this.destination = destination;
        this.createdDate = creatDate;
        this.truckID = truckID;
        this.driverName = driverName;
        this.weight = calculateWeight();
    }

    public DocumentDL(DocumentDTO documentDTO) {
        this.id = documentDTO.getId();
        this.listOfItems = documentDTO.getListOfItems();
        this.origin = new LocationDL(documentDTO.getOrigin());
        this.destination = new LocationDL(documentDTO.getDestination());
        this.createdDate = documentDTO.getCreatedDate();
        this.truckID = documentDTO.getTruckID();
        this.driverName = documentDTO.getDriverName();
        this.weight = documentDTO.getWeight();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LinkedList<SimpleEntry<String, Integer>> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(LinkedList<SimpleEntry<String, Integer>> listOfItems) {
        this.listOfItems = listOfItems;
    }

    public void addItem(SimpleEntry<String, Integer> item) {
        this.listOfItems.add(item);
    }

    public void removeItem(String item) {
        this.listOfItems.removeIf(entry -> entry.getKey().equals(item));
    }

    public LocationDL getOrigin() {
        return origin;
    }

    public void setOrigin(LocationDL origin) {
        this.origin = origin;
    }

    public LocationDL getDestination() {
        return destination;
    }

    public void setDestination(LocationDL destination) {
        this.destination = destination;
    }

    public Date getExitedTime() {
        return exitedTime;
    }

    public void setExitedTime(Date exitedTime) {
        this.exitedTime = exitedTime;
    }

    public void setDueToDate(Date exitedTime) {
        this.exitedTime = exitedTime;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public int getTruckID() {
        return truckID;
    }

    public void setTruckID(int truckID) {
        this.truckID = truckID;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    //PRECONDITION: null
    //POSTCONDITION: The weight of the items in the document is returned.
    // The weight is calculated by summing the weights of all items in the list.

    public int calculateWeight(){
        int totalWeight = 0;
        for (SimpleEntry<String, Integer> item : listOfItems) {
            totalWeight += item.getValue();
        }
        return totalWeight;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Document ID: ").append(id).append("\n")
          .append("Created Date: ").append(createdDate).append("\n")
          .append("Exited Time: ").append(exitedTime).append("\n")
          .append("Truck ID: ").append(truckID == -1 ? "No truck assigned." : truckID).append("\n")
          .append("Driver Name: ").append(driverName).append("\n")
          .append("Origin: ").append(origin.getAddress() + ", " + origin.getAreaName()).append("\n")
          .append("Destination: ").append(destination.getAddress() + ", " + destination.getAreaName()).append("\n")
          .append("Items: ").append(listOfItems).append("\n")
          .append("Weight: ").append(weight).append("\n");
        return sb.toString();
    }
    
}
