package Backend.ServiceLayer.ServiceLayerHR.HRService;

import Backend.DTO.RoleDTO;
import Backend.DomainLayer.DomainLayerHR.Role;
import Backend.DomainLayer.DomainLayerHR.RolesFacade;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.util.ArrayList;
import java.util.List;

public class RolesService {

    private final RolesFacade rolesFacade = RolesFacade.getInstance();

    public List<RoleDTO> getRoles() {
        List<Role> domainRoles = rolesFacade.getRoles();
        List<RoleDTO> roleDTOs = new ArrayList<>();
        for (Role r : domainRoles) {
            roleDTOs.add(new RoleDTO(r));
        }
        return roleDTOs;
    }

    public RoleDTO getRoleById(int id) {
        Role role = rolesFacade.getRoleById(id);
        return new RoleDTO(role);
    }

    public RoleDTO getRoleByName(String roleName) {
        Role role = rolesFacade.getRoleByName(roleName);
        return new RoleDTO(role);
    }

    public Response printRoles() {
        rolesFacade.printRoles();
        return new Response("Roles printed successfully.", null);
    }

    public int getNumberOfRoles() {
        return rolesFacade.getNumberOfRoles();
    }

    public Response addRole(String roleName, ArrayList<String> permissions) {
        try {
            rolesFacade.addRole(roleName, permissions);
            return new Response("Role " + roleName + " added successfully.", null);
        } catch (Exception e) {
            return new Response("Error adding role: " + e.getMessage());
        }
    }

    public int printPermissions(int roleId) {
        return rolesFacade.printPermissions(roleId);
    }

    public Response removePermissionFromRole(int roleId, int permissionIndex) {
        try {
            rolesFacade.removePermissionFromRole(roleId, permissionIndex);
            return new Response("Permission removed successfully.", null);
        } catch (Exception e) {
            return new Response("Error removing permission: " + e.getMessage());
        }
    }

    public Response addPermissionToRole(RoleDTO roleDTO, ArrayList<String> permissions) {
        Role role = new Role(roleDTO.getRoleId(), roleDTO.getRoleName());
        try {
            rolesFacade.addPermissionToRole(role, permissions);
            return new Response("Permission added successfully.", null);
        } catch (Exception e) {
            return new Response("Error adding permission: " + e.getMessage());
        }
    }


    public Response printRolePermissions(int roleId) {
        try {
            rolesFacade.printRolePermissions(roleId);
            return new Response("Permissions retrieved successfully.", null);
        } catch (Exception e) {
            return new Response("Error retrieving permissions: " + e.getMessage());
        }
    }

    public Response removeRole(int roleId) {
        try {
            rolesFacade.removeRole(roleId);
            return new Response("Role removed successfully.", null);
        } catch (Exception e) {
            return new Response("Error removing role: " + e.getMessage());
        }
    }

    public int getLastRoleID() {
        return rolesFacade.getLastRoleId();
    }

    public void checkIfRoleExists(String roleName) {
        rolesFacade.checkIfRoleExists(roleName);
    }

    public Response getRolesResponse() {
        List<RoleDTO> roles = getRoles();
        StringBuilder sb = new StringBuilder();
        for (RoleDTO role : roles) {
            sb.append(role.toString()).append("\n");
        }
        return new Response(sb.toString(), null);
    }

    public Response getRolePermissions(int roleId) {
        try {
            ArrayList<String> rolePer = rolesFacade.getRolePermissions(roleId);
            return new Response(rolePer);
        }
        catch (Exception e) {
            return new Response("Error retrieving role permissions: " + e.getMessage());
        }
    }
}
