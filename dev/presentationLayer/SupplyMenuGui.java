package presentationLayer;

import serviceLayer.StorageController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SupplyMenuGui {
    private StorageController storageController;
    private JFrame frame;
    private JPanel panel;

    public SupplyMenuGui() {
        this.storageController = new StorageController();
        frame = new JFrame("Supply Management Menu");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null); // Center the window

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));
        panel.setBackground(new Color(224, 224, 224)); // Soft background

        // Title
        JLabel title = new JLabel("Supply Management Menu");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(new EmptyBorder(0, 0, 25, 0));
        panel.add(title);

        // Buttons
        addStyledButton("📋 Show all suppliers", this::showAllSuppliers);
        addStyledButton("📅 Show supplier's supply days", this::showSupplierSupplyDays);
        addStyledButton("📞 Show supplier's contacts", this::showSupplierContacts);
        addStyledButton("👩‍💼 Add new supplier", this::addNewSupplier);
        addStyledButton("🗑️ Remove a supplier", this::removeSupplier);
        addStyledButton("🤝 Add agreement to supplier", this::addAgreementToSupplier);
        addStyledButton("🗑️ Remove agreement from supplier", this::removeAgreementFromSupplier);
        addStyledButton("🕴️ Add contact to supplier", this::addContactToSupplier);
        addStyledButton("🛍️ Add a product to agreement", this::addProductToAgreement);
        addStyledButton("🗑️ Remove product from agreement", this::removeProductFromAgreement);
        addStyledButton("Exit", frame::dispose);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        frame.getContentPane().add(scrollPane);
        frame.setVisible(true);
    }

    private void addStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 50));
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(new Color(158, 187, 238)); // cornflower blue
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(new Color(90, 105, 132), 2, true)); // softer rounded border
        button.addActionListener(e -> action.run());
        panel.add(button);
        panel.add(Box.createVerticalStrut(15));
    }

private void showAllSuppliers() {
    String result = storageController.showAllSuppliers();  // Your actual supplier string

    JTextArea textArea = new JTextArea(result);
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    textArea.setCaretPosition(0);  // scroll to top

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(600, 400));

    JOptionPane.showMessageDialog(frame, scrollPane, "All Suppliers", JOptionPane.INFORMATION_MESSAGE);
}

    private void showSupplierSupplyDays() {
    String supplierID = JOptionPane.showInputDialog(frame, "Enter Supplier ID:", "Supplier's supply days", JOptionPane.PLAIN_MESSAGE);

    // User pressed cancel or closed the dialog
    if (supplierID == null) return;

    supplierID = supplierID.trim();

    // Loop until valid input or cancel
    while (!supplierID.matches("[a-zA-Z0-9]+") || supplierID.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Please enter letters and digits only!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        supplierID = JOptionPane.showInputDialog(frame, "Enter Supplier ID:", "Supplier ID", JOptionPane.PLAIN_MESSAGE);

        if (supplierID == null) return; // Cancelled
        supplierID = supplierID.trim();
    }

    try {
        String result = storageController.showSupplierSupplyDays(supplierID);
        JOptionPane.showMessageDialog(frame, result, "Supply Days", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error retrieving supply days: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void showSupplierContacts() {
    String supplierID = JOptionPane.showInputDialog(frame, "Enter Supplier ID:", "Supplier's contacts", JOptionPane.PLAIN_MESSAGE);

    // User pressed cancel or closed the dialog
    if (supplierID == null) return;

    supplierID = supplierID.trim();

    // Loop until valid input or user cancels
    while (!supplierID.matches("[a-zA-Z0-9]+") || supplierID.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Please enter letters and digits only!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        supplierID = JOptionPane.showInputDialog(frame, "Enter Supplier ID:", "Supplier ID", JOptionPane.PLAIN_MESSAGE);

        if (supplierID == null) return; // User cancelled
        supplierID = supplierID.trim();
    }

    try {
        String result = storageController.showSupplierContacts(supplierID);
        JOptionPane.showMessageDialog(frame, result, "Supplier Contacts", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error retrieving contacts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void addNewSupplier() {
        JFrame frame = new JFrame("Add New Supplier");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
    
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5); // vertical & horizontal spacing
        gbc.gridx = 0;
        gbc.gridy = 0;
    
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Dimension fieldSize = new Dimension(300, 30);
    
        // Helper method to add label and field
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
    
        JTextField nameField = new JTextField();
        addField.accept("Name:", nameField);
    
        JTextField addressField = new JTextField();
        addField.accept("Address:", addressField);
    
        JComboBox<String> paymentTypeBox = new JComboBox<>(new String[]{"TRANSFER", "CASH", "CHECK"});
        addField.accept("Payment Type:", paymentTypeBox);
    
        JTextField bankAccountField = new JTextField();
        addField.accept("Bank Account:", bankAccountField);
    
        JTextField contactNameField = new JTextField();
        addField.accept("Contact Name:", contactNameField);
    
        JTextField contactPhoneField = new JTextField();
        addField.accept("Contact Phone:", contactPhoneField);
    
        JTextField contactEmailField = new JTextField();
        addField.accept("Contact Email:", contactEmailField);
    
        JTextField supplyDaysField = new JTextField();
        addField.accept("Supply Days (comma-separated):", supplyDaysField);
    
        // Submit Button
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        submitButton.setPreferredSize(new Dimension(300, 40));
        submitButton.setBackground(new Color(158, 187, 238));
        submitButton.setFocusPainted(false);
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);
    
        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String paymentType = (String) paymentTypeBox.getSelectedItem();
            String bankAccount = bankAccountField.getText().trim();
            String contactName = contactNameField.getText().trim();
            String contactPhone = contactPhoneField.getText().trim();
            String contactEmail = contactEmailField.getText().trim();
            String supplyInput = supplyDaysField.getText().trim();
    
            // Validate inputs (same as CLI logic)
            if (!name.matches("[a-zA-Z ]+")) {
                showError("Invalid name. Use letters only."); return;
            }
            if (!address.matches("[\\w\\s,.\\-]+")) {
                showError("Invalid address."); return;
            }
            if (!bankAccount.matches("[a-zA-Z0-9]+")) {
                showError("Invalid bank account."); return;
            }
            if (!contactName.matches("[a-zA-Z ]+")) {
                showError("Invalid contact name."); return;
            }
            if (!contactPhone.matches("[+\\-\\d]+")) {
                showError("Invalid phone number."); return;
            }
            if (!contactEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                showError("Invalid email format."); return;
            }
    
            List<String> supplyDays = new ArrayList<>();
            String[] days = supplyInput.split(",");
            for (String day : days) {
                String trimmed = day.trim();
                if (trimmed.matches("(?i)Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday")) {
                    supplyDays.add(trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase());
                } else {
                    showError("Invalid day: " + trimmed); return;
                }
            }
    
            // Call controller
            String result = storageController.addNewSupplier(
                name, address, paymentType, bankAccount, contactName, contactPhone, contactEmail, supplyDays
            );
    
            JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });
    
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }
    
    private void removeSupplier() {
    JTextField textField = new JTextField(15);
    JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
    inputPanel.add(new JLabel("Enter Supplier ID:"), BorderLayout.NORTH);
    inputPanel.add(textField, BorderLayout.CENTER);

    while (true) {
        int result = JOptionPane.showConfirmDialog(
            frame,
            inputPanel,
            "Remove supplier",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String supplierID = textField.getText().trim();

        if (supplierID.matches("[a-zA-Z0-9]+") && !supplierID.isEmpty()) {
            try {
                String message = storageController.removeSupplier(supplierID);
                JOptionPane.showMessageDialog(frame, message, "Result", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error removing supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        // Show validation error and loop again
        JOptionPane.showMessageDialog(frame, "Please enter letters and digits only!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        textField.setText(""); // Clear previous input
    }
}

private void addAgreementToSupplier() {
    JFrame frame = new JFrame("Add Agreement to Supplier");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(400, 650);
    frame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(20, 30, 20, 30));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 0, 3, 0);
    gbc.gridx = 0;
    gbc.gridy = 0;

    Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
    Dimension fieldSize = new Dimension(300, 30);

    BiConsumer<String, JComponent> addField = (labelText, field) -> {
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        panel.add(label, gbc);
        gbc.gridy++;

        field.setFont(fieldFont);
        field.setPreferredSize(fieldSize);
        panel.add(field, gbc);
        gbc.gridy++;
    };

    JTextField supplierIdField = new JTextField();
    addField.accept("Supplier ID:", supplierIdField);

    JTextField contactNameField = new JTextField();
    addField.accept("Contact Name:", contactNameField);

    JTextField contactPhoneField = new JTextField();
    addField.accept("Contact Phone:", contactPhoneField);

    JTextField contactEmailField = new JTextField();
    addField.accept("Contact Email:", contactEmailField);

    JCheckBox hasRegularDaysCheck = new JCheckBox("Has Regular Supply Days?");
    hasRegularDaysCheck.setFont(labelFont);
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    panel.add(hasRegularDaysCheck, gbc);
    gbc.gridy++;

    // Products Label and Add Product Button on the same row with spacing
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    JLabel productsLabel = new JLabel("Products:");
    productsLabel.setFont(labelFont);
    panel.add(productsLabel, gbc);

    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.LINE_START;
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    JButton addProductButton = new JButton("Add Product");
    addProductButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    addProductButton.setBackground(new Color(190, 210, 250));
    buttonPanel.add(addProductButton);
    panel.add(buttonPanel, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;

    DefaultListModel<String> productListModel = new DefaultListModel<>();
    JList<String> productList = new JList<>(productListModel);
    productList.setFont(fieldFont);
    JScrollPane scrollPane = new JScrollPane(productList);
    scrollPane.setPreferredSize(new Dimension(300, 200));
    panel.add(scrollPane, gbc);
    gbc.gridy++;

    JButton submitButton = new JButton("Submit");
    submitButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
    submitButton.setPreferredSize(new Dimension(300, 40));
    submitButton.setBackground(new Color(158, 187, 238));
    submitButton.setFocusPainted(false);
    panel.add(submitButton, gbc);

    addProductButton.addActionListener(e -> {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField barcodeField = new JTextField();
        Object[] fields = {
            "Product Name:", nameField,
            "Price:", priceField,
            "Barcode:", barcodeField
        };
        int result = JOptionPane.showConfirmDialog(frame, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String price = priceField.getText().trim();
            String barcode = barcodeField.getText().trim();
            if (!name.matches("[a-zA-Z ]+")) {
                showError("Invalid product name."); return;
            }
            if (!price.matches("\\d+(\\.\\d{1,2})?")) {
                showError("Invalid price format."); return;
            }
            if (!barcode.matches("[a-zA-Z0-9]+")) {
                showError("Invalid barcode."); return;
            }
            productListModel.addElement(name + " - $" + price + " (" + barcode + ")");
            productListModel.addElement(name + "," + price + "," + barcode); // actual data
        }
    });

    submitButton.addActionListener(e -> {
        String supplierId = supplierIdField.getText().trim();
        String contactName = contactNameField.getText().trim();
        String contactPhone = contactPhoneField.getText().trim();
        String contactEmail = contactEmailField.getText().trim();

        if (!supplierId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Supplier ID."); return;
        }
        if (!contactName.matches("[a-zA-Z ]+")) {
            showError("Invalid contact name."); return;
        }
        if (!contactPhone.matches("[+\\-\\d]+")) {
            showError("Invalid phone number."); return;
        }
        if (!contactEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showError("Invalid email format."); return;
        }

        List<String[]> productDetails = new ArrayList<>();
        for (int i = 0; i < productListModel.size(); i += 2) {
            String[] parts = productListModel.get(i + 1).split(",");
            productDetails.add(parts);
        }

        boolean hasRegularDays = hasRegularDaysCheck.isSelected();
        String result = storageController.AddNewAgreementForSupplier(supplierId, contactName, contactPhone, contactEmail, productDetails, hasRegularDays);
        JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    });

    JScrollPane scroll = new JScrollPane(panel);
    frame.setContentPane(scroll);
    frame.setVisible(true);
}
    
private void removeAgreementFromSupplier() {
    JTextField supplierField = new JTextField(15);
    JTextField agreementField = new JTextField(15);

    JPanel inputPanel = new JPanel(new GridLayout(4, 1, 5, 5));
    inputPanel.add(new JLabel("Enter Supplier ID:"));
    inputPanel.add(supplierField);
    inputPanel.add(new JLabel("Enter Agreement ID:"));
    inputPanel.add(agreementField);

    while (true) {
        int result = JOptionPane.showConfirmDialog(
            frame,
            inputPanel,
            "Remove Agreement from Supplier",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String supplierId = supplierField.getText().trim();
        String agreementId = agreementField.getText().trim();

        boolean validSupplier = supplierId.matches("[a-zA-Z0-9]+");
        boolean validAgreement = agreementId.matches("[a-zA-Z0-9]+");

        if (!validSupplier || supplierId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid Supplier ID. Use letters and digits only.", "Input Error", JOptionPane.ERROR_MESSAGE);
            supplierField.setText("");
            continue;
        }

        if (!validAgreement || agreementId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid Agreement ID. Use letters and digits only.", "Input Error", JOptionPane.ERROR_MESSAGE);
            agreementField.setText("");
            continue;
        }

        try {
            String message = storageController.removeAgreementForSupplier(supplierId, agreementId);
            JOptionPane.showMessageDialog(frame, message, "Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return;
    }
}

private void addContactToSupplier() {
    JTextField supplierField = new JTextField(15);
    JTextField nameField = new JTextField(15);
    JTextField phoneField = new JTextField(15);
    JTextField emailField = new JTextField(15);

    JPanel panel = new JPanel(new GridLayout(8, 1, 5, 5));
    panel.add(new JLabel("Enter Supplier ID:"));
    panel.add(supplierField);
    panel.add(new JLabel("Contact Name (letters only):"));
    panel.add(nameField);
    panel.add(new JLabel("Contact Phone (digits, '-' or '+'):"));
    panel.add(phoneField);
    panel.add(new JLabel("Contact Email:"));
    panel.add(emailField);

    while (true) {
        int result = JOptionPane.showConfirmDialog(
            frame,
            panel,
            "Add Contact to Supplier",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String supplierId = supplierField.getText().trim();
        String contactName = nameField.getText().trim();
        String contactPhone = phoneField.getText().trim();
        String contactEmail = emailField.getText().trim();

        // Validation
        if (!supplierId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Supplier ID. Use letters and digits only.");
            supplierField.setText("");
            continue;
        }

        if (!contactName.matches("[a-zA-Z ]+")) {
            showError("Invalid contact name. Use letters only.");
            nameField.setText("");
            continue;
        }

        if (!contactPhone.matches("[\\d\\-+]+")) {
            showError("Invalid phone. Use digits, '-' and '+'.");
            phoneField.setText("");
            continue;
        }

        if (!contactEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            showError("Invalid email format.");
            emailField.setText("");
            continue;
        }

        // Call the controller
        try {
            String message = storageController.addNewContactForSupplier(supplierId, contactName, contactPhone, contactEmail);
            JOptionPane.showMessageDialog(frame, message, "Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return;
    }
}

private void addProductToAgreement() {
    JTextField supplierIdField = new JTextField(15);
    JTextField agreementIdField = new JTextField(15);
    JTextField productNameField = new JTextField(15);
    JTextField productPriceField = new JTextField(15);
    JTextField productNumberField = new JTextField(15);

    JPanel panel = new JPanel(new GridLayout(10, 1, 5, 5));
    panel.add(new JLabel("Enter Supplier ID:"));
    panel.add(supplierIdField);
    panel.add(new JLabel("Enter Agreement ID:"));
    panel.add(agreementIdField);
    panel.add(new JLabel("Product Name (letters only):"));
    panel.add(productNameField);
    panel.add(new JLabel("Product Price (decimal):"));
    panel.add(productPriceField);
    panel.add(new JLabel("Product Barcode (letters and digits):"));
    panel.add(productNumberField);

    while (true) {
        int result = JOptionPane.showConfirmDialog(
            frame,
            panel,
            "Add Product to Supplier Agreement",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;

        String supplierId = supplierIdField.getText().trim();
        String agreementId = agreementIdField.getText().trim();
        String productName = productNameField.getText().trim();
        String productPriceInput = productPriceField.getText().trim();
        String productNumber = productNumberField.getText().trim();

        // Validations
        if (!supplierId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Supplier ID. Use digits and letters only.");
            supplierIdField.setText("");
            continue;
        }
        if (!agreementId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Agreement ID. Use digits and letters only.");
            agreementIdField.setText("");
            continue;
        }
        if (!productName.matches("[a-zA-Z ]+")) {
            showError("Invalid Product Name. Use letters only.");
            productNameField.setText("");
            continue;
        }
        if (!productPriceInput.matches("\\d+(\\.\\d{1,2})?")) {
            showError("Invalid Product Price. Use a decimal number (e.g., 12.99).");
            productPriceField.setText("");
            continue;
        }
        double productPrice = Double.parseDouble(productPriceInput);
        if (!productNumber.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Product Barcode. Use alphanumeric characters only.");
            productNumberField.setText("");
            continue;
        }
        try {
            String message = storageController.addNewProductToSupplierAgreement(
                supplierId, agreementId, productName, productPrice, productNumber
            );
            JOptionPane.showMessageDialog(frame, message, "Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return;
    }
}

private void removeProductFromAgreement() {
    JTextField supplierIdField = new JTextField(15);
    JTextField agreementIdField = new JTextField(15);
    JTextField productNumField = new JTextField(15);

    JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
    panel.add(new JLabel("Enter Supplier ID:"));
    panel.add(supplierIdField);
    panel.add(new JLabel("Enter Agreement ID:"));
    panel.add(agreementIdField);
    panel.add(new JLabel("Enter Product Barcode:"));
    panel.add(productNumField);

    while (true) {
        int result = JOptionPane.showConfirmDialog(
            frame,
            panel,
            "Remove Product from Agreement",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;
        String supplierId = supplierIdField.getText().trim();
        String agreementId = agreementIdField.getText().trim();
        String productNum = productNumField.getText().trim();
        if (!supplierId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Supplier ID. Use letters and digits only.");
            supplierIdField.setText("");
            continue;
        }
        if (!agreementId.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Agreement ID. Use letters and digits only.");
            agreementIdField.setText("");
            continue;
        }
        if (!productNum.matches("[a-zA-Z0-9]+")) {
            showError("Invalid Product Barcode. Use letters and digits only.");
            productNumField.setText("");
            continue;
        }

        try {
            String message = storageController.removeProductFromSupplierAgreement(supplierId, agreementId, productNum);
            JOptionPane.showMessageDialog(frame, message, "Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return;
    }
}

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
