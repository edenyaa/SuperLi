package Backend.PresentationLayerHR;

import Backend.DTO.*;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import Backend.ServiceLayer.ServiceLayerHR.HRService.RolesService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.ShiftsManageService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class ShiftsManagePresentation {

    private final ShiftsManageService shiftsService;
    private final RolesService rolesService;

    public ShiftsManagePresentation() {
        this.shiftsService = new ShiftsManageService();
        this.rolesService = new RolesService();
    }

    public void ShiftsManagePresentationMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            printMenu();
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> ManageShiftMenu(scanner);
                case 2 -> ManageWeeklyShiftMenu(scanner);
                case 3 -> ManageWeeklyConstraintsMenu(scanner);
                case 4 -> manageCompanyRoles();
                case 0 -> System.out.println("Exiting Shift Management Menu");
                default -> System.out.println("Invalid choice. Please select again.");
            }
        } while (choice != 0);
    }

    private void ManageWeeklyConstraintsMenu(Scanner scanner) {
        int choice = -1;
        do {
            printConstraintsMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> printResponse(shiftsService.viewWeeklyConstraints());
                case 2 -> printResponse(shiftsService.setNextWeekConstraints());
                case 3 -> changeDeadline();
                case 0 -> System.out.println("Exiting Weekly Constraints Management Menu");
                default -> System.out.println("Invalid choice. Please select again.");
            }
        } while (choice != 0);
    }

    public void ManageWeeklyShiftMenu(Scanner scanner){
        int choice = -1;
        do {
            printWeeklyShiftsMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> printResponse(shiftsService.viewPublishedWeeklyShifts());
                case 2 -> printResponse(shiftsService.viewUnpublishedWeeklyShifts());
                case 3 -> printResponse(shiftsService.publishWeeklyShift());
                case 0 -> System.out.println("Exiting Weekly Shift Management Menu");
                default -> System.out.println("Invalid choice. Please select again.");
            }
        } while (choice != 0);
    }

    public void ManageShiftMenu(Scanner scanner){
        int choice = -1;
        do {
            printShiftMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> printResponse(shiftsService.addShift(getDay(), getTime()));
                case 2 -> printResponse(shiftsService.removeShift(getDay(), getTime()));
                case 3 -> editShift();
                case 4 -> showShift();
                case 0 -> System.out.println("Exiting Shift Management Menu");
            }
        }
        while (choice != 0);
    }

    public void showShift(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you wish to view published or unpublished shifts?");
        System.out.println("1 for published, 2 for unpublished");
        System.out.print("Enter your choice: ");
        int weekChoice = 0;
        weekChoice = scanner.nextInt();
        scanner.nextLine();
        while (weekChoice != 1 && weekChoice != 2) {
            System.out.print("Invalid Choice Try Again.");
            weekChoice = scanner.nextInt();
            scanner.nextLine();
        }
        if (weekChoice == 1) {
            printResponse(shiftsService.showShiftDetails(getDay(), getTime(), true));
        }
        else {
            printResponse(shiftsService.showShiftDetails(getDay(), getTime(), false));
        }
    }

    private void printShiftMenu() {
        System.out.println("\n--- Single Shift Management Menu ---");
        System.out.println("1. Add Shift");
        System.out.println("2. Remove Shift");
        System.out.println("3. Edit Shift");
        System.out.println("4. Show Shift Details");
        System.out.println("0. Exit Single Shift Management Menu");
    }

    private void printConstraintsMenu(){
        System.out.println("\n--- Weekly Constraints Management Menu ---");
        System.out.println("1. View Weekly Constraints");
        System.out.println("2. Set Next Week Constraints");
        System.out.println("3. Change Constraints Deadline");
        System.out.println("0. Exit Weekly Constraints Management Menu");
    }

    private void printWeeklyShiftsMenu(){
        System.out.println("\n--- Weekly Shifts Management Menu ---");
        System.out.println("1. View published Weekly Shift");
        System.out.println("2. View Unpublished Weekly Shift");
        System.out.println("3. Publish Weekly Shift");
        System.out.println("0. Exit Weekly Shifts Management Menu");
    }

    private void manageCompanyRoles() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            printRoleManagementMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice){
                case 1 -> printResponse(addRole(scanner));
                case 2 -> printResponse(removeRole(scanner));
                case 3 -> printResponse(rolesService.printRoles());
                case 4 -> printResponse(getRolePermissions(scanner));
                case 5 -> printResponse(addRolePermissions(scanner));
                case 6 -> printResponse(removeRolePermissions(scanner));
                case 0 -> System.out.println("Exiting Roles Management Menu");
                default -> System.out.println("Invalid choice. Please select again.");
            }
        }
        while (choice != 0);
    }

    private Response removeRolePermissions(Scanner scanner) {
        rolesService.printRoles();
        System.out.print("Enter Role ID to remove permission from: ");
        int roleId = validateRoleId(scanner);
        System.out.println("Enter permission number to remove (type 'exit' to finish): ");
        int permissionsCount = -1;
        try { permissionsCount = rolesService.printPermissions(roleId);}
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new Response(e.getMessage());
        }
        int permissionIndex = scanner.nextInt();
        scanner.nextLine();
        while (permissionIndex < 1 || permissionIndex > permissionsCount) {
            System.out.print("Invalid permission number. Please enter again: ");
            permissionIndex = scanner.nextInt();
            scanner.nextLine();
        }
        permissionIndex--;
        return rolesService.removePermissionFromRole(roleId, permissionIndex);
    }

    private Response addRolePermissions(Scanner scanner) {
        rolesService.printRoles();
        System.out.print("Enter Role ID to add permission: ");
        int roleId = validateRoleId(scanner);
        RoleDTO role = null;
        try {
            role = rolesService.getRoleById(roleId);
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new Response(e.getMessage());
        }
        ArrayList<String> permissions = choosePermissions(scanner);;
        return rolesService.addPermissionToRole(role, permissions);
    }

    private Response getRolePermissions(Scanner scanner) {
        rolesService.printRoles();
        System.out.print("Enter Role ID to view permissions: ");
        int roleId = validateRoleId(scanner);
        return rolesService.printRolePermissions(roleId);
    }

    private Response removeRole(Scanner scanner) {
        rolesService.printRoles();
        System.out.print("Enter Role ID to remove: ");
        int roleId = validateRoleId(scanner);
        return rolesService.removeRole(roleId);
    }

    public void printRoleManagementMenu() {
        System.out.println("\n--- Roles Management Menu ---");
        System.out.println("1. Add Role");
        System.out.println("2. Remove Role");
        System.out.println("3. Show All Roles");
        System.out.println("4. Show Role Permissions");
        System.out.println("5. Add Permission To Role");
        System.out.println("6. Remove Permission From Role");
        System.out.println("0. Exit Roles Management Menu");
    }

    private Response addRole(Scanner scanner) {
        System.out.print("Enter Role Name: ");
        String roleName = scanner.nextLine();
        validateRoleName(roleName);
        try {
            rolesService.checkIfRoleExists(roleName);
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new Response(e.getMessage());
        }
        ArrayList<String> permissions = enterPermissions();
        return rolesService.addRole(roleName, permissions);
    }

    private ArrayList<String> enterPermissions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to add special permissions to the new role?");
        System.out.println("1 for yes, 2 for no");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        while (choice != 1 && choice != 2) {
            System.out.print("Invalid Choice - please enter again: ");
            choice = scanner.nextInt();
            scanner.nextLine();
        }
        if (choice == 2) {
            return new ArrayList<>();
        }
        return choosePermissions(scanner);
    }

    private ArrayList<String> choosePermissions(Scanner scanner) {
        ArrayList<String> permissions = new ArrayList<>();
        System.out.println("Type a Role permission:");
        String permissionString = scanner.nextLine();
        while (!(permissionString.equalsIgnoreCase("exit"))){
            permissions.add(permissionString);
            System.out.println("Type another permission or 'exit' to finish:");
            permissionString = scanner.nextLine();
        }
        return permissions;
    }

    private void validateRoleName(String roleName) {
        if (roleName.isEmpty() || !roleName.matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException("Role name must contain only letters.");
        }
    }

    private void editShift() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you wish to edit published or unpublished shifts?");
        System.out.println("1 for published, 2 for unpublished");
        int weekChoice = scanner.nextInt();
        scanner.nextLine();
        while (weekChoice != 1 && weekChoice != 2) {
            System.out.println("Invalid choice. Please enter again: ");
            weekChoice = scanner.nextInt();
            scanner.nextLine();
        }
        if (weekChoice == 1) {
            editShift(scanner, true);
        } else {
            editShift(scanner, false);
        }
    }

    public void editShift(Scanner scanner, boolean published) {
        try {
            System.out.print("Enter day (1 = Sunday, 7 = Saturday): ");
            int day = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter shift (1 = Morning, 2 = Evening): ");
            int time = scanner.nextInt();
            scanner.nextLine();

            if (published) {
                LocalDate today = LocalDate.now();
                LocalDate weekStart = shiftsService.getPublishedWeekStart();
                LocalDate shiftDate = weekStart.plusDays(day - 1);
                if (!shiftDate.isAfter(today))
                    throw new IllegalArgumentException("Cannot edit shifts on the same day or in the past.");
            }
            while (true) {
                System.out.println("\n=== Editing Shift (" + day + ", " + (time == 1 ? "Morning" : "Evening") + ") ===");
                printResponse(shiftsService.showShiftDetails(day, time, published));
                printEditShiftMenu();
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> {
                        System.out.print("Enter Employee ID to assign: ");
                        String empId = scanner.nextLine();
                        validateEmployeeId(empId);
                        rolesService.printRoles();
                        System.out.println("Enter Role ID to assign: ");
                        int roleId = validateRoleId(scanner);
                        RoleDTO role = rolesService.getRoleById(roleId);
                        printResponse(shiftsService.assignEmployeeToShift(day, time, empId, role, published));
                    }
                    case "2" -> {
                        System.out.print("Enter Employee ID to remove: ");
                        String empId = scanner.nextLine();
                        validateEmployeeId(empId);
                        printResponse(shiftsService.removeEmployeeFromShift(day, time, empId, published));
                    }
                    case "3" -> {
                        rolesService.printRoles();
                        System.out.print("Enter Role ID to add: ");
                        int roleId = validateRoleId(scanner);
                        RoleDTO role = rolesService.getRoleById(roleId);
                        System.out.print("Enter number of employees required for this role: ");
                        int numOfEmployees = scanner.nextInt();
                        scanner.nextLine();
                        printResponse(shiftsService.addRoleToShift(day, time, role, numOfEmployees, published));
                    }
                    case "4" -> {
                        rolesService.printRoles();
                        System.out.print("Enter Role ID to remove: ");
                        int roleId = validateRoleId(scanner);
                        RoleDTO role = rolesService.getRoleById(roleId);
                        printResponse(shiftsService.removeRoleFromShift(day, time, role, published));
                    }
                    case "5" -> {
                        rolesService.printRoles();
                        System.out.println("Enter Role ID to update: ");
                        int roleId = validateRoleId(scanner);
                        RoleDTO role = rolesService.getRoleById(roleId);
                        System.out.print("Enter new number of employees required for this role: ");
                        int numOfEmployees = scanner.nextInt();
                        scanner.nextLine();
                        printResponse(shiftsService.updateNumOfEmployeesForRole(day, time, role, numOfEmployees, published));
                    }
                    case "0" -> {
                        System.out.println("Finished editing shift.");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void validateEmployeeId(String empId) {
        if (!empId.matches("[0-9]+") || empId.length() != 9) {
            throw new IllegalArgumentException("Employee ID must contain only 9 digits.");
        }
        shiftsService.checkIfEmployeeExists(empId);
    }

    private void printEditShiftMenu(){
        System.out.println("\nOptions:");
        System.out.println("1. Assign Employee");
        System.out.println("2. Remove Employee");
        System.out.println("3. Add Required Role");
        System.out.println("4. Remove Required Role");
        System.out.println("5. Update Number of Required Employee For Role");
        System.out.println("0. Exit");
        System.out.print("Your choice: ");
    }

    private void changeDeadline() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter day of week (e.g., MONDAY): ");
            DayOfWeek day = DayOfWeek.valueOf(scanner.nextLine().toUpperCase());
            System.out.print("Enter hour (0–23): ");
            int hour = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter minute (0–59): ");
            int minute = Integer.parseInt(scanner.nextLine());
            printResponse(shiftsService.changeConstraintsDeadline(day, LocalTime.of(hour, minute)));
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private int getDay() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter day (1 = Sunday, 7 = Saturday): ");
        return scanner.nextInt();
    }

    private int getTime() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter shift (1 = Morning, 2 = Evening): ");
        return scanner.nextInt();
    }

    private void printMenu() {
        System.out.println("\n--- Shifts Management Menu ---");
        System.out.println("1. Manage A Single Shift Menu");
        System.out.println("2. Manage Weekly Shifts Menu");
        System.out.println("3. Manage Weekly Constraints");
        System.out.println("4. Manage Company Roles");
        System.out.println("0. Exit Shifts Management Menu");
        System.out.print("Enter your choice: ");
    }

    private void printResponse(Response response) {
        if (response.getReturnValue() != null)
            System.out.println(response.getReturnValue());
        else
            System.out.println(response.getErrorMsg());
    }

    private int validateRoleId(Scanner scanner) {
        int choice = scanner.nextInt();
        scanner.nextLine();
        while (choice < 1 || choice > rolesService.getLastRoleID()) {
            System.out.print("Invalid Role ID. Please enter again: ");
            choice = scanner.nextInt();
            scanner.nextLine();
        }
        return choice;
    }
}
