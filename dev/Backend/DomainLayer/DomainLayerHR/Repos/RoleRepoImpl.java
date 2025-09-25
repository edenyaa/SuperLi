package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.RoleDTO;
import Backend.DataAccessLayer.Controllers.JdbcRoleDAO;
import Backend.DataAccessLayer.DAO.RoleDAO;

import java.sql.SQLException;
import java.util.LinkedList;

public class RoleRepoImpl implements RoleRepository {
    private final RoleDAO roleDAO;

    public RoleRepoImpl() {
        this.roleDAO = new JdbcRoleDAO();
    }

    @Override
    public RoleDTO findByName(String name) {
        return roleDAO.getByName(name);
    }

    @Override
    public LinkedList<RoleDTO> selectAll() throws SQLException {
        return roleDAO.getAll();
    }

    @Override
    public void deleteAll() throws SQLException {
        roleDAO.deleteAll();
    }

    @Override
    public void insert(RoleDTO item) throws SQLException {
        roleDAO.insert(item);
    }

    @Override
    public void update(RoleDTO item) throws SQLException {
        roleDAO.update(item);
    }

    @Override
    public void delete(RoleDTO item) throws SQLException {
        roleDAO.delete(item);
    }

    @Override
    public RoleDTO select(Integer roleId) throws SQLException {
        return roleDAO.getBy(roleId);
    }
}
