package Backend.PresentationLayerHR;

import java.util.Scanner;

public class HRPresentation {
    private final ShiftsManagePresentation shiftsManagePresentation;
    private final WorkersManagePresentation workersManagePresentation;
    private final HRInboxPresentation inboxPresentation;

    public HRPresentation(){
        this.shiftsManagePresentation = new ShiftsManagePresentation();
        this.workersManagePresentation = new WorkersManagePresentation();
        this.inboxPresentation = new HRInboxPresentation();
    }

    public void ShowHRMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("Welcome to the HR Menu, select an option:\n" +
                    "1. Workers Management\n" +
                    "2. Shifts Management\n" +
                    "3. View inbox messages\n" +
                    "4. Exit System");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1: workersManagePresentation.manageWorkersMenu(); break;
                case 2: shiftsManagePresentation.ShiftsManagePresentationMenu(); break;
                case 3:
                    System.out.println("Inbox Menu:");
                    inboxPresentation.showInboxMenu(scanner);
                    break;
                case 4: System.out.println("Exiting System"); break;
                default: System.out.println("Invalid choice. Please select again.");
            }
        } while (choice != 4);
    }
}