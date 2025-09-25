package Backend.DTO;

import Backend.DomainLayer.DomainLayerHR.HRDL;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class HRDTO extends EmployeeDTO {
    private int unreadMessagesCount;
    private int readMessagesCount;
    private int totalMessages;

    public HRDTO(String id, String fullName,String password, LocalDate startDate,
                 ArrayList<RoleDTO> positions, double salary,
                 String bankAccount, int monthlyWorkHours, int seniority,
                 List<String> licenseTypes,
                 int unreadMessagesCount, int readMessagesCount) {
        super(id, fullName,password, startDate, positions, salary, bankAccount,
                monthlyWorkHours, seniority, licenseTypes,null);
        this.unreadMessagesCount = unreadMessagesCount;
        this.readMessagesCount = readMessagesCount;
        this.totalMessages = unreadMessagesCount + readMessagesCount;
    }

    public HRDTO(HRDL hr) {
        super(hr);
        this.unreadMessagesCount = hr.getUnreadMessagesCount();
        this.readMessagesCount = hr.getReadMessagesCount();
        this.totalMessages = unreadMessagesCount + readMessagesCount;
    }

    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public int getReadMessagesCount() {
        return readMessagesCount;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nUnread Messages: " + unreadMessagesCount +
                "\nRead Messages: " + readMessagesCount +
                "\nTotal Messages: " + totalMessages;
    }
}
