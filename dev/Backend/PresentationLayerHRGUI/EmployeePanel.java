package Backend.PresentationLayerHRGUI;

import Backend.DTO.EmployeeDTO;
import Backend.DTO.RoleDTO;
import Backend.DomainLayer.DomainLayerHR.EmployeeDL;
import Backend.ServiceLayer.ServiceLayerHR.EmloyeeService.EmployeeManageService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.WorkersManageService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Function;

public class EmployeePanel extends JPanel {

    private final EmployeeManageService svc;
    private final WorkersManageService wsvc;

    public EmployeePanel(EmployeeDTO employee) {
        this.svc = new EmployeeManageService(employee);
        this.wsvc = new WorkersManageService();

        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F5F5));

        JLabel banner = new JLabel("Employee Console", SwingConstants.CENTER);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 28));
        banner.setBorder(new EmptyBorder(20, 0, 20, 0));
        banner.setForeground(new Color(0x1565C0));
        add(banner, BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(0, 2, 20, 20));
        cards.setBorder(new EmptyBorder(30, 30, 30, 30));
        cards.setBackground(new Color(0xF5F5F5));

        cards.add(card("✏️ Edit Name",          () -> field("New full name", svc::editName)));
        cards.add(card("🏦 Edit BankAccount",    () -> field("New bank account", svc::editBankAccount)));
        cards.add(card("🔑 Edit Password",      () -> field("New password",    svc::editPassword)));
        cards.add(card("📅 Update Constraints", () -> new ConstraintsDialog(svc).setVisible(true)));
        cards.add(card("⬆️ Upload Constraints", () -> show(svc.uploadConstraints())));
        cards.add(card("🧾 Show Details",       () -> show(svc.showDetails())));
        cards.add(card("📆 Weekly Shift",       () -> show(svc.showWeeklyShift())));
        cards.add(card("📜 Shift History",      () -> show(svc.showShiftHistory())));
        Response res = svc.showEmployeeDetails(employee.getId());
        if (res.isSuccess()) {
            EmployeeDL emp = (EmployeeDL) res.getReturnValue();
            boolean isManager = emp.hasRole("ShiftManager") || emp.hasRole("HRManager");
            if (isManager) {
                cards.add(card("🔍 Employee Details", () -> {
                    String id = JOptionPane.showInputDialog(this, "Employee ID:");
                    if (id != null) show(svc.showEmployeeDetails(id.trim()));
                }));
                cards.add(card("📋 Show All Employees ", () -> {
                    show(showAllEmployees());
                }));
            }
        }
//        cards.add(card("🔍 Employee Details",   () -> {
//            String id = JOptionPane.showInputDialog(this, "Employee ID:");
//            if (id != null) show(svc.showEmployeeDetails(id.trim()));
        cards.add(card("🚪 Logout", () -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }));

        add(cards, BorderLayout.CENTER);
    }

    private boolean showAllEmployees() {
        Response res = wsvc.showAllEmployees();
        if (res.isSuccess()) {
            ArrayList<EmployeeDTO> employees = (ArrayList<EmployeeDTO>) res.getReturnValue();
            StringBuilder sb = new StringBuilder("All Employees:\n");
            for (EmployeeDTO emp : employees) {
                sb.append(emp.getId())
                        .append(": ")
                        .append(emp.getFullName())
                        .append(" (")
                        .append(emp.getPositions().stream().map(RoleDTO::getRoleName).reduce((a, b) -> a + ", " + b).orElse("No Roles"))
                        .append(") ")
                        .append("\n");
            }
            show(new Response(sb.toString(), null));
            return true;
        } else {
            show(res);
            return false;
        }
    }

    /** one-row tile: emoji + caption, centred, compact */
    private JPanel card(String title, Runnable action) {

        /* split the first token (emoji) from the caption */
        String[] parts   = title.split(" ", 2);
        String   emoji   = parts[0];
        String   caption = parts.length > 1 ? parts[1] : "";

        /* -------- outer tile -------- */
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xE1F5FE));
        card.setBorder(new LineBorder(new Color(0x81D4FA), 2, true));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        /* exact height you want – try 52-56 px */
        int rowH = 56;
        card.setPreferredSize(new Dimension(280, rowH));      // width , height
        card.setMaximumSize  (new Dimension(Integer.MAX_VALUE, rowH));

        /* -------- inner line (icon + text) -------- */
        JPanel line = new JPanel();                         // keeps both centred
        line.setOpaque(false);
        line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
        line.setBorder(new EmptyBorder(0, 8, 0, 8));        // little left/right pad

        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel text = new JLabel(caption);
        text.setFont(new Font("Segoe UI", Font.BOLD, 17));
        text.setForeground(new Color(0x01579B));
        text.setAlignmentY(Component.CENTER_ALIGNMENT);

        line.add(icon);
        line.add(Box.createHorizontalStrut(12));            // gap between icon & text
        line.add(text);

        card.add(line, BorderLayout.CENTER);

        /* -------- hover / click -------- */
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked (MouseEvent e){ action.run(); }
            @Override public void mouseEntered (MouseEvent e){ card.setBackground(new Color(0xB3E5FC)); }
            @Override public void mouseExited  (MouseEvent e){ card.setBackground(new Color(0xE1F5FE)); }
        });

        return card;
    }



    private void field(String prompt, Function<String,Response> fn) {
        String in = JOptionPane.showInputDialog(this, prompt + ":");
        if (in != null) show(fn.apply(in.trim()));
    }

    private void show(Response r) {
        String msg = (r.getReturnValue()!= null)
                ? r.getReturnValue().toString()
                : r.getErrorMsg();
        JTextArea area = new JTextArea(msg);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane sc = new JScrollPane(area);
        sc.setPreferredSize(new Dimension(500,350));
        JOptionPane.showMessageDialog(this, sc, "Result",
                r.isSuccess()
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.ERROR_MESSAGE);
    }
}
