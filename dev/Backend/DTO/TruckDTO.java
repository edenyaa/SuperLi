package Backend.DTO;

import java.util.AbstractMap.SimpleEntry;

import Backend.DomainLayer.DomainLayerT.TruckDL;

import java.util.LinkedList;

public class TruckDTO {
    
    private int id;
    private String model;
    private int dryWeight;
    private int weight;
    private int maxLoad;
    private boolean available;
    private String license;
    private LinkedList<SimpleEntry<String, Integer>> items;

    public TruckDTO(int id, String model, int dryWeight, int weight, int maxLoad, boolean available, String license, LinkedList<SimpleEntry<String, Integer>> items) {
        this.id = id;
        this.model = model;
        this.dryWeight = dryWeight;
        this.weight = weight;
        this.maxLoad = maxLoad;
        this.available = available;
        this.license = license;
        this.items = items;
    }

    public TruckDTO(TruckDL truckDL) {
        this.id = truckDL.getId();
        this.model = truckDL.getModel();
        this.dryWeight = truckDL.getDryWeight();
        this.weight = truckDL.getWeight();
        this.maxLoad = truckDL.getMaxLoad();
        this.available = truckDL.isAvailable();
        this.license = truckDL.getLicense();
        this.items = truckDL.getItems();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getDryWeight() {
        return dryWeight;
    }

    public void setDryWeight(int dryWeight) {
        this.dryWeight = dryWeight;
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

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public LinkedList<SimpleEntry<String, Integer>> getItems() {
        return items;
    }

    public void setItems(LinkedList<SimpleEntry<String, Integer>> items) {
        this.items = items;
    }
}
