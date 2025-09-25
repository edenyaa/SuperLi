package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.RoleDTO;

public interface RoleRepository extends Repository<RoleDTO, Integer> {
    RoleDTO findByName(String name);
}
