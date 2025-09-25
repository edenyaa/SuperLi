package Backend.PresentationLayerHR;

import Backend.DTO.*;
import Backend.DomainLayer.DomainLayerHR.Repos.DataRepositoryImpl;
import Backend.ServiceLayer.ServiceLayerT.ManagerService;
import Backend.ServiceLayer.ServiceLayerT.ShipmentService;
import Exceptions.*;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import javax.swing.*;

import java.awt.Component;
import java.awt.Dimension;

public class TransportationWindowGUI {
    private static final TransportationWindowGUI instance = new TransportationWindowGUI();
    private final ShipmentService shipmentService;
    private final ManagerService managerService;

    public TransportationWindowGUI() {
        shipmentService = ShipmentService.getInstance();
        managerService = ManagerService.getInstance();
    }

    public static TransportationWindowGUI getInstance() {
        return instance;
    }

    public void addArea(JFrame previousFrame) {
        JFrame frame = new JFrame("Add New Area");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        // Area name
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel areaLabel = new JLabel("Area Name:");
        JTextField areaNameField = new JTextField(20);
        mainPanel.add(areaLabel);
        mainPanel.add(areaNameField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Location form
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(20);
        JLabel contactNameLabel = new JLabel("Contact Name:");
        JTextField contactNameField = new JTextField(20);
        JLabel contactNumberLabel = new JLabel("Contact Number:");
        JTextField contactNumberField = new JTextField(20);

        mainPanel.add(addressLabel);
        mainPanel.add(addressField);
        mainPanel.add(contactNameLabel);
        mainPanel.add(contactNameField);
        mainPanel.add(contactNumberLabel);
        mainPanel.add(contactNumberField);

        mainPanel.add(Box.createVerticalStrut(20));

        JButton addLocationButton = new JButton("Add Location");
        mainPanel.add(addLocationButton);
        mainPanel.add(Box.createVerticalStrut(20));

        // Display list of added locations
        DefaultListModel<String> locationListModel = new DefaultListModel<>();
        JList<String> locationList = new JList<>(locationListModel);
        JScrollPane locationScroll = new JScrollPane(locationList);
        locationScroll.setBorder(BorderFactory.createTitledBorder("Added Locations"));
        mainPanel.add(locationScroll);
        mainPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        JButton submitButton = new JButton("Submit Area");
        JButton cancelButton = new JButton("Cancel");
        bottomPanel.add(submitButton);
        bottomPanel.add(Box.createHorizontalStrut(10));
        bottomPanel.add(cancelButton);
        frame.add(bottomPanel);

        // Logic
        List<LocationDTO> locations = new ArrayList<>();

        addLocationButton.addActionListener(e -> {
            String address = addressField.getText().trim();
            String contactName = contactNameField.getText().trim();
            String contactNumber = contactNumberField.getText().trim();

            if (address.isEmpty() || contactName.isEmpty() || contactNumber.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All location fields must be filled.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (LocationDTO loc : locations) {
                if (loc.getAddress().equalsIgnoreCase(address)) {
                    JOptionPane.showMessageDialog(frame, "Location already added.", "Duplicate Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            try {
                int locationID = locations.size() == 0 ? managerService.getMaxLocationID() + 1
                        : locations.stream().mapToInt(LocationDTO::getId).max().orElse(0) + 1;
                String areaName = areaNameField.getText().trim();
                LocationDTO location = new LocationDTO(locationID, areaName, address, contactNumber, contactName);
                locations.add(location);
                locationListModel.addElement(address);
                // Clear input
                addressField.setText("");
                contactNameField.setText("");
                contactNumberField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error adding location: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        submitButton.addActionListener(e -> {
            String areaName = areaNameField.getText().trim();
            if (areaName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Area name is required.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Check if area exists
                try {
                    managerService.getAreaByName(areaName);
                    JOptionPane.showMessageDialog(frame, "Area already exists.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                } catch (Exception ex) {
                    // Area doesn't exist — continue
                }

                AreaDTO area = new AreaDTO(areaName, new LinkedList<>(locations));
                managerService.addArea(area);

                StringBuilder summary = new StringBuilder("Area added successfully with locations:\n");
                for (int i = 0; i < locations.size(); i++) {
                    summary.append(i).append(". ").append(locations.get(i).getAddress()).append("\n");
                }

                JOptionPane.showMessageDialog(frame, summary.toString(), "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close window
                previousFrame.setVisible(true); // Show previous frame
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to add area: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.add(mainPanel);
        containerPanel.add(bottomPanel);

        frame.setContentPane(new JScrollPane(containerPanel));
        frame.setVisible(true);

    }

    private void addLocationToArea(JFrame previousFrame) {
        JFrame frame = new JFrame("Add Location to Area");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        // Area selection
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel areaLabel = new JLabel("Select Area:");
        JComboBox<String> areaComboBox = new JComboBox<>();
        try {
            LinkedList<AreaDTO> areas = managerService.getAllAreas();
            for (AreaDTO area : areas) {
                areaComboBox.addItem(area.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading areas: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainPanel.add(areaLabel);
        mainPanel.add(areaComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between area selection and location form

        // Location form
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(20);
        JLabel contactNameLabel = new JLabel("Contact Name:");
        JTextField contactNameField = new JTextField(20);
        JLabel contactNumberLabel = new JLabel("Contact Number:");
        JTextField contactNumberField = new JTextField(20);

        mainPanel.add(addressLabel);
        mainPanel.add(addressField);
        mainPanel.add(contactNameLabel);
        mainPanel.add(contactNameField);
        mainPanel.add(contactNumberLabel);
        mainPanel.add(contactNumberField);
        mainPanel.add(Box.createVerticalStrut(20));

        JButton addLocationButton = new JButton("Add Location");
        mainPanel.add(addLocationButton);
        mainPanel.add(Box.createVerticalStrut(20));

        addLocationButton.addActionListener(e -> {
            String address = addressField.getText().trim();
            String contactName = contactNameField.getText().trim();
            String contactNumber = contactNumberField.getText().trim();

            if (address.isEmpty() || contactName.isEmpty() || contactNumber.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int locationID = managerService.getMaxLocationID() + 1;
                String areaName = (String) areaComboBox.getSelectedItem();
                LocationDTO location = new LocationDTO(locationID, areaName, address, contactNumber, contactName);
                managerService.addLocation(areaName, location);

                JOptionPane.showMessageDialog(frame, "Location added successfully to area: " + areaName, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close window
                previousFrame.setVisible(true); // Show previous frame
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to add location: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(addLocationButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // space between buttons
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel); // add the horizontal button panel to vertical main panel
        mainPanel.add(Box.createVerticalStrut(20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void addDelivery(JFrame previousFrame) {
        JFrame frame = new JFrame("Add Delivery");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel deliveryLabel = new JLabel("Delivery Details");
        mainPanel.add(deliveryLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Delivery form
        JLabel deliveryYearTimeLabel = new JLabel("Enter year:");
        JTextField deliveryYearField = new JTextField(4);
        JLabel deliveryMonthTimeLabel = new JLabel("Enter month:");
        JTextField deliveryMonthField = new JTextField(2);
        JLabel deliveryDayTimeLabel = new JLabel("Enter day:");
        JTextField deliveryDayField = new JTextField(2);
        JLabel areaSourceLabel = new JLabel("Select Source:");
        JComboBox<String> areaSourceComboBox = new JComboBox<>();
        JComboBox<String> locationsOnSourceComboBox = new JComboBox<>();
        JLabel areaDestinationLabel = new JLabel("Select Destination:");
        JComboBox<String> areaDestinationComboBox = new JComboBox<>();
        JComboBox<String> locationsOnDestinationComboBox = new JComboBox<>();
        JLabel itemNameLabel = new JLabel("Item Name:");
        JTextField itemNameField = new JTextField(20);
        JLabel itemWeightLabel = new JLabel("Item Weight:");
        JTextField itemWeightField = new JTextField(10);

        mainPanel.add(deliveryYearTimeLabel);
        mainPanel.add(deliveryYearField);
        mainPanel.add(deliveryMonthTimeLabel);
        mainPanel.add(deliveryMonthField);
        mainPanel.add(deliveryDayTimeLabel);
        mainPanel.add(deliveryDayField);
        mainPanel.add(areaSourceLabel);
        mainPanel.add(areaSourceComboBox);
        mainPanel.add(locationsOnSourceComboBox);
        mainPanel.add(areaDestinationLabel);
        mainPanel.add(areaDestinationComboBox);
        mainPanel.add(locationsOnDestinationComboBox);
        mainPanel.add(itemNameLabel);
        mainPanel.add(itemNameField);
        mainPanel.add(itemWeightLabel);
        mainPanel.add(itemWeightField);
        mainPanel.add(Box.createVerticalStrut(20));

        JButton addItemButton = new JButton("Add Item");
        mainPanel.add(addItemButton);
        mainPanel.add(Box.createVerticalStrut(20));

        DefaultListModel<String> itemListModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(itemListModel);
        JScrollPane itemScroll = new JScrollPane(itemList);
        itemScroll.setBorder(BorderFactory.createTitledBorder("Added Items"));
        mainPanel.add(itemScroll);
        mainPanel.add(Box.createVerticalStrut(20));

        JButton addDeliveryButton = new JButton("Add Delivery");

        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(addDeliveryButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // space between buttons
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel); // add the horizontal button panel to vertical main panel
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(Box.createVerticalStrut(20));

        // Load areas and locations
        LinkedList<AreaDTO> areas;
        try {
            areas = managerService.getAllAreas();
            for (AreaDTO area : areas) {
                areaSourceComboBox.addItem(area.getName());
                areaDestinationComboBox.addItem(area.getName());
            }
            areaSourceComboBox.setSelectedIndex(0);
            areaDestinationComboBox.setSelectedIndex(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading areas: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        areaSourceComboBox.addActionListener(e -> {
            locationsOnSourceComboBox.removeAllItems();
            String selectedArea = (String) areaSourceComboBox.getSelectedItem();
            if (selectedArea != null) {
                try {
                    LinkedList<LocationDTO> locations;
                    for (AreaDTO area : areas) {
                        if (area.getName().equals(selectedArea)) {
                            locations = area.getLocations();
                            for (LocationDTO location : locations) {
                                locationsOnSourceComboBox.addItem(location.getAddress());
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error loading locations: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        areaDestinationComboBox.addActionListener(e -> {
            locationsOnDestinationComboBox.removeAllItems();
            String selectedArea = (String) areaDestinationComboBox.getSelectedItem();
            if (selectedArea != null) {
                try {
                    LinkedList<LocationDTO> locations;
                    for (AreaDTO area : areas) {
                        if (area.getName().equals(selectedArea)) {
                            locations = area.getLocations();
                            for (LocationDTO location : locations) {
                                locationsOnDestinationComboBox.addItem(location.getAddress());
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error loading locations: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addItemButton.addActionListener(e -> {
            String itemName = itemNameField.getText().trim();
            String itemWeightStr = itemWeightField.getText().trim();

            if (itemName.isEmpty() || itemWeightStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Item name and weight must be filled.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int itemWeight = Integer.parseInt(itemWeightStr);
                if (itemWeight <= 0) {
                    JOptionPane.showMessageDialog(frame, "Item weight must be a positive number.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String deliveryItem = itemName + " (" + itemWeight + " kg)";
                itemListModel.addElement(deliveryItem);
                // Clear input fields
                itemNameField.setText("");
                itemWeightField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid item weight. Please enter a valid number.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        addDeliveryButton.addActionListener(e -> {
            String yearStr = deliveryYearField.getText().trim();
            String monthStr = deliveryMonthField.getText().trim();
            String dayStr = deliveryDayField.getText().trim();
            String sourceArea = (String) areaSourceComboBox.getSelectedItem();
            String sourceLocation = (String) locationsOnSourceComboBox.getSelectedItem();
            LocationDTO sourceLocationDTO = null;
            for (AreaDTO area : areas) {
                if (area.getName().equals(sourceArea)) {
                    for (LocationDTO location : area.getLocations()) {
                        if (location.getAddress().equals(sourceLocation)) {
                            sourceLocationDTO = location;
                            break;
                        }
                    }
                    break;
                }
            }
            String destinationArea = (String) areaDestinationComboBox.getSelectedItem();
            String destinationLocation = (String) locationsOnDestinationComboBox.getSelectedItem();
            LocationDTO destinationLocationDTO = null;
            for (AreaDTO area : areas) {
                if (area.getName().equals(destinationArea)) {
                    for (LocationDTO location : area.getLocations()) {
                        if (location.getAddress().equals(destinationLocation)) {
                            destinationLocationDTO = location;
                            break;
                        }
                    }
                    break;
                }
            }
            List<String> items = Collections.list(itemListModel.elements());
            if (yearStr.isEmpty() || monthStr.isEmpty() || dayStr.isEmpty() || sourceArea == null
                    || sourceLocationDTO == null || destinationArea == null || destinationLocationDTO == null
                    || items.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled and at least one item must be added.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                int day = Integer.parseInt(dayStr);
                LocalDate deliveryDate = LocalDate.of(year, month, day);

                LinkedList<SimpleEntry<String, Integer>> deliveryItems = new LinkedList<>();
                for (String item : items) {
                    String[] parts = item.split(" \\(");
                    String itemName = parts[0];
                    int itemWeight = Integer.parseInt(parts[1].replace(" kg)", ""));
                    deliveryItems.add(new SimpleEntry<>(itemName, itemWeight));
                }
                int deliveryID = managerService.getMaxDeliveryID() + 1;
                DeliveryDTO delivery = new DeliveryDTO(deliveryID, LocalDate.now(), deliveryDate, sourceLocationDTO,
                        destinationLocationDTO, deliveryItems);
                shipmentService.addDelivery(delivery);
                JOptionPane.showMessageDialog(frame, "Delivery added successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close window
                previousFrame.setVisible(true); // Show previous frame
            } catch (DateTimeException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date. Please enter a valid date.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid year, month, or day. Please enter valid numbers.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to add delivery: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.add(mainPanel);
        containerPanel.add(new JScrollPane(mainPanel));
        frame.setContentPane(containerPanel);
        frame.setVisible(true);
    }

    private void assignDeliveryToTruck(JFrame previousFrame) {
        JFrame frame = new JFrame("Assign Delivery to Truck");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel deliveryLabel = new JLabel("Select Delivery:");
        JComboBox<String> deliveryComboBox = new JComboBox<>();
        try {
            LinkedList<DeliveryDTO> deliveries = shipmentService.getNotAssignedDeliveries();
            for (DeliveryDTO delivery : deliveries) {
                int weight = delivery.getListOfItems().stream().mapToInt(SimpleEntry::getValue).sum();
                deliveryComboBox.addItem("Delivery ID: " + delivery.getId() + " - "
                        + delivery.getOriginLoc().getAddress() + ", " + delivery.getOriginLoc().getAreaName() + " to "
                        + delivery.getDestinationLoc().getAddress() + ", " + delivery.getDestinationLoc().getAreaName()
                        + " with weight: " + weight + " kg");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading deliveries: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainPanel.add(deliveryLabel);
        mainPanel.add(deliveryComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between delivery selection and truck selection

        JLabel truckLabel = new JLabel("Select Truck:");
        JComboBox<String> truckComboBox = new JComboBox<>();
        try {
            LinkedList<TruckDTO> trucks = shipmentService.getAvailableTrucks();
            for (TruckDTO truck : trucks) {
                truckComboBox.addItem("Truck ID: " + truck.getId() + " - " + truck.getModel() + ", Available Capacity: "
                        + (truck.getMaxLoad() - truck.getWeight()) + " kg");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading trucks: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainPanel.add(truckLabel);
        mainPanel.add(truckComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between truck selection and buttons

        JButton assignButton = new JButton("Assign Delivery to Truck");
        assignButton.addActionListener(e -> {
            String selectedDelivery = (String) deliveryComboBox.getSelectedItem();
            String selectedTruck = (String) truckComboBox.getSelectedItem();

            if (selectedDelivery == null || selectedTruck == null) {
                JOptionPane.showMessageDialog(frame, "Please select both a delivery and a truck.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int deliveryID = Integer.parseInt(selectedDelivery.split(" - ")[0].split(": ")[1]);
            int truckID = Integer.parseInt(selectedTruck.split(" - ")[0].split(": ")[1]);

            try {
                shipmentService.assignDeliveryToTruck(deliveryID, truckID);
                JOptionPane.showMessageDialog(frame,
                        "Delivery " + deliveryID + " assigned to truck " + truckID + " successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close window
                previousFrame.setVisible(true); // Show previous frame
            } catch (OverWeightException owe) {
                JOptionPane.showMessageDialog(frame, "Cannot assign delivery to truck: " + owe.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                frame.dispose(); // Close window
                handleExceedingTruckWeight(previousFrame, truckID, deliveryID);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to assign delivery: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(assignButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // space between buttons
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel); // add the horizontal button panel to vertical main panel
        mainPanel.add(Box.createVerticalStrut(20));
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void handleExceedingTruckWeight(JFrame mainMenuFrame, int truckID, int deliveryID) {
        JFrame optionsFrame = new JFrame("Exceeding Truck Weight");
        optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        optionsFrame.setSize(400, 300);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel("The selected truck's weight limit will be exceeded by this delivery.");
        optionsPanel.add(messageLabel);
        optionsPanel.add(Box.createVerticalStrut(20));

        JLabel optionsLabel = new JLabel("Choose an option:");
        optionsPanel.add(optionsLabel);
        optionsPanel.add(Box.createVerticalStrut(10));

        JButton changeTruckButton = new JButton("Change Truck");
        JButton cancelDeliveryButton = new JButton("Cancel Delivery");
        JButton removeItemsButton = new JButton("Remove Items from truck");

        // --------Change Truck Button Action--------
        changeTruckButton.addActionListener(e -> {
            optionsFrame.dispose();
            JFrame changeTruckFrame = new JFrame("Change Truck");
            changeTruckFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            changeTruckFrame.setSize(400, 300);

            JPanel changeTruckPanel = new JPanel();
            changeTruckPanel.setLayout(new BoxLayout(changeTruckPanel, BoxLayout.Y_AXIS));

            JLabel truckLabel = new JLabel("Select a different truck:");
            JComboBox<String> truckComboBox = new JComboBox<>();
            try {
                LinkedList<TruckDTO> trucks = shipmentService.getAvailableTrucks();
                for (TruckDTO truck : trucks) {
                    truckComboBox.addItem("Truck ID: " + truck.getId() + " - " + truck.getModel()
                            + ", Available Capacity: " + (truck.getMaxLoad() - truck.getWeight()) + " kg");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(changeTruckFrame, "Error loading trucks: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            changeTruckPanel.add(truckLabel);
            changeTruckPanel.add(truckComboBox);
            changeTruckPanel.add(Box.createVerticalStrut(20));

            JButton confirmChangeButton = new JButton("Confirm Change");
            confirmChangeButton.addActionListener(ce -> {
                String selectedTruck = (String) truckComboBox.getSelectedItem();
                if (selectedTruck == null) {
                    JOptionPane.showMessageDialog(changeTruckFrame, "Please select a truck.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int newTruckID = Integer.parseInt(selectedTruck.split(" - ")[0].split(": ")[1]);
                try {
                    shipmentService.changeTruck(deliveryID, newTruckID);
                    JOptionPane.showMessageDialog(changeTruckFrame,
                            "Delivery " + deliveryID + " changed to truck " + newTruckID + " successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    changeTruckFrame.dispose();
                    mainMenuFrame.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(changeTruckFrame, "Failed to change truck: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            changeTruckPanel.add(confirmChangeButton);

            JScrollPane changeTruckScroll = new JScrollPane(changeTruckPanel);
            changeTruckScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            changeTruckScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            changeTruckFrame.getContentPane().add(changeTruckScroll);
            changeTruckFrame.setVisible(true);
        });
        optionsPanel.add(changeTruckButton);
        optionsPanel.add(Box.createVerticalStrut(10));

        // --------Cancel Delivery Button Action--------
        cancelDeliveryButton.addActionListener(e -> {
            optionsFrame.dispose();
            try {
                shipmentService.cancelDelivery(deliveryID);
                JOptionPane.showMessageDialog(mainMenuFrame, "Delivery " + deliveryID + " cancelled successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                mainMenuFrame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainMenuFrame, "Failed to cancel delivery: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        optionsPanel.add(cancelDeliveryButton);
        optionsPanel.add(Box.createVerticalStrut(10));

        // --------Remove Items Button Action--------
        // that one is a bit more complex, so its logic is separated
        removeItemsButton.addActionListener(e -> {
            optionsFrame.setVisible(false);
            JFrame removeItemsFrame = new JFrame("Remove Items from Truck");
            removeItemsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            removeItemsFrame.setSize(500, 400);

            JPanel removeItemsPanel = new JPanel();
            removeItemsPanel.setLayout(new BoxLayout(removeItemsPanel, BoxLayout.Y_AXIS));

            JLabel questionLabel = new JLabel("Do you want to remove items from this delivery?");
            removeItemsPanel.add(questionLabel);

            ButtonGroup yesNoGroup = new ButtonGroup();
            JRadioButton yesButton = new JRadioButton("Yes");
            JRadioButton noButton = new JRadioButton("No");
            yesNoGroup.add(yesButton);
            yesNoGroup.add(noButton);
            JPanel radioPanel = new JPanel();
            radioPanel.add(yesButton);
            radioPanel.add(noButton);
            removeItemsPanel.add(radioPanel);

            JButton confirmButton = new JButton("Confirm");
            removeItemsPanel.add(confirmButton);

            JPanel dynamicPanel = new JPanel();
            dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.Y_AXIS));
            removeItemsPanel.add(dynamicPanel);

            confirmButton.addActionListener(qe -> {
                dynamicPanel.removeAll();
                if (yesButton.isSelected()) {
                    // Show checkboxes for items
                    try {
                        DeliveryDTO delivery = shipmentService.getDelivery(deliveryID);
                        LinkedList<SimpleEntry<String, Integer>> items = delivery.getListOfItems();

                        JLabel itemsLabel = new JLabel("Select items to remove:");
                        dynamicPanel.add(itemsLabel);

                        java.util.List<JCheckBox> checkBoxes = new LinkedList<>();
                        for (SimpleEntry<String, Integer> item : items) {
                            JCheckBox cb = new JCheckBox(item.getKey() + " (" + item.getValue() + " kg)");
                            dynamicPanel.add(cb);
                            checkBoxes.add(cb);
                        }

                        JButton removeSelectedButton = new JButton("Remove Selected Items");
                        dynamicPanel.add(Box.createVerticalStrut(10));
                        dynamicPanel.add(removeSelectedButton);

                        removeSelectedButton.addActionListener(re -> {
                            LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();
                            for (JCheckBox cb : checkBoxes) {
                                if (cb.isSelected()) {
                                    String[] parts = cb.getText().split(" \\(");
                                    String name = parts[0];
                                    int weight = Integer.parseInt(parts[1].replace(" kg)", ""));
                                    itemsToRemove.add(new SimpleEntry<>(name, weight));
                                }
                            }

                            if (itemsToRemove.isEmpty()) {
                                JOptionPane.showMessageDialog(removeItemsFrame, "No items selected.", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            try {
                                shipmentService.removeItemsFromTruck(-1, deliveryID, itemsToRemove);
                                JOptionPane.showMessageDialog(removeItemsFrame, "Items removed successfully.",
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                                JOptionPane.showMessageDialog(removeItemsFrame,
                                        "Now you can try to assign the delivery to the truck again.", "Info",
                                        JOptionPane.INFORMATION_MESSAGE);
                                removeItemsFrame.dispose();
                                mainMenuFrame.setVisible(true);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(removeItemsFrame,
                                        "Failed to remove items: " + ex.getMessage(), "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        });

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(removeItemsFrame, "Error: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } else if (noButton.isSelected()) {
                    try {
                        LinkedList<DocumentDTO> documents = shipmentService.getTruckDocuments(truckID);
                        if (documents.isEmpty()) {
                            JOptionPane.showMessageDialog(removeItemsFrame,
                                    "No other deliveries are assigned to this truck.", "Info",
                                    JOptionPane.INFORMATION_MESSAGE);
                            removeItemsFrame.dispose();
                            optionsFrame.setVisible(true);
                            return;
                        }

                        // Step 1: Select a document
                        JLabel selectDocLabel = new JLabel("Select a delivery to remove items from:");
                        dynamicPanel.add(selectDocLabel);
                        JComboBox<String> docComboBox = new JComboBox<>();
                        Map<String, Integer> docDisplayToId = new HashMap<>();

                        for (DocumentDTO doc : documents) {
                            int totalWeight = doc.getListOfItems().stream().mapToInt(SimpleEntry::getValue).sum();
                            String display = "Delivery " + doc.getId() + " - Total Weight: " + totalWeight + " kg";
                            docComboBox.addItem(display);
                            docDisplayToId.put(display, doc.getId());
                        }

                        dynamicPanel.add(docComboBox);

                        JButton confirmDocButton = new JButton("Select Delivery");
                        dynamicPanel.add(Box.createVerticalStrut(10));
                        dynamicPanel.add(confirmDocButton);

                        confirmDocButton.addActionListener(docSelectEvent -> {
                            String selected = (String) docComboBox.getSelectedItem();
                            if (selected == null) {
                                JOptionPane.showMessageDialog(removeItemsFrame, "Please select a delivery.",
                                        "Input Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            int selectedDocID = docDisplayToId.get(selected);
                            DocumentDTO selectedDoc = documents.stream()
                                    .filter(doc -> doc.getId() == selectedDocID)
                                    .findFirst()
                                    .orElse(null);

                            if (selectedDoc == null || selectedDoc.getListOfItems().isEmpty()) {
                                JOptionPane.showMessageDialog(removeItemsFrame,
                                        "No items found in the selected delivery.", "Info",
                                        JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }

                            // Clear old components
                            dynamicPanel.removeAll();
                            dynamicPanel.add(new JLabel("Select items to remove from Delivery " + selectedDocID + ":"));

                            // Create checkbox list for items
                            JPanel itemPanel = new JPanel();
                            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
                            JScrollPane itemScroll = new JScrollPane(itemPanel);
                            itemScroll.setPreferredSize(new Dimension(450, 200));
                            itemScroll.setBorder(BorderFactory.createTitledBorder("Items"));

                            LinkedList<JCheckBox> checkBoxes = new LinkedList<>();
                            for (SimpleEntry<String, Integer> item : selectedDoc.getListOfItems()) {
                                JCheckBox cb = new JCheckBox(item.getKey() + " (" + item.getValue() + " kg)");
                                checkBoxes.add(cb);
                                itemPanel.add(cb);
                            }

                            dynamicPanel.add(itemScroll);

                            JButton removeSelected = new JButton("Remove Selected Items");
                            dynamicPanel.add(Box.createVerticalStrut(10));
                            dynamicPanel.add(removeSelected);

                            removeSelected.addActionListener(removeEvent -> {
                                LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();
                                for (JCheckBox cb : checkBoxes) {
                                    if (cb.isSelected()) {
                                        String[] parts = cb.getText().split(" \\(");
                                        String name = parts[0];
                                        int weight = Integer.parseInt(parts[1].replace(" kg)", ""));
                                        itemsToRemove.add(new SimpleEntry<>(name, weight));
                                    }
                                }

                                if (itemsToRemove.isEmpty()) {
                                    JOptionPane.showMessageDialog(removeItemsFrame, "No items selected for removal.",
                                            "Input Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                try {
                                    shipmentService.removeItemsFromTruck(truckID, selectedDocID, itemsToRemove);
                                    JOptionPane.showMessageDialog(removeItemsFrame,
                                            "Items removed successfully from delivery " + selectedDocID + ".",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                                    removeItemsFrame.dispose();
                                    mainMenuFrame.setVisible(true);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(removeItemsFrame,
                                            "Failed to remove items: " + ex.getMessage(), "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            });

                            dynamicPanel.revalidate();
                            dynamicPanel.repaint();
                        });

                        dynamicPanel.revalidate();
                        dynamicPanel.repaint();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(removeItemsFrame,
                                "Failed to retrieve truck documents: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(removeItemsFrame, "Please select Yes or No.", "Input Error",
                            JOptionPane.WARNING_MESSAGE);
                }

                dynamicPanel.revalidate();
                dynamicPanel.repaint();
            });

            JScrollPane scroll = new JScrollPane(removeItemsPanel);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            removeItemsFrame.getContentPane().add(scroll);
            removeItemsFrame.setVisible(true);
        });

        optionsPanel.add(removeItemsButton);

        optionsFrame.getContentPane().add(optionsPanel);
        optionsFrame.setVisible(true);
    }

    private void addTruck(JFrame previousFrame) {
        JFrame frame = new JFrame("Add Truck");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel truckLabel = new JLabel("Truck Details");
        mainPanel.add(truckLabel);
        JLabel modelLabel = new JLabel("Model:");
        JTextField modelField = new JTextField(20);
        JLabel dryWeightLabel = new JLabel("Dry Weight (kg):");
        JTextField dryWeightField = new JTextField(10);
        JLabel maxLoadLabel = new JLabel("Max Load (kg) including dry weight:");
        JTextField maxLoadField = new JTextField(10);
        JLabel licenseTypeLabel = new JLabel("License Type (capital letter only, e.g. A, B, C...):");
        JTextField licenseTypeField = new JTextField(2);

        mainPanel.add(modelLabel);
        mainPanel.add(modelField);
        mainPanel.add(dryWeightLabel);
        mainPanel.add(dryWeightField);
        mainPanel.add(maxLoadLabel);
        mainPanel.add(maxLoadField);
        mainPanel.add(licenseTypeLabel);
        mainPanel.add(licenseTypeField);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between form and buttons

        JButton addTruckButton = new JButton("Add Truck");

        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        addTruckButton.addActionListener(e -> {
            String model = modelField.getText().trim();
            String dryWeightStr = dryWeightField.getText().trim();
            String maxLoadStr = maxLoadField.getText().trim();
            String licenseType = licenseTypeField.getText().trim();

            if (model.isEmpty() || dryWeightStr.isEmpty() || maxLoadStr.isEmpty() || licenseType.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int dryWeight = Integer.parseInt(dryWeightStr);
                int maxLoad = Integer.parseInt(maxLoadStr);
                if (dryWeight <= 0 || maxLoad <= 0) {
                    JOptionPane.showMessageDialog(frame, "Dry weight and max load must be positive numbers.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (maxLoad <= dryWeight) {
                    JOptionPane.showMessageDialog(frame, "Max load must be greater than dry weight.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (licenseType.length() != 1 || !Character.isUpperCase(licenseType.charAt(0))) {
                    JOptionPane.showMessageDialog(frame, "License type must be a single uppercase letter.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int truckID = managerService.getMaxTruckID() + 1;
                TruckDTO truck = new TruckDTO(truckID, model, dryWeight, dryWeight, maxLoad, true, licenseType,
                        new LinkedList<>());
                managerService.addTruck(truck);
                JOptionPane.showMessageDialog(frame, "Truck added successfully with ID: " + truckID, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close window
                previousFrame.setVisible(true); // Show previous frame
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid dry weight or max load. Please enter valid numbers.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to add truck: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(addTruckButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // space between buttons
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel); // add the horizontal button panel to vertical main panel
        mainPanel.add(Box.createVerticalStrut(20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void getTruckDeliveries(JFrame previousFrame) {
        JFrame frame = new JFrame("Get Truck Deliveries");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel truckLabel = new JLabel("Select Truck:");
        JComboBox<String> truckComboBox = new JComboBox<>();
        try {
            LinkedList<TruckDTO> trucks = shipmentService.getAllTrucks();
            for (TruckDTO truck : trucks) {
                truckComboBox.addItem("Truck ID: " + truck.getId() + " - " + truck.getModel() + ", License Type: "
                        + truck.getLicense());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading trucks: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainPanel.add(truckLabel);
        mainPanel.add(truckComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between truck selection and button

        JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(e -> {
            String selectedTruck = (String) truckComboBox.getSelectedItem();
            if (selectedTruck == null) {
                JOptionPane.showMessageDialog(frame, "Please select a truck.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int truckID = Integer.parseInt(selectedTruck.split(" - ")[0].split(": ")[1]);
            try {
                LinkedList<DocumentDTO> documents = shipmentService.getTruckDocuments(truckID);
                if (documents.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No deliveries assigned to this truck.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                StringBuilder deliveryInfo = new StringBuilder("Deliveries for Truck ID: " + truckID + "\n\n\n");
                for (DocumentDTO doc : documents) {
                    int totalWeight = doc.getListOfItems().stream().mapToInt(SimpleEntry::getValue).sum();
                    deliveryInfo.append("Delivery ID: ").append(doc.getId()).append(" - Total Weight: ")
                            .append(totalWeight).append(" kg\n")
                            .append("Origin: ").append(doc.getOrigin().getAddress()).append(", ")
                            .append(doc.getOrigin().getAreaName()).append("\n")
                            .append("Destination: ").append(doc.getDestination().getAddress()).append(", ")
                            .append(doc.getDestination().getAreaName()).append("\n\n\n\n");
                }
                JTextArea textArea = new JTextArea(deliveryInfo.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                mainPanel.add(scrollPane);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to retrieve truck deliveries: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        JButton doneButton = new JButton("Done");

        doneButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // space between buttons
        buttonPanel.add(doneButton);

        mainPanel.add(buttonPanel); // add the horizontal button panel to vertical main panel
        mainPanel.add(Box.createVerticalStrut(20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void sendTruckToDistributeDeliveries(JFrame previousFrame) {
        JFrame frame = new JFrame("Distribute Deliveries");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel truckLabel = new JLabel("Select Truck:");
        mainPanel.add(truckLabel);
    
        JComboBox<String> truckComboBox = new JComboBox<>();
        LinkedList<TruckDTO> trucks;
    
        try {
            trucks = shipmentService.getAllTrucks();
            for (TruckDTO truck : trucks) {
                truckComboBox.addItem("Truck ID: " + truck.getId() + " - " + truck.getModel() + ", License Type: "
                        + truck.getLicense());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading trucks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            previousFrame.setVisible(true);
            return;
        }
    
        if (truckComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No trucks available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            previousFrame.setVisible(true);
            return;
        }
    
        mainPanel.add(truckComboBox);
        mainPanel.add(Box.createVerticalStrut(20));
    
        // Day of the week input
        JLabel dayLabel = new JLabel("Select Day of the Week:");
        JComboBox<String> dayComboBox = new JComboBox<>(new String[]{
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        });
        mainPanel.add(dayLabel);
        mainPanel.add(dayComboBox);
        mainPanel.add(Box.createVerticalStrut(10));
    
        // Hour input
        JLabel hourLabel = new JLabel("Enter Hour (0-23):");
        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(LocalTime.now().getHour(), 0, 23, 1));
        mainPanel.add(hourLabel);
        mainPanel.add(hourSpinner);
        mainPanel.add(Box.createVerticalStrut(20));
    
        // Buttons
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
    
        confirmButton.addActionListener(e -> {
            String selectedTruck = (String) truckComboBox.getSelectedItem();
            String selectedDay = (String) dayComboBox.getSelectedItem();
            int hour = (int) hourSpinner.getValue();
    
            if (selectedTruck != null && selectedDay != null) {
                try {
                    int truckID = Integer.parseInt(selectedTruck.split(" - ")[0].split(": ")[1]);
                    shipmentService.sendTruckToDistributeDeliveries(truckID, selectedDay, hour);
                    JOptionPane.showMessageDialog(frame, "Truck " + truckID + " sent to distribute deliveries.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    previousFrame.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to send truck: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    
        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
    
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
    
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    
    private void changeDeliveryDestination(JFrame previousFrame) {
        JFrame frame = new JFrame("Change Delivery Destination");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel deliveryLabel = new JLabel("Select Delivery:");
        JComboBox<String> deliveryComboBox = new JComboBox<>();
        try {
            LinkedList<DeliveryDTO> deliveries = shipmentService.getAllDeliveries();
            for (DeliveryDTO delivery : deliveries) {
                int weight = delivery.getListOfItems().stream().mapToInt(SimpleEntry::getValue).sum();
                deliveryComboBox.addItem("Delivery ID: " + delivery.getId() + " - "
                        + delivery.getOriginLoc().getAddress() + ", " + delivery.getOriginLoc().getAreaName() + " to "
                        + delivery.getDestinationLoc().getAddress() + ", " + delivery.getDestinationLoc().getAreaName()
                        + " with weight: " + weight + " kg");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading deliveries: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainPanel.add(deliveryLabel);
        mainPanel.add(deliveryComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between delivery selection and destination input

        JLabel destinationLabel = new JLabel("Select New Destination:");
        JComboBox<String> destinationComboBox = new JComboBox<>();
        LinkedList<AreaDTO> areas;
        try {
            areas = managerService.getAllAreas();
            for (AreaDTO area : areas) {
                destinationComboBox.addItem(area.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading areas: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> locationsComboBox = new JComboBox<>();

        destinationComboBox.addItemListener(e -> {
            locationsComboBox.removeAllItems();
            String selectedArea = (String) destinationComboBox.getSelectedItem();
            if (selectedArea != null) {
                try {
                    LinkedList<LocationDTO> locations;
                    for (AreaDTO area : areas) {
                        if (area.getName().equals(selectedArea)) {
                            locations = area.getLocations();
                            for (LocationDTO location : locations) {
                                locationsComboBox.addItem(location.getAddress());
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error loading locations: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainPanel.add(destinationLabel);
        mainPanel.add(destinationComboBox);
        mainPanel.add(locationsComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between destination selection and button

        JButton changeDestinationButton = new JButton("Change Destination");
        JButton cancelButton = new JButton("Cancel");

        changeDestinationButton.addActionListener(e -> {
            String selectedDelivery = (String) deliveryComboBox.getSelectedItem();
            String selectedLocation = (String) locationsComboBox.getSelectedItem();
            String selectedArea = (String) destinationComboBox.getSelectedItem();

            if (selectedDelivery == null || selectedLocation == null || selectedArea == null) {
                JOptionPane.showMessageDialog(frame, "Please select a delivery and a new destination.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int deliveryID = Integer.parseInt(selectedDelivery.split(" - ")[0].split(": ")[1]);
            try {
                shipmentService.changeDestination(selectedArea, deliveryID, selectedLocation);
                JOptionPane
                        .showMessageDialog(
                                frame, "Delivery " + deliveryID + " destination changed to " + selectedLocation
                                        + " in area " + selectedArea + " successfully.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close window
                previousFrame.setVisible(true); // Show previous frame
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to change delivery destination: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(changeDestinationButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // space between buttons
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel); // add the horizontal button panel to vertical main panel
        mainPanel.add(Box.createVerticalStrut(20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void cancelDelivery(JFrame previousFrame) {
        JFrame frame = new JFrame("Cancel Delivery");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel deliveryLabel = new JLabel("Select Delivery to Cancel:");
        JComboBox<String> deliveryComboBox = new JComboBox<>();
        try {
            LinkedList<DeliveryDTO> deliveries = shipmentService.getAllDeliveries();
            for (DeliveryDTO delivery : deliveries) {
                int weight = delivery.getListOfItems().stream().mapToInt(SimpleEntry::getValue).sum();
                deliveryComboBox.addItem("Delivery ID: " + delivery.getId() + " - "
                        + delivery.getOriginLoc().getAddress() + ", " + delivery.getOriginLoc().getAreaName() + " to "
                        + delivery.getDestinationLoc().getAddress() + ", " + delivery.getDestinationLoc().getAreaName()
                        + " with weight: " + weight + " kg");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading deliveries: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainPanel.add(deliveryLabel);
        mainPanel.add(deliveryComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between delivery selection and button

        JButton confirmButton = new JButton("Confirm Cancellation");

        confirmButton.addActionListener(e -> {
            String selectedDelivery = (String) deliveryComboBox.getSelectedItem();
            if (selectedDelivery == null) {
                JOptionPane.showMessageDialog(frame, "Please select a delivery to cancel.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int deliveryID = Integer.parseInt(selectedDelivery.split(" - ")[0].split(": ")[1]);
            try {
                shipmentService.cancelDelivery(deliveryID);
                JOptionPane.showMessageDialog(frame, "Delivery " + deliveryID + " cancelled successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close window
                previousFrame.setVisible(true); // Show previous frame
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to cancel delivery: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // space between buttons
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel); // add the horizontal button panel to vertical main panel
        mainPanel.add(Box.createVerticalStrut(20)); // Add space before the scroll pane

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void changeTruck(JFrame previousFrame) {
        JFrame frame = new JFrame("Change Truck");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel deliveryLabel = new JLabel("Select Delivery:");
        JComboBox<String> deliveryComboBox = new JComboBox<>();
        try {
            LinkedList<DeliveryDTO> deliveries = shipmentService.getAssignedDeliveries();
            for (DeliveryDTO delivery : deliveries) {
                int weight = delivery.getListOfItems().stream().mapToInt(SimpleEntry::getValue).sum();
                deliveryComboBox.addItem("Delivery ID: " + delivery.getId() + " - "
                        + delivery.getOriginLoc().getAddress() + ", " + delivery.getOriginLoc().getAreaName() + " to "
                        + delivery.getDestinationLoc().getAddress() + ", " + delivery.getDestinationLoc().getAreaName()
                        + " with weight: " + weight + " kg");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading deliveries: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainPanel.add(deliveryLabel);
        mainPanel.add(deliveryComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between delivery selection and truck selection

        JLabel truckLabel = new JLabel("Select New Truck:");
        JComboBox<String> truckComboBox = new JComboBox<>();
        try {
            LinkedList<TruckDTO> trucks = shipmentService.getAvailableTrucks();
            for (TruckDTO truck : trucks) {
                truckComboBox.addItem("Truck ID: " + truck.getId() + " - " + truck.getModel() + ", Available Weight: "
                        + (truck.getMaxLoad() - truck.getWeight()) + " kg");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading trucks: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainPanel.add(truckLabel);
        mainPanel.add(truckComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between truck selection and button

        JButton changeTruckButton = new JButton("Change Truck");
        JButton cancelButton = new JButton("Cancel");

        changeTruckButton.addActionListener(e -> {
            String selectedDelivery = (String) deliveryComboBox.getSelectedItem();
            String selectedTruck = (String) truckComboBox.getSelectedItem();

            if (selectedDelivery == null || selectedTruck == null) {
                JOptionPane.showMessageDialog(frame, "Please select a delivery and a new truck.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int deliveryID = Integer.parseInt(selectedDelivery.split(" - ")[0].split(": ")[1]);
            int truckID = Integer.parseInt(selectedTruck.split(" - ")[0].split(": ")[1]);

            try {
                shipmentService.changeTruck(deliveryID, truckID);
                JOptionPane.showMessageDialog(frame,
                        "Delivery " + deliveryID + " changed to truck " + truckID + " successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close window
                previousFrame.setVisible(true); // Show previous frame
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to change truck: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(changeTruckButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // space between buttons
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel); // add the horizontal button panel to vertical main panel
        mainPanel.add(Box.createVerticalStrut(20)); // Add space before the scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    public void TransportManagerMenu() {
        JFrame menuframe = new JFrame("Transportation Manager Menu");
        menuframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        menuframe.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Tranportation Manager Menu");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20)); // Add space between title and buttons

        JButton addAreaButton = new JButton("Add Area");
        addAreaButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        addAreaButton.addActionListener(e -> {
            menuframe.setVisible(false);
            addArea(menuframe);
        });
        panel.add(addAreaButton);
        panel.add(Box.createVerticalStrut(10));

        JButton addLocationToAreaButton = new JButton("Add Location to Area");
        addLocationToAreaButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        addLocationToAreaButton.addActionListener(e -> {
            menuframe.setVisible(false);
            addLocationToArea(menuframe);
        });
        panel.add(addLocationToAreaButton);
        panel.add(Box.createVerticalStrut(10));

        JButton addDeliveryButton = new JButton("Add Delivery");
        addDeliveryButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        addDeliveryButton.addActionListener(e -> {
            menuframe.setVisible(false);
            addDelivery(menuframe);
        });
        panel.add(addDeliveryButton);
        panel.add(Box.createVerticalStrut(10));

        JButton assignDeliveryButton = new JButton("Assign Delivery to Truck");
        assignDeliveryButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        assignDeliveryButton.addActionListener(e -> {
            menuframe.setVisible(false);
            assignDeliveryToTruck(menuframe);
        });
        panel.add(assignDeliveryButton);
        panel.add(Box.createVerticalStrut(10));

        JButton getTruckDeliveriesButton = new JButton("Get Truck Deliveries");
        getTruckDeliveriesButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        getTruckDeliveriesButton.addActionListener(e -> {
            menuframe.setVisible(false);
            getTruckDeliveries(menuframe);
        });
        panel.add(getTruckDeliveriesButton);
        panel.add(Box.createVerticalStrut(10));

        JButton changeDeliveryDestinationButton = new JButton("Change Delivery Destination");
        changeDeliveryDestinationButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        changeDeliveryDestinationButton.addActionListener(e -> {
            menuframe.setVisible(false);
            changeDeliveryDestination(menuframe);
        });
        panel.add(changeDeliveryDestinationButton);
        panel.add(Box.createVerticalStrut(10));

        JButton cancelDeliveryButton = new JButton("Cancel Delivery");
        cancelDeliveryButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        cancelDeliveryButton.addActionListener(e -> {
            menuframe.setVisible(false);
            cancelDelivery(menuframe);
        });
        panel.add(cancelDeliveryButton);
        panel.add(Box.createVerticalStrut(10));

        JButton updateDocumentButton = new JButton("Update Document");
        updateDocumentButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        updateDocumentButton.addActionListener(e -> {
            menuframe.setVisible(false);
            updateDocument(menuframe);
        });
        panel.add(updateDocumentButton);
        panel.add(Box.createVerticalStrut(10));

        JButton changeTruckButton = new JButton("Change Truck");
        changeTruckButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        changeTruckButton.addActionListener(e -> {
            menuframe.setVisible(false);
            changeTruck(menuframe);
        });
        panel.add(changeTruckButton);
        panel.add(Box.createVerticalStrut(10));

        JButton removeItemsFromDeliveryButton = new JButton("Remove Items from Delivery");
        removeItemsFromDeliveryButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        removeItemsFromDeliveryButton.addActionListener(e -> {
            menuframe.setVisible(false);
            removeItemsFromDelivery(menuframe);
        });
        panel.add(removeItemsFromDeliveryButton);
        panel.add(Box.createVerticalStrut(10));

        JButton addTruckButton = new JButton("Add Truck");
        addTruckButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        addTruckButton.addActionListener(e -> {
            menuframe.setVisible(false);
            addTruck(menuframe);
        });
        panel.add(addTruckButton);
        panel.add(Box.createVerticalStrut(10));

        JButton distributeButton = new JButton("Send Truck to Distribute Deliveries");
        distributeButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        distributeButton.addActionListener(e -> {
            menuframe.setVisible(false);
            sendTruckToDistributeDeliveries(menuframe);
        });
        panel.add(distributeButton);
        panel.add(Box.createVerticalStrut(10));

        JButton listDeliveriesButton = new JButton("List All Deliveries by Time");
        listDeliveriesButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        listDeliveriesButton.addActionListener(e -> {
            menuframe.setVisible(false);
            listDeliveriesByTime(menuframe);
        });
        panel.add(listDeliveriesButton);
        panel.add(Box.createVerticalStrut(10));

        JButton complaintButton = new JButton("Complain On Driver");
        complaintButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        complaintButton.addActionListener(e -> {
            menuframe.setVisible(false);
            complainOnDriver(menuframe);
        });
        panel.add(complaintButton);
        panel.add(Box.createVerticalStrut(10));

        JButton deleteTruckButton = new JButton("Delete Truck");
        deleteTruckButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        deleteTruckButton.addActionListener(e -> {
            menuframe.setVisible(false);
            deleteTruck(menuframe);
        });
        panel.add(deleteTruckButton);
        panel.add(Box.createVerticalStrut(10));

        JButton deleteAreaButton = new JButton("Delete Area");
        deleteAreaButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        deleteAreaButton.addActionListener(e -> {
            menuframe.setVisible(false);
            deleteArea(menuframe);
        });
        panel.add(deleteAreaButton);
        panel.add(Box.createVerticalStrut(10));

        JButton deleteLocationButton = new JButton("Delete Location");
        deleteLocationButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        deleteLocationButton.addActionListener(e -> {
            menuframe.setVisible(false);
            deleteLocation(menuframe);
        });
        panel.add(deleteLocationButton);
        panel.add(Box.createVerticalStrut(10));

        JButton supplierPickupButton = new JButton("Supplier Pickup Items");
        supplierPickupButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        supplierPickupButton.addActionListener(e -> {
            menuframe.setVisible(false);
            supplierPickUpItems(menuframe);
        });
        panel.add(supplierPickupButton);
        panel.add(Box.createVerticalStrut(10));

        JButton getAllShiftsButton = new JButton("Get All Shifts Assignments For The Day");
        getAllShiftsButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        getAllShiftsButton.addActionListener(e -> {
            menuframe.setVisible(false);
            getAllShiftAssignmentsForTheDay(menuframe);
        });
        panel.add(getAllShiftsButton);
        panel.add(Box.createVerticalStrut(10));

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(menuframe, "Returning to login screen.");
            menuframe.dispose();
        });
        panel.add(backButton);
        panel.add(Box.createVerticalStrut(20));

        JScrollPane scrollPane = new JScrollPane(panel);
        menuframe.add(scrollPane);
        menuframe.setVisible(true);
    }

    private void getAllShiftAssignmentsForTheDay(JFrame previousFrame) {
        JFrame frame = new JFrame("Get All Shift Assignments For The Day");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Select Day to View Shift Assignments:");
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        JComboBox<String> dayComboBox = new JComboBox<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dayComboBox.addItem(day.name()); // MONDAY, TUESDAY...
        }
        mainPanel.add(dayComboBox);
        mainPanel.add(Box.createVerticalStrut(10));

        JTextArea shiftArea = new JTextArea(15, 40);
        shiftArea.setEditable(false);
        JScrollPane shiftScrollPane = new JScrollPane(shiftArea);
        mainPanel.add(shiftScrollPane);
        mainPanel.add(Box.createVerticalStrut(10));

        JButton showButton = new JButton("Show Shifts");
        JButton cancelButton = new JButton("Cancel");

        showButton.addActionListener(e -> {
            String selectedDay = (String) dayComboBox.getSelectedItem();
            if (selectedDay == null) {
                JOptionPane.showMessageDialog(frame, "Please select a day.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LinkedList<LinkedList<TransportationEmployeeDTO>> shifts = managerService
                        .getAllShiftAssignmentsForTheDay(selectedDay);

                if (shifts.isEmpty()) {
                    shiftArea.setText("No shift assignments for " + selectedDay + ".");
                    return;
                }

                StringBuilder sb = new StringBuilder();
                int shiftNumber = 1;
                for (LinkedList<TransportationEmployeeDTO> shift : shifts) {
                    sb.append("Shift ").append(shiftNumber++).append(":\n");
                    for (TransportationEmployeeDTO emp : shift) {
                        sb.append("- ").append(emp.getName()).append("\n");
                    }
                    sb.append("\n");
                }

                shiftArea.setText(sb.toString());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error fetching shifts: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            frame.dispose();
            previousFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(showButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void supplierPickUpItems(JFrame menuframe) {
        JFrame frame = new JFrame("Supplier Pickup Items");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel titleLabel = new JLabel("Select Delivery for Pickup:");
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
    
        JComboBox<String> deliveryComboBox = new JComboBox<>();
        LinkedList<DeliveryDTO> deliveries;
    
        try {
            deliveries = shipmentService.getNotAssignedDeliveries();
            if (deliveries.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No deliveries available for pickup.", "Info", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                menuframe.setVisible(true);
                return;
            }
    
            for (DeliveryDTO delivery : deliveries) {
                StringBuilder itemListStr = new StringBuilder();
                for (SimpleEntry<String, Integer> item : delivery.getListOfItems()) {
                    itemListStr.append(item.getKey()).append(" (").append(item.getValue()).append(" kg), ");
                }
                if (!delivery.getListOfItems().isEmpty()) {
                    itemListStr.setLength(itemListStr.length() - 2);
                }
    
                deliveryComboBox.addItem("Delivery ID: " + delivery.getId() + " - "
                        + delivery.getOriginLoc().getAddress() + ", " + delivery.getOriginLoc().getAreaName()
                        + " to " + delivery.getDestinationLoc().getAddress() + ", "
                        + delivery.getDestinationLoc().getAreaName()
                        + " | Items: " + itemListStr.toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading deliveries: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            menuframe.setVisible(true);
            return;
        }
    
        mainPanel.add(deliveryComboBox);
        mainPanel.add(Box.createVerticalStrut(20));
    
        JButton confirmButton = new JButton("Confirm Pickup");
        confirmButton.addActionListener(e -> {
            String selectedDelivery = (String) deliveryComboBox.getSelectedItem();
            if (selectedDelivery != null) {
                try {
                    int deliveryID = Integer.parseInt(selectedDelivery.split(" - ")[0].split(": ")[1]);
                    DocumentDTO doc = shipmentService.supplierPickUpItems(deliveryID); // assume this returns the DocumentDTO
    
                    // Build document summary
                    StringBuilder docSummary = new StringBuilder("Document Details:\n");
                    docSummary.append("Document ID: ").append(doc.getId()).append("\n");
                    docSummary.append("From: ").append(doc.getOrigin().getAddress()).append("\n");
                    docSummary.append("To: ").append(doc.getDestination().getAddress()).append("\n");
                    docSummary.append("Items:\n");
                    for (SimpleEntry<String, Integer> item : doc.getListOfItems()) {
                        docSummary.append("- ").append(item.getKey()).append(": ").append(item.getValue()).append(" kg\n");
                    }
    
                    JOptionPane.showMessageDialog(frame, docSummary.toString(), "Pickup Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    menuframe.setVisible(true);
    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to confirm pickup: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            frame.dispose();
            menuframe.setVisible(true);
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
    
        mainPanel.add(buttonPanel);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    

    private void deleteLocation(JFrame menuframe) {
        JFrame frame = new JFrame("Delete Location");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Select Location to Delete:");
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        JComboBox<String> locationComboBox = new JComboBox<>();
        try {
            LinkedList<AreaDTO> areas = managerService.getAllAreas();
            for (AreaDTO area : areas) {
                for (LocationDTO location : area.getLocations()) {
                    locationComboBox.addItem(area.getName() + " - " + location.getAddress());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading locations: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(locationComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No locations available to delete.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            menuframe.setVisible(true);
            return;
        }
        mainPanel.add(locationComboBox);
        mainPanel.add(Box.createVerticalStrut(20));
        JButton confirmButton = new JButton("Confirm Deletion");
        confirmButton.addActionListener(e -> {
            String selectedLocation = (String) locationComboBox.getSelectedItem();
            if (selectedLocation != null) {
                String[] parts = selectedLocation.split(" - ");
                String areaName = parts[0];
                String address = parts[1];
                try {
                    managerService.deleteLocation(areaName, address);
                    JOptionPane.showMessageDialog(frame, "Location " + address + " in area " + areaName
                            + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose(); // Close window
                    menuframe.setVisible(true); // Show previous frame
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to delete location: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        mainPanel.add(confirmButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            frame.dispose();
            menuframe.setVisible(true);
        });
        mainPanel.add(cancelButton);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space before the scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);

    }

    private void deleteArea(JFrame menuframe) {
        JFrame frame = new JFrame("Delete Area");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Select Area to Delete:");
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        JComboBox<String> areaComboBox = new JComboBox<>();
        try {
            LinkedList<AreaDTO> areas = managerService.getAllAreas();
            for (AreaDTO area : areas) {
                areaComboBox.addItem(area.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading areas: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(areaComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No areas available to delete.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            menuframe.setVisible(true);
            return;
        }
        mainPanel.add(areaComboBox);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between area selection and button
        JButton confirmButton = new JButton("Confirm Deletion");
        confirmButton.addActionListener(e -> {
            String selectedArea = (String) areaComboBox.getSelectedItem();
            if (selectedArea != null) {
                try {
                    managerService.deleteArea(selectedArea);
                    JOptionPane.showMessageDialog(frame, "Area " + selectedArea + " deleted successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose(); // Close window
                    menuframe.setVisible(true); // Show previous frame
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to delete area: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        mainPanel.add(confirmButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            frame.dispose();
            menuframe.setVisible(true);
        });
        mainPanel.add(cancelButton);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space before the scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);



    }

    private void deleteTruck(JFrame menuframe) {
        JFrame frame = new JFrame("Delete Truck");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel titleLabel = new JLabel("Select Truck to Delete:");
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
    
        JComboBox<String> truckComboBox = new JComboBox<>();
        LinkedList<TruckDTO> trucks;
    
        try {
            trucks = shipmentService.getAllTrucks();
            for (TruckDTO truck : trucks) {
                truckComboBox.addItem("Truck ID: " + truck.getId() + " - " + truck.getModel() + ", License Type: " + truck.getLicense());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading trucks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (truckComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No trucks available to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            menuframe.setVisible(true);
            return;
        }
    
        mainPanel.add(truckComboBox);
        mainPanel.add(Box.createVerticalStrut(10));
    
        JPanel reassignmentPanel = new JPanel();
        reassignmentPanel.setLayout(new BoxLayout(reassignmentPanel, BoxLayout.Y_AXIS));
        mainPanel.add(reassignmentPanel);
    
        truckComboBox.addActionListener(e -> {
            reassignmentPanel.removeAll();
            int selectedIndex = truckComboBox.getSelectedIndex();
            if (selectedIndex != -1) {
                TruckDTO selectedTruck = trucks.get(selectedIndex);
                LinkedList<DocumentDTO> documents = shipmentService.getTruckDocuments(selectedTruck.getId());
    
                if (documents.isEmpty()) {
                    reassignmentPanel.add(new JLabel("No deliveries assigned to this truck."));
                } else {
                    reassignmentPanel.add(new JLabel("Reassign each delivery before deleting truck:"));
    
                    for (DocumentDTO document : documents) {
                        JPanel deliveryPanel = new JPanel();
                        deliveryPanel.setLayout(new BoxLayout(deliveryPanel, BoxLayout.Y_AXIS));
                        deliveryPanel.setBorder(BorderFactory.createTitledBorder("Delivery ID: " + document.getId()));
    
                        JLabel deliveryInfo = new JLabel("From " + document.getOrigin().getAddress() + " to " + document.getDestination().getAddress());
                        JComboBox<String> reassignmentBox = new JComboBox<>();
    
                        for (TruckDTO otherTruck : trucks) {
                            if (otherTruck.getId() != selectedTruck.getId()) {
                                reassignmentBox.addItem("Truck ID: " + otherTruck.getId() + " - " + otherTruck.getModel());
                            }
                        }
    
                        deliveryPanel.add(deliveryInfo);
                        deliveryPanel.add(reassignmentBox);
                        reassignmentPanel.add(deliveryPanel);
                    }
                }
    
                reassignmentPanel.revalidate();
                reassignmentPanel.repaint();
            }
        });
    
        JButton confirmButton = new JButton("Confirm Deletion");
        confirmButton.addActionListener(e -> {
            int selectedIndex = truckComboBox.getSelectedIndex();
            if (selectedIndex == -1) return;
        
            TruckDTO truckToDelete = trucks.get(selectedIndex);
            LinkedList<DocumentDTO> documents = shipmentService.getTruckDocuments(truckToDelete.getId());
        
            try {
                int docIndex = 0;
                for (Component comp : reassignmentPanel.getComponents()) {
                    if (!(comp instanceof JPanel)) continue;
                    JPanel panel = (JPanel) comp;
                    if (docIndex >= documents.size()) break;
        
                    DocumentDTO currentDoc = documents.get(docIndex);
                    JComboBox<?> comboBox = null;
                    for (Component inner : panel.getComponents()) {
                        if (inner instanceof JComboBox<?>) {
                            comboBox = (JComboBox<?>) inner;
                            break;
                        }
                    }
        
                    if (comboBox != null && comboBox.getSelectedItem() != null) {
                        String selectedText = comboBox.getSelectedItem().toString();
                        int newTruckID = Integer.parseInt(selectedText.split(" - ")[0].split(": ")[1]);
                        shipmentService.assignDeliveryToTruck(currentDoc.getId(), newTruckID); // use real doc ID
                    }
        
                    docIndex++;
                }
        
                managerService.deleteTruck(truckToDelete.getId());
                JOptionPane.showMessageDialog(frame, "Truck deleted and deliveries reassigned.", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                menuframe.setVisible(true);
        
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to delete truck: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
    
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            frame.dispose();
            menuframe.setVisible(true);
        });
    
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(confirmButton);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(cancelButton);
    
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }
    

    private void complainOnDriver(JFrame menuframe) {
        JFrame frame = new JFrame("Complain About a Driver");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel idLabel = new JLabel("Enter Driver Identifier :");
        JTextField idField = new JTextField(30);
    
        JLabel complaintLabel = new JLabel("Enter Complaint:");
        JTextArea complaintArea = new JTextArea(5, 30);
        JScrollPane complaintScroll = new JScrollPane(complaintArea);
    
        JButton submitButton = new JButton("Submit Complaint");
        JButton cancelButton = new JButton("Cancel");
        mainPanel.add(idLabel);
        mainPanel.add(idField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(complaintLabel);
        mainPanel.add(complaintScroll);
        mainPanel.add(Box.createVerticalStrut(15));
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(submitButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);
    
        submitButton.addActionListener(e -> {
            String driverId = idField.getText().trim();
            String complaint = complaintArea.getText().trim();
    
            if (driverId.isEmpty() || complaint.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill out both fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                String driverIdFixed = idField.getText().trim().replaceAll("\\s+", " ");
                String complaintFixed = complaintArea.getText().trim().replaceAll("\\s+", " ");
                managerService.complainOnDriver(driverIdFixed, complaintFixed);
                JOptionPane.showMessageDialog(frame, "Complaint submitted. ", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                menuframe.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error submitting complaint: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(e -> {
            frame.dispose();
            menuframe.setVisible(true);
        });
    
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    

    private void listDeliveriesByTime(JFrame menuframe) {
        JFrame frame = new JFrame("List Delivires by Time");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Select Amount of Delivieries to be Displayed:");
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        JTextField amountField = new JTextField(10);
        mainPanel.add(amountField);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space between input and button
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            String amountText = amountField.getText();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int amount = Integer.parseInt(amountText);
                LinkedList<DeliveryDTO> deliveries = shipmentService.listDeliveriesByTime(amount);
                if (deliveries.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No deliveries found for the specified amount.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (DeliveryDTO delivery : deliveries) {
                    sb.append("Delivery ID: ").append(delivery.getId()).append(" - ")
                            .append(delivery.getOriginLoc().getAddress()).append(", ")
                            .append(delivery.getOriginLoc().getAreaName()).append(" to ")
                            .append(delivery.getDestinationLoc().getAddress()).append(", ")
                            .append(delivery.getDestinationLoc().getAreaName()).append("\n")
                            .append("Items:\n");
                    for (SimpleEntry<String, Integer> item : delivery.getListOfItems()) {
                        sb.append("- ").append(item.getKey()).append(" (").append(item.getValue()).append(" kg)\n");
                    }
                }
                JOptionPane.showMessageDialog(frame, sb.toString(), "Deliveries", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number format: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error fetching deliveries: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        mainPanel.add(confirmButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            frame.dispose();
            menuframe.setVisible(true);
        });
        mainPanel.add(cancelButton);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space before the scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void removeItemsFromDelivery(JFrame menuframe) {
        JFrame frame = new JFrame("Remove Items from Delivery");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel deliveryLabel = new JLabel("Select Delivery to Remove Items From:");
        mainPanel.add(deliveryLabel);
    
        JComboBox<String> deliveryComboBox = new JComboBox<>();
        mainPanel.add(deliveryComboBox);
        mainPanel.add(Box.createVerticalStrut(20));
    
        JLabel itemLabel = new JLabel("Select Items to Remove (Ctrl+Click to select multiple):");
        mainPanel.add(itemLabel);
    
        DefaultListModel<String> itemListModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(itemListModel);
        itemList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane itemScroll = new JScrollPane(itemList);
        itemScroll.setPreferredSize(new Dimension(450, 150));
        mainPanel.add(itemScroll);
        mainPanel.add(Box.createVerticalStrut(20));
    
        JButton removeButton = new JButton("Remove Selected Items");
        JButton cancelButton = new JButton("Cancel");
    
        LinkedList<DeliveryDTO> deliveries;
    
        try {
            deliveries = shipmentService.getAllDeliveries();
            if (deliveries.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No deliveries found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                menuframe.setVisible(true);
                return;
            }
    
            for (DeliveryDTO delivery : deliveries) {
                int weight = delivery.getListOfItems().stream().mapToInt(SimpleEntry::getValue).sum();
                deliveryComboBox.addItem("Delivery ID: " + delivery.getId() + " - "
                        + delivery.getOriginLoc().getAddress() + ", " + delivery.getOriginLoc().getAreaName() + " to "
                        + delivery.getDestinationLoc().getAddress() + ", " + delivery.getDestinationLoc().getAreaName()
                        + " with weight: " + weight + " kg");
            }
    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading deliveries: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            menuframe.setVisible(true);
            return;
        }
    
        deliveryComboBox.addActionListener(e -> {
            int selectedIndex = deliveryComboBox.getSelectedIndex();
            itemListModel.clear();
    
            if (selectedIndex != -1) {
                DeliveryDTO selectedDelivery = deliveries.get(selectedIndex);
                for (SimpleEntry<String, Integer> item : selectedDelivery.getListOfItems()) {
                    itemListModel.addElement(item.getKey() + " (" + item.getValue() + " kg)");
                }
            }
        });
    
        removeButton.addActionListener(e -> {
            int deliveryIndex = deliveryComboBox.getSelectedIndex();
            List<String> selectedItems = itemList.getSelectedValuesList();
    
            if (deliveryIndex == -1 || selectedItems.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select a delivery and at least one item to remove.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            DeliveryDTO selectedDelivery = deliveries.get(deliveryIndex);
            LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();
    
            try {
                for (String itemStr : selectedItems) {
                    String name = itemStr.substring(0, itemStr.indexOf(" ("));
                    int weight = Integer.parseInt(itemStr.substring(itemStr.indexOf("(") + 1, itemStr.indexOf(" kg")));
                    itemsToRemove.add(new SimpleEntry<>(name, weight));
                }
    
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to remove the selected items from delivery ID " + selectedDelivery.getId() + "?",
                        "Confirm Removal", JOptionPane.YES_NO_OPTION);
    
                if (confirm == JOptionPane.YES_OPTION) {
                    shipmentService.removeItemsFromDelivery(selectedDelivery.getId(), itemsToRemove);
                    JOptionPane.showMessageDialog(frame, "Items removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    menuframe.setVisible(true);
                }
    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to remove items: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(e -> {
            frame.dispose();
            menuframe.setVisible(true);
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(removeButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
    
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
    
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    
    

    private void updateDocument(JFrame menuframe) {
        JFrame frame = new JFrame("Update Document");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel docLabel = new JLabel("Select Document to Update:");
        mainPanel.add(docLabel);
    
        JComboBox<String> docComboBox = new JComboBox<>();
        mainPanel.add(docComboBox);
        mainPanel.add(Box.createVerticalStrut(10));
    
        // Remove items list
        JLabel removeLabel = new JLabel("Select Items to Remove: (hold Ctrl to select multiple)");
        DefaultListModel<String> removeListModel = new DefaultListModel<>();
        JList<String> removeList = new JList<>(removeListModel);
        removeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane removeScroll = new JScrollPane(removeList);
        removeScroll.setPreferredSize(new Dimension(550, 120));
        mainPanel.add(removeLabel);
        mainPanel.add(removeScroll);
        mainPanel.add(Box.createVerticalStrut(10));
    
        // Add new items input
        JLabel addLabel = new JLabel("Add New Item:");
        JTextField itemNameField = new JTextField(15);
        JTextField itemWeightField = new JTextField(5);
        JButton addItemButton = new JButton("Add Item");
    
        JPanel addPanel = new JPanel();
        addPanel.add(new JLabel("Name:"));
        addPanel.add(itemNameField);
        addPanel.add(new JLabel("Weight:"));
        addPanel.add(itemWeightField);
        addPanel.add(addItemButton);
        mainPanel.add(addLabel);
        mainPanel.add(addPanel);
    
        // List of new items to be added
        DefaultListModel<String> addedListModel = new DefaultListModel<>();
        JList<String> addedList = new JList<>(addedListModel);
        JScrollPane addedScroll = new JScrollPane(addedList);
        addedScroll.setPreferredSize(new Dimension(550, 100));
        addedScroll.setBorder(BorderFactory.createTitledBorder("Items to Add"));
        mainPanel.add(addedScroll);
        mainPanel.add(Box.createVerticalStrut(10));
    
        JButton confirmButton = new JButton("Confirm Update");
        JButton cancelButton = new JButton("Cancel");
    
        LinkedList<DocumentDTO> documents;
    
        try {
            documents = shipmentService.getAllDocuments();
            if (documents.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No documents found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                menuframe.setVisible(true);
                return;
            }
    
            for (DocumentDTO doc : documents) {
                docComboBox.addItem("Document ID: " + doc.getId());
            }
    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading documents: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            menuframe.setVisible(true);
            return;
        }
    
        // Load items for selected document
        docComboBox.addActionListener(e -> {
            int selectedIndex = docComboBox.getSelectedIndex();
            removeListModel.clear();
    
            if (selectedIndex != -1) {
                DocumentDTO selectedDoc = documents.get(selectedIndex);
                for (SimpleEntry<String, Integer> item : selectedDoc.getListOfItems()) {
                    removeListModel.addElement(item.getKey() + " (" + item.getValue() + " kg)");
                }
            }
        });
    
        // Add new item to the "to add" list
        addItemButton.addActionListener(e -> {
            String name = itemNameField.getText().trim();
            String weightStr = itemWeightField.getText().trim();
    
            if (name.isEmpty() || weightStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Item name and weight are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                int weight = Integer.parseInt(weightStr);
                if (weight <= 0) throw new NumberFormatException();
                addedListModel.addElement(name + " (" + weight + " kg)");
                itemNameField.setText("");
                itemWeightField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid weight. Enter a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        // Confirm and call the service
        confirmButton.addActionListener(e -> {
            int docIndex = docComboBox.getSelectedIndex();
            if (docIndex == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a document.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            DocumentDTO selectedDoc = documents.get(docIndex);
            int documentID = selectedDoc.getId();
    
            LinkedList<SimpleEntry<String, Integer>> itemsToRemove = new LinkedList<>();
            for (String itemStr : removeList.getSelectedValuesList()) {
                String name = itemStr.substring(0, itemStr.indexOf(" ("));
                int weight = Integer.parseInt(itemStr.substring(itemStr.indexOf("(") + 1, itemStr.indexOf(" kg")));
                itemsToRemove.add(new SimpleEntry<>(name, weight));
            }
    
            LinkedList<SimpleEntry<String, Integer>> itemsToAdd = new LinkedList<>();
            for (int i = 0; i < addedListModel.size(); i++) {
                String itemStr = addedListModel.get(i);
                String name = itemStr.substring(0, itemStr.indexOf(" ("));
                int weight = Integer.parseInt(itemStr.substring(itemStr.indexOf("(") + 1, itemStr.indexOf(" kg")));
                itemsToAdd.add(new SimpleEntry<>(name, weight));
            }
    
            try {
                shipmentService.updateDocument(documentID, itemsToAdd, itemsToRemove);
                JOptionPane.showMessageDialog(frame, "Document updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                menuframe.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to update document: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(e -> {
            frame.dispose();
            menuframe.setVisible(true);
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
    
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
    
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    

    public void EmployeeMenu() {
        JFrame menuframe = new JFrame("Employee Menu");
        menuframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        menuframe.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Employee Menu");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20)); // Add space between title and buttons

        JButton getTruckDeliveryButton = new JButton("Get Truck Deliveries");
        getTruckDeliveryButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        getTruckDeliveryButton.addActionListener(e -> {
            menuframe.setVisible(false);
            getTruckDeliveries(menuframe);
        });
        panel.add(getTruckDeliveryButton);
        panel.add(Box.createVerticalStrut(10));

        JButton updateDocumentButton = new JButton("Update Document");
        updateDocumentButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        updateDocumentButton.addActionListener(e -> {
            menuframe.setVisible(false);
            updateDocument(menuframe);
        });
        panel.add(updateDocumentButton);
        panel.add(Box.createVerticalStrut(10));

        JButton listDeliveriesButton = new JButton("List All Deliveries by Time");
        listDeliveriesButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        listDeliveriesButton.addActionListener(e -> {
            menuframe.setVisible(false);
            listDeliveriesByTime(menuframe);
        });
        panel.add(listDeliveriesButton);
        panel.add(Box.createVerticalStrut(10));

        JButton supplierPickupButton = new JButton("Supplier Pickup Items");
        supplierPickupButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        supplierPickupButton.addActionListener(e -> {
            menuframe.setVisible(false);
            supplierPickUpItems(menuframe);
        });
        panel.add(supplierPickupButton);
        panel.add(Box.createVerticalStrut(10));

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(menuframe, "Returning to login screen.");
            menuframe.dispose();
        });
        panel.add(backButton);
        panel.add(Box.createVerticalStrut(20)); // Add space at the bottom

        JScrollPane scrollPane = new JScrollPane(panel);
        menuframe.add(scrollPane);
        menuframe.setVisible(true);
    }

    public static void main(String[] args) {
        // For testing purposes
        TransportationWindowGUI gui = TransportationWindowGUI.getInstance();
        DataRepositoryImpl dataRepository = new DataRepositoryImpl();
        dataRepository.loadData();
        gui.TransportManagerMenu();
    }
}