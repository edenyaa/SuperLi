package Backend.PresentationLayerHRGUI;

import Backend.DTO.EmployeeDTO;
import Backend.ServiceLayer.ServiceLayerHR.HRService.RolesService;
import Backend.ServiceLayer.ServiceLayerHR.PermissionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.function.BiConsumer;

public class MainFrame extends JFrame {
    public MainFrame(EmployeeDTO user, PermissionLevel level) {
        super("Dashboard");

        // build the tab‐pane
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setBackground(new Color(0xF5F5F5));


        BiConsumer<String, JComponent> addTab = (title, panel) -> {
            Icon icon = null;
            String path = "/icons/" + title.toLowerCase().replace(" ", "") + ".png";
            URL url = getClass().getResource(path);
            if (url != null) {
                try {
                    Image img = ImageIO.read(url)
                            .getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(img);
                } catch (IOException ignored) {}
            }
            tabs.addTab(title, icon, panel);
        };

        // HR section: only SYSTEMMANAGER or HRMANAGER
        if (level == PermissionLevel.SYSTEMMANAGER || level == PermissionLevel.HRMANAGER) {
            addTab.accept("HR",      new HRPanel());
            addTab.accept("Workers", new WorkersPanel());
            addTab.accept("Inbox",   new InboxPanel());
        }

        // Shifts: system, hr, or transport manager
        if (level == PermissionLevel.SYSTEMMANAGER
                || level == PermissionLevel.HRMANAGER
                || level == PermissionLevel.TRANSPORTMANAGER) {
            addTab.accept("Shifts", new ShiftsPanel());
        }

        // Everybody gets their own console
        addTab.accept("Employee", new EmployeePanel(user));

        // Company Roles only for HR or system manager
        if (level == PermissionLevel.SYSTEMMANAGER || level == PermissionLevel.HRMANAGER) {
            addTab.accept("Company Roles", new RolesPanel(new RolesService()));
        }

        // put it all in place
        setContentPane(tabs);

        // simple File→Logout menu
        JMenuBar mb = new JMenuBar();
        JMenu     file = new JMenu("File");
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        file.add(logout);
        mb.add(file);
        setJMenuBar(mb);

        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}