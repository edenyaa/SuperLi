package Backend.DataAccessLayer.Mappers;

import Backend.DTO.RoleDTO;
import Backend.DomainLayer.DomainLayerHR.Role;

import java.util.ArrayList;

public class RoleMapper {

    public static RoleDTO toDTO(Role role) {
        return new RoleDTO(role.getRoleId(), role.getRoleName());
    }

    public static Role fromDTO(RoleDTO dto) {
        // יצירה לפי מזהה ושם, הרשאות יווצרו אוטומטית בבנאי
        return new Role(dto.getRoleId(), dto.getRoleName());
    }

    public static ArrayList<RoleDTO> toDTOList(ArrayList<Role> roles) {
        ArrayList<RoleDTO> dtos = new ArrayList<>();
        for (Role r : roles) {
            dtos.add(toDTO(r));
        }
        return dtos;
    }

    public static ArrayList<Role> fromDTOList(ArrayList<RoleDTO> dtos) {
        ArrayList<Role> roles = new ArrayList<>();
        for (RoleDTO dto : dtos) {
            roles.add(fromDTO(dto));
        }
        return roles;
    }
}
