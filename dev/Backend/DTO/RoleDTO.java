package Backend.DTO;

import Backend.DomainLayer.DomainLayerHR.Role;

public class RoleDTO {
    private int id;
    private String name;

    public RoleDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDTO(Role role) {
        this.id = role.getRoleId();
        this.name = role.getRoleName();
    }

    public int getRoleId() { return id; }
    public String getRoleName() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RoleDTO roleDTO = (RoleDTO) obj;
        return this.id == roleDTO.id && this.name.equalsIgnoreCase(roleDTO.name);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + name.toLowerCase().hashCode(); // case-insensitive
        return result;
    }

    public String toString() {
        return "Role: " + name + ", id: " + id;
    }
}
