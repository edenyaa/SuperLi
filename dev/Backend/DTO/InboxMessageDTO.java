package Backend.DTO;

public class InboxMessageDTO {
    private int id;
    private String sender;
    private String message;
    private boolean isRead;



    public InboxMessageDTO(int id,String sender, String message, boolean isRead) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public String getSender() { return sender; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
}