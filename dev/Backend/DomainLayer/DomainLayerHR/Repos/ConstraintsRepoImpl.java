package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.ConstraintsDTO;
import Backend.DataAccessLayer.DAO.ConstraintsDAO;
import Backend.DataAccessLayer.Controllers.JdbcConstraintsDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

public class ConstraintsRepoImpl implements ConstraintsRepository {

    private final ConstraintsDAO constraintsDAO;

    public ConstraintsRepoImpl() {
        this.constraintsDAO = new JdbcConstraintsDAO();
    }


    @Override
    public ConstraintsDTO findByIdAndDate(String id, int timeAtDay, LocalDate date) {
        return constraintsDAO.get(id, timeAtDay, date);
    }

    @Override
    public LinkedList<ConstraintsDTO> findEmpCons(String employeeId) {
        return constraintsDAO.getEmpCons(employeeId);
    }

    @Override
    public LinkedList<ConstraintsDTO> selectAll() throws SQLException {
        return constraintsDAO.getAll();
    }

    @Override
    public void deleteAll() throws SQLException {
        constraintsDAO.deleteAll();
    }

    @Override
    public void insert(ConstraintsDTO item) throws SQLException {
        constraintsDAO.insert(item);
    }

    @Override
    public void update(ConstraintsDTO item) throws SQLException {
        constraintsDAO.update(item);
    }

    @Override
    public void delete(ConstraintsDTO item) throws SQLException {
        constraintsDAO.delete(item);
    }

    @Override
    public ConstraintsDTO select(String empId) throws SQLException {
        return constraintsDAO.getBy(empId);
    }
}
