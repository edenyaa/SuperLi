package Backend.DomainLayer.DomainLayerT;
import java.util.LinkedList;
import java.util.AbstractMap.SimpleEntry;
import Backend.DTO.TruckDTO;

public class TruckDL {

    private int id;
    private String model;
    final private int dryWeight;
    private int weight;
    private int maxLoad;
    private boolean available;
    private String license;
    private LinkedList<SimpleEntry<String, Integer>> items;

    public TruckDL(int id, String model, int dryWeight, int maxLoad, String license) {
        this.license = license;
        this.id = id;
        this.model = model;
        this.dryWeight = dryWeight;
        this.weight = dryWeight;
        this.maxLoad = maxLoad;
        this.available = true; // Default to available
        this.items = new LinkedList<SimpleEntry<String, Integer>>();
    }

    public TruckDL(TruckDTO truckDTO) {
        this.id = truckDTO.getId();
        this.model = truckDTO.getModel();
        this.dryWeight = truckDTO.getDryWeight();
        this.weight = truckDTO.getWeight();
        this.maxLoad = truckDTO.getMaxLoad();
        this.available = truckDTO.getAvailable();
        this.license = truckDTO.getLicense();
        this.items = truckDTO.getItems();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getDryWeight() {
        return dryWeight;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getMaxLoad() {
        return maxLoad;
    }

    public void setMaxLoad(int maxLoad) {
        this.maxLoad = maxLoad;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public boolean isAvailable() {
        return available;
    }

    public void markAsAvailable() {
        this.available = true;
    }

    public void markAsUnavailable() {
        this.available = false;
    }
    public void addItem(SimpleEntry<String, Integer> item) {
        this.items.add(item);
    }
    public boolean removeItem(String item) {
        return this.items.removeIf(entry -> entry.getKey().equals(item));
    }
    public LinkedList<SimpleEntry<String, Integer>> getItems() {
        return items;
    }
    //PRECONDITION: null
    //POSTCONDITION: The weight of the items in the truck is returned.
    public int calculateWeight() {
        int totalWeight = this.dryWeight;
        for (SimpleEntry<String, Integer> item : items) {
            totalWeight += item.getValue();
        }
        return totalWeight;
    }
    public boolean isOverloaded() {
        return calculateWeight() > maxLoad;
    }

    public void removeItems(LinkedList<SimpleEntry<String, Integer>> itemsToRemove) {
        for (SimpleEntry<String, Integer> item : itemsToRemove) {
            removeItem(item.getKey());
        }
    }
}
