package presentationLayer;

import serviceLayer.StorageController;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

public class UIController {

    private final Scanner scanner;
    private StorageController storageController;

    public UIController() {
        this.scanner = new Scanner(System.in);
        this.storageController = new StorageController();
    }

    public void start() { 
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    supplyMenu();
                    break;
                case 2:
                    inventoryMenu();
                    break;
                case 3:
                    managerMenu();
                    break;
                case 4:
                    forwardDays();
                    break;    
                case 0:
                    System.out.println("Exiting Storage Controller. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n=== Storage Main Menu ===");
        System.out.println("1. Supply Management");
        System.out.println("2. Inventory Management");
        System.out.println("3. Manager");
        System.out.println("4. Simulate time pass");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public void supplyMenu() {
        while (true) {
            System.out.println("\nSupply Menu:");
            System.out.println("1. Show all suppliers");
            System.out.println("2. Show supplier's supply days");
            System.out.println("3. Show supplier's contacts");
            System.out.println("4. Add new supplier");
            System.out.println("5. Remove a supplier");
            System.out.println("6. Add agreement to supplier");
            System.out.println("7. Remove agreement from supplier");
            System.out.println("8. Add contact to supplier");
            System.out.println("9. Add a product to an agreement of a supplier");
            System.out.println("10. Remove a product from an agreement of a supplier");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = getUserChoice();
            scanner.nextLine(); // consume newline

            if (choice == 0) break;

            switch (choice) {
                case 1:
                    System.out.println(this.storageController.showAllSuppliers());
                    break;
                case 2:
                    System.out.println("Enter Supplier ID:");
                    String supplierID2 = scanner.nextLine().trim();
                    // Validate that the supplier ID only contains digits
                    while (!supplierID2.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Invalid input. Please enter numbers&letters only:");
                    supplierID2 = scanner.nextLine().trim();
                    }
                    System.out.println(storageController.showSupplierSupplyDays(supplierID2));
                    break;
                case 3:
                    System.out.println("Enter Supplier ID:");
                    String supplierID3 = scanner.nextLine().trim();
                    // Validate that the supplier ID only contains digits
                    while (!supplierID3.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Invalid input. Please enter numbers&letters only:");
                    supplierID3 = scanner.nextLine().trim();
                    }
                    System.out.println(storageController.showSupplierContacts(supplierID3));
                    break;
                case 4:
                    addNewSupplierMenu();
                    break;
                case 5:
                    System.out.println("Enter Supplier ID:");
                    String supplierID5 = scanner.nextLine().trim();
                    // Validate that the supplier ID only contains digits
                    while (!supplierID5.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Invalid input. Please enter numbers&letters only:");
                    supplierID5 = scanner.nextLine().trim();
                    }
                    System.out.println(storageController.removeSupplier(supplierID5));
                    break;
                case 6:
                    addAgreementToSupplierMenu();
                    break;
                case 7:
                    String supplierId7;
                    while (true) {
                        System.out.print("Enter supplier ID: ");
                        supplierId7 = scanner.nextLine().trim();
                        if (supplierId7.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid supplier ID. Use digits&letters only.");
                    }
                    String agreementId;
                    while (true) {
                        System.out.print("Enter agreement ID: ");
                        agreementId = scanner.nextLine().trim();
                        if (agreementId.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid agreement ID. Use digits&letters only.");
                    }
                    System.out.println(this.storageController.removeAgreementForSupplier(supplierId7, agreementId));
                    break;
                case 8:
                    String supplierId8;
                    while (true) {
                        System.out.print("Enter supplier ID: ");
                        supplierId8 = scanner.nextLine().trim();
                        if (supplierId8.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid supplier ID. Use digits&letters only.");
                    }
                    String contactName;
                    while (true) {
                        System.out.print("Enter contact name (letters only): ");
                        contactName = scanner.nextLine().trim();
                        if (contactName.matches("[a-zA-Z ]+")) break;
                        System.out.println("Invalid name. Use letters only.");
                    }
                    String contactPhone;
                    while (true) {
                        System.out.print("Enter contact phone (digits, '-', '+'): ");
                        contactPhone = scanner.nextLine().trim();
                        if (contactPhone.matches("[\\d\\-+]+")) break;
                        System.out.println("Invalid phone. Use digits, '-' and '+'.");
                    }
                    String contactEmail;
                    while (true) {
                        System.out.print("Enter contact email: ");
                        contactEmail = scanner.nextLine().trim();
                        if (contactEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) break;
                        System.out.println("Invalid email format.");
                    }
                    System.out.println(this.storageController.addNewContactForSupplier(supplierId8, contactName, contactPhone, contactEmail));
                    break;
                case 9:
                    addProductToAgreementMenu();
                    break;
                case 10:
                    String supplierId10;
                    while (true) {
                        System.out.print("Enter supplier ID: ");
                        supplierId10 = scanner.nextLine().trim();
                        if (supplierId10.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid supplier ID. Use digits&letters only.");
                    }
                    String agreementId10;
                    while (true) {
                        System.out.print("Enter agreement ID: ");
                        agreementId10 = scanner.nextLine().trim();
                        if (agreementId10.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid agreement ID. Use digits&letters only.");
                    }
                    String productNum;
                    while (true) {
                        System.out.print("Enter product number (letters and numbers): ");
                        productNum = scanner.nextLine().trim();
                        if (productNum.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid product number. Use alphanumeric characters only.");
                    }
                    System.out.println(storageController.removeProductFromSupplierAgreement(supplierId10,agreementId10,productNum));
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    public void inventoryMenu() {
        while (true) {
            System.out.println("\nInventory Menu:");
            System.out.println("1. Get Product by Barcode");
            System.out.println("2. Update product's stock");
            System.out.println("3. Register Purchase");
            System.out.println("4. Add New Product");
            System.out.println("5. Show reservations from a supplier");
            System.out.println("6. Add reservation to supplier");
            System.out.println("7. Add weekly reservation to supplier");
            System.out.println("8. Update reservation items");
            System.out.println("9. Add Expiry Report");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = getUserChoice();
            scanner.nextLine();

            if (choice == 0) break;

            switch (choice) {
                case 1:
                    String productNum1;
                    while (true) {
                        System.out.print("Enter product number (letters and numbers): ");
                        productNum1 = scanner.nextLine().trim();
                        if (productNum1.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid product number. Use alphanumeric characters only.");
                    }
                    System.out.println(this.storageController.showProductByBarcode(productNum1));
                    break;
                case 2:
                    String productNum2;
                    while (true) {
                        System.out.print("Enter product number (letters and numbers): ");
                        productNum2 = scanner.nextLine().trim();
                        if (productNum2.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid product number. Use alphanumeric characters only.");
                    }
                    int quantity;
                    while (true) {
                        System.out.print("Enter new stock for product " + productNum2 + ": ");
                        String quantityStr = scanner.nextLine().trim();
                        if (!quantityStr.matches("\\d+")) {
                            System.out.println("Invalid stock. Use positive numbers only.");
                            continue;
                        }
                        quantity = Integer.parseInt(quantityStr);
                        break;
                    }
                    System.out.println(this.storageController.updateProductStock(productNum2, quantity));
                    break;
                case 3:
                    registerPurchaseMenu();
                    break;
                case 4:
                    addNewProductMenu();
                    break;
                case 5:
                    System.out.println("Enter Supplier ID:");
                    String supplierID5 = scanner.nextLine().trim();
                    // Validate that the supplier ID only contains digits
                    while (!supplierID5.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Invalid input. Please enter numbers&letters only:");
                    supplierID5 = scanner.nextLine().trim();
                    }
                    System.out.println(storageController.showAllSupplierReservations(supplierID5));
                    break;
                case 6:
                    addReservationToSupplierMenu();
                    break;
                case 7:
                    addWeeklyReservationToSupplierMenu();
                    break;
                case 8:
                    updateReservationItemsMenu();
                    break;
                case 9:
                    addExpiryReportMenu();
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
    public void managerMenu() {
        while (true) {
            System.out.println("\nManager Menu:");
            System.out.println("1. Get Product by Barcode");
            System.out.println("2. Generate Report by Category");
            System.out.println("3. Generate Insufficient Stock Report");
            System.out.println("4. Generate Expiry Reports from Last X Days");
            System.out.println("5. View All Expiry Reports");
            System.out.println("6. View Expiry Reports by Product");
            System.out.println("7. Reset All Expiry Reports");
            System.out.println("8. Get Most Demanded Items");
            System.out.println("9. Get Demand for Item");
            System.out.println("10. Apply Discount");
            System.out.println("11. Calculate Minimum Thresholds");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            int choice = getUserChoice();
            scanner.nextLine();

            if (choice == 0) break;

            switch (choice) {
                case 1:
                    String productNum1;
                    while (true) {
                        System.out.print("Enter product number (letters and numbers): ");
                        productNum1 = scanner.nextLine().trim();
                        if (productNum1.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid product number. Use alphanumeric characters only.");
                    }
                    System.out.println(this.storageController.showProductByBarcode(productNum1));
                    break;
                case 2: 
                    List<String> categoryNames = new ArrayList<>();
                    while (true) {
                        System.out.print("Enter category name (letters only), or type 'done' to finish: ");
                        String category = scanner.nextLine().trim();
                        if (category.equalsIgnoreCase("done")) break;
                        if (!category.matches("[a-zA-Z]+")) {
                        System.out.println("Invalid category name. Use letters only.");
                        continue;
                        }
                        categoryNames.add(category);
                    }
                    if (categoryNames.isEmpty()) {
                        System.out.println("No categories were entered.");
                        return;
                    }
                    System.out.println(this.storageController.showReportByCategory(categoryNames));
                    break;
                case 3: 
                    System.out.println(this.storageController.showInsufficiencyReport());
                    break;
                case 4:
                    int limit;
                    while (true) {
                        System.out.print("Show expiry reports from (...) last days: ");
                        String limitStr = scanner.nextLine().trim();
                        if (!limitStr.matches("\\d+")) {
                            System.out.println("Invalid input. Use positive numbers only.");
                            continue;
                        }
                        limit = Integer.parseInt(limitStr);
                        break;
                    }
                    System.out.println(this.storageController.showExpiryReport(limit));
                    break;
                case 5:
                    System.out.println(this.storageController.showAllExpiryReports());
                    break;
                case 6:
                    String productNum6;
                    while (true) {
                        System.out.print("Enter product number (letters and numbers): ");
                        productNum6 = scanner.nextLine().trim();
                        if (productNum6.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid product number. Use alphanumeric characters only.");
                    }
                    System.out.println(this.storageController.showExpiryReportsByProduct(productNum6));
                    break;
                case 7: 
                    System.out.println(this.storageController.resetAllExpiryReports());
                    break;
                case 8:
                    int demandLimit;
                    while (true) {
                        System.out.print("Show most demanded items from (...) last months: ");
                        String demandLimitStr = scanner.nextLine().trim();
                        if (!demandLimitStr.matches("\\d+")) {
                            System.out.println("Invalid input. Use positive numbers only.");
                            continue;
                        }
                        demandLimit = Integer.parseInt(demandLimitStr);
                        break;
                    }
                    System.out.println(this.storageController.showMostDemandedItems(demandLimit));
                    break;
                case 9:
                    String productNum9;
                    while (true) {
                        System.out.print("Enter product number (letters and numbers): ");
                        productNum9 = scanner.nextLine().trim();
                        if (productNum9.matches("[a-zA-Z0-9]+")) break;
                        System.out.println("Invalid product number. Use alphanumeric characters only.");
                    }
                    System.out.println(this.storageController.showDemandForItem(productNum9));
                    break;
                case 10:
                    applyDiscountMenu();
                    break;
                case 11:
                    System.out.println(this.storageController.calculateMinimums());
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    //.....Helper methods.....//
    public void addNewSupplierMenu() {
        System.out.println("=== Add New Supplier ===");
        // Supplier Name: Letters only
        String name;
        while (true) {
            System.out.print("Enter supplier name (letters only): ");
            name = scanner.nextLine().trim();
            if (name.matches("[a-zA-Z ]+")) break;
            System.out.println("Invalid name. Use letters only.");
        }
        // Address: Letters, numbers, and spaces
        String address;
        while (true) {
            System.out.print("Enter supplier address: ");
            address = scanner.nextLine().trim();
            if (address.matches("[\\w\\s,.\\-]+")) break;
            System.out.println("Invalid address. Use letters, numbers, and common punctuation.");
        }
        // Payment Type: TRANSFER, CASH, or CHECK
        String paymentType;
        while (true) {
            System.out.print("Enter payment type (TRANSFER, CASH, CHECK): ");
            paymentType = scanner.nextLine().trim().toUpperCase();
            if (paymentType.equals("TRANSFER") || paymentType.equals("CASH") || paymentType.equals("CHECK")) break;
            System.out.println("Invalid payment type. Must be TRANSFER, CASH, or CHECK.");
        }
        // Bank Account: Letters and numbers
        String bankAccount;
        while (true) {
            System.out.print("Enter bank account: ");
            bankAccount = scanner.nextLine().trim();
            if (bankAccount.matches("[a-zA-Z0-9]+")) break;
            System.out.println("Invalid bank account. Use letters and numbers only.");
        }
        // Contact Name: Letters only
        String contactName;
        while (true) {
            System.out.print("Enter contact name (letters only): ");
            contactName = scanner.nextLine().trim();
            if (contactName.matches("[a-zA-Z ]+")) break;
            System.out.println("Invalid contact name. Use letters only.");
        }
        // Contact Phone: Digits only
        String contactPhone;
        while (true) {
            System.out.print("Enter contact phone (digits only): ");
            contactPhone = scanner.nextLine().trim();
            if (contactPhone.matches("[+\\d\\- ]+")) break;
            System.out.println("Invalid phone number. Use digits only.");
        }
        // Contact Email: Basic email pattern
        String contactEmail;
        while (true) {
            System.out.print("Enter contact email: ");
            contactEmail = scanner.nextLine().trim();
            if (contactEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) break;
            System.out.println("Invalid email format.");
        }
        // Supply days: comma-separated (e.g. Monday,Wednesday,Friday)
        List<String> supplyDays;
        while (true) {
            System.out.print("Enter supply days (comma-separated, e.g., Monday,Wednesday): ");
            String supplyInput = scanner.nextLine().trim();
            String[] days = supplyInput.split(",");
            boolean allValid = true;
            supplyDays = new ArrayList<>();
            for (String day : days) {
                String trimmed = day.trim();
                if (trimmed.matches("(?i)Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday")) {
                    supplyDays.add(trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase());
                } else {
                    allValid = false;
                    break;
                }
            }
            if (allValid) break;
            System.out.println("Invalid day(s). Please enter valid weekday names (Sunday to Saturday).");
        }
        // Call service
        String result = storageController.addNewSupplier(name, address, paymentType, bankAccount,contactName, contactPhone, contactEmail, supplyDays);
        System.out.println(result);
    }

    public void addReservationToSupplierMenu() {
    System.out.println("=== Add Reservation to Supplier ===");
    // Supplier ID (numbers only)
    String supplierId;
    while (true) {
        System.out.print("Enter supplier ID: ");
        supplierId = scanner.nextLine().trim();
        if (supplierId.matches("[a-zA-Z0-9]+")) break;
        System.out.println("Invalid supplier ID. Use digits&letters only.");
    }
    // Agreement ID (numbers only)
    String agreementId;
    while (true) {
        System.out.print("Enter agreement ID: ");
        agreementId = scanner.nextLine().trim();
        if (agreementId.matches("[a-zA-Z0-9]+")) break;
        System.out.println("Invalid agreement ID. Use digits&letters only.");
    }
    List<String> productNums = new ArrayList<>();
    List<Integer> quantities = new ArrayList<>();
    System.out.println("Enter product barcodes and quantities (type 'done' to finish):");
    while (true) {
        // Product barcode: letters + digits
        System.out.print("Enter product barcode (or 'done' to finish): ");
        String barcodeInput = scanner.nextLine().trim();
        if (barcodeInput.equalsIgnoreCase("done")) break;
        if (!barcodeInput.matches("[a-zA-Z0-9]+")) {
            System.out.println("Invalid barcode. Use only letters and numbers.");
            continue;
        }
        // Quantity: positive integer
        int quantity;
        while (true) {
            System.out.print("Enter quantity for product " + barcodeInput + ": ");
            String quantityStr = scanner.nextLine().trim();
            if (!quantityStr.matches("\\d+")) {
                System.out.println("Invalid quantity. Use positive numbers only.");
                continue;
            }
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than zero.");
                continue;
            }
            break;
        }
        productNums.add(barcodeInput);
        quantities.add(quantity);
    }
    if (productNums.isEmpty()) {
        System.out.println("No products were entered. Reservation canceled.");
        return;
    }
    String result = storageController.makeNewReservation(supplierId, productNums, quantities, agreementId);
    System.out.println(result);
}
    
    public void addWeeklyReservationToSupplierMenu() {
        System.out.println("=== Add Weekly Reservation to Supplier ===");
        // Supplier ID
        String supplierId;
        while (true) {
            System.out.print("Enter supplier ID: ");
            supplierId = scanner.nextLine().trim();
            if (supplierId.matches("[a-zA-Z0-9]+")) break;
            System.out.println("Invalid supplier ID. Use digits&letters only.");
        }
        // Agreement ID
        String agreementId;
        while (true) {
            System.out.print("Enter agreement ID: ");
            agreementId = scanner.nextLine().trim();
            if (agreementId.matches("[a-zA-Z0-9]+")) break;
            System.out.println("Invalid agreement ID. Use digits&letters only.");
        }
        // Supply Day of Week
        DayOfWeek supplyDay = null;
        while (true) {
            System.out.print("Enter supply day of the week (e.g., monday, tuesday): ");
            String dayInput = scanner.nextLine().trim().toUpperCase();
            try {
                supplyDay = DayOfWeek.valueOf(dayInput);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid day. Please enter a valid day of the week (e.g., monday).");
            }
        }
        // Product barcodes and quantities
        List<String> productNums = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        System.out.println("Enter product barcodes and quantities (type 'done' to finish):");
        while (true) {
            System.out.print("Enter product barcode (or 'done' to finish): ");
            String barcodeInput = scanner.nextLine().trim();
            if (barcodeInput.equalsIgnoreCase("done")) break;
            if (!barcodeInput.matches("[a-zA-Z0-9]+")) {
                System.out.println("Invalid barcode. Use only letters and numbers.");
                continue;
            }
            int quantity;
            while (true) {
                System.out.print("Enter quantity for product " + barcodeInput + ": ");
                String quantityStr = scanner.nextLine().trim();
                if (!quantityStr.matches("\\d+")) {
                    System.out.println("Invalid quantity. Use positive numbers only.");
                    continue;
                }
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    System.out.println("Quantity must be greater than zero.");
                    continue;
                }
                break;
            }
            productNums.add(barcodeInput);
            quantities.add(quantity);
        }
        if (productNums.isEmpty()) {
            System.out.println("No products were entered. Weekly reservation canceled.");
            return;
        }
        String result = storageController.makeNewReservationTemplate(supplierId, productNums, quantities, agreementId, supplyDay);
        System.out.println(result);
    }

    public void updateReservationItemsMenu() {
    System.out.println("=== Update Reservation Item ===");
    // Supplier Name (letters only)
    String supplierName;
    while (true) {
        System.out.print("Enter supplier name (letters only): ");
        supplierName = scanner.nextLine().trim();
        if (supplierName.matches("[a-zA-Z ]+")) break;
        System.out.println("Invalid name. Use letters only.");
    }
    // Reservation ID (numbers only)
    String resId;
    while (true) {
        System.out.print("Enter reservation ID: ");
        resId = scanner.nextLine().trim();
        if (resId.matches("[a-zA-Z0-9]+")) break;
        System.out.println("Invalid ID. Use numbers&letters only.");
    }
    // Product barcode (letters and numbers)
    String barcode;
    while (true) {
        System.out.print("Enter product barcode (letters and numbers only): ");
        barcode = scanner.nextLine().trim();
        if (barcode.matches("[a-zA-Z0-9]+")) break;
        System.out.println("Invalid barcode. Use letters and numbers only.");
    }
    // New quantity (numbers only)
    int newQuantity;
    while (true) {
        System.out.print("Enter new quantity: ");
        String quantityStr = scanner.nextLine().trim();
        if (quantityStr.matches("\\d+")) {
            newQuantity = Integer.parseInt(quantityStr);
            break;
        }
        System.out.println("Invalid quantity. Use a positive number.");
    }
    // Call the service method
    String result = storageController.editReservation(supplierName, resId, barcode, newQuantity);
    System.out.println(result);
}

public void addAgreementToSupplierMenu() {
    System.out.println("=== Add Agreement to Supplier ===");
    // Supplier ID
    String supplierId;
    while (true) {
        System.out.print("Enter supplier ID: ");
        supplierId = scanner.nextLine().trim();
        if (supplierId.matches("[a-zA-Z0-9]+")) break;
        System.out.println("Invalid supplier ID. Use numbers&letters only.");
    }
    // Contact Name
    String contactName;
    while (true) {
        System.out.print("Enter contact name (letters only): ");
        contactName = scanner.nextLine().trim();
        if (contactName.matches("[a-zA-Z ]+")) break;
        System.out.println("Invalid name. Use letters only.");
    }
    // Contact Phone
    String contactPhone;
    while (true) {
        System.out.print("Enter contact phone (digits, '-', '+'): ");
        contactPhone = scanner.nextLine().trim();
        if (contactPhone.matches("[\\d\\-+]+")) break;
        System.out.println("Invalid phone. Use digits, '-' and '+'.");
    }
    // Contact Email
    String contactEmail;
    while (true) {
        System.out.print("Enter contact email: ");
        contactEmail = scanner.nextLine().trim();
        if (contactEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) break;
        System.out.println("Invalid email format.");
    }
    // Products
    List<String[]> productDetails = new ArrayList<>();
    while (true) {
        System.out.print("Add a product to the agreement? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("n")) break;
        if (!choice.equals("y")) {
            System.out.println("Please enter 'y' or 'n'.");
            continue;
        }
        // Product Name
        String productName;
        while (true) {
            System.out.print("Enter product name (letters only): ");
            productName = scanner.nextLine().trim();
            if (productName.matches("[a-zA-Z ]+")) break;
            System.out.println("Invalid name. Use letters only.");
        }
        // Product Price
        String price;
        while (true) {
            System.out.print("Enter product price (decimal): ");
            price = scanner.nextLine().trim();
            if (price.matches("\\d+(\\.\\d{1,2})?")) break;
            System.out.println("Invalid price. Enter a valid decimal number.");
        }
        // Product Barcode
        String barcode;
        while (true) {
            System.out.print("Enter product barcode (letters and numbers): ");
            barcode = scanner.nextLine().trim();
            if (barcode.matches("[a-zA-Z0-9]+")) break;
            System.out.println("Invalid barcode. Use letters and numbers only.");
        }
        productDetails.add(new String[]{productName, price, barcode});
    }
    // Regular Supply Days
    boolean hasRegularDays = false;
    while (true) {
        System.out.print("Does this agreement have regular supply days? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("y")) {
            hasRegularDays = true;
            break;
        } else if (response.equals("n")) {
            hasRegularDays = false;
            break;
        } else {
            System.out.println("Please enter 'y' or 'n'.");
        }
    }
    // Call service
    String result = storageController.AddNewAgreementForSupplier(
        supplierId, contactName, contactPhone, contactEmail, productDetails, hasRegularDays
    );
    System.out.println(result);
}

public void addProductToAgreementMenu() {
    System.out.println("=== Add Product to Supplier Agreement ===");
    // Supplier ID
    String supplierId;
    while (true) {
        System.out.print("Enter supplier ID: ");
        supplierId = scanner.nextLine().trim();
        if (supplierId.matches("[a-zA-Z0-9]+")) break;
        System.out.println("Invalid supplier ID. Use digits&letters only.");
    }
    // Agreement ID
    String agreementId;
    while (true) {
        System.out.print("Enter agreement ID: ");
        agreementId = scanner.nextLine().trim();
        if (agreementId.matches("[a-zA-Z0-9]+")) break;
        System.out.println("Invalid agreement ID. Use digits&letters only.");
    }
    // Product Name
    String productName;
    while (true) {
        System.out.print("Enter product name (letters only): ");
        productName = scanner.nextLine().trim();
        if (productName.matches("[a-zA-Z ]+")) break;
        System.out.println("Invalid product name. Use letters only.");
    }
    // Product Price
    double productPrice;
    while (true) {
        System.out.print("Enter product price (decimal): ");
        String priceInput = scanner.nextLine().trim();
        if (priceInput.matches("\\d+(\\.\\d{1,2})?")) {
            productPrice = Double.parseDouble(priceInput);
            break;
        }
        System.out.println("Invalid price. Enter a valid decimal number.");
    }
    // Product Number (Barcode)
    String productNum;
    while (true) {
        System.out.print("Enter product number (letters and numbers): ");
        productNum = scanner.nextLine().trim();
        if (productNum.matches("[a-zA-Z0-9]+")) break;
        System.out.println("Invalid product number. Use alphanumeric characters only.");
    }
    // Call service
    String result = storageController.addNewProductToSupplierAgreement(
        supplierId, agreementId, productName, productPrice, productNum
    );
    System.out.println(result);
}

public void registerPurchaseMenu() {
    System.out.println("=== Register Purchase ===");
    Map<String, Integer> boughtItems = new HashMap<>();
    while (true) {
        System.out.print("Enter product barcode (alphanumeric), or type 'done' to finish: ");
        String barcode = scanner.nextLine().trim();
        if (barcode.equalsIgnoreCase("done")) break;

        if (!barcode.matches("[a-zA-Z0-9]+")) {
            System.out.println("Invalid barcode. Use alphanumeric characters only.");
            continue;
        }
        System.out.print("Enter quantity (non-negative integer): ");
        String qtyInput = scanner.nextLine().trim();
        if (!qtyInput.matches("\\d+")) {
            System.out.println("Invalid quantity. Use a non-negative integer.");
            continue;
        }
        int quantity = Integer.parseInt(qtyInput);
        boughtItems.put(barcode, boughtItems.getOrDefault(barcode, 0) + quantity);
    }
    if (boughtItems.isEmpty()) {
        System.out.println("No items were registered.");
        return;
    }
    String result = storageController.registerPurchase(boughtItems);
    System.out.println(result);
}

public void addNewProductMenu() {
    System.out.println("=== Add New Product ===");
    System.out.print("Enter barcode (letters and numbers): ");
    String barcode = scanner.nextLine().trim();
    if (!barcode.matches("[a-zA-Z0-9]+")) {
        System.out.println("Invalid barcode.");
        return;
    }
    System.out.print("Enter product name (letters only): ");
    String name = scanner.nextLine().trim();
    if (!name.matches("[a-zA-Z ]+")) {
        System.out.println("Invalid product name.");
        return;
    }
    List<String> categories = new ArrayList<>();
    while (true) {
        System.out.print("Enter category (letters and numbers), or type 'done' to finish: ");
        String category = scanner.nextLine().trim();
        if (category.equalsIgnoreCase("done")) break;
        if (!category.matches("[a-zA-Z0-9 ]+")) {
            System.out.println("Invalid category name.");
            continue;
        }
        categories.add(category);
    }
    if (categories.isEmpty()) {
        System.out.println("At least one category is required.");
        return;
    }
    System.out.print("Enter manufacturer (letters only): ");
    String manufacturer = scanner.nextLine().trim();
    if (!manufacturer.matches("[a-zA-Z ]+")) {
        System.out.println("Invalid manufacturer.");
        return;
    }
    System.out.print("Enter sell price: ");
    double sellPrice;
    try {
        sellPrice = Double.parseDouble(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("Invalid price.");
        return;
    }
    System.out.print("Enter expiry period (e.g., '6 months', '2 weeks'): ");
    String expiryPeriod = scanner.nextLine().trim();
    if (expiryPeriod.isEmpty()) {
        System.out.println("Expiry period cannot be empty.");
        return;
    }
    System.out.print("Enter location (letters only): ");
    String location = scanner.nextLine().trim();
    if (!location.matches("[a-zA-Z]+(\\s\\d+)?")) {
        System.out.println("Invalid location.");
        return;
    }
    System.out.print("Enter quantity on shelf (integer): ");
    int quantityOnShelf;
    try {
        quantityOnShelf = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("Invalid quantity.");
        return;
    }
    System.out.print("Enter quantity in storage (integer): ");
    int quantityInStorage;
    try {
        quantityInStorage = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("Invalid quantity.");
        return;
    }
    System.out.print("Enter minimum threshold (integer): ");
    int minimumThreshold;
    try {
        minimumThreshold = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("Invalid threshold.");
        return;
    }
    System.out.print("Enter supplier name (letters only): ");
    String supplierName = scanner.nextLine().trim();
    if (!supplierName.matches("[a-zA-Z ]+")) {
        System.out.println("Invalid supplier name.");
        return;
    }
    System.out.print("Enter delivery time (in days): ");
    int delTime;
    try {
        delTime = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("Invalid delivery time.");
        return;
    }
    String result = storageController.addNewProduct(barcode, name, categories, manufacturer,sellPrice, expiryPeriod, location, quantityOnShelf, quantityInStorage,
            minimumThreshold, supplierName, delTime);
    System.out.println(result);
}

public void applyDiscountMenu() {
    System.out.println("=== Apply Discount ===");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    Date startDate, endDate;
    // Get start date
    try {
        System.out.print("Enter start date (yyyy-MM-dd): ");
        String startStr = scanner.nextLine().trim();
        startDate = dateFormat.parse(startStr);
    } catch (Exception e) {
        System.out.println("Invalid start date format.");
        return;
    }
    // Get end date
    try {
        System.out.print("Enter end date (yyyy-MM-dd): ");
        String endStr = scanner.nextLine().trim();
        endDate = dateFormat.parse(endStr);
    } catch (Exception e) {
        System.out.println("Invalid end date format.");
        return;
    }
    // Get discount type
    int percentage = 0;
    Double fixedPrice = null;
    System.out.print("Do you want to apply a percentage discount? (y/n): ");
    String discountType = scanner.nextLine().trim().toLowerCase();
    if (discountType.equals("y")) {
        try {
            System.out.print("Enter discount percentage (0-100): ");
            percentage = Integer.parseInt(scanner.nextLine().trim());
            fixedPrice = 0.0;
            if (percentage < 0 || percentage > 100) {
                System.out.println("Percentage must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid percentage format.");
            return;
        }
    } else {
        try {
            System.out.print("Enter fixed discount price: ");
            fixedPrice = Double.parseDouble(scanner.nextLine().trim());
            if (fixedPrice < 0) {
                System.out.println("Fixed price must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid fixed price format.");
            return;
        }
    }
    // Get target type: barcodes or category
    List<String> barcodes = new ArrayList<>();
    String category = null;
    System.out.print("Apply discount to specific barcodes? (y/n): ");
    String targetType = scanner.nextLine().trim().toLowerCase();
    if (targetType.equals("y")) {
        category = "";
        while (true) {
            System.out.print("Enter barcode (letters and numbers), or type 'done' to finish: ");
            String barcode = scanner.nextLine().trim();
            if (barcode.equalsIgnoreCase("done")) break;
            if (!barcode.matches("[a-zA-Z0-9]+")) {
                System.out.println("Invalid barcode.");
                continue;
            }
            barcodes.add(barcode);
        }
        if (barcodes.isEmpty()) {
            System.out.println("At least one barcode must be provided.");
            return;
        }
    } else {
        System.out.print("Enter target category (letters only): ");
        category = scanner.nextLine().trim();
        if (!category.matches("[a-zA-Z]+")) {
            System.out.println("Invalid category.");
            return;
        }
    }
    // Call the service method
    String result = storageController.applyDiscount(startDate, endDate,percentage,fixedPrice,barcodes, category);
    System.out.println(result);
}

    public void addExpiryReportMenu() {
    System.out.println("=== Add Expiry Report ===");
    // Barcode (letters + numbers)
    System.out.print("Enter product barcode: ");
    String barcode = scanner.nextLine().trim();
    if (!barcode.matches("[a-zA-Z0-9]+")) {
        System.out.println("Invalid barcode. Must contain only letters and numbers.");
        return;
    }
    // Quantity (non-negative integer)
    System.out.print("Enter expired quantity: ");
    int quantity;
    try {
        quantity = Integer.parseInt(scanner.nextLine().trim());
        if (quantity < 0) {
            System.out.println("Quantity must be non-negative.");
            return;
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid quantity format.");
        return;
    }
    // Location (letters only)
    System.out.print("Enter location: ");
    String location = scanner.nextLine().trim();
    if (!location.matches("[a-zA-Z]+")) {
        System.out.println("Invalid location. Must contain letters only.");
        return;
    }
    // Reported by (letters only)
    System.out.print("Enter name of reporter: ");
    String reportedBy = scanner.nextLine().trim();
    if (!reportedBy.matches("[a-zA-Z]+")) {
        System.out.println("Invalid reporter name. Must contain letters only.");
        return;
    }
    // Call the service method
    String result = storageController.reportExpiredProduct(barcode, quantity, location, reportedBy);
    System.out.println(result);
}

    //.....Time simulation.....//
    public void forwardDays(){
        System.out.print("Enter days to forward: ");
        int days = getUserChoice();
        if (days <= 0) {
            System.out.println("Please enter a valid number of days.");
            return;
        }
        System.out.println(this.storageController.forwardDays(days));
    }

}