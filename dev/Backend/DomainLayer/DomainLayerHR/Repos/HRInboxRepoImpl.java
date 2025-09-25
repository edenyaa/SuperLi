package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.InboxMessageDTO;
import Backend.DataAccessLayer.DAO.HRInboxDAO;
import Backend.DataAccessLayer.Controllers.JdbcHRInboxDAO;

import java.sql.SQLException;
import java.util.LinkedList;

public class HRInboxRepoImpl implements HRInboxRepository {
    private final HRInboxDAO hrInboxDAO;

    public HRInboxRepoImpl() {
        hrInboxDAO = new JdbcHRInboxDAO();
    }

    @Override
    public int insertAndGetId(InboxMessageDTO dto) {
        return hrInboxDAO.getIdAndInsert(dto);
    }

    @Override
    public void markAsRead(int messageId) {
        hrInboxDAO.markAsRead(messageId);
    }

    @Override
    public LinkedList<InboxMessageDTO> selectAll() throws SQLException {
        return hrInboxDAO.getAll();
    }

    @Override
    public void deleteAll() throws SQLException {
        hrInboxDAO.deleteAll();
    }

    @Override
    public void insert(InboxMessageDTO item) throws SQLException {
        hrInboxDAO.insert(item);
    }

    @Override
    public void update(InboxMessageDTO item) throws SQLException {
        hrInboxDAO.update(item);
    }

    @Override
    public void delete(InboxMessageDTO item) throws SQLException {
        hrInboxDAO.delete(item);
    }

    @Override
    public InboxMessageDTO select(Integer messageId) throws SQLException {
        return hrInboxDAO.getBy(messageId);
    }
}
