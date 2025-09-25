package Backend.ServiceLayer.ServiceLayerHR;

import Backend.DTO.RoleDTO;

import java.util.ArrayList;

public class PermissionService {

    public static PermissionLevel getPermissionLevel(ArrayList<RoleDTO> roles) {
        if (hasRole(roles, "SystemManager"))
            return PermissionLevel.SYSTEMMANAGER;
        if (hasRole(roles, "HrManager"))
            return PermissionLevel.HRMANAGER;
        if (hasRole(roles, "TransportManager"))
            return PermissionLevel.TRANSPORTMANAGER;
        if (hasRole(roles, "Driver") || hasRole(roles, "WareHouse Worker"))
            return PermissionLevel.TRANSPORTEMPLOYEE;

        return PermissionLevel.REGULAREMPLOYEE;
    }

    public static boolean hasRole(ArrayList<RoleDTO> roles, String roleName) {
        for (RoleDTO role : roles) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }
}