package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.InboxMessageDTO;

public interface HRInboxRepository extends Repository<InboxMessageDTO, Integer> {
    int insertAndGetId(InboxMessageDTO dto);

    void markAsRead(int messageId);

}
