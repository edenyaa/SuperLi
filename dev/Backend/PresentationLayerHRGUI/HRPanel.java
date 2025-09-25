package Backend.PresentationLayerHRGUI;

import Backend.ServiceLayer.ServiceLayerHR.HRService.HRInboxService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.RolesService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.ShiftsManageService;
import Backend.ServiceLayer.ServiceLayerHR.HRService.WorkersManageService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class HRPanel extends JPanel {
    private final WorkersManageService wsvc   = new WorkersManageService();
    private final ShiftsManageService  ssvc   = new ShiftsManageService();
    private final HRInboxService       inbox  = new HRInboxService();
    private final RolesService         rsvc   = new RolesService();

    public HRPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F5F5));
        JLabel banner = new JLabel("Administration", SwingConstants.CENTER);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 28));
        banner.setForeground(new Color(0x1565C0));
        banner.setBorder(new EmptyBorder(20, 0, 20, 0));
        ImageIcon ico = loadIcon("admin.png", 48, 48);
        if (ico != null) banner.setIcon(ico);
        add(banner, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(0, 2, 20, 20));
        cards.setBorder(new EmptyBorder(30, 30, 30, 30));
        cards.setBackground(new Color(0xF5F5F5));

        cards.add(createCard("Workers Management", "workers.png", () -> switchToTab(1)));
        cards.add(createCard("Inbox",              "inbox.png",     () -> switchToTab(2)));
        cards.add(createCard("Shifts Management",  "shifts.png",    () -> switchToTab(3)));
        cards.add(createCard("Employee",           "employee.png",  () -> switchToTab(4)));
        cards.add(createCard("Company Roles",      "companyroles.png",     () -> switchToTab(5)));
        cards.add(createCard(" Logout",           "logout.png",    () -> {

            SwingUtilities.getWindowAncestor(this).dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }));

        add(cards, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, String iconFile, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xE1F5FE));
        card.setBorder(new LineBorder(new Color(0x81D4FA), 2, true));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(0x01579B));
        lbl.setVerticalTextPosition(SwingConstants.BOTTOM);
        lbl.setHorizontalTextPosition(SwingConstants.CENTER);
        lbl.setBorder(new EmptyBorder(20, 0, 20, 0));
        ImageIcon ico = loadIcon(iconFile, 64, 64);
        if (ico != null) lbl.setIcon(ico);

        card.add(lbl, BorderLayout.CENTER);
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { onClick.run(); }
            @Override public void mouseEntered(MouseEvent e)  { card.setBackground(new Color(0xB3E5FC)); }
            @Override public void mouseExited(MouseEvent e)   { card.setBackground(new Color(0xE1F5FE)); }
        });

        return card;
    }

    private ImageIcon loadIcon(String name, int w, int h) {
        URL res = getClass().getResource("/icons/" + name);
        if (res == null) return null;
        try {
            Image img = ImageIO.read(res).getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ex) {
            return null;
        }
    }

    private void switchToTab(int idx) {
        JTabbedPane tabs = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this);
        if (tabs != null && idx < tabs.getTabCount()) {
            tabs.setSelectedIndex(idx);
        }
    }
}
