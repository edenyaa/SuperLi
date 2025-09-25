package Exceptions;

public class StorageWorkerNotPresentException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String locationName;
    private String AreaName;

    public StorageWorkerNotPresentException(String locationName, String areaName) {
        super("No storage worker is present in " + locationName + " in the area " + areaName);
        this.locationName = locationName;
        this.AreaName = areaName;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getAreaName() {
        return AreaName;
    }
    
}
