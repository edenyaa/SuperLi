package Backend.DataAccessLayer.DAO;

import Backend.DTO.*;

import java.util.LinkedList;

public interface AreaLocationDAO extends DAO<AreaDTO, String> {

    LinkedList<LocationDTO> getAllLocationsByArea(String areaName);

    void insertLocationToArea(String areaName, LocationDTO location);

    void deleteLocationFromArea(String areaName, LocationDTO location);

    void updateLocationInArea(String areaName, LocationDTO location);
   LocationDTO getLocationById(int id) ;
}
