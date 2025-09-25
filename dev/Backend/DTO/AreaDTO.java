package Backend.DTO;
import java.util.LinkedList;

import Backend.DomainLayer.DomainLayerT.AreaDL;
import Backend.DomainLayer.DomainLayerT.LocationDL;

public class AreaDTO{

    private String name;
    private LinkedList<LocationDTO> locations;

    public AreaDTO(String name, LinkedList<LocationDTO> locations) {
        this.name = name;
        this.locations = locations;
    }

    public AreaDTO(AreaDL area) {
        this.name = area.getName();
        this.locations = new LinkedList<>();
        for (LocationDL location : area.getLocations()) {
            this.locations.add(new LocationDTO(location));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<LocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(LinkedList<LocationDTO> locations) {
        this.locations = locations;
    }
} 
