package Backend.DomainLayer.DomainLayerT;
import java.time.LocalDate;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import Backend.DTO.DeliveryDTO;

public class DeliveryDL {

    private int id;
    private LocalDate createdDate;
    private LocalDate deliveryDate;
    private LocationDL originLoc;
    private LocationDL destinationLoc;
    private LinkedList<SimpleEntry<String, Integer>> listOfItems;

    public DeliveryDL(int id, LocalDate deliveryDate, LocationDL originLoc, LocationDL destinationLoc, LinkedList<SimpleEntry<String, Integer>> listOfItems) {
        this.id = id;
        this.createdDate = LocalDate.now();
        this.deliveryDate = deliveryDate;
        this.originLoc = originLoc;
        this.destinationLoc = destinationLoc;
        this.listOfItems = listOfItems;
    }

    public DeliveryDL(DeliveryDTO deliveryDTO) {
        this.id = deliveryDTO.getId();
        this.createdDate = deliveryDTO.getCreatedDate();
        this.deliveryDate = deliveryDTO.getDeliveryDate();
        this.originLoc = new LocationDL(deliveryDTO.getOriginLoc());
        this.destinationLoc = new LocationDL(deliveryDTO.getDestinationLoc());
        this.listOfItems = deliveryDTO.getListOfItems();
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

    public LocationDL getOriginLoc() {
        return originLoc;
    }

    public void setOriginLoc(LocationDL originLoc) {
        this.originLoc = originLoc;
    }

    public LocationDL getDestinationLoc() {
        return destinationLoc;
    }

    public void setDestinationLoc(LocationDL destinationLoc) {
        this.destinationLoc = destinationLoc;
    }

    public LinkedList<SimpleEntry<String, Integer>> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(LinkedList<SimpleEntry<String, Integer>> listOfItems) {
        this.listOfItems = listOfItems;
    }

    public void addItem(SimpleEntry<String, Integer> item) {
        listOfItems.add(item);
    }
    
    public void removeItem(String name, int weight) {
        listOfItems.removeIf(entry ->
            entry.getKey().equals(name) && entry.getValue().equals(weight));
    }
    

    public void clearItems() {
        listOfItems.clear();
    }

    public int getItemCount() {
        return listOfItems.size();
    }
    // PRECONDITION: null
    // POSTCONDITION: The weight of the items in the delivery is returned.
    // The weight is calculated by summing the weights of all items in the listOfItems.
    public int calculateWeight() {
        
        int totalWeight = 0;
        for (SimpleEntry<String, Integer> item : listOfItems) {
            totalWeight += item.getValue();
        }
        return totalWeight;
    }   
}
