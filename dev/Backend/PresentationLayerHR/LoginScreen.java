package Backend.PresentationLayerHR;

import Backend.DTO.EmployeeDTO;
import Backend.DomainLayer.DomainLayerHR.HRDL;
import Backend.ServiceLayer.SuperService;
import Backend.ServiceLayer.ServiceLayerHR.LoginService;
import Backend.ServiceLayer.ServiceLayerHR.PermissionLevel;
import Backend.ServiceLayer.ServiceLayerHR.PermissionService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import java.util.Scanner;

public class LoginScreen {

    private LoginService ls;
    private boolean loadedData = false;
    private final SuperService superService;

    public LoginScreen() {
        superService = new SuperService();
        superService.loadLoginData();
        ls = new LoginService();
    }

    public void login(String id, String password) {
        try {
            Response response = ls.login(id, password);
            EmployeeDTO employee = (EmployeeDTO) response.getReturnValue();
            PermissionLevel permission = PermissionService.getPermissionLevel(employee.getPositions());
            Scanner scanner = new Scanner(System.in);
//            withOrWithoutData();
            switch (permission) {
                case SYSTEMMANAGER -> {
                    int choice = -1;
                    while (true) {
                        System.out.println("You have System Manager privileges. Choose login mode:");
                        System.out.println("1. HR Manager Menu");
                        System.out.println("2. Employee Menu");
                        System.out.println("3. TransportManager Menu");
                        System.out.println("4. Transport Employee Menu");
                        System.out.println("5.delete all data");
                        System.out.println("0. Return to start menu");
                        System.out.print("Enter your choice: ");
                        if (scanner.hasNextInt()) {
                            choice = scanner.nextInt();
                            scanner.nextLine();
                            switch (choice) {
                                case 1 -> {
                                    HRPresentation hrPresentation = new HRPresentation();
                                    hrPresentation.ShowHRMenu();
                                }
                                case 2 -> {
                                    EmployeePresentation empUI = new EmployeePresentation(employee);
                                    empUI.ShowEmployeeMenu();
                                }
                                case 3 -> {
                                    TransportationWindowCLI tw = new TransportationWindowCLI();
                                    tw.TransportManagerMenu();
                                }
                                case 4 -> {
                                    TransportationWindowCLI tw = new TransportationWindowCLI();
                                    tw.EmployeeMenu();
                                }
                                case 5 -> {
                                    ls.deleteAllData();
                                    System.out.println("All data has been deleted.");
                                }
                                case 0 -> {
                                    System.out.println("Returning to start menu...");
                                    return;
                                }
                                default -> System.out.println("Invalid choice. Please try again.");
                            }
                        } else {
                            scanner.nextLine();
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }
                }
                case HRMANAGER -> {
                    int choice = -1;
                    while (true) {
                        System.out.println("You have HR Manager privileges. Choose login mode:");
                        System.out.println("1. HR Manager Menu");
                        System.out.println("2. Employee Menu");
                        System.out.println("0. Return to start menu");
                        System.out.print("Enter your choice: ");
                        if (scanner.hasNextInt()) {
                            choice = scanner.nextInt();
                            scanner.nextLine();
                            if (choice == 1) {
                                HRPresentation hrPresentation = new HRPresentation();
                                hrPresentation.ShowHRMenu();
                            } else if (choice == 2) {
                                EmployeePresentation empUI = new EmployeePresentation(employee);
                                empUI.ShowEmployeeMenu();
                            } else if (choice == 0) {
                                System.out.println("Returning to start menu...");
                                return;
                            } else {
                                System.out.println("Invalid choice. Please try again.");
                            }
                        } else {
                            scanner.nextLine();
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }
                }
                case TRANSPORTMANAGER -> {
                    int choice = -1;
                    while (true) {
                        System.out.println("You have Transport Manager privileges. Choose login mode:");
                        System.out.println("1. Transport Manager Menu");
                        System.out.println("2. Regular Employee Menu");
                        System.out.println("0. Return to start menu");
                        System.out.print("Enter your choice: ");
                        if (scanner.hasNextInt()) {
                            choice = scanner.nextInt();
                            scanner.nextLine();
                            if (choice == 1) {
                                TransportationWindowCLI tw = new TransportationWindowCLI();
                                tw.TransportManagerMenu();
                            } else if (choice == 2) {
                                EmployeePresentation empUI = new EmployeePresentation(employee);
                                empUI.ShowEmployeeMenu();
                            } else if (choice == 0) {
                                System.out.println("Returning to start menu...");
                                return;
                            } else {
                                System.out.println("Invalid choice. Please try again.");
                            }
                        } else {
                            scanner.nextLine();
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }
                }
                case TRANSPORTEMPLOYEE -> {
                    int choice = -1;
                    while (true) {
                        System.out.println("You are a Transport Employee. Choose login mode:");
                        System.out.println("1. Transport Employee Menu");
                        System.out.println("2. Regular Employee Menu");
                        System.out.println("0. Return to start menu");
                        System.out.print("Enter your choice: ");
                        if (scanner.hasNextInt()) {
                            choice = scanner.nextInt();
                            scanner.nextLine();
                            if (choice == 1) {
                                TransportationWindowCLI tw = new TransportationWindowCLI();
                                tw.EmployeeMenu();
                            } else if (choice == 2) {
                                EmployeePresentation empUI = new EmployeePresentation(employee);
                                empUI.ShowEmployeeMenu();
                            } else if (choice == 0) {
                                System.out.println("Returning to start menu...");
                                return;
                            } else {
                                System.out.println("Invalid choice. Please try again.");
                            }
                        } else {
                            scanner.nextLine();
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }
                }
                case REGULAREMPLOYEE -> {
                    EmployeePresentation empUI = new EmployeePresentation(employee);
                    empUI.ShowEmployeeMenu();
                }
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Login failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during login: " + e.getMessage());
        }
    }

    public void withOrWithoutData() {
        if (loadedData) { return;}
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to load data? (yes/no)");
        String choice = scanner.nextLine().trim().toLowerCase();
        while (!choice.equals("yes") && !choice.equals("no")) {
            System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
            choice = scanner.nextLine().trim().toLowerCase();
        }
        if (choice.equals("yes")) {
            SuperService superService = new SuperService();
            superService.loadData();
        }
        if (choice.equals("no")) {
            System.out.println("Do you wish to delete all data or just go on empty local data?");
            System.out.println("yes for delete data no for empty local data");
            String choice2 = scanner.nextLine().trim().toLowerCase();
            while (!choice2.equals("yes") && !choice2.equals("no")) {
                System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
                choice2 = scanner.nextLine().trim().toLowerCase();
            }
            if (choice2.equals("yes")){
                SuperService superService = new SuperService();
                superService.deleteData();
                addDefaultManagers();
            }
        }
        loadedData = true;
    }

    public void addDefaultManagers() {
        if (!loadedData) {
            System.out.println("=== Adding Default Managers ===");
            ls.createDefaultManagers();
            System.out.println("=== Default Managers Created ===");
            loadedData = true;
        }
        else {
            ls.createDefaultManagers();
        }
    }

    public LoginService getLoginService() {
        return ls;
    }
    public SuperService getSuperService() {
        return superService;
    }
}

