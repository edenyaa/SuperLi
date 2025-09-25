package Backend.DataAccessLayer.DAO;

import Backend.DTO.InboxMessageDTO;

public interface HRInboxDAO extends DAO<InboxMessageDTO, Integer> {
    int getIdAndInsert(InboxMessageDTO message);
    void markAsRead(int messageId);
    void clearAll();
}
