package Backend.PresentationLayerHR;

import Backend.ServiceLayer.ServiceLayerHR.HRService.HRInboxService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.util.Scanner;

public class HRInboxPresentation {

    private final HRInboxService inboxService = new HRInboxService();

    public void showInboxMenu(Scanner scanner) {
        while (true) {
            System.out.println("What do you want to read?");
            System.out.println("1. Unread messages");
            System.out.println("2. Read messages");
            System.out.println("3. All messages");
            System.out.println("4. Inbox statistics");
            System.out.println("5. Clear inbox");
            System.out.println("6. Back to HR Menu");
            System.out.print("Enter your choice: ");
            String subChoice = scanner.nextLine();
            switch (subChoice) {
                case "1": {
                    viewFilteredMessages(scanner, true, false);
                    break;
                }
                case "2": {
                    viewFilteredMessages(scanner, false, true);
                    break;
                }
                case "3": {
                    Response response = inboxService.viewAllMessages();
                    System.out.println(response.getReturnValue() != null
                            ? response.getReturnValue()
                            : response.getErrorMsg());
                    break;
                }
                case "4": {
                    Response stats = inboxService.getInboxStats();
                    System.out.println(stats.getReturnValue() != null
                            ? stats.getReturnValue()
                            : stats.getErrorMsg());
                    break;
                }
                case "5": {
                    inboxService.clearInbox();
                    System.out.println("Inbox cleared.");
                    break;
                }
                case "6":
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }
    private void viewFilteredMessages(Scanner scanner, boolean onlyUnread, boolean onlyRead) {
        System.out.print("How many messages would you like to read? ");
        int howMany;
        try {
            howMany = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Returning to menu.");
            return;
        }
        Response response = inboxService.viewMessages(onlyUnread, onlyRead, howMany);
        System.out.println(response.getReturnValue() != null
                ? response.getReturnValue()
                : response.getErrorMsg());
    }
}

