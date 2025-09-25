package Backend.DomainLayer.DomainLayerT;

import Backend.DTO.LocationDTO;

public class LocationDL {

    private int id;
    private String areaName;
    private String address;
    private String phoneNumber;
    private String contactName;

    public LocationDL(int id, String areaName, String address, String phoneNumber, String contactName) {
        this.id = id;
        this.areaName = areaName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
    }

    public LocationDL(LocationDTO location) {
        this.id = location.getId();
        this.areaName = location.getAreaName();
        this.address = location.getAddress();
        this.phoneNumber = location.getPhoneNumber();
        this.contactName = location.getContactName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
