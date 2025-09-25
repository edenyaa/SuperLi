package presentationLayer;

import serviceLayer.StorageController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class InventoryMenuGui {
    private StorageController storageController;
    private JFrame frame;
    private JPanel panel;

    public InventoryMenuGui() {
    this.storageController = new StorageController();
    frame = new JFrame("Inventory Management Menu");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(800, 800);
    frame.setLocationRelativeTo(null); // Center the window

    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(30, 50, 30, 50));
    panel.setBackground(new Color(224, 224, 224)); // Soft background

    // Title
    JLabel title = new JLabel("Inventory Management Menu");
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
    title.setBorder(new EmptyBorder(0, 0, 25, 0));
    panel.add(title);

    // Buttons (mapped from CLI options)
    addStyledButton("🔍 Get Product by Barcode", this::getProductByBarcode);
    addStyledButton("📦 Update Product Stock", this::updateProductStock);
    addStyledButton("🧾 Register Purchase", this::registerPurchase);
    addStyledButton("➕ Add New Product", this::addNewProduct);
    addStyledButton("📄 Show Reservations from Supplier", this::showReservations);
    addStyledButton("➕ Add Reservation to Supplier", this::addReservation);
    addStyledButton("🔁 Add Weekly Reservation", this::addWeeklyReservation);
    addStyledButton("✏️ Update Reservation Items", this::updateReservationItems);
    addStyledButton("🧊 Add Expiry Report", this::addExpiryReport);
    addStyledButton("Exit", frame::dispose);

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(null);
    frame.getContentPane().add(scrollPane);
    frame.setVisible(true);
} 

// Helper method to create styled buttons
private void addStyledButton(String text, Runnable action) {
    JButton button = new JButton(text);
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.setMaximumSize(new Dimension(400, 50));
    button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
    button.setFocusPainted(false);
    button.setBackground(new Color(158, 187, 238)); // cornflower blue
    button.setForeground(Color.WHITE);
    button.setBorder(new LineBorder(new Color(90, 105, 132), 2, true)); // rounded border
    button.addActionListener(e -> action.run());
    panel.add(button);
    panel.add(Box.createVerticalStrut(15));
}

// Menu Options
private void getProductByBarcode() {
    JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
    JLabel label = new JLabel("Enter Product Barcode:");
    JTextField textField = new JTextField(15);
    inputPanel.add(label, BorderLayout.NORTH);
    inputPanel.add(textField, BorderLayout.CENTER);

    int result = JOptionPane.showConfirmDialog(
        frame,
        inputPanel,
        "📦 Get Product by Barcode",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    String barcode = textField.getText().trim();

    while (!barcode.matches("[a-zA-Z0-9]+") || barcode.isEmpty()) {
        textField.setText("");
        int retry = JOptionPane.showConfirmDialog(
            frame,
            inputPanel,
            "Invalid barcode. Use letters and numbers only!",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (retry != JOptionPane.OK_OPTION) return;
        barcode = textField.getText().trim();
    }

    try {
        String message = storageController.showProductByBarcode(barcode);

        JTextArea textArea = new JTextArea(message, 15, 50);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JOptionPane.showMessageDialog(
            frame,
            scrollPane,
            "Product Info",
            JOptionPane.INFORMATION_MESSAGE
        );
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void updateProductStock() {
    JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    JTextField barcodeField = new JTextField();
    JTextField quantityField = new JTextField();

    inputPanel.add(new JLabel("Enter Product Barcode:"));
    inputPanel.add(barcodeField);
    inputPanel.add(new JLabel("Enter New Stock Quantity:"));
    inputPanel.add(quantityField);

    int result = JOptionPane.showConfirmDialog(
        frame,
        inputPanel,
        "📦 Update Product Stock",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    String barcode = barcodeField.getText().trim();
    String quantityStr = quantityField.getText().trim();

    // Validate barcode
    while (!barcode.matches("[a-zA-Z0-9]+")) {
        barcode = JOptionPane.showInputDialog(frame, "Invalid barcode. Use letters and digits only:");
        if (barcode == null) return;
        barcode = barcode.trim();
    }

    // Validate quantity
    while (!quantityStr.matches("\\d+")) {
        quantityStr = JOptionPane.showInputDialog(frame, "Invalid stock. Enter a positive number:");
        if (quantityStr == null) return;
        quantityStr = quantityStr.trim();
    }

    int quantity = Integer.parseInt(quantityStr);

    try {
        String message = storageController.updateProductStock(barcode, quantity);
        JOptionPane.showMessageDialog(frame, message, "Result", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error updating stock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void registerPurchase() {
    Map<String, Integer> boughtItems = new HashMap<>();

    while (true) {
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField barcodeField = new JTextField();
        JTextField quantityField = new JTextField();

        inputPanel.add(new JLabel("Enter Product Barcode:"));
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("Enter Quantity:"));
        inputPanel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(
            frame,
            inputPanel,
            "🛒 Register Purchase (Press Cancel to finish)",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) break;

        String barcode = barcodeField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        if (!barcode.matches("[a-zA-Z0-9]+")) {
            JOptionPane.showMessageDialog(frame, "Invalid barcode. Use alphanumeric characters only.", "Input Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }
        if (!quantityStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(frame, "Invalid quantity. Enter a non-negative integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }

        int quantity = Integer.parseInt(quantityStr);
        boughtItems.put(barcode, boughtItems.getOrDefault(barcode, 0) + quantity);
    }

    if (boughtItems.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "No items were registered.", "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    try {
        String result = storageController.registerPurchase(boughtItems);
        JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error registering purchase: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void addNewProduct() {
    JFrame frame = new JFrame("Add New Product");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(450, 700);
    frame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(20, 30, 20, 30));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 5, 2, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;

    Font labelFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
    Font fieldFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
    Dimension fieldSize = new Dimension(300, 30);

    BiConsumer<String, JComponent> addField = (labelText, field) -> {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);
        gbc.gridy++;

        field.setFont(fieldFont);
        field.setPreferredSize(fieldSize);
        panel.add(field, gbc);
        gbc.gridy++;
    };

    JTextField barcodeField = new JTextField();
    addField.accept("Barcode:", barcodeField);

    JTextField nameField = new JTextField();
    addField.accept("Name:", nameField);

    JTextField categoryField = new JTextField();
    addField.accept("Categories (comma-separated):", categoryField);

    JTextField manufacturerField = new JTextField();
    addField.accept("Manufacturer:", manufacturerField);

    JTextField sellPriceField = new JTextField();
    addField.accept("Sell Price:", sellPriceField);

    JTextField expiryPeriodField = new JTextField();
    addField.accept("Expiry Period (e.g., '6 months'):", expiryPeriodField);

    JTextField locationField = new JTextField();
    addField.accept("Location (e.g., Aisle 3):", locationField);

    JTextField shelfQtyField = new JTextField();
    addField.accept("Quantity on Shelf:", shelfQtyField);

    JTextField storageQtyField = new JTextField();
    addField.accept("Quantity in Storage:", storageQtyField);

    JTextField minThresholdField = new JTextField();
    addField.accept("Minimum Threshold:", minThresholdField);

    JTextField supplierField = new JTextField();
    addField.accept("Supplier Name:", supplierField);

    JTextField deliveryTimeField = new JTextField();
    addField.accept("Delivery Time (days):", deliveryTimeField);

    JButton submitButton = new JButton("Submit");
    submitButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
    submitButton.setPreferredSize(new Dimension(300, 40));
    submitButton.setBackground(new Color(158, 187, 238));
    submitButton.setFocusPainted(false);
    submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    gbc.gridwidth = 2;
    panel.add(submitButton, gbc);

    submitButton.addActionListener(e -> {
        try {
            String barcode = barcodeField.getText().trim();
            String name = nameField.getText().trim();
            String categoriesRaw = categoryField.getText().trim();
            String manufacturer = manufacturerField.getText().trim();
            double sellPrice = Double.parseDouble(sellPriceField.getText().trim());
            String expiry = expiryPeriodField.getText().trim();
            String location = locationField.getText().trim();
            int shelfQty = Integer.parseInt(shelfQtyField.getText().trim());
            int storageQty = Integer.parseInt(storageQtyField.getText().trim());
            int minThreshold = Integer.parseInt(minThresholdField.getText().trim());
            String supplier = supplierField.getText().trim();
            int delTime = Integer.parseInt(deliveryTimeField.getText().trim());

            if (!barcode.matches("[a-zA-Z0-9]+") || !name.matches("[a-zA-Z ]+") ||
                !manufacturer.matches("[a-zA-Z ]+") || !supplier.matches("[a-zA-Z ]+") ||
                !location.matches("[a-zA-Z]+(\\s\\d+)?")) {
                showError("Invalid input format detected.");
                return;
            }

            List<String> categories = Arrays.stream(categoriesRaw.split(","))
                                            .map(String::trim)
                                            .filter(s -> !s.isEmpty())
                                            .collect(Collectors.toList());

            if (categories.isEmpty()) {
                showError("At least one category is required.");
                return;
            }

            String result = storageController.addNewProduct(barcode, name, categories, manufacturer, sellPrice,
                expiry, location, shelfQty, storageQty, minThreshold, supplier, delTime);

            JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        } catch (NumberFormatException ex) {
            showError("Invalid number input. Please check your values.");
        }
    });

    JScrollPane scroll = new JScrollPane(panel);
    frame.setContentPane(scroll);
    frame.setVisible(true);
}

private void showReservations() {
    JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
    JLabel label = new JLabel("Enter Supplier ID:");
    JTextField textField = new JTextField(15);
    inputPanel.add(label, BorderLayout.NORTH);
    inputPanel.add(textField, BorderLayout.CENTER);

    int result = JOptionPane.showConfirmDialog(
        frame,
        inputPanel,
        "📦 Show Supplier Reservations",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    String supplierID = textField.getText().trim();

    while (!supplierID.matches("[a-zA-Z0-9]+") || supplierID.isEmpty()) {
        textField.setText("");
        int retry = JOptionPane.showConfirmDialog(
            frame,
            inputPanel,
            "Please enter a valid Supplier ID (letters & digits only)",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (retry != JOptionPane.OK_OPTION) return;
        supplierID = textField.getText().trim();
    }

    try {
        String reservations = storageController.showAllSupplierReservations(supplierID);

        JTextArea textArea = new JTextArea(reservations);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(
            frame,
            scrollPane,
            "📦 Reservations for Supplier " + supplierID,
            JOptionPane.INFORMATION_MESSAGE
        );
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            frame,
            "Error retrieving reservations: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

private void addReservation() {
    JFrame frame = new JFrame("📦 Add Reservation to Supplier");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(400, 500);
    frame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(20, 30, 20, 30));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(4, 5, 4, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;

    Font labelFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
    Font fieldFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
    Dimension fieldSize = new Dimension(300, 30);

    BiConsumer<String, JComponent> addField = (labelText, field) -> {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);
        gbc.gridy++;

        field.setFont(fieldFont);
        field.setPreferredSize(fieldSize);
        panel.add(field, gbc);
        gbc.gridy++;
    };

    JTextField supplierIdField = new JTextField();
    addField.accept("Supplier ID:", supplierIdField);

    JTextField agreementIdField = new JTextField();
    addField.accept("Agreement ID:", agreementIdField);

    DefaultListModel<String> productListModel = new DefaultListModel<>();
    JList<String> productList = new JList<>(productListModel);
    productList.setFont(fieldFont);
    JScrollPane scrollPane = new JScrollPane(productList);
    scrollPane.setPreferredSize(new Dimension(300, 150));

    JButton addProductButton = new JButton("➕ Add Product");
    addProductButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
    addProductButton.setBackground(new Color(190, 210, 250));

    gbc.gridwidth = 2;
    panel.add(addProductButton, gbc);
    gbc.gridy++;
    panel.add(scrollPane, gbc);
    gbc.gridy++;

    JButton submitButton = new JButton("Submit");
    submitButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
    submitButton.setPreferredSize(new Dimension(300, 40));
    submitButton.setBackground(new Color(158, 187, 238));
    submitButton.setFocusPainted(false);
    panel.add(submitButton, gbc);

    List<String> productBarcodes = new ArrayList<>();
    List<Integer> productQuantities = new ArrayList<>();

    addProductButton.addActionListener(e -> {
        JTextField barcodeField = new JTextField();
        JTextField quantityField = new JTextField();

        Object[] inputs = {
            "Product Barcode:", barcodeField,
            "Quantity:", quantityField
        };

        int result = JOptionPane.showConfirmDialog(frame, inputs, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String barcode = barcodeField.getText().trim();
            String qtyStr = quantityField.getText().trim();

            if (!barcode.matches("[a-zA-Z0-9]+")) {
                showError("Invalid barcode. Use only letters and numbers.");
                return;
            }
            if (!qtyStr.matches("\\d+")) {
                showError("Invalid quantity. Use positive numbers only.");
                return;
            }

            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                showError("Quantity must be greater than zero.");
                return;
            }

            productBarcodes.add(barcode);
            productQuantities.add(quantity);
            productListModel.addElement(barcode + " - Qty: " + quantity);
        }
    });

    submitButton.addActionListener(e -> {
        String supplierId = supplierIdField.getText().trim();
        String agreementId = agreementIdField.getText().trim();

        if (!supplierId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Supplier ID."); return;
        }
        if (!agreementId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Agreement ID."); return;
        }
        if (productBarcodes.isEmpty()) {
            showError("Please add at least one product."); return;
        }

        String result = storageController.makeNewReservation(supplierId, productBarcodes, productQuantities, agreementId);
        JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    });

    JScrollPane scroll = new JScrollPane(panel);
    frame.setContentPane(scroll);
    frame.setVisible(true);
}

private void addWeeklyReservation() {
    JFrame frame = new JFrame("📆 Add Weekly Reservation");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(450, 550);
    frame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(20, 30, 20, 30));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(4, 5, 4, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;

    Font labelFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
    Font fieldFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
    Dimension fieldSize = new Dimension(300, 30);

    BiConsumer<String, JComponent> addField = (labelText, field) -> {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);
        gbc.gridy++;

        field.setFont(fieldFont);
        field.setPreferredSize(fieldSize);
        panel.add(field, gbc);
        gbc.gridy++;
    };

    JTextField supplierIdField = new JTextField();
    addField.accept("Supplier ID:", supplierIdField);

    JTextField agreementIdField = new JTextField();
    addField.accept("Agreement ID:", agreementIdField);

    JComboBox<String> dayOfWeekBox = new JComboBox<>(Arrays.stream(DayOfWeek.values())
            .map(d -> d.name().charAt(0) + d.name().substring(1).toLowerCase())
            .toArray(String[]::new));
    addField.accept("Supply Day of Week:", dayOfWeekBox);

    DefaultListModel<String> productListModel = new DefaultListModel<>();
    JList<String> productList = new JList<>(productListModel);
    productList.setFont(fieldFont);
    JScrollPane scrollPane = new JScrollPane(productList);
    scrollPane.setPreferredSize(new Dimension(300, 150));

    JButton addProductButton = new JButton("➕ Add Product");
    addProductButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
    addProductButton.setBackground(new Color(190, 210, 250));

    gbc.gridwidth = 2;
    panel.add(addProductButton, gbc);
    gbc.gridy++;
    panel.add(scrollPane, gbc);
    gbc.gridy++;

    JButton submitButton = new JButton("Submit");
    submitButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
    submitButton.setPreferredSize(new Dimension(300, 40));
    submitButton.setBackground(new Color(158, 187, 238));
    submitButton.setFocusPainted(false);
    panel.add(submitButton, gbc);

    List<String> productBarcodes = new ArrayList<>();
    List<Integer> productQuantities = new ArrayList<>();

    addProductButton.addActionListener(e -> {
        JTextField barcodeField = new JTextField();
        JTextField quantityField = new JTextField();

        Object[] inputs = {
            "Product Barcode:", barcodeField,
            "Quantity:", quantityField
        };

        int result = JOptionPane.showConfirmDialog(frame, inputs, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String barcode = barcodeField.getText().trim();
            String qtyStr = quantityField.getText().trim();

            if (!barcode.matches("[a-zA-Z0-9]+")) {
                showError("Invalid barcode. Use only letters and numbers.");
                return;
            }
            if (!qtyStr.matches("\\d+")) {
                showError("Invalid quantity. Use positive numbers only.");
                return;
            }

            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                showError("Quantity must be greater than zero.");
                return;
            }

            productBarcodes.add(barcode);
            productQuantities.add(quantity);
            productListModel.addElement(barcode + " - Qty: " + quantity);
        }
    });

    submitButton.addActionListener(e -> {
        String supplierId = supplierIdField.getText().trim();
        String agreementId = agreementIdField.getText().trim();
        String dayString = ((String) dayOfWeekBox.getSelectedItem()).toUpperCase();

        if (!supplierId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Supplier ID."); return;
        }
        if (!agreementId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Agreement ID."); return;
        }
        if (productBarcodes.isEmpty()) {
            showError("Please add at least one product."); return;
        }

        DayOfWeek selectedDay;
        try {
            selectedDay = DayOfWeek.valueOf(dayString);
        } catch (Exception ex) {
            showError("Invalid supply day.");
            return;
        }

        String result = storageController.makeNewReservationTemplate(supplierId, productBarcodes, productQuantities, agreementId, selectedDay);
        JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    });

    JScrollPane scroll = new JScrollPane(panel);
    frame.setContentPane(scroll);
    frame.setVisible(true);
}

private void updateReservationItems() {
    JFrame frame = new JFrame("✏️ Update Reservation Item");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(400, 500);
    frame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(20, 30, 20, 30));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 10, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;

    Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
    Dimension fieldSize = new Dimension(300, 30);

    // Helper method to add label + field
    BiConsumer<String, JComponent> addField = (labelText, field) -> {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);
        gbc.gridy++;

        field.setFont(fieldFont);
        field.setPreferredSize(fieldSize);
        panel.add(field, gbc);
        gbc.gridy++;
    };

    JTextField supplierNameField = new JTextField();
    addField.accept("Supplier Name:", supplierNameField);

    JTextField resIdField = new JTextField();
    addField.accept("Reservation ID:", resIdField);

    JTextField barcodeField = new JTextField();
    addField.accept("Product Barcode:", barcodeField);

    JTextField quantityField = new JTextField();
    addField.accept("New Quantity:", quantityField);

    // Submit Button
    JButton submitButton = new JButton("Submit");
    submitButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
    submitButton.setPreferredSize(new Dimension(300, 40));
    submitButton.setBackground(new Color(158, 187, 238));
    submitButton.setFocusPainted(false);
    submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(submitButton, gbc);

    submitButton.addActionListener(e -> {
        String supplierName = supplierNameField.getText().trim();
        String resId = resIdField.getText().trim();
        String barcode = barcodeField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        // Validation
        if (!supplierName.matches("[a-zA-Z ]+")) {
            showError("Invalid supplier name. Use letters only.");
            return;
        }
        if (!resId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid reservation ID. Use letters and digits only.");
            return;
        }
        if (!barcode.matches("[a-zA-Z0-9]+")) {
            showError("Invalid product barcode. Use alphanumeric only.");
            return;
        }
        if (!quantityStr.matches("\\d+")) {
            showError("Invalid quantity. Use a positive number.");
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        String result = storageController.editReservation(supplierName, resId, barcode, quantity);
        JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    });

    JScrollPane scrollPane = new JScrollPane(panel);
    frame.setContentPane(scrollPane);
    frame.setVisible(true);
}

private void addExpiryReport() {
    JFrame frame = new JFrame("🗓️ Add Expiry Report");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(450, 500);
    frame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(20, 30, 20, 30));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 10, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;

    Font labelFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
    Font fieldFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
    Dimension fieldSize = new Dimension(300, 30);

    // Helper method to add label + field
    BiConsumer<String, JComponent> addField = (labelText, field) -> {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);
        gbc.gridy++;

        field.setFont(fieldFont);
        field.setPreferredSize(fieldSize);
        panel.add(field, gbc);
        gbc.gridy++;
    };

    JTextField barcodeField = new JTextField();
    addField.accept("Product Barcode:", barcodeField);

    JTextField quantityField = new JTextField();
    addField.accept("Expired Quantity:", quantityField);

    JTextField locationField = new JTextField();
    addField.accept("Location:", locationField);

    JTextField reporterField = new JTextField();
    addField.accept("Reported By:", reporterField);

    // Submit Button
    JButton submitButton = new JButton("Submit");
    submitButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
    submitButton.setPreferredSize(new Dimension(300, 40));
    submitButton.setBackground(new Color(158, 187, 238));
    submitButton.setFocusPainted(false);
    submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(submitButton, gbc);

    submitButton.addActionListener(e -> {
        String barcode = barcodeField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String location = locationField.getText().trim();
        String reporter = reporterField.getText().trim();

        if (!barcode.matches("[a-zA-Z0-9]+")) {
            showError("Invalid barcode. Use letters and numbers only.");
            return;
        }
        if (!quantityStr.matches("\\d+")) {
            showError("Invalid quantity. Use a non-negative integer.");
            return;
        }
        int quantity = Integer.parseInt(quantityStr);
        if (!location.matches("[a-zA-Z]+")) {
            showError("Invalid location. Use letters only.");
            return;
        }
        if (!reporter.matches("[a-zA-Z]+")) {
            showError("Invalid reporter name. Use letters only.");
            return;
        }

        String result = storageController.reportExpiredProduct(barcode, quantity, location, reporter);
        JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    });

    JScrollPane scrollPane = new JScrollPane(panel);
    frame.setContentPane(scrollPane);
    frame.setVisible(true);
}

private void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
}

}
