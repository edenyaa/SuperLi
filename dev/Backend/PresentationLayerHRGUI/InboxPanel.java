package Backend.PresentationLayerHRGUI;

import Backend.ServiceLayer.ServiceLayerHR.HRService.HRInboxService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InboxPanel extends JPanel {
    private final HRInboxService svc = new HRInboxService();

    public InboxPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F5F5)); // very light gray

        // Top banner with mail graphic (using emoji)
        JLabel banner = new JLabel("My Mail Inbox", SwingConstants.CENTER);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 28));
        banner.setBorder(new EmptyBorder(20, 0, 20, 0));
        banner.setForeground(new Color(0x1565C0));
        add(banner, BorderLayout.NORTH);

        // Cards area
        JPanel cards = new JPanel(new GridLayout(0, 2, 20, 20));
        cards.setBorder(new EmptyBorder(30, 30, 30, 30));
        cards.setBackground(new Color(0xF5F5F5));

        cards.add(createCard("📭 Unread Mail",    () -> count(true, false)));
        cards.add(createCard("📬 Read Mail",      () -> count(false, true)));
        cards.add(createCard("✉️ All Mail",       () -> showResponse(svc.viewAllMessages())));
        cards.add(createCard("📊 Mail Statistics",() -> showResponse(svc.getInboxStats())));
        cards.add(createCard("🗑️ Clear Inbox",   () -> {
            svc.clearInbox();
            showInfo("All messages cleared.", "Cleared");
        }));

        add(cards, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xE1F5FE)); // soft cyan
        card.setBorder(new LineBorder(new Color(0x81D4FA), 2, true));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(title, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setBorder(new EmptyBorder(20,0,0,0));

        JLabel label = new JLabel(title.substring(title.indexOf(' ')+1), SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(0x01579B));
        label.setBorder(new EmptyBorder(0,0,20,0));

        card.add(icon, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(new Color(0xB3E5FC)); }
            @Override public void mouseExited(MouseEvent e)  { card.setBackground(new Color(0xE1F5FE)); }
        });

        return card;
    }

    private void count(boolean unread, boolean read) {
        String n = JOptionPane.showInputDialog(this, "How many messages?", "Input", JOptionPane.PLAIN_MESSAGE);
        if (n == null) return;
        try {
            int num = Integer.parseInt(n.trim());
            Response r = svc.viewMessages(unread, read, num);
            showResponse(r);
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
        }
    }

    private void showResponse(Response r) {
        JTextArea area = new JTextArea(r.getReturnValue() != null
                ? r.getReturnValue().toString()
                : r.getErrorMsg());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBackground(Color.WHITE);
        area.setBorder(new EmptyBorder(10,10,10,10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500,350));

        JOptionPane pane = new JOptionPane(scroll, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Inbox Result");
        styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void showError(String msg) { showMessage(msg, "Error", JOptionPane.ERROR_MESSAGE); }
    private void showInfo(String msg, String title) { showMessage(msg, title, JOptionPane.INFORMATION_MESSAGE); }

    private void showMessage(String msg, String title, int type) {
        JLabel label = new JLabel("<html><body style='width:300px;'>" + msg + "</body></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JOptionPane pane = new JOptionPane(label, type);
        JDialog dialog = pane.createDialog(this, title);
        styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void styleDialog(JDialog dialog) {
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(0xF5F5F5));
        dialog.setResizable(false);
    }
}
