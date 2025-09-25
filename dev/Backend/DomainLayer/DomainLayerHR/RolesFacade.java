package Backend.DomainLayer.DomainLayerHR;

import Backend.DTO.RoleDTO;
import Backend.DomainLayer.DomainLayerHR.Repos.RoleRepoImpl;
import Backend.DomainLayer.DomainLayerHR.Repos.RoleRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RolesFacade {

    private List<Role> RolesList;
    private int lastRoleId;
    private final RoleRepository roleRepo;

    private static final RolesFacade rolesFacade = new RolesFacade();

    public static RolesFacade getInstance() {
        return rolesFacade;
    }

    private RolesFacade() {
        // Initialize the roles list with predefined roles
        RolesList = new ArrayList<>();
        // Adding roles to the list
        RolesList.add(new Role("HRManager"));
        RolesList.add(new Role("ShiftManager"));
        RolesList.add(new Role("Cashier"));
        RolesList.add(new Role("Driver"));
        RolesList.add(new Role("WareHouse Worker"));
        RolesList.add(new Role("TransportManager"));
        RolesList.add(new Role("SystemManager"));
        RolesList.add(new Role("InventoryManager"));

        lastRoleId = 8;
        roleRepo = new RoleRepoImpl();
    }

    public void insertRolesToDatabase() {
        try {
            LinkedList<RoleDTO> existingRoles = roleRepo.selectAll();
            LinkedList<Integer> existingRoleIds = new LinkedList<>();
            for (RoleDTO roleDTO : existingRoles) {
                existingRoleIds.add(roleDTO.getRoleId());
            }
            for (Role role : RolesList) {
                if (!existingRoleIds.contains(role.getRoleId())) {
                    roleRepo.insert(new RoleDTO(role));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Role> getRoles() {
        return RolesList;
    }

    public Role getRoleById(int id) {
        for (Role role : RolesList) {
            if (role.getRoleId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role not found.");
    }

    public void addRole(String roleName, ArrayList<String> permissions) {
        try {
            if (getRoleByName(roleName) != null) {
                throw new IllegalArgumentException("Role already exists.");
            }
            lastRoleId++;
            Role newRole = new Role(lastRoleId, roleName, permissions);
            roleRepo.insert(new RoleDTO(newRole.getRoleId(), newRole.getRoleName()));
            RolesList.add(newRole);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public void removeRole(int roleId) {
        Role role = getRoleById(roleId);
        if (role == null) throw new IllegalArgumentException("Role does not exist.");
        try {
            roleRepo.delete(new RoleDTO(role.getRoleId(), role.getRoleName()));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        RolesList.remove(role);
    }

    public Role getRoleByName(String roleName) {
        for (Role role : RolesList) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return null;
    }


    public void printRoles() {
        for (Role role : RolesList) {
            if (role.getRoleName().equalsIgnoreCase("HRManager")) continue;
            if (role.getRoleName().equalsIgnoreCase("SystemManager")) continue;
            System.out.println("Role ID: " + role.getRoleId() + ", Role Name: " + role.getRoleName());
        }
    }

    public int getNumberOfRoles() {
        return RolesList.size();
    }

    public int printPermissions(int roleId) {
        Role role = getRoleById(roleId);
        int index = 0;
        if (role != null) {
            System.out.println("Permissions for role " + role.getRoleName() + ":");
            for (String permission : role.getPermissions()) {
                index++;
                System.out.println(index + "." + permission);
            }
            return index; // Return the number of permissions
        }
        throw new IllegalArgumentException("Role not found.");
    }

    public void removePermissionFromRole(int roleId, int permissionIndex) {
        Role role = getRoleById(roleId);
        if (role == null) throw new IllegalArgumentException("Role does not exist.");
        if (permissionIndex < 0 || permissionIndex >= role.getPermissions().size()) throw new IllegalArgumentException("Invalid permission index.");
        role.removePermission(permissionIndex);
    }

    public void addPermissionToRole(Role role, ArrayList<String> permissions) {
        if (role == null) throw new IllegalArgumentException("Role does not exist.");
        if (permissions == null || permissions.isEmpty()) throw new IllegalArgumentException("Permissions cannot be null or empty.");
        role.addPermission(permissions);
    }

    public void printRolePermissions(int roleId) {
        Role role = getRoleById(roleId);
        if (role == null) throw new IllegalArgumentException("Role does not exist.");
        role.printRolePermissions();

    }

    public ArrayList<String> getRolePermissions(int roleId) {
        Role role = getRoleById(roleId);
        if (role == null) throw new IllegalArgumentException("Role does not exist.");
        return role.getPermissions();
    }

    public int getLastRoleId() {
        return lastRoleId;
    }

    public void checkIfRoleExists(String roleName) {
        for (Role role : RolesList) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                throw new IllegalArgumentException("Role already exists.");
            }
        }
    }

    public void loadData() {
        insertRolesToDatabase();
        try {
            LinkedList<RoleDTO> existingRoles = roleRepo.selectAll();
            for (RoleDTO roleDTO : existingRoles) {
                Role role = new Role(roleDTO.getRoleId(), roleDTO.getRoleName());
                if (RolesList.contains(role)) {
                    continue;
                }
                RolesList.add(role);
                if (role.getRoleId() > lastRoleId) {
                    lastRoleId = role.getRoleId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteData() {
        try{
            roleRepo.deleteAll();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
