package Backend.PresentationLayerHRGUI;

import Backend.ServiceLayer.ServiceLayerHR.HRService.RolesService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.ShiftsManageService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class ShiftsPanel extends JPanel {
    private final ShiftsManageService svc  = new ShiftsManageService();
    private final RolesService         rsvc = new RolesService();

    public ShiftsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F5F5));

        // Banner
        JLabel banner = new JLabel("Shifts Management", SwingConstants.CENTER);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 28));
        banner.setBorder(new EmptyBorder(20, 0, 20, 0));
        banner.setForeground(new Color(0x1565C0));
        add(banner, BorderLayout.NORTH);

        /* ---------- cards grid ---------- */
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
//        cards.setBorder(new EmptyBorder(30, 30, 30, 30));

        cards.setBackground(new Color(0xF5F5F5));

        cards.add(card("➕ Add Shift",         () -> single(true)));
        cards.add(card("➖ Remove Shift",      () -> single(false)));
        cards.add(card("⌨️ Edit Shift",       this::editShiftDialog));
        cards.add(card("📑 View Published",   () -> show(svc.viewPublishedWeeklyShifts())));
        cards.add(card("📝 View Unpublished", () -> show(svc.viewUnpublishedWeeklyShifts())));
        cards.add(card("🚀 Publish Weekly",   () -> show(svc.publishWeeklyShift())));
        cards.add(card("👥 View Constraints", () -> show(svc.viewWeeklyConstraints())));
        cards.add(card("📆 Next Constraints", () -> show(svc.setNextWeekConstraints())));
        cards.add(card("⏰ Change Deadline",   this::deadline));

        add(cards, BorderLayout.CENTER);
    }

    private JPanel card(String title, Runnable action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0xE1F5FE));
        p.setBorder(new LineBorder(new Color(0x81D4FA), 2, true));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.setPreferredSize(new Dimension(180, 100));

        JLabel icon = new JLabel(title.split(" ", 2)[0], SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        icon.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel label = new JLabel(title.substring(title.indexOf(' ') + 1), SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(0x01579B));
        label.setBorder(new EmptyBorder(0, 0, 20, 0));

        p.add(icon, BorderLayout.CENTER);
        p.add(label, BorderLayout.SOUTH);

        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
            @Override public void mouseEntered(MouseEvent e) { p.setBackground(new Color(0xB3E5FC)); }
            @Override public void mouseExited (MouseEvent e) { p.setBackground(new Color(0xE1F5FE)); }
        });
        return p;
    }

    private void single(boolean add) {
        String day   = JOptionPane.showInputDialog(this, "Day (1–7):");
        String shift = JOptionPane.showInputDialog(this, "Shift (1–2):");
        if (day == null || shift == null) return;
        try {
            int d = Integer.parseInt(day.trim()), s = Integer.parseInt(shift.trim());
            Response r = add ? svc.addShift(d, s) : svc.removeShift(d, s);
            show(r);
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers.");
        }
    }

    private void deadline() {
        String d = JOptionPane.showInputDialog(this, "Day of week (MONDAY–SUNDAY):");
        String h = JOptionPane.showInputDialog(this, "Hour (0–23):");
        String m = JOptionPane.showInputDialog(this, "Minute (0–59):");
        if (d==null||h==null||m==null) return;
        try {
            DayOfWeek dow = DayOfWeek.valueOf(d.toUpperCase().trim());
            LocalTime tm = LocalTime.of(Integer.parseInt(h.trim()), Integer.parseInt(m.trim()));
            show(svc.changeConstraintsDeadline(dow, tm));
        } catch (Exception ex) {
            showError("Invalid input.");
        }
    }

    private void show(Response r) {
        JTextArea area = new JTextArea(
                r.getReturnValue()!=null
                        ? r.getReturnValue().toString()
                        : r.getErrorMsg()
        );
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane sc = new JScrollPane(area);
        sc.setPreferredSize(new Dimension(800, 400));

        JOptionPane.showMessageDialog(this, sc, "Result", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ---- EDIT SHIFT UI ----
    private void editShiftDialog() {
        // ask published or not
        int pub = JOptionPane.showOptionDialog(this,
                "Edit a published or unpublished shift?",
                "Edit Shift",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new String[]{"Published","Unpublished"}, "Published"
        );
        if (pub<0) return;
        boolean published = (pub==0);

        // Day & shift
        String dayStr = JOptionPane.showInputDialog(this,"Day (1=Sun…7=Sat):");
        String shiftStr = JOptionPane.showInputDialog(this,"Shift (1=Morning,2=Evening):");
        if (dayStr==null||shiftStr==null) return;
        int day, time;
        try {
            day = Integer.parseInt(dayStr.trim());
            time = Integer.parseInt(shiftStr.trim());
            if (published) {
                LocalDate today = LocalDate.now();
                LocalDate start = svc.getPublishedWeekStart();
                if (!start.plusDays(day-1).isAfter(today)) {
                    showError("Cannot edit past or today in published week.");
                    return;
                }
            }
        } catch (Exception ex) {
            showError("Invalid day/shift.");
            return;
        }

        // Build the modal dialog
        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Edit Shift ("+day+","+time+(published?"/P":"")+")",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        JPanel grid = new JPanel(new GridLayout(0, 3, 20, 20));
        grid.setBackground(new Color(0xF5F5F5));
        grid.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Assign Employee
        grid.add(buildEditCard("👤 Assign", 48, () -> {
            dlg.dispose();
            String id = JOptionPane.showInputDialog(this,"Employee ID to assign:");
            if (id!=null) {
                rsvc.printRoles();
                String rid = JOptionPane.showInputDialog(this,"Role ID to assign:");
                try {
                    Response resp = svc.assignEmployeeToShift(day,time,id,
                            rsvc.getRoleById(Integer.parseInt(rid.trim())),
                            published
                    );
                    show(resp);
                } catch(Exception e){ showError(e.getMessage()); }
            }
        }));

        // Remove Employee
        grid.add(buildEditCard("✖️ Remove", 48, () -> {
            dlg.dispose();
            String id = JOptionPane.showInputDialog(this,"Employee ID to remove:");
            if (id!=null) {
                Response resp = svc.removeEmployeeFromShift(day,time,id,published);
                show(resp);
            }
        }));

        // Add Required Role
        grid.add(buildEditCard("➕ Role", 48, () -> {
            dlg.dispose();
            rsvc.printRoles();
            String rid = JOptionPane.showInputDialog(this,"Role ID to add:");
            String num = JOptionPane.showInputDialog(this,"# employees required:");
            try {
                Response resp = svc.addRoleToShift(
                        day,time,
                        rsvc.getRoleById(Integer.parseInt(rid.trim())),
                        Integer.parseInt(num.trim()),
                        published
                );
                show(resp);
            } catch(Exception e){ showError(e.getMessage()); }
        }));

        // Remove Required Role
        grid.add(buildEditCard("➖ Role", 48, () -> {
            dlg.dispose();
            rsvc.printRoles();
            String rid = JOptionPane.showInputDialog(this,"Role ID to remove:");
            try {
                Response resp = svc.removeRoleFromShift(
                        day,time,
                        rsvc.getRoleById(Integer.parseInt(rid.trim())),
                        published
                );
                show(resp);
            } catch(Exception e){ showError(e.getMessage()); }
        }));

        // Update # required
        grid.add(buildEditCard("✏️ Role Count", 48, () -> {
            dlg.dispose();
            rsvc.printRoles();
            String rid = JOptionPane.showInputDialog(this,"Role ID to update:");
            String num = JOptionPane.showInputDialog(this,"New # required:");
            try {
                Response resp = svc.updateNumOfEmployeesForRole(
                        day,time,
                        rsvc.getRoleById(Integer.parseInt(rid.trim())),
                        Integer.parseInt(num.trim()),
                        published
                );
                show(resp);
            } catch(Exception e){ showError(e.getMessage()); }
        }));

        // Cancel
        grid.add(buildEditCard("❌ Cancel", 48, dlg::dispose));

        dlg.getContentPane().add(grid);
        dlg.pack();
        dlg.setSize(1800, 500);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private JPanel buildEditCard(String titleEmoji, int emojiSize, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xE1F5FE));
        card.setBorder(new LineBorder(new Color(0x81D4FA), 2, true));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(titleEmoji, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, emojiSize));
        icon.setBorder(new EmptyBorder(20,0,0,0));

        JLabel txt = new JLabel(
                titleEmoji.length()>2 ? titleEmoji.substring(titleEmoji.indexOf(' ')+1) : titleEmoji,
                SwingConstants.CENTER
        );
        txt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txt.setForeground(new Color(0x01579B));
        txt.setBorder(new EmptyBorder(0,0,20,0));

        card.add(icon, BorderLayout.CENTER);
        card.add(txt, BorderLayout.SOUTH);
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onClick.run(); }
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(0xB3E5FC)); }
            public void mouseExited(MouseEvent e)  { card.setBackground(new Color(0xE1F5FE)); }
        });
        return card;
    }
}
