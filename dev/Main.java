import Backend.PresentationLayerHR.LoginScreen;
import Backend.PresentationLayerHR.TransportationWindowCLI;
import Backend.PresentationLayerHR.TransportationWindowGUI;
import Backend.PresentationLayerHRGUI.LoadDataPanel;
import Backend.PresentationLayerHRGUI.LoginFrame;
import Backend.ServiceLayer.ServiceLayerHR.PermissionLevel;
import Backend.ServiceLayer.SuperService;
import presentationLayer.InventoryMenuGui;
import presentationLayer.ManagerMenuGui;
import presentationLayer.SupplyMenuGui;
import presentationLayer.UIController;
import util.DatabaseSeeder;

import javax.swing.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            showMenuWithoutArgs();
        }
        if (args.length == 2) {
            String Mode = args[0];
            String Role = args[1];
            switch (Mode.toUpperCase()) {
                case "CLI":
                    loadDataOrNot(new Scanner(System.in));
                    PermissionLevel level;
                    try{
                        level = PermissionLevel.valueOf(Role.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Unknown role: " + Role);
                        return;
                    }
                    switch (level) {
                        case HRMANAGER-> runHRWithLogin();
                        case TRANSPORTMANAGER->runTransportManagerByName(Role);
                        case TRANSPORTEMPLOYEE -> runTransportEmployeeByName(Role);
                        case REGULAREMPLOYEE -> runRegularEmployeeByName(Role);
                       case WAREHOUSEEMPLOYEE -> runWarehouseEmployeeByName(Role);
                       case STOREMANAGER -> runStoreManagerByName(Role);
                       case SUPPLIERMANAGER -> runSupplierManagerByName(Role);
                    }
                    break;
                case "GUI":
                    PermissionLevel guiLevel;
                    try {
                        guiLevel = PermissionLevel.valueOf(Role.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Unknown role: " + Role);
                        return;
                    }
                    Runnable openGUI = switch (guiLevel) {
                        case HRMANAGER -> Main::RunHRloginGUI;
                        case TRANSPORTMANAGER -> Main::runTransportationManagerGui;
                        case TRANSPORTEMPLOYEE -> Main::runTransportationEmployeeGui;
                        case REGULAREMPLOYEE -> Main::RunHRloginGUI;
                        case WAREHOUSEEMPLOYEE -> Main::runWarehouseEmployeeGui;
                        case STOREMANAGER -> Main::runStoreManagerGui;
                        case SUPPLIERMANAGER -> Main::runSupplyManagerGui;
                        case SYSTEMMANAGER -> null;
                    };
                    loadDataOrNotGUI(openGUI);
                    break;
            }
        }
    }
    private static void runTransportationManagerGui() {
        SwingUtilities.invokeLater(() -> {
            new TransportationWindowGUI().TransportManagerMenu(); // Launches the GUI window
        });
    }
    private static void runTransportationEmployeeGui() {
        SwingUtilities.invokeLater(() -> {
            new TransportationWindowGUI().EmployeeMenu(); // Launches the GUI window
        });
    }

    private static void loadDataOrNotGUI(Runnable nextStep) {
        SwingUtilities.invokeLater( () -> {
            JFrame frame = new JFrame("Load Data");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setContentPane( new LoadDataPanel(() -> {
                frame.dispose();
                nextStep.run();
            }));

            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void runSupplyManagerGui(){
        SwingUtilities.invokeLater(() -> {
            new SupplyMenuGui(); // Launches the GUI window
        });
    }

    private static void runWarehouseEmployeeGui(){
        SwingUtilities.invokeLater(() -> {
            new InventoryMenuGui(); // Launches the GUI window
        });
    }

    private static void runStoreManagerGui(){
        SwingUtilities.invokeLater(() -> {
            new ManagerMenuGui(); // Launches the GUI window
        });
    }

    private static void RunHRloginGUI(){
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
    
    private static void runHRWithLogin() {
        Scanner scanner = new Scanner(System.in);
        LoginScreen loginScreen = new LoginScreen();
        System.out.println("=== HR Manager Login ===");
        while (true) {
            System.out.print("ID (or 'exit'): ");
            String id = scanner.nextLine().trim();
            if (id.equalsIgnoreCase("exit")) return;
            System.out.print("Password: ");
            String pw = scanner.nextLine().trim();
            loginScreen.login(id, pw);

        }
    }

    private static void runTransportManagerByName(String name) {
        System.out.println("Hello Transport Manager");
        new TransportationWindowCLI().TransportManagerMenu();
    }

    private static void runTransportEmployeeByName(String name) {
        System.out.println("Hello Transport Employee");
        new TransportationWindowCLI().EmployeeMenu();
    }
    private static void runWarehouseEmployeeByName(String name) {
        System.out.println("Hello Warehouse Employee");
            new UIController().inventoryMenu();
    }

    private static void runStoreManagerByName(String name) {
        System.out.println("Hello Store Manager");
        new UIController().managerMenu();
    }
    private static void runSupplierManagerByName(String name) {
        System.out.println("Hello Supplier Manager");
        new UIController().supplyMenu();
    }


    private static void runRegularEmployeeByName(String name) {
        Scanner scanner = new Scanner(System.in);
        LoginScreen loginScreen = new LoginScreen();
        System.out.println("=== employee Login ===");
        while (true) {
            System.out.print("ID (or 'exit'): ");
            String id = scanner.nextLine().trim();
            if (id.equalsIgnoreCase("exit")) return;
            System.out.print("Password: ");
            String pw = scanner.nextLine().trim();
            loginScreen.login(id, pw);

        }
    }
        private static void showMenuWithoutArgs(){
            Scanner scanner = new Scanner(System.in);
            loadDataOrNot(scanner);
            System.out.println("choose an moodle option:");
            System.out.println("1. suppliers/inventory");
            System.out.println("2. HR/Transportation");
            System.out.println("0. Exit");
            System.out.print("Your choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println("You chose suppliers/inventory.");
                    new UIController().start();
                    break;
                case "2":
                    System.out.println("You chose HR/Transportation.");
                    startMain(new String[0]);
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    showMenuWithoutArgs();
            }
        }

    private static void loadDataOrNot(Scanner scanner) {
        System.out.println("========Welcome to SuperLi========");
        System.out.println("Do you wish to load persistent data? (yes/no)");
        String answer = scanner.nextLine().trim().toLowerCase();
        while (!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("no")) {
            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            answer = scanner.nextLine().trim().toLowerCase();
        }
        if (answer.equals("yes")) {
            System.out.println("Loading persistent data...");
            new LoginScreen().addDefaultManagers();
            new SuperService().loadData();
            DatabaseSeeder.seed();
            System.out.println("Data loaded successfully.");
        }
        else  if (answer.equals("no")) {
            System.out.println("No persistent data loaded.");
            new LoginScreen().addDefaultManagers();
        }
    }

    public static void startMain(String[] args){
        Scanner scanner = new Scanner(System.in);
        LoginScreen loginScreen = new LoginScreen();
        System.out.println("=== Welcome to the Employee Management System ===");

        while (true) {
            System.out.print("\nEnter your ID (or type 'exit' to quit): ");
            String id = scanner.nextLine();
            if (id.equalsIgnoreCase("exit")) {
                System.out.println("Exiting system...");
                break;
            }
            System.out.print("Enter your password (4 letters only): ");
            String password = scanner.nextLine();
            loginScreen.login(id ,password);
        }
        scanner.close();
    }
}


