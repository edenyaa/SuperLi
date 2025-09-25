package Backend.DataAccessLayer.DAO;

import Backend.DTO.RoleDTO;

public interface RoleDAO extends DAO<RoleDTO, Integer> {
    RoleDTO getByName(String name);
}