package Backend.DomainLayer.DomainLayerT;

import java.util.*;
import Backend.DTO.*;

public class AreaDL {

    private LinkedList<LocationDL> locations;
    private String name;

    public AreaDL(String name) {
        this.name = name;
        this.locations = new LinkedList<>();
    }

    public AreaDL(String name, LinkedList<LocationDL> locations) {
        this.name = name;
        this.locations = locations;
    }

    public AreaDL(AreaDTO area){
        this.name = area.getName();
        this.locations = new LinkedList<>();
        for (LocationDTO location : area.getLocations()) {
            this.locations.add(new LocationDL(location));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<LocationDL> getLocations() {
        return locations;
    }

    public void setLocations(LinkedList<LocationDL> locations) {
        this.locations = locations;
    }

    public void addLocation(LocationDL location) {
        locations.add(location);
    }

    public void removeLocation(LocationDL location) {
        locations.remove(location);
    }

    public void clearLocations() {
        locations.clear();
    }

    public int getLocationCount() {
        return locations.size();
    }
    //PRECONDITION: The address is unique in the area.
    //POSTCONDITION: The location with the given address is returned.
    // If no location with the given address is found, null is returned.
    public LocationDL getLocationByAddress(String address) {
        for (LocationDL location : locations) {
            if (location.getAddress().equals(address)) {
                return location;
            }
        }
        return null;
    }
    
}
