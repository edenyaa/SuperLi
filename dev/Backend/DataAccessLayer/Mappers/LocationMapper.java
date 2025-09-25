package Backend.DataAccessLayer.Mappers;

        import Backend.DomainLayer.DomainLayerT.LocationDL;
        import Backend.DTO.LocationDTO;

public class LocationMapper {

    public static LocationDTO toDTO(LocationDL locationDL) {
        if (locationDL == null) return null;
        return new LocationDTO(
                locationDL.getId(),
                locationDL.getAreaName(),
                locationDL.getAddress(),
                locationDL.getPhoneNumber(),
                locationDL.getContactName()
        );
    }

    public static LocationDL fromDTO(LocationDTO locationDTO) {
        if (locationDTO == null) return null;
        return new LocationDL(
                locationDTO.getId(),
                locationDTO.getAreaName(),
                locationDTO.getAddress(),
                locationDTO.getPhoneNumber(),
                locationDTO.getContactName()
        );
    }
}
