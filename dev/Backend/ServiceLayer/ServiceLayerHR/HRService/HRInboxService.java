package Backend.ServiceLayer.ServiceLayerHR.HRService;

import Backend.DomainLayer.DomainLayerHR.HRDL;
import Backend.ServiceLayer.ServiceLayerHR.Response;

public class HRInboxService {

    private final HRDL hr = HRDL.getInstance();

    public Response sendMessageToHR(String sender, String content) {
        try {
            if (sender == null || sender.isEmpty() || content == null || content.isEmpty()) {
                throw new IllegalArgumentException("Sender and content must not be empty.");
            }
            hr.addMessageFromModule(sender, content);
            return new Response("Message sent to HR successfully.");
        } catch (IllegalArgumentException e) {
            return new Response("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return new Response("Failed to send message to HR: " + e.getMessage());
        }
    }

    public Response viewMessages(boolean onlyUnread, boolean onlyRead, int howMany) {
        return hr.viewMessages(onlyUnread, onlyRead, howMany);
    }
    public Response getInboxStats() {
        return hr.getInboxStats();
    }
    public Response viewAllMessages() {
        return hr.viewMessages(false, false, hr.getTotalMessagesCount());
    }

    public void clearInbox() {
        hr.clearAllMessages();
    }
}