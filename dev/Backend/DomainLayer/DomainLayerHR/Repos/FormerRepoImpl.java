package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.FormerEmployeeDTO;
import Backend.DataAccessLayer.Controllers.JdbcFormerEmployeeDAO;
import Backend.DataAccessLayer.DAO.FormerEmployeeDAO;

import java.sql.SQLException;
import java.util.LinkedList;

public class FormerRepoImpl implements FormerRepository {
    private final FormerEmployeeDAO formerDAO;

    public FormerRepoImpl() {
        formerDAO = new JdbcFormerEmployeeDAO();
    }
    @Override
    public LinkedList<FormerEmployeeDTO> selectAll() throws SQLException {
        return formerDAO.getAll();
    }

    @Override
    public void deleteAll() throws SQLException {
        formerDAO.deleteAll();
    }

    @Override
    public void insert(FormerEmployeeDTO item) throws SQLException {
        formerDAO.insert(item);
    }

    @Override
    public void update(FormerEmployeeDTO item) throws SQLException {
        formerDAO.update(item);
    }

    @Override
    public void delete(FormerEmployeeDTO item) throws SQLException {
        formerDAO.delete(item);
    }

    @Override
    public FormerEmployeeDTO select(String empId) throws SQLException {
        return formerDAO.getBy(empId);
    }
}
