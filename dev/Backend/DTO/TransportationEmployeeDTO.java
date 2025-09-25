package Backend.DTO;

import java.util.LinkedList;

public class TransportationEmployeeDTO {
    
    private String id;
    private String name;
    private LinkedList<String> licenseTypes;
    private boolean available;
    private LocationDTO branch;


    public TransportationEmployeeDTO(String id, String name, LinkedList<String> licenseTypes, boolean available, LocationDTO branch) {
        this.id = id;
        this.name = name;
        this.licenseTypes = licenseTypes;
        this.available = available;
        this.branch = branch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<String> getLicenseTypes() {
        return licenseTypes;
    }

    public void setLicenseTypes(LinkedList<String> licenseTypes) {
        this.licenseTypes = licenseTypes;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocationDTO getBranch() {
        return branch;
    }

    public void setBranch(LocationDTO branch) {
        this.branch = branch;
    }
}
