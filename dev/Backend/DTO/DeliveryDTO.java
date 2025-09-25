package Backend.DTO;

import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

import Backend.DomainLayer.DomainLayerT.DeliveryDL;

public class DeliveryDTO {

    private int id;
    private LocalDate createdDate;
    private LocalDate deliveryDate;
    private LocationDTO originLoc;
    private LocationDTO destinationLoc;
    private LinkedList<SimpleEntry<String, Integer>> listOfItems;

    public DeliveryDTO(int id, LocalDate createdDate, LocalDate deliveryDate, LocationDTO originLoc, LocationDTO destinationLoc, LinkedList<SimpleEntry<String, Integer>> listOfItems) {
        this.id = id;
        this.createdDate = createdDate;
        this.deliveryDate = deliveryDate;
        this.originLoc = originLoc;
        this.destinationLoc = destinationLoc;
        this.listOfItems = listOfItems;
    }

    public DeliveryDTO(DeliveryDL deliveryDL) {
        this.id = deliveryDL.getId();
        this.createdDate = deliveryDL.getCreatedDate();
        this.deliveryDate = deliveryDL.getDeliveryDate();
        this.originLoc = new LocationDTO(deliveryDL.getOriginLoc());
        this.destinationLoc = new LocationDTO(deliveryDL.getDestinationLoc());
        this.listOfItems = deliveryDL.getListOfItems();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LocationDTO getOriginLoc() {
        return originLoc;
    }

    public void setOriginLoc(LocationDTO originLoc) {
        this.originLoc = originLoc;
    }

    public LocationDTO getDestinationLoc() {
        return destinationLoc;
    }

    public void setDestinationLoc(LocationDTO destinationLoc) {
        this.destinationLoc = destinationLoc;
    }

    public LinkedList<SimpleEntry<String, Integer>> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(LinkedList<SimpleEntry<String, Integer>> listOfItems) {
        this.listOfItems = listOfItems;
    }

}
