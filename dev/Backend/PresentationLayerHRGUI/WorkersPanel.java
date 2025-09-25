package Backend.PresentationLayerHRGUI;

import Backend.DTO.EmployeeDTO;
import Backend.DTO.LocationDTO;
import Backend.DTO.RoleDTO;
import Backend.ServiceLayer.ServiceLayerHR.HRService.DeliveryEmployeeService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.RolesService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.WorkersManageService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WorkersPanel extends JPanel {
    private final WorkersManageService svc    = new WorkersManageService();
    private final RolesService      roles     = new RolesService();
    private final DeliveryEmployeeService del = DeliveryEmployeeService.getInstance();

    public WorkersPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F5F5));

        JLabel banner = new JLabel("Workers Management", SwingConstants.CENTER);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 28));
        banner.setForeground(new Color(0x1565C0));
        banner.setBorder(new EmptyBorder(20, 0, 20, 0));
        loadIcon(banner, "workers.png", 48, 48);
        add(banner, BorderLayout.NORTH);

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
//        cards.setBorder(new EmptyBorder(30, 30, 30, 30));
        cards.setBackground(new Color(0xF5F5F5));

        cards.add(createCard("Hire Employee",   "hire.png",   this::hireDialog));
        cards.add(createCard("Fire Employee",   "fire.png",   () -> idDialog("Enter ID to fire:", svc::fireEmployee)));
        cards.add(createCard("Edit Employee",   "edit.png",   this::editDialog));
        cards.add(createCard("Show All Employees", "coWorkers.png", this::showAllEmployeesDialog));
        cards.add(createCard("Show Employee",   "show.png",   () -> idDialog("Enter ID to show:", svc::showEmployee)));
        cards.add(createCard("Show Former Employee",     "former.png", () -> idDialog("Enter ID to show former:", svc::showFormerEmployee)));
        cards.add(new JLabel());  // filler

        add(cards, BorderLayout.CENTER);
    }

    private void showAllEmployeesDialog() {
        Response res = svc.showAllEmployees();
        StringBuilder sb = new StringBuilder("<html><h2>All Employees:</h2><ul>");;
        if (res.isSuccess()) {
            List<EmployeeDTO> employees = (List<EmployeeDTO>) res.getReturnValue();
            for (EmployeeDTO emp : employees) {
                sb.append("<li>")
                  .append(emp.getId()).append(" - ")
                  .append(emp.getFullName()).append(" (")
                  .append(emp.getPositions().stream().map(RoleDTO::getRoleName).reduce((a, b) -> a + ", " + b).orElse("No Roles"))
                  .append(")</li>")
                        .append("\n");
            }
        }
        showResponseDialog(new Response(sb, null), "All Employees");
    }

    /** one reusable card */
    private JPanel createCard(String title, String iconFile, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xE1F5FE));
        card.setBorder(new LineBorder(new Color(0x81D4FA), 2, true));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        /* 1) ask Swing for a roomy tile */
        card.setPreferredSize(new Dimension(260, 180));   // width , height

        /* 2) big-enough icon */
        JLabel pic = new JLabel();
        pic.setHorizontalAlignment(SwingConstants.CENTER);
        pic.setBorder(new EmptyBorder(15, 0, 0, 0));      // push down a bit
        loadIcon(pic, iconFile, 72, 72);                  // scale icon to 72×72

        /* 3) caption below the icon */
        JLabel caption = new JLabel(title, SwingConstants.CENTER);
        caption.setFont(new Font("Segoe UI", Font.BOLD, 18));
        caption.setForeground(new Color(0x01579B));
        caption.setBorder(new EmptyBorder(0, 0, 15, 0));

        card.add(pic,      BorderLayout.CENTER);
        card.add(caption,  BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
            @Override public void mouseEntered(MouseEvent e){ card.setBackground(new Color(0xB3E5FC)); }
            @Override public void mouseExited (MouseEvent e){ card.setBackground(new Color(0xE1F5FE)); }
        });
        return card;
    }




    private void loadIcon(JLabel lbl, String fileName, int w, int h) {
        URL res = getClass().getResource("/icons/" + fileName);
        if (res == null) {
            System.err.println("Resource not found: " + fileName);
            return;
        }
        try {
            Image img = ImageIO.read(res).getScaledInstance(w, h, Image.SCALE_SMOOTH);
            lbl.setIcon(new ImageIcon(img));
            lbl.setIconTextGap(10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hireDialog() {
        JTextField txtId     = new JTextField();
        JTextField txtPw     = new JPasswordField();
        JTextField txtName   = new JTextField();
        JTextField txtSalary = new JTextField();
        JTextField txtBank   = new JTextField();
        JTextField txtHours  = new JTextField();
        JComboBox<RoleDTO>     cbRoles = new JComboBox<>(roles.getRoles().toArray(new RoleDTO[0]));
        JComboBox<LocationDTO> cbLoc   = new JComboBox<>(del.getAllAvailableLocations().toArray(new LocationDTO[0]));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: ID
        gbc.gridx   = 0;
        gbc.gridy   = 0;
        gbc.weightx = 0;
        form.add(new JLabel("ID:"), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        form.add(txtId, gbc);

        // Row 1: Password
        gbc.gridy   = 1;
        gbc.gridx   = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Password:"), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        form.add(txtPw, gbc);

        // Row 2: Full Name
        gbc.gridy   = 2;
        gbc.gridx   = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Full Name:"), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        form.add(txtName, gbc);

        // Row 3: Salary
        gbc.gridy   = 3;
        gbc.gridx   = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Salary:"), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        form.add(txtSalary, gbc);

        // Row 4: Bank Account
        gbc.gridy   = 4;
        gbc.gridx   = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Bank Account:"), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        form.add(txtBank, gbc);

        // Row 5: Monthly Hours
        gbc.gridy   = 5;
        gbc.gridx   = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Monthly Hours:"), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        form.add(txtHours, gbc);

        // Row 6: Role
        gbc.gridy   = 6;
        gbc.gridx   = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Role:"), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        form.add(cbRoles, gbc);

        // Row 7: Location
        gbc.gridy   = 7;
        gbc.gridx   = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Location:"), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        form.add(cbLoc, gbc);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Hire Employee",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            RoleDTO    r = (RoleDTO) cbRoles.getSelectedItem();
            LocationDTO l = (LocationDTO) cbLoc.getSelectedItem();
            var rl = new ArrayList<RoleDTO>();
            rl.add(r);

            Response res = svc.hireEmployee(
                    txtId.getText().trim(),
                    txtPw.getText().trim(),
                    txtName.getText().trim(),
                    LocalDate.now(), rl,
                    Double.parseDouble(txtSalary.getText().trim()),
                    txtBank.getText().trim(),
                    Integer.parseInt(txtHours.getText().trim()),
                    0, new ArrayList<>(), l
            );
            showResponseDialog(res, "Hire Employee");
        }
    }

    private void idDialog(String prompt, Function<String, Response> fn) {
        String id = JOptionPane.showInputDialog(this, prompt);
        if (id != null) {
            Response r = fn.apply(id);
            showResponseDialog(r, prompt);
        }
    }

    private void editDialog() {
        String id = JOptionPane.showInputDialog(this, "Enter employee ID to edit:");
        if (id == null || id.trim().isEmpty()) return;

        Object[][] actions = {
                {"Password",     "edit.png",   (Runnable) () -> updateField(id, "New password (4 chars):", pw -> svc.updatePassword(id, pw))},
                {"Full Name",    "edit.png",   (Runnable) () -> updateField(id, "New full name:",     nm -> svc.updateFullName(id, nm))},
                {"Positions",    "edit.png",   (Runnable) () -> {
                    List<RoleDTO> sel = chooseRolesSwing();
                    if (sel != null) showResponseDialog(svc.updatePositions(id, sel), "Positions Update");
                }},
                {"Salary",       "edit.png",   (Runnable) () -> updateField(id, "New salary:", str -> {
                    try {
                        double v = Double.parseDouble(str);
                        return svc.updateSalary(id, v);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                })},
                {"Bank Account", "edit.png",   (Runnable) () -> updateField(id, "New bank acct:", acct -> svc.updateBankAccount(id, acct))},
                {"Monthly Hours","edit.png",   (Runnable) () -> updateField(id, "New monthly hours:", str -> {
                    try {
                        int v = Integer.parseInt(str);
                        return svc.updateMonthlyHours(id, v);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid integer", "Error", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                })},
                {"License Type", "edit.png",   (Runnable) () -> {
                    List<String> lic = chooseLicensesSwing();
                    if (lic != null) showResponseDialog(svc.updateLicenseType(id, lic), "License Update");
                }},
                {"Location",     "edit.png",   (Runnable) () -> {
                    List<LocationDTO> locs = del.getAllAvailableLocations();
                    LocationDTO sel = chooseLocationSwing(locs);
                    if (sel != null) showResponseDialog(svc.updateLocation(id, sel), "Location Update");
                }}
        };

        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Edit Employee — ID: " + id,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        JPanel grid = new JPanel(new GridLayout(0, 3, 20, 20));
        grid.setBackground(new Color(0xF5F5F5));
        grid.setBorder(new EmptyBorder(30, 30, 30, 30));

        for (Object[] act : actions) {
            String  label = (String) act[0];
            String  icon  = (String) act[1];
            Runnable run  = (Runnable) act[2];
            JPanel card   = createCard(label, icon, () -> {
                dlg.dispose();
                run.run();
            });
            grid.add(card);
        }
        // cancel card
        grid.add(createCard("Cancel", "cancel.png", dlg::dispose));

        dlg.getContentPane().add(grid);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void updateField(String id, String prompt, Function<String, Response> updater) {
        String in = JOptionPane.showInputDialog(this, prompt);
        if (in != null) {
            Response r = updater.apply(in.trim());
            showResponseDialog(r, prompt);
        }
    }

    private void showResponseDialog(Response r, String title) {
        int type = r.isSuccess()
                ? JOptionPane.INFORMATION_MESSAGE
                : JOptionPane.ERROR_MESSAGE;

        JOptionPane.showMessageDialog(
                this,
                r.isSuccess() ? r.getReturnValue() : r.getErrorMsg(),
                title,
                type
        );
    }

    private List<RoleDTO> chooseRolesSwing() {
        List<RoleDTO> all = roles.getRoles();
        JList<RoleDTO> list = new JList<>(all.toArray(new RoleDTO[0]));
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        int ok = JOptionPane.showConfirmDialog(
                this, new JScrollPane(list), "Select Roles",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        return ok == JOptionPane.OK_OPTION
                ? list.getSelectedValuesList()
                : null;
    }

    private List<String> chooseLicensesSwing() {
        List<String> licenses = new ArrayList<>();
        while (true) {
            String in = JOptionPane.showInputDialog(this,
                    "Enter license type (or leave blank to finish):");
            if (in == null || in.trim().isEmpty()) break;
            if (in.matches("\\d+")) {
                JOptionPane.showMessageDialog(this,
                        "Letters only, please", "Invalid", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            licenses.add(in.trim());
        }
        return licenses;
    }

    private LocationDTO chooseLocationSwing(List<LocationDTO> locs) {
        LocationDTO[] arr = locs.toArray(new LocationDTO[0]);
        return (LocationDTO) JOptionPane.showInputDialog(
                this,
                "Select new location:",
                "Location",
                JOptionPane.PLAIN_MESSAGE,
                null,
                arr,
                arr.length>0 ? arr[0] : null
        );
    }
}