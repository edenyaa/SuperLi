package Backend.PresentationLayerHRGUI;

import Backend.DTO.RoleDTO;
import Backend.ServiceLayer.ServiceLayerHR.HRService.RolesService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RolesPanel extends JPanel {

    private final RolesService svc;

    public RolesPanel(RolesService svc) {
        this.svc = svc;

        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F5F5));

        /* ---------- banner ---------- */
        JLabel banner = new JLabel("Company Roles", SwingConstants.CENTER);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 28));
        banner.setBorder(new EmptyBorder(20, 0, 20, 0));
        banner.setForeground(new Color(0x1565C0));
        add(banner, BorderLayout.NORTH);

        /* ---------- cards grid ---------- */
        JPanel cards = new JPanel(new GridLayout(0, 2, 20, 20));
        cards.setBorder(new EmptyBorder(30, 30, 30, 30));
        cards.setBackground(new Color(0xF5F5F5));

        cards.add(card("📋 Show All Roles", this::showAll));
        cards.add(card("➕ Add Role",        this::addRole));
        cards.add(card("➖ Remove Role",     this::removeRole));
        cards.add(card("🔍 View Permissions",this::viewPermissions));
        cards.add(card("➕ Add Permission",  this::addPermission));
        cards.add(card("➖ Remove Permission",this::removePermission));

        add(cards, BorderLayout.CENTER);
    }

    /* ------------ single “card” ------------- */
    private JPanel card(String title, Runnable action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0xE1F5FE));
        p.setBorder(new LineBorder(new Color(0x81D4FA), 2, true));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(title.split(" ", 2)[0], SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setBorder(new EmptyBorder(20,0,0,0));

        JLabel label = new JLabel(title.substring(title.indexOf(' ')+1), SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(0x01579B));
        label.setBorder(new EmptyBorder(0,0,20,0));

        p.add(icon, BorderLayout.CENTER);
        p.add(label, BorderLayout.SOUTH);

        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
            @Override public void mouseEntered(MouseEvent e){ p.setBackground(new Color(0xB3E5FC)); }
            @Override public void mouseExited(MouseEvent e) { p.setBackground(new Color(0xE1F5FE)); }
        });
        return p;
    }

    /* -------- actions wired to RolesService ---------- */
    private void showAll()            { show(svc.getRolesResponse()); }
    private void addRole() {
        String name = JOptionPane.showInputDialog(this,"Role name:");
        if (name==null||name.isBlank()) return;
        String perms = JOptionPane.showInputDialog(this,"Permissions (comma-separated):");
        ArrayList<String> list = new ArrayList<>();
        if (perms!=null && !perms.isBlank())
            for (String p:perms.split(",")) list.add(p.trim());
        show(svc.addRole(name,list));
    }
    private void removeRole() {
        String id = JOptionPane.showInputDialog(this,"Role ID to remove:");
        if (id==null) return;
        show(svc.removeRole(Integer.parseInt(id.trim())));
    }
    private void viewPermissions() {
        String id = JOptionPane.showInputDialog(this,"Role ID:");
        if (id==null) return;
        show(svc.getRolePermissions(Integer.parseInt(id.trim())));
    }
    private void addPermission() {
        String id = JOptionPane.showInputDialog(this,"Role ID:");
        if (id==null) return;
        String perm = JOptionPane.showInputDialog(this,"Permission:");
        if (perm==null||perm.isBlank()) return;
        ArrayList<String> list=new ArrayList<>(List.of(perm.trim()));
        show(svc.addPermissionToRole(svc.getRoleById(Integer.parseInt(id.trim())), list));
    }
    private void removePermission() {
        String rid = JOptionPane.showInputDialog(this,"Role ID:");
        if (rid==null) return;
        String idx = JOptionPane.showInputDialog(this,"Permission index (1-based):");
        if (idx==null) return;
        show(svc.removePermissionFromRole(Integer.parseInt(rid.trim()),
                Integer.parseInt(idx.trim())-1));
    }

    /* -------- pretty dialog ---------- */
    private void show(Response r) {
        Object val = r.getReturnValue();
        String msg = (val != null) ? val.toString() : r.getErrorMsg();

        JTextArea area = new JTextArea(msg);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane sc = new JScrollPane(area);
        sc.setPreferredSize(new Dimension(500, 350));

        JOptionPane.showMessageDialog(this, sc, "Result", JOptionPane.INFORMATION_MESSAGE);
    }
}
