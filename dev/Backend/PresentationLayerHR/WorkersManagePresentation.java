package Backend.PresentationLayerHR;

import Backend.DTO.*;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import Backend.ServiceLayer.ServiceLayerHR.HRService.RolesService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.WorkersManageService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.DeliveryEmployeeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

public class WorkersManagePresentation {
    private final WorkersManageService workersService;
    private final RolesService rolesService;

    public WorkersManagePresentation() {
        this.workersService = new WorkersManageService();
        this.rolesService = new RolesService();
    }

    public void manageWorkersMenu() {
        int choice;
        do {
            printMenu();
            choice = validateChoice();
            chooseAction(choice);
        } while (choice != 6);
    }

    private int validateChoice() {
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        while (choice < 1 || choice > 6) {
            System.out.println("Invalid choice. Please select again.");
            choice = scanner.nextInt();
        }
        return choice;
    }

    private void chooseAction(int choice) {
        switch (choice) {
            case 1: hireEmployee(); break;
            case 2: fireEmployee(); break;
            case 3: editEmployee(); break;
            case 4: showEmployee(); break;
            case 5: showFormerEmployee(); break;
            case 6: System.out.println("Exiting Workers Management Menu..."); break;
            default: System.out.println("Invalid choice.");
        }
    }


    private void hireEmployee() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Employee ID (9 digits):");
            String employeeId = scanner.nextLine();
            System.out.println("Enter Employee Password:");
            String password = scanner.nextLine();
            System.out.println("Enter Employee Full Name:");
            String fullName = scanner.nextLine();
            LocalDate startDate = LocalDate.now();
            System.out.println("Enter Employee Position:");
            ArrayList<RoleDTO> positions = chooseRoles(scanner);
            List<String> licenseType = new ArrayList<>();

            for (RoleDTO role : positions) {
                if (role.getRoleName().equalsIgnoreCase("Driver")) {
                    System.out.println("Enter license types for driver (separated by commas):");
                    String input = scanner.nextLine();
                    String[] licenses = input.split(",");
                    for (String license : licenses) {
                        licenseType.add(license.trim());
                    }
                    break;
                }
            }
            System.out.println("Enter Salary:");
            Double salary = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("Enter Bank Account (10 digits):");
            String bankAccount = scanner.nextLine();
            System.out.println("Enter Monthly Hours:");
            int monthlyHours = scanner.nextInt();
            int hoursWorked = 0;
            LocationDTO location;
            if (requiresLocation(positions)) {
                location = chooseLocation(scanner);
            } else {
                location = LocationDTO.GENERAL_LOCATION;
            }

            Response res = workersService.hireEmployee(
                    employeeId, password, fullName, startDate,
                    positions, salary, bankAccount, monthlyHours, hoursWorked, licenseType, location
            );
            printResponse(res);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid data.");
        }
    }

    private ArrayList<RoleDTO> chooseRoles(Scanner scanner) {
        ArrayList<RoleDTO> roles = new ArrayList<>();
        System.out.println("Available roles:");
        rolesService.printRoles();
        int choice = -1;
        do {
            System.out.println("Enter role ID to add (or 0 to finish):");
            choice = scanner.nextInt();
            scanner.nextLine();
            while (choice < 0 || choice > rolesService.getNumberOfRoles() || choice == 1 || choice == 7) {
                System.out.println("Invalid choice. Please select again.");
                choice = scanner.nextInt();
                scanner.nextLine();
            }
            if (choice != 0) roles.add(rolesService.getRoleById(choice));
        } while (choice != 0);
        return roles;
    }
    private LocationDTO chooseLocation(Scanner scanner) {
        List<LocationDTO> locations = DeliveryEmployeeService.getInstance().getAllAvailableLocations();
        if (locations.isEmpty()) {
            throw new IllegalStateException("No locations are currently available.");
        }

        while (true) {
            System.out.println("Available Locations:");
            for (int i = 0; i < locations.size(); i++) {
                LocationDTO loc = locations.get(i);
                System.out.println((i + 1) + ". " + loc.getAreaName() + " - " + loc.getAddress());
            }

            System.out.print("Choose location by number: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (choice >= 1 && choice <= locations.size()) {
                    return locations.get(choice - 1);
                } else {
                    System.out.println("Invalid selection. Please choose a number between 1 and " + locations.size() + ".");
                }
            } catch (Exception e) {
                scanner.nextLine(); // consume bad input
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void fireEmployee() {
        String id = getGoodId();
        printResponse(workersService.fireEmployee(id));
    }

    private void editEmployee() {
        Scanner scanner = new Scanner(System.in);
        String id = getGoodId();
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n--- Edit Employee Menu ---");
            System.out.println("1. Update Password");
            System.out.println("2. Update Full Name");
            System.out.println("3. Update Positions");
            System.out.println("4. Update Salary");
            System.out.println("5. Update Bank Account");
            System.out.println("6. Update Monthly Hours");
            System.out.println("7. Update License Type");
            System.out.println("8. Update Location");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input.");
                scanner.next();
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter new password (4 characters):");
                    printResponse(workersService.updatePassword(id, scanner.nextLine()));
                    break;
                case 2:
                    System.out.println("Enter new full name:");
                    printResponse(workersService.updateFullName(id, scanner.nextLine()));
                    break;
                case 3:
                    ArrayList<RoleDTO> positions = chooseRoles(new Scanner(System.in));
                    printResponse(workersService.updatePositions(id, positions));
                    break;
                case 4:
                    System.out.println("Enter salary:");
                    if (scanner.hasNextDouble()) {
                        double salary = scanner.nextDouble();
                        scanner.nextLine();
                        printResponse(workersService.updateSalary(id, salary));
                    } else {
                        System.out.println("Invalid salary.");
                        scanner.next();
                    }
                    break;
                case 5:
                    System.out.println("Enter bank account (10 digits):");
                    printResponse(workersService.updateBankAccount(id, scanner.nextLine()));
                    break;
                case 6:
                    System.out.println("Enter monthly hours:");
                    if (scanner.hasNextInt()) {
                        int hours = scanner.nextInt();
                        scanner.nextLine();
                        printResponse(workersService.updateMonthlyHours(id, hours));
                    } else {
                        System.out.println("Invalid hours.");
                        scanner.next();
                    }
                    break;
                case 7:
                    List<String> licenseTypes = new ArrayList<>();
                    String licenseInput;
                    while (true) {
                        System.out.println("Enter license type (or 'done' to finish):");
                        licenseInput = scanner.nextLine().trim();
                        if (licenseInput.equalsIgnoreCase("done")) {
                            break;
                        }
                        // reject empty or numeric-only input
                        if (licenseInput.isEmpty() || licenseInput.matches("\\d+")) {
                            System.out.println("Invalid license type. Please enter non-empty letters only.");
                            continue;
                        }
                        licenseTypes.add(licenseInput);
                    }
                    printResponse(workersService.updateLicenseType(id, licenseTypes));
                    break;
                case 8:
                    try {
                        Response response = workersService.showEmployee(id);
                        if (!response.isSuccess() || !(response.getReturnValue() instanceof EmployeeDTO)) {
                            System.out.println("Failed to retrieve employee details.");
                            break;
                        }

                        EmployeeDTO employee = (EmployeeDTO) response.getReturnValue();
                        ArrayList<RoleDTO> employeeRoles = employee.getPositions();

                        if (!requiresLocation(employeeRoles)) {
                            System.out.println("This employee's roles do not require a location assignment.");
                            break;
                        }

                        List<LocationDTO> locations = DeliveryEmployeeService.getInstance().getAllAvailableLocations();
                        if (locations.isEmpty()) {
                            System.out.println("No available locations found.");
                            break;
                        }

                        System.out.println("Available Locations:");
                        for (int i = 0; i < locations.size(); i++) {
                            System.out.println((i + 1) + ". " + locations.get(i).getAreaName() + " - " + locations.get(i).getAddress());
                        }

                        System.out.print("Choose location number: ");
                        int locationChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (locationChoice < 1 || locationChoice > locations.size()) {
                            System.out.println("Invalid location selection.");
                            break;
                        }

                        LocationDTO selectedLocation = locations.get(locationChoice - 1);
                        printResponse(workersService.updateLocation(id, selectedLocation));

                    } catch (Exception e) {
                        System.out.println("An error occurred while updating location: " + e.getMessage());
                    }
                    break;

                case 0:
                    System.out.println("Exiting edit mode.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void showEmployee() {
        String id = getGoodId();
        printResponse(workersService.showEmployee(id));
    }

    private void showFormerEmployee() {
        String id = getGoodId();
        printResponse(workersService.showFormerEmployee(id));
    }

    private void printMenu() {
        System.out.println("\n--- Workers Management Menu ---");
        System.out.println("1. Hire Employee");
        System.out.println("2. Fire Employee");
        System.out.println("3. Edit Employee");
        System.out.println("4. Show Employee");
        System.out.println("5. Show former Employees");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private String getGoodId() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Employee ID:");
        String id = scanner.nextLine();
        while (!(id.length() == 9 && id.matches("[0-9]+"))) {
            System.out.println("Invalid ID. Please enter 9 digit number:");
            id = scanner.nextLine();
        }
        return id;
    }

    private void printResponse(Response response) {
        if (response.getReturnValue() != null)
            System.out.println(response.getReturnValue());
        else
            System.out.println(response.getErrorMsg());
    }
    private boolean requiresLocation(ArrayList<RoleDTO> roles) {
        for (RoleDTO role : roles) {
            String name = role.getRoleName();
            if (name.equalsIgnoreCase("Driver") ||
                    name.equalsIgnoreCase("WareHouse Worker")) {
                return true;
            }
        }
        return false;
    }

}