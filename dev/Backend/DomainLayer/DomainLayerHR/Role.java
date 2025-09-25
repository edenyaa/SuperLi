package Backend.DomainLayer.DomainLayerHR;

import java.util.ArrayList;

public class Role {
    private int roleId;
    private String roleName;
    private ArrayList<String> permissions;

    public Role(int roleId, String roleName, ArrayList<String> permissions) {
        this.roleName = roleName;
        this.roleId = roleId;
        this.permissions = permissions;
    }

    public Role(int roleId, String roleName) {
        this.roleName = roleName;
        this.roleId = roleId;
        this.permissions = new ArrayList<>();
    }

    public Role(String nameOfRole) {
        this.roleName = nameOfRole;
        this.roleId = getRoleIdFromName(roleName);
        this.permissions = getPermissionsFromName(roleName);
    }


    public ArrayList<String> getPermissionsFromName(String roleName) {
        roleName = roleName.toLowerCase();
        switch (roleName) {
            case "hrmanager":
                return hrPermissions();
            case "shiftmanager":
                return shiftManagerPermissions();
            case "cashier":
                return cashierPermissions();
            case "driver":
                return driverPermissions();
            case "warehouse worker":
                return wareHouseWorkerPermissions();
            case "transportmanager":
                return transportManagerPermissions();
            case "systemmanager":
                return systemManagerPermissions();
            case "inventorymanager":
                return inventoryManagerPermissions();
            default:
                return new ArrayList<>();
        }
    }
    private ArrayList<String> systemManagerPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("hire");
        permissions.add("fire");
        permissions.add("edit_employee");
        permissions.add("view_all_shifts");
        permissions.add("publish_shift");
        permissions.add("view_all_employees");
        permissions.add("manage_transportation");
        return permissions;
    }
    private ArrayList<String> transportManagerPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("manage transportation");
        return permissions;
    }

    private ArrayList<String> wareHouseWorkerPermissions() {
        return new ArrayList<>();
    }

    private ArrayList<String> driverPermissions() {
        return new ArrayList<>();
    }

    private ArrayList<String> cashierPermissions() {
        return new ArrayList<>();
    }

    private ArrayList<String> shiftManagerPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("manage shift");
        permissions.add("watch employees");
        return permissions;
    }
    private ArrayList<String> inventoryManagerPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("manage inventory");
        return permissions;
    }


    private ArrayList<String> hrPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("manage employees and shifts schedules");
        return permissions;
    }

    public int getRoleIdFromName(String roleName) {
        roleName = roleName.toLowerCase();
        switch (roleName) {
            case "hrmanager":
                return 1;
            case "shiftmanager":
                return 2;
            case "cashier":
                return 3;
            case "driver":
                return 4;
            case "warehouse worker":
                return 5;
            case "transportmanager":
                return 6;
            case "systemmanager":
                return 7;
            case "inventorymanager":
                return 8;
            default:
                return -1; // Invalid role name
        }
    }

    public int getRoleId() {
        return roleId;
    }
    public String getRoleName() {
        return roleName;
    }
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public void addPermission(ArrayList<String> newPermissions) {
        if (newPermissions == null || newPermissions.isEmpty()) {
            throw new IllegalArgumentException("New permissions cannot be null or empty.");
        }
        for (String perm : newPermissions) {
            if (!hasPermission(perm)) permissions.add(perm);
        }
    }

    public void removePermission(int permissionIndex) {
        if (permissionIndex >= 0 && permissionIndex < permissions.size()) {
            permissions.remove(permissionIndex);
        } else {
            throw new IllegalArgumentException("Invalid permission index.");
        }
    }

    @Override
    public String toString() {
        return roleName;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return roleName.equalsIgnoreCase(role.roleName);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(roleId);
        result = 31 * result + roleName.toLowerCase().hashCode(); // case-insensitive
        return result;
    }

    public void printRolePermissions() {
        System.out.println("Permissions for role " + roleName + ":");
        int index = 1;
        for (String perm : permissions) {
            System.out.println(index + ". " + perm);
            index++;
        }
    }

}
