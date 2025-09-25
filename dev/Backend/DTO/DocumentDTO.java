package Backend.DTO;

import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.LinkedList;

import Backend.DomainLayer.DomainLayerT.DocumentDL;

public class DocumentDTO {
    
    private int id;
    private LinkedList<SimpleEntry<String, Integer>> listOfItems;
    private LocationDTO origin;
    private LocationDTO destination;
    private Date exitedTime;
    private LocalDate createdDate;
    private int truckID;
    private String driverName;
    private int weight;

    public DocumentDTO(int id, LinkedList<SimpleEntry<String, Integer>> listOfItems, LocationDTO origin, LocationDTO destination, LocalDate creatDate, int truckID, String driverName, int weight, Date exitedTime) {
        this.id = id;
        this.listOfItems = listOfItems;
        this.origin = origin;
        this.destination = destination;
        this.createdDate = creatDate;
        this.truckID = truckID;
        this.driverName = driverName;
        this.weight = weight;
    }

    public DocumentDTO(DocumentDL documentDL) {
        this.id = documentDL.getId();
        this.listOfItems = documentDL.getListOfItems();
        this.origin = new LocationDTO(documentDL.getOrigin());
        this.destination = new LocationDTO(documentDL.getDestination());
        this.createdDate = documentDL.getCreatedDate();
        this.truckID = documentDL.getTruckID();
        this.driverName = documentDL.getDriverName();
        this.weight = documentDL.getWeight();
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

    public LocationDTO getOrigin() {
        return origin;
    }

    public void setOrigin(LocationDTO origin) {
        this.origin = origin;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public void setDestination(LocationDTO destination) {
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
}
