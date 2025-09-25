package presentationLayer;

import serviceLayer.StorageController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ManagerMenuGui {

    private StorageController storageController;
    private JFrame frame;
    private JPanel panel;

    public ManagerMenuGui() {
        this.storageController = new StorageController();
        frame = new JFrame("Manager Menu");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null); // Center the window

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));
        panel.setBackground(new Color(224, 224, 224)); // Soft background

        JLabel title = new JLabel("Manager Menu");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        title.setBorder(new EmptyBorder(0, 0, 25, 0));
        panel.add(title);

        addStyledButton("🔍 Get Product by Barcode", this::getProductByBarcode);
        addStyledButton("📊 Generate Report by Category", this::generateReportByCategory);
        addStyledButton("⚠️ Generate Insufficient Stock Report", this::generateInsufficientStockReport);
        addStyledButton("🗓️ Expiry Reports From Last X Days", this::expiryReportsFromLastXDays);
        addStyledButton("📋 View All Expiry Reports", this::viewAllExpiryReports);
        addStyledButton("🔎 View Expiry Reports by Product", this::viewExpiryReportsByProduct);
        addStyledButton("♻️ Reset All Expiry Reports", this::resetAllExpiryReports);
        addStyledButton("🔥 Get Most Demanded Items", this::getMostDemandedItems);
        addStyledButton("📈 Get Demand for Item", this::getDemandForItem);
        addStyledButton("💸 Apply Discount", this::applyDiscount);
        addStyledButton("📉 Calculate Minimum Thresholds", this::calculateMinimumThresholds);
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
        button.setBackground(new Color(158, 187, 238));
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(new Color(90, 105, 132), 2, true));
        button.addActionListener(e -> action.run());
        panel.add(button);
        panel.add(Box.createVerticalStrut(15));
    }

    // Stub methods for now – to be implemented next
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

    private void generateReportByCategory() {
        List<String> categoryNames = new ArrayList<>();
    
        while (true) {
            String input = JOptionPane.showInputDialog(frame, "Enter category name (letters only), or type 'done' to finish:");
            if (input == null || input.equalsIgnoreCase("done")) break;
    
            input = input.trim();
            if (!input.matches("[a-zA-Z]+")) {
                JOptionPane.showMessageDialog(frame, "Invalid category name. Use letters only.", "Input Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            categoryNames.add(input);
        }
    
        if (categoryNames.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No categories were entered.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        try {
            String result = storageController.showReportByCategory(categoryNames);
    
            JTextArea textArea = new JTextArea(result);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
    
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
    
            JOptionPane.showMessageDialog(frame, scrollPane, "📊 Report by Category", JOptionPane.INFORMATION_MESSAGE);
    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateInsufficientStockReport() {
    try {
        String report = storageController.showInsufficiencyReport();

        if (report.startsWith("No matching items were found")) {
            JOptionPane.showMessageDialog(frame, "No insufficient stock items found.", "📉 Stock Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea textArea = new JTextArea(report);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(frame, scrollPane, "📉 Insufficient Stock Report", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error retrieving report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void expiryReportsFromLastXDays() {
    String input = JOptionPane.showInputDialog(frame, "Show expiry reports from how many last days:");

    if (input == null) return; // Cancelled

    input = input.trim();
    while (!input.matches("\\d+")) {
        input = JOptionPane.showInputDialog(frame, "Invalid input. Enter a positive number:");
        if (input == null) return; // Cancelled again
        input = input.trim();
    }

    int days = Integer.parseInt(input);

    try {
        String result = storageController.showExpiryReport(days);

        if (result.startsWith("No expiry reports in the last")) {
            JOptionPane.showMessageDialog(frame, "No expiry reports found for the last " + days + " days.", "🧊 Expiry Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea textArea = new JTextArea(result);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(frame, scrollPane, "🧊 Expiry Reports - Last " + days + " Days", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error retrieving expiry reports: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

   private void viewAllExpiryReports() {
    try {
        String result = storageController.showAllExpiryReports();

        if (result.startsWith("There are no expiry reports")) {
            JOptionPane.showMessageDialog(frame, "There are no expiry reports available.", "🧊 All Expiry Reports", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea textArea = new JTextArea(result);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(frame, scrollPane, "🧊 All Expiry Reports", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error retrieving expiry reports: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

   private void viewExpiryReportsByProduct() {
    String productID = JOptionPane.showInputDialog(frame, "Enter Product Barcode:");

    if (productID == null) return; // user canceled
    productID = productID.trim();

    while (!productID.matches("[a-zA-Z0-9]+") || productID.isEmpty()) {
        productID = JOptionPane.showInputDialog(frame, "Invalid barcode. Use alphanumeric characters only:");
        if (productID == null) return; // canceled again
        productID = productID.trim();
    }

    try {
        String result = storageController.showExpiryReportsByProduct(productID);

        if (result.startsWith("No expiry reports found for product with barcode")) {
            JOptionPane.showMessageDialog(frame, "No expiry reports found for product " + productID, "🧊 Expiry Reports", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea textArea = new JTextArea(result);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(frame, scrollPane, "🧊 Expiry Reports for Product " + productID, JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void resetAllExpiryReports() {
    int confirm = JOptionPane.showConfirmDialog(
        frame,
        "Are you sure you want to reset all expiry reports?\nThis action cannot be undone.",
        "⚠️ Confirm Reset",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        String result = storageController.resetAllExpiryReports();
        JOptionPane.showMessageDialog(frame, result, "✅ Reset Complete", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void getMostDemandedItems() {
    String input = JOptionPane.showInputDialog(
        frame,
        "Show most demanded items from how many last months?",
        "📈 Demand Report",
        JOptionPane.QUESTION_MESSAGE
    );

    if (input == null) return; // User cancelled

    input = input.trim();
    while (!input.matches("\\d+") && (Integer.parseInt(input) > 0 && Integer.parseInt(input) < 13)) {
        input = JOptionPane.showInputDialog(
            frame,
            "Please enter a valid positive number of months:",
            "📈 Demand Report",
            JOptionPane.WARNING_MESSAGE
        );
        if (input == null) return;
        input = input.trim();
    }

    int days = Integer.parseInt(input);
    try {
        String result = storageController.showMostDemandedItems(days);
        showLargeTextDialog("📦 Most Demanded Items", result);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void showLargeTextDialog(String title, String content) {
    JTextArea textArea = new JTextArea(content);
    textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(600, 400));

    JOptionPane.showMessageDialog(frame, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
}

    private void getDemandForItem() {
    String barcode = JOptionPane.showInputDialog(frame, "Enter product barcode (letters and numbers):");
    if (barcode == null || !barcode.matches("[a-zA-Z0-9]+")) {
        showError("Invalid barcode.");
        return;
    }

    String rawDemand = storageController.showDemandForItem(barcode);
    if (rawDemand == null || rawDemand.isBlank()) {
        showError("No demand data found.");
        return;
    }

    // Parse the string "[0, 0, 5, ...]" into a list of integers
    rawDemand = rawDemand.replaceAll("[\\[\\]\\s]", ""); // Remove brackets and spaces
    String[] tokens = rawDemand.split(",");
    String[] months = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    StringBuilder report = new StringBuilder("📊 Monthly Demand for Product: " + barcode + "\n");
    report.append("-------------------------------------\n");

    for (int i = 0; i < tokens.length && i < months.length; i++) {
        report.append(String.format("%-10s : %s\n", months[i], tokens[i]));
    }

    JTextArea textArea = new JTextArea(report.toString());
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JOptionPane.showMessageDialog(frame, scrollPane, "Monthly Demand", JOptionPane.INFORMATION_MESSAGE);
}

    private void applyDiscount() {
        JFrame frame = new JFrame("Apply Discount");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(425, 475);
        frame.setLocationRelativeTo(null);
    
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
    
        Font labelFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
    
        BiConsumer<String, JComponent> addField = (labelText, field) -> {
            JLabel label = new JLabel(labelText);
            label.setFont(labelFont);
            gbc.gridwidth = 2;
            panel.add(label, gbc);
            gbc.gridy++;
            field.setFont(fieldFont);
            panel.add(field, gbc);
            gbc.gridy++;
        };
    
        // Start Date Picker
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        addField.accept("Start Date:", startDateSpinner);
    
        // End Date Picker
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        addField.accept("End Date:", endDateSpinner);
    
        // Discount Type
        JCheckBox isPercentageCheck = new JCheckBox("Use Percentage Discount");
        panel.add(isPercentageCheck, gbc);
        gbc.gridy++;
    
        JTextField discountField = new JTextField();
        addField.accept("Enter Discount (0-100% or Fixed Price):", discountField);
    
        // Target Type
        JCheckBox barcodeModeCheck = new JCheckBox("Target Specific Barcodes");
        panel.add(barcodeModeCheck, gbc);
        gbc.gridy++;
    
        JTextField barcodeOrCategoryField = new JTextField();
        addField.accept("Enter Barcodes (comma-separated) or Category:", barcodeOrCategoryField);
    
        // Submit
        JButton submitButton = new JButton("Apply Discount");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.setPreferredSize(new Dimension(300, 40));
        submitButton.setBackground(new Color(158, 187, 238));
        submitButton.setFocusPainted(false);
    
        panel.add(submitButton, gbc);
    
        submitButton.addActionListener(e -> {
            try {
                Date startDate = (Date) startDateSpinner.getValue();
                Date endDate = (Date) endDateSpinner.getValue();
    
                String discountInput = discountField.getText().trim();
                int percentage = 0;
                Double fixedPrice = null;
    
                if (isPercentageCheck.isSelected()) {
                    percentage = Integer.parseInt(discountInput);
                    if (percentage < 0 || percentage > 100) {
                        showError("Percentage must be between 0 and 100.");
                        return;
                    }
                    fixedPrice = 0.0;
                } else {
                    fixedPrice = Double.parseDouble(discountInput);
                    if (fixedPrice < 0) {
                        showError("Fixed price must be non-negative.");
                        return;
                    }
                }
    
                String input = barcodeOrCategoryField.getText().trim();
                List<String> barcodes = new ArrayList<>();
                String category = null;
    
                if (barcodeModeCheck.isSelected()) {
                    String[] split = input.split(",");
                    for (String s : split) {
                        String code = s.trim();
                        if (!code.matches("[a-zA-Z0-9]+")) {
                            showError("Invalid barcode: " + code);
                            return;
                        }
                        barcodes.add(code);
                    }
                    if (barcodes.isEmpty()) {
                        showError("At least one barcode must be entered.");
                        return;
                    }
                    category = "";
                } else {
                    if (!input.matches("[a-zA-Z]+")) {
                        showError("Invalid category.");
                        return;
                    }
                    category = input;
                }
    
                // Call the controller
                String result = storageController.applyDiscount(
                    startDate, endDate, percentage, fixedPrice, barcodes, category
                );
                JOptionPane.showMessageDialog(frame, result, "Result", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } catch (Exception ex) {
                showError("Invalid input: " + ex.getMessage());
            }
        });
    
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }
    
    private void calculateMinimumThresholds() {
    String result = storageController.calculateMinimums();
    
    if (result == null || result.isBlank()) {
        showError("No data found.");
        return;
    }

    JTextArea textArea = new JTextArea(result);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(450, 300));

    JOptionPane.showMessageDialog(frame, scrollPane, "Calculated Minimum Thresholds", JOptionPane.INFORMATION_MESSAGE);
}

    private void showError(String message) {
    JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
}

}
