package Backend.PresentationLayerHR;

import Backend.DTO.*;
import Backend.ServiceLayer.ServiceLayerT.ManagerService;
import Backend.ServiceLayer.ServiceLayerT.ShipmentService;
import Exceptions.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class TransportationWindowCLI {
    private static final TransportationWindowCLI instance = new TransportationWindowCLI();
    private final ManagerService managerService;
    private final ShipmentService shipmentService;
    private final Scanner scanner;

    public TransportationWindowCLI() {
        this.managerService = ManagerService.getInstance();
        this.shipmentService = ShipmentService.getInstance();
        this.scanner = new Scanner(System.in);
    }

    public static TransportationWindowCLI getInstance() {
        return instance;
    }

    private void addArea() {
        System.out.print("Enter area name: ");
        String areaName = scanner.nextLine().trim();
        try {
            // Check if area already exists
            try {
                managerService.getAreaByName(areaName);
                System.out.println("Error: Area '" + areaName + "' already exists.");
                return;
            } catch (Exception e) {
                // Exception means area does not exist – continue to create it
            }
            LinkedList<LocationDTO> locations = new LinkedList<>();
            while (true) {
                System.out.print("Do you want to add a location to this area? (yes/no): ");
                String addLocation = scanner.nextLine().trim();
                if (addLocation.equalsIgnoreCase("yes")) {
                    System.out.print("Enter location address: ");
                    String locationAddress = scanner.nextLine().trim();
                    //check if location already exists
                    boolean exists = false;
                    for (LocationDTO location : locations) {
                        if (location.getAddress().equals(locationAddress)) {
                            System.out.println("Location already exists in this area. Please enter a different address.");
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        continue;
                    }
                    System.out.print("Enter location's contact name: ");
                    String contactName = scanner.nextLine().trim();
                    System.out.print("Enter location's contact number: ");
                    String contactNumber = scanner.nextLine().trim();
                    if (!locationAddress.trim().isEmpty() &&
                            !contactName.trim().isEmpty() &&
                            !contactNumber.trim().isEmpty()) {
                        int locationID = locations.size() == 0 ? managerService.getMaxLocationID() + 1 : locations.stream().mapToInt(LocationDTO::getId).max().orElse(0) + 1;
                        LocationDTO location = new LocationDTO(locationID, areaName, locationAddress, contactNumber, contactName);
                        locations.add(location);
                    } else {
                        System.out.println("Empty input detected. Location not added.");
                    }
                } else if (addLocation.equalsIgnoreCase("no")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
            AreaDTO area = new AreaDTO(areaName, locations);
            managerService.addArea(area);
            System.out.println("Area " + areaName + " with locations:");
            int i = 0;
            for (LocationDTO location : locations) {
                System.out.println(i + ". " + location.getAddress());
                i++;
            }
            System.out.println("added successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addLocationToArea() {
        // Add location to area logic here
        System.out.print("Enter area name: ");
        String areaName = scanner.nextLine().trim();
        try {
            AreaDTO area = managerService.getAreaByName(areaName);
            String locationAddress = null;
            while (true) {
                System.out.print("Enter location address: ");
                locationAddress = scanner.nextLine().trim();
                //check if location already exists
                boolean exists = false;
                for (LocationDTO location : area.getLocations()) {
                    if (location.getAddress().equals(locationAddress)) {
                        System.out.println("Location already exists in this area. Please enter a different address.");
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    break;
                }
            }
            System.out.print("Enter location's contact name: ");
            String contactName = scanner.nextLine().trim();
            System.out.print("Enter location's contact number: ");
            String contactNumber = scanner.nextLine().trim();
            LocationDTO location = new LocationDTO(managerService.getMaxLocationID() + 1, areaName, locationAddress, contactNumber, contactName);
            managerService.addLocation(areaName, location);
            System.out.println("Location " + locationAddress + " added to area " + areaName + " successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addDelivery() {
        System.out.println("Enter delivery details...");
        
        try {
            int deliveryID = managerService.getMaxDeliveryID() + 1;
            System.out.print("Enter year: ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter month: ");
            int month = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter day: ");
            int day = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter area name for source: ");
            String areaNameSource = scanner.nextLine().trim();
            managerService.getAreaByName(areaNameSource); // Check if area exists
            System.out.print("Enter delivery source address: ");
            String sourceAddress = scanner.nextLine().trim();
            LocationDTO sourceLoc = managerService.getLocationByAddress(areaNameSource, sourceAddress); // Check if location exists
            System.out.print("Enter area name for destination: ");
            String areaNameDestination = scanner.nextLine().trim();
            managerService.getAreaByName(areaNameDestination); // Check if area exists
            System.out.print("Enter delivery destination address: ");
            String destinationAddress = scanner.nextLine().trim();
            LocationDTO destinationLoc = managerService.getLocationByAddress(areaNameDestination, destinationAddress); // Check if location exists
            System.out.print("Enter number of items: ");
            int numberOfItems = Integer.parseInt(scanner.nextLine().trim());

            if (numberOfItems <= 0) {
                throw new IllegalArgumentException("Number of items must be positive.");
            }

            LinkedList<SimpleEntry<String, Integer>> items = new LinkedList<>();

            for (int i = 0; i < numberOfItems; i++) {
                System.out.print("Enter item name: ");
                String itemName = scanner.nextLine().trim();

                System.out.print("Enter item weight: ");
                int itemWeight;
                try {
                    itemWeight = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid weight. Please enter a number.");
                    i--; // redo this item
                    continue;
                }

                if (itemWeight <= 0) {
                    System.out.println("Item weight must be positive.");
                    i--; // redo this item
                    continue;
                }

                items.add(new SimpleEntry<>(itemName, itemWeight));
            }

            DeliveryDTO delivery = new DeliveryDTO(
                    deliveryID,
                    LocalDate.now(),
                    LocalDate.of(year, month, day),
                    sourceLoc,
                    destinationLoc,
                    items
            );

            shipmentService.addDelivery(delivery);
            System.out.println("Delivery " + deliveryID + " to " + destinationAddress + " that in area " + areaNameDestination + " added successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid number input. Please enter only numeric values.");
            
        } catch (DateTimeException e) {
            System.out.println("Invalid date entered. Please try again.");
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            
        }
    }


    private void assignDeliveryToTruck() {
        // Assign delivery to truck logic here
        System.out.print("Enter delivery ID: ");
        int deliveryID = 0;
        int truckID = 0;
        try {
            deliveryID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            System.out.print("Enter truck ID: ");
            truckID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            shipmentService.assignDeliveryToTruck(deliveryID, truckID);
            System.out.println("Delivery " + deliveryID + " assigned to truck " + truckID + " successfully.");
        } catch (OverWeightException e) {
            handleExceedingTruckWeight(deliveryID, truckID);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleExceedingTruckWeight(int deliveryID, int truckID) {
        while (true) {
            try {
                System.out.println("\nTruck cannot carry the weight of the delivery.");
                System.out.println("Please choose one of the following options:");
                System.out.println("1. Change truck");
                System.out.println("2. Cancel delivery");
                System.out.println("3. Remove items from truck");
                System.out.print("Your choice: ");
                if (truckID == -1) {
                    truckID = shipmentService.getDocumentByID(deliveryID).getTruckID();
                }

                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        System.out.println("Available trucks: ");
                        LinkedList<TruckDTO> availableTrucks = shipmentService.getAvailableTrucks();

                        if (availableTrucks.isEmpty()) {
                            System.out.println("No trucks available.");
                            return;
                        }

                        for (TruckDTO truck : availableTrucks) {
                            System.out.println("Truck ID: " + truck.getId() + ", Current Weight: " + truck.getWeight() + ", Max Load: " + truck.getMaxLoad());
                        }

                        TruckDTO selectedTruck = null;
                        while (true) {
                            System.out.print("Enter new truck ID: ");
                            try {
                                int truckIDInput = Integer.parseInt(scanner.nextLine().trim());
                                for (TruckDTO truck : availableTrucks) {
                                    if (truck.getId() == truckIDInput) {
                                        selectedTruck = truck;
                                        break;
                                    }
                                }
                                if (selectedTruck != null) {
                                    break;
                                } else {
                                    System.out.println("Invalid truck ID. Please try again.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid number.");
                            }
                        }

                        try {
                            shipmentService.changeTruck(deliveryID, selectedTruck.getId());
                            System.out.println("Delivery assigned to truck " + selectedTruck.getId() + " successfully.");
                            return;
                        } catch (Exception e) {
                            System.out.println("Error assigning truck: " + e.getMessage());
                        }


                    case 2:
                        try {
                            shipmentService.cancelDelivery(deliveryID);
                            System.out.println("Delivery " + deliveryID + " cancelled successfully.");
                            return;
                        } catch (Exception e) {
                            System.out.println("Error cancelling delivery: " + e.getMessage());
                        }

                    case 3:
                        System.out.print("Do you want to remove items from this delivery? (yes/no): ");
                        String response = scanner.nextLine().trim();

                        if (response.equalsIgnoreCase("yes")) {
                            LinkedList<SimpleEntry<String, Integer>> items = shipmentService.getDelivery(deliveryID).getListOfItems();

                            if (items.isEmpty()) {
                                System.out.println("No items found in this delivery.");
                                break;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                SimpleEntry<String, Integer> item = items.get(i);
                                System.out.println("Item " + i + ": " + item.getKey() + ", Weight: " + item.getValue());
                            }

                            System.out.print("Enter number of items to remove: ");
                            int itemAmount = Integer.parseInt(scanner.nextLine().trim());

                            if (itemAmount <= 0 || itemAmount > items.size()) {
                                System.out.println("Invalid item amount. Please try again.");
                                break;
                            }

                            LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();
                            for (int j = 0; j < itemAmount; j++) {
                                System.out.print("Enter item number to remove: ");
                                int itemNumber = Integer.parseInt(scanner.nextLine().trim());

                                if (itemNumber < 0 || itemNumber >= items.size()) {
                                    System.out.println("Invalid item number. Skipping...");
                                    continue;
                                }

                                itemsToRemove.add(items.get(itemNumber));
                            }

                            shipmentService.removeItemsFromTruck(-1, deliveryID, itemsToRemove);
                            System.out.println("Items removed from delivery " + deliveryID + " successfully.");
                        } else if (response.equalsIgnoreCase("no")) {
                            LinkedList<DocumentDTO> documents = shipmentService.getTruckDocuments(truckID);

                            for (DocumentDTO document : documents) {
                                System.out.print("Do you want to remove items from delivery " + document.getId() + "? (yes/no): ");
                                String docAnswer = scanner.nextLine().trim();

                                if (!docAnswer.equalsIgnoreCase("yes")) {
                                    if (!docAnswer.equalsIgnoreCase("no")) {
                                        System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                                    }
                                    continue;
                                }

                                LinkedList<SimpleEntry<String, Integer>> items = document.getListOfItems();
                                if (items.isEmpty()) {
                                    System.out.println("No items in this delivery.");
                                    continue;
                                }

                                for (int i = 0; i < items.size(); i++) {
                                    SimpleEntry<String, Integer> item = items.get(i);
                                    System.out.println("Item " + i + ": " + item.getKey() + ", Weight: " + item.getValue());
                                }

                                System.out.print("Enter number of items to remove: ");
                                int itemAmount = Integer.parseInt(scanner.nextLine().trim());

                                if (itemAmount <= 0 || itemAmount > items.size()) {
                                    System.out.println("Invalid item amount. Skipping this delivery.");
                                    continue;
                                }

                                LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();
                                for (int j = 0; j < itemAmount; j++) {
                                    System.out.print("Enter item number to remove: ");
                                    int itemNumber = Integer.parseInt(scanner.nextLine().trim());

                                    if (itemNumber < 0 || itemNumber >= items.size()) {
                                        System.out.println("Invalid item number. Skipping...");
                                        continue;
                                    }

                                    itemsToRemove.add(items.get(itemNumber));
                                }

                                shipmentService.removeItemsFromTruck(truckID, document.getId(), itemsToRemove);
                                System.out.println("Items removed from delivery " + document.getId() + " successfully.");
                            }
                        } else {
                            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                            break;
                        }

                        System.out.println("Now you can try to assign the delivery to the truck again.");
                        return;


                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void getTruckDeliveries() {
        // Get truck deliveries logic here
        System.out.print("Enter truck ID: ");
        try {
            int truckID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            managerService.getTruckById(truckID);
            LinkedList<DocumentDTO> documents = shipmentService.getTruckDocuments(truckID);
            System.out.println("Truck " + truckID + " deliveries:");
            for (DocumentDTO document : documents) {
                LocationDTO destination = document.getDestination();
                System.out.println("Delivery ID: " + document.getId() + " , To: " + destination.getAddress() + " , In area: " + destination.getAreaName());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void changeDeliveryDestination() {
        try {
            System.out.print("Enter delivery ID: ");
            int deliveryID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()

            System.out.print("Enter area name for new destination: ");
            String areaName = scanner.nextLine().trim();

            System.out.print("Enter new destination address: ");
            String newDestinationAddress = scanner.nextLine().trim();

            shipmentService.changeDestination(areaName, deliveryID, newDestinationAddress);
            System.out.println("Delivery " + deliveryID + " changed to new destination " + newDestinationAddress + " in area " + areaName + " successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input: Delivery ID must be a number.");
            scanner.nextLine(); // clear the invalid input
        } catch (Exception e) {
            System.out.println("Error changing delivery destination: " + e.getMessage());
        }
    }


    private void cancelDelivery() {
        // Cancel delivery logic here
        System.out.print("Enter delivery ID: ");
        try {
            int deliveryID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            shipmentService.cancelDelivery(deliveryID);
            System.out.println("Delivery " + deliveryID + " cancelled successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateDocument() {
        while (true) {
            try {
                System.out.print("Enter document ID: ");
                int documentID = Integer.parseInt(scanner.nextLine().trim());
                DocumentDTO document = shipmentService.getDocumentByID(documentID);
                LinkedList<SimpleEntry<String, Integer>> itemsToAdd = new LinkedList<>();
                LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();

                System.out.print("Do you want to change items in the document? (yes/no): ");
                String changeItems = scanner.nextLine().trim();

                if (changeItems.equalsIgnoreCase("yes")) {
                    while (true) {
                        System.out.print("Do you want to add or remove items from the document? (add/remove/exit): ");
                        String action = scanner.nextLine().trim();

                        if (action.equalsIgnoreCase("add")) {
                            try {
                                System.out.print("Enter item name: ");
                                String itemName = scanner.nextLine().trim();
                                System.out.print("Enter item weight: ");
                                int itemWeight = Integer.parseInt(scanner.nextLine().trim());
                                itemsToAdd.add(new SimpleEntry<>(itemName, itemWeight));
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input for item weight. Please enter an integer.");
                            }

                        } else if (action.equalsIgnoreCase("remove")) {
                            try {
                                System.out.print("Enter item name: ");
                                String itemName = scanner.nextLine().trim();
                                System.out.print("Enter item weight: ");
                                int itemWeight = Integer.parseInt(scanner.nextLine().trim());
                                itemsToRemove.add(new SimpleEntry<>(itemName, itemWeight));
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input for item weight. Please enter an integer.");
                            }

                        } else if (action.equalsIgnoreCase("exit")) {
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 'add', 'remove' or 'exit'.");
                        }
                    }

                    try {
                        shipmentService.updateDocument(documentID, itemsToAdd, itemsToRemove);
                        System.out.println("Document " + documentID + " updated successfully.");
                    } catch (OverWeightException e) {
                        handleExceedingTruckWeight(documentID, document.getTruckID());
                    } catch (Exception e) {
                        System.out.println("Error while updating document items: " + e.getMessage());
                    }
                }

                System.out.print("Do you want to change origin of the document? (yes/no): ");
                String changeOrigin = scanner.nextLine().trim();

                if (changeOrigin.equalsIgnoreCase("yes")) {
                    System.out.print("Enter area name for new origin: ");
                    String areaName = scanner.nextLine().trim();
                    System.out.print("Enter location name for new origin: ");
                    String origin = scanner.nextLine().trim();
                    try {
                        managerService.changeDocuementOrigin(documentID, areaName, origin);
                        System.out.println("Document origin changed to " + origin + " in area " + areaName + " successfully.");
                    } catch (Exception e) {
                        System.out.println("Error while changing origin: " + e.getMessage());
                    }
                }

                System.out.print("Do you want to change destination of the document? (yes/no): ");
                String changeDestination = scanner.nextLine().trim();

                if (changeDestination.equalsIgnoreCase("yes")) {
                    System.out.print("Enter area name for new destination: ");
                    String areaName = scanner.nextLine().trim();
                    System.out.print("Enter location name for new destination: ");
                    String destination = scanner.nextLine().trim();
                    try {
                        managerService.changeDocuementDestination(documentID, areaName, destination);
                        System.out.println("Document destination changed to " + destination + " in area " + areaName + " successfully.");
                    } catch (Exception e) {
                        System.out.println("Error while changing destination: " + e.getMessage());
                    }
                }

                if (changeDestination.equalsIgnoreCase("no") && changeOrigin.equalsIgnoreCase("no") && changeItems.equalsIgnoreCase("no")) {
                    System.out.println("No changes made to the document.");
                }

                break; // exit outer while-loop after successful run

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numeric values where expected.");
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }

            System.out.println("Let's try again.\n");
        }
    }


    private void changeTruck() {
        // Change truck logic here
        System.out.print("Enter delivery ID: ");
        try {
            int deliveryID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            System.out.print("Enter new truck ID: ");
            int newTruckID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            shipmentService.changeTruck(deliveryID, newTruckID);
            System.out.println("Delivery " + deliveryID + " changed to truck " + newTruckID + " successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeItemsFromDelivery() {
        // Remove items from delivery logic here
        System.out.print("Enter delivery ID: ");
        try {
            int deliveryID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            DeliveryDTO delivery = shipmentService.getDelivery(deliveryID);
            LinkedList<SimpleEntry<String, Integer>> items = delivery.getListOfItems();
            int i = 0;
            for (SimpleEntry<String, Integer> item : items) {
                System.out.println("Item " + (i) + ": " + item.getKey() + ", Weight: " + item.getValue());
                i++;
            }
            int itemAmount = 0;
            while (true) {
                System.out.print("Enter item amount to remove: ");
                itemAmount = scanner.nextInt();
                scanner.nextLine(); // consume the leftover newline after nextInt()
                if (itemAmount > items.size() || itemAmount < 0) {
                    System.out.println("Invalid item amount. Please enter a valid amount.");
                    continue;
                } else {
                    break;
                }
            }
            LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();
            for (int j = 0; j < itemAmount; j++) {
                System.out.print("Enter item number to remove: ");
                int itemNumber = scanner.nextInt();
                scanner.nextLine(); // consume the leftover newline after nextInt()
                if (itemNumber < 0 || itemNumber >= items.size()) {
                    System.out.println("Invalid item number. Please enter a valid number.");
                    j--; // Decrement j to repeat the loop for valid input
                    continue;
                }
                SimpleEntry<String, Integer> itemToRemove = items.get(itemNumber);
                itemsToRemove.add(itemToRemove);
            }
            DocumentDTO document = null;
            try {
                document = shipmentService.getDocumentByID(deliveryID);
            } catch (Exception e) {
                // Document not found, proceed with truckID -1
            }
            int truckID = document != null ? document.getTruckID() : -1;
            shipmentService.removeItemsFromTruck(truckID, deliveryID, itemsToRemove);
            System.out.println("Items removed from delivery " + deliveryID + " successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addTruck() {
        int truckID = managerService.getMaxTruckID() + 1;

        try {
            System.out.print("Enter truck model: ");
            String model = scanner.nextLine().trim();

            System.out.print("Enter truck's dry weight: ");
            int dryWeight = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter truck's max load (including truck weight): ");
            int maxLoad = Integer.parseInt(scanner.nextLine().trim());

            if (maxLoad <= dryWeight) {
                System.out.println("Max load must be greater than dry weight.");
                
                return;
            }

            System.out.print("Enter truck's license type (capital letter only, e.g., A, B, C...): ");
            String license = scanner.nextLine().trim();

            if (license.length() != 1 || !Character.isUpperCase(license.charAt(0))) {
                System.out.println("Invalid license type. Please enter a single capital letter.");
                
                return;
            }

            TruckDTO truck = new TruckDTO(truckID, model, dryWeight, dryWeight, maxLoad, true, license, new LinkedList<>());
            managerService.addTruck(truck);
            System.out.println("Truck " + truckID + " added successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid number input. Please enter valid integers.");
            
        } catch (Exception e) {
            System.out.println("Error while adding truck: " + e.getMessage());
            
        }
    }

    private void sendTruckToDistributeDeliveries(){
        // Send truck to distribute deliveries logic here
        System.out.print("Enter truck ID: ");
        
        try{
            int truckID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            LinkedList<DocumentDTO> deliveries = shipmentService.getTruckDocuments(truckID);
            Set<String> areas = new HashSet<>();
            for(DocumentDTO document : deliveries){
                areas.add(document.getDestination().getAreaName());
            }
            System.out.print("choose day of the week: ");
            String day = scanner.nextLine().trim();
            if(!managerService.isValidDay(day)){
                System.out.println("Invalid day. Please enter a valid day.");
                return;
            }
            String dayName = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
            System.out.print("Enter time of the day(HOUR): ");
            int hour = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            if(!managerService.isValidShift(hour)){
                System.out.println("Invalid hour.\nPlease enter a hour that within the working hours of the day (8-20).");
                return;
            }
            shipmentService.sendTruckToDistributeDeliveries(truckID, dayName, hour);
            System.out.println("Truck " + truckID + " sent successfully to distribute deliveries in areas: ");
            int i = 1;
            for(String area : areas){
                System.out.println(i + ". " + area);
                i++;
            }
        } catch (NoDriverAvailableException e){
            System.out.println("Error: " + e.getMessage());
            System.out.println("Please try again in another day, or shift.");
        } catch (StorageWorkerNotPresentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Please try again in another day, or shift.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listDeliveriesByTime() {
        // List deliveries by time logic here
        try {
            System.out.print("Enter amount of deliveries to show: ");
            int amount = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            LinkedList<DeliveryDTO> deliveries = shipmentService.listDeliveriesByTime(amount);
            for (DeliveryDTO delivery : deliveries) {
                System.out.println("Delivery ID: " + delivery.getId() + ", Delivery Date: " + delivery.getDeliveryDate() +
                        "\nFrom: " + delivery.getOriginLoc().getAddress() + ", In Area: " + delivery.getOriginLoc().getAreaName() +
                        "\nTo: " + delivery.getDestinationLoc().getAddress() + ", In Area: " + delivery.getDestinationLoc().getAreaName());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void complainOnDriver() {
        // Delete driver logic here
        System.out.print("Enter driver ID: ");
        try {
            String driverID = scanner.nextLine().trim();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            System.out.print("Enter complaint: ");
            String complaint = scanner.nextLine().trim();
            managerService.complainOnDriver(driverID, complaint);
            System.out.println("Complaint on driver " + driverID + " has beem sent to the HR supervisor successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteTruck() {
        // Delete truck logic here
        System.out.print("Enter truck ID: ");
        try {
            int truckID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            LinkedList<DocumentDTO> deliveries = shipmentService.getTruckDocuments(truckID);
            managerService.deleteTruck(truckID);
            System.out.println("Truck " + truckID + " deleted successfully.");
            System.out.println("Truck " + truckID + " had the following deliveries, Please assign them to a new truck:");
            for (DocumentDTO document : deliveries) {
                System.out.println("Delivery ID: " + document.getId() +
                        "\nFrom: " + document.getOrigin().getAddress() + ", In Area: " + document.getOrigin().getAreaName() +
                        "\nTo: " + document.getDestination().getAddress() + ", In Area: " + document.getDestination().getAreaName());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteArea() {
        // Delete area logic here
        System.out.print("Enter area name: ");
        try {
            String areaName = scanner.nextLine().trim();
            managerService.deleteArea(areaName);
            System.out.println("Area " + areaName + " and all associated deliveries deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteLocation() {
        // Delete location logic here
        System.out.print("Enter area name: ");
        String areaName = scanner.nextLine().trim();
        System.out.print("Enter location address: ");
        String locationAddress = scanner.nextLine().trim();
        try {
            managerService.deleteLocation(areaName, locationAddress);
            System.out.println("Location " + locationAddress + " and all associated deliveries deleted successfully from area " + areaName + ".");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void supplierPickUpItems() {
        // Supplier pick up items logic here
        System.out.print("Enter delivery ID: ");
        try {
            int deliveryID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            DocumentDTO document = shipmentService.supplierPickUpItems(deliveryID);
            System.out.println(document.toString());
            System.out.println("Items picked up successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void getAllShiftAssignmentsForTheDay(){
        // Get all shift assignments for the day logic here
        System.out.print("Enter day: ");
        try{
            String day = scanner.nextLine();
            scanner.nextLine(); // consume the leftover newline after nextInt()
            if(!managerService.isValidDay(day)){
                System.out.println("Invalid day. Please enter a valid day.");
                return;

            }
            String dayName = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
            System.out.println("Getting all shift assignments for " + dayName + " from the HR supervisor...");
            LinkedList<LinkedList<TransportationEmployeeDTO>> allShifts = managerService.getAllShiftAssignmentsForTheDay(dayName);
            System.out.println("Morning shift assignments:");
            for(TransportationEmployeeDTO employee : allShifts.get(0)){
                String role = employee.getLicenseTypes().isEmpty() ? "storage worker" : "driver";
                System.out.println("Employee ID: " + employee.getId() + ", Name: " + employee.getName() + ", Role: " + role);
            }
            System.out.println("Evening shift assignments:");
            for(TransportationEmployeeDTO employee : allShifts.get(1)){
                String role = employee.getLicenseTypes().isEmpty() ? "storage worker" : "driver";
                System.out.println("Employee ID: " + employee.getId() + ", Name: " + employee.getName() + ", Role: " + role);
            }
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void TransportManagerMenu(){
        do{
            System.out.println("");
            System.out.println("===========================================================");
            System.out.println("Transport Manager Menu:");
            System.out.println("1. Add Area"); 
            System.out.println("2. Add location to area");
            System.out.println("3. Add Delivery");
            System.out.println("4. Asign Delivery to Truck");
            System.out.println("5. Get Truck Deliveries");
            System.out.println("6. Change Delivery Destination");
            System.out.println("7. Cancel Delivery");
            System.out.println("8. Update document");
            System.out.println("9. Change truck");
            System.out.println("10. Remove items from delivery");
            System.out.println("11. Add truck");
            System.out.println("12. Send truck to distribute deliveries");
            System.out.println("13. List all deliveries by time");
            System.out.println("14. Complain on driver");
            System.out.println("15. Delete truck");
            System.out.println("16. Delete area");
            System.out.println("17. Delete location");
            System.out.println("18. Supplier pick up items");
            System.out.println("19. Get shift assignments for the day");
            System.out.println("20. Exit");
            System.out.print("Please enter your choice: ");
            
            int choice = 0;
            while(true){
                try{
                    choice = scanner.nextInt();
                    if(choice < 1 || choice > 20){
                        System.out.println("Invalid choice. Please enter a number between 1 and 20.");
                        continue; // Retry the loop
                    }
                    else{
                        break; // Valid choice, exit the loop
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear the invalid input
                    continue; // Retry the loop
                }
            }
            scanner.nextLine();
            

            switch (choice) {
                case 1:
                    addArea();
                    break;
                case 2:
                    addLocationToArea();
                    break;
                case 3:
                    addDelivery();
                    break;
                case 4:
                    assignDeliveryToTruck();
                    break;
                case 5:
                    getTruckDeliveries();
                    break;
                case 6:
                    changeDeliveryDestination();
                    break;
                case 7:
                    cancelDelivery();
                    break;
                case 8:
                    updateDocument();
                    break;
                case 9:
                    changeTruck();
                    break;
                case 10:
                    removeItemsFromDelivery();
                    break;
                case 11:
                    addTruck();
                    break;
                case 12:
                    sendTruckToDistributeDeliveries();
                    break;
                case 13:
                    listDeliveriesByTime();
                    break;
                case 14:
                    complainOnDriver();
                    break;
                case 15:
                    deleteTruck();
                    break;
                case 16:
                    deleteArea();
                    break;
                case 17:
                    deleteLocation();
                    break;
                case 18:
                    supplierPickUpItems();
                    break;
                case 19:
                    getAllShiftAssignmentsForTheDay();
                    break;
                case 20:
                    System.out.println("Returning to login screen...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }

    public void EmployeeMenu(){
        do{
            System.out.println("");
            System.out.println("===========================================================");
            System.out.println("Employee Menu:");
            System.out.println("1. Get Truck Deliveries");
            System.out.println("2. Update document");
            System.out.println("3. List all deliveries by time");
            System.out.println("4. Supplier pick up items");
            System.out.println("5. Exit");
            System.out.print("Please enter your choice: ");
            
            int choice = 0;
            while(true){
                try{
                    choice = scanner.nextInt();
                    if(choice < 1 || choice > 5){
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                        continue; // Retry the loop
                    }
                    else{
                        break; // Valid choice, exit the loop
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear the invalid input
                    continue; // Retry the loop
                }
            }
            scanner.nextLine();
            

            switch (choice) {
                case 1:
                    getTruckDeliveries();
                    break;
                case 2:
                    updateDocument();
                    break;
                case 3:
                    listDeliveriesByTime();
                    break;
                case 4:
                    supplierPickUpItems();
                    break;

                case 5:
                    System.out.println("Returning to login screen...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }
}
