package Backend.PresentationLayerHR;

import Backend.DTO.*;
import Backend.ServiceLayer.ServiceLayerHR.EmloyeeService.EmployeeManageService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePresentation {
    private final EmployeeManageService employeeService;
    private boolean isShiftManager;

    public EmployeePresentation(EmployeeDTO employeeDTO) {
        this.employeeService = new EmployeeManageService(employeeDTO);
        RoleDTO smRole = new RoleDTO(2, "ShiftManager");
        isShiftManager = employeeDTO.getPositions().contains(smRole);
    }

    public void ShowEmployeeMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printEmployeeMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter new Full Name: ");
                    String fullName = scanner.nextLine();
                    printResponse(employeeService.editName(fullName));
                    break;
                case "2":
                    System.out.print("Enter new Bank Account: ");
                    String bankAccount = scanner.nextLine();
                    printResponse(employeeService.editBankAccount(bankAccount));
                    break;
                case "3":
                    System.out.print("Enter new Password: ");
                    String password = scanner.nextLine();
                    printResponse(employeeService.editPassword(password));
                    break;
                case "4":
                    List<TimeSlotDTO> constraints = collectConstraints(scanner);
                    printResponse(employeeService.updateConstraints(constraints));
                    break;
                case "5":
                    printResponse(employeeService.uploadConstraints());
                    break;
                case "6":
                    printResponse(employeeService.showDetails());
                    break;
                case "7":
                    printResponse(employeeService.showWeeklyShift());
                    break;
                case "8":
                    printResponse(employeeService.showShiftHistory());
                    break;
                case "9":
                    if (isShiftManager) {
                        System.out.print("Enter Employee ID to show details: ");
                        String empId = scanner.nextLine();
                        printResponse(employeeService.showEmployeeDetails(empId));
                    } else {
                        System.out.println("You do not have permission to view other employees' details.");
                    }
                    break;
                case "0":
                    System.out.println("Exiting Employee Menu...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
    }

    public void printEmployeeMenu() {
        System.out.println("=== Employee Menu ===");
        System.out.println("1. Edit Full Name");
        System.out.println("2. Edit Bank Account");
        System.out.println("3. Edit Password");
        System.out.println("4. Update Constraint");
        System.out.println("5. Upload Constraint");
        System.out.println("6. Show Details");
        System.out.println("7. Show Weekly Shift");
        System.out.println("8. Show Shifts History");
        if (isShiftManager) {
            System.out.println("9. Show An Employee Details");
        }
        System.out.println("0. Exit");
        System.out.print("Please choose an option: ");
    }

    private List<TimeSlotDTO> collectConstraints(Scanner scanner) {
        List<TimeSlotDTO> selectedSlots = new ArrayList<>();
        int numConstraints = -1;
        while (numConstraints < 0 || numConstraints > 12) {
            System.out.print("Enter number of constraints (0–12): ");
            String input = scanner.nextLine();
            try {
                numConstraints = Integer.parseInt(input);
                if (numConstraints < 0 || numConstraints > 12) {
                    System.out.println("Invalid number. Please enter a number between 0 and 12.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        for (int i = 0; i < numConstraints; i++) {
            int day = -1;
            while (day < 1 || day > 7) {
                System.out.print("Enter day (1=Sunday to 7=Saturday): ");
                String dayInput = scanner.nextLine();
                try {
                    day = Integer.parseInt(dayInput);
                    if (day < 1 || day > 7) {
                        System.out.println("Invalid day. Must be between 1 and 7.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
            int shift = -1;
            while (shift < 1 || shift > 2) {
                System.out.print("Enter shift (1=Morning, 2=Evening): ");
                String shiftInput = scanner.nextLine();
                try {
                    shift = Integer.parseInt(shiftInput);
                    if (shift < 1 || shift > 2) {
                        System.out.println("Invalid shift. Must be 1 or 2.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
            selectedSlots.add(new TimeSlotDTO(day, shift));
        }
        if (!validateNumberOfConstraints(selectedSlots)) {
            System.out.println("Too many working days selected. Please try again.");
            return collectConstraints(scanner);
        }
        return selectedSlots;
    }


    private boolean validateNumberOfConstraints(List<TimeSlotDTO> selectedSlots) {
        int numberOfWeekDays = 0;
        for (int i = 1; i <= 7; i++) {
            if (checkIfDayExists(i, selectedSlots)) numberOfWeekDays++;
        }
        return numberOfWeekDays <= 6;
    }

    private boolean checkIfDayExists(int dayNumber, List<TimeSlotDTO> selectedSlots) {
        for (TimeSlotDTO timeSlot : selectedSlots) {
            if (timeSlot.getDayNumber() == dayNumber) {
                return true;
            }
        }
        return false;
    }

    private void printResponse(Response response) {
        if (response.getReturnValue() != null)
            System.out.println(response.getReturnValue());
        else
            System.out.println(response.getErrorMsg());
    }
}