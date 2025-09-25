package Backend.PresentationLayerHRGUI;

import Backend.PresentationLayerHR.LoginScreen;
import Backend.ServiceLayer.SuperService;
import util.DatabaseSeeder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoadDataPanel extends JPanel {

    @FunctionalInterface
    public interface Proceed { void go(); }

    public LoadDataPanel(Proceed nextStep) {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F5F5));

        /* ---------- banner ---------- */
        JLabel banner = new JLabel("Welcome to SuperLi", SwingConstants.CENTER);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 28));
        banner.setBorder(new EmptyBorder(20, 0, 20, 0));
        banner.setForeground(new Color(0x1565C0));
        add(banner, BorderLayout.NORTH);

        /* ---------- cards grid ---------- */
        JPanel cards = new JPanel(new GridLayout(0, 2, 20, 20));
        cards.setBorder(new EmptyBorder(30, 30, 30, 30));
        cards.setBackground(new Color(0xF5F5F5));

        cards.add(card("📂 Load Data", () -> {
            loadPersistent();
            nextStep.go();
        }));
        cards.add(card("🆕 Start Fresh", () -> {
            loadInitial();
            nextStep.go();
        }));

        add(cards, BorderLayout.CENTER);
    }

    /* ---------- create a single clickable card ---------- */
    private JPanel card(String title, Runnable action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0xE1F5FE));
        p.setBorder(new LineBorder(new Color(0x81D4FA), 2, true));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(title.split(" ", 2)[0], SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
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

    /* ---------- actual loading logic ---------- */
    private void loadPersistent() {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.addDefaultManagers();
        loginScreen.getSuperService().loadData();
        DatabaseSeeder.seed();
    }

    private void loadInitial() {
        new LoginScreen().addDefaultManagers();
    }
}
