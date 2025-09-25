package Backend.PresentationLayerHRGUI;

import Backend.DTO.EmployeeDTO;
import Backend.PresentationLayerHR.LoginScreen;
import Backend.ServiceLayer.ServiceLayerHR.PermissionLevel;
import Backend.ServiceLayer.ServiceLayerHR.PermissionService;
import Backend.ServiceLayer.ServiceLayerHR.Response;
import Backend.ServiceLayer.SuperService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        super("Login System");

        JLabel header = new JLabel("Welcome to SuperLi Employee System", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 32));
        header.setOpaque(true);
        header.setBackground(new Color(0x2E86C1));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        JTextField txtId     = new JTextField(20);
        txtId.setFont(new Font("Arial", Font.PLAIN, 18));
        JPasswordField txtPw = new JPasswordField(20);
        txtPw.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton btnLogin     = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 20));
        btnLogin.setBackground(new Color(0x117A65));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        getRootPane().setDefaultButton(btnLogin);
        btnLogin.addActionListener(e -> {
            String id = txtId.getText().trim();
            String pw = new String(txtPw.getPassword()).trim();

            try {
                // 1) Prepare only login data
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.addDefaultManagers();


                // 2) Perform authentication
                Response resp = loginScreen.getLoginService().login(id, pw);
                if (!resp.isSuccess()) {
                    showMessage(resp.getErrorMsg(), "Login Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 3) Now load the rest of the domain (constraints, shifts, etc.)
                try {
                    new SuperService().loadData();
                } catch (Exception ignore) {}

                // 4) Open main application
                EmployeeDTO dto = (EmployeeDTO) resp.getReturnValue();
                PermissionLevel level = PermissionService.getPermissionLevel(dto.getPositions());
                new MainFrame(dto, level).setVisible(true);
                dispose();

            } catch (IllegalArgumentException ex) {
                showMessage("Invalid username or password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                showMessage("Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xD6EAF8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel prompt = new JLabel("Please enter your login details:", SwingConstants.CENTER);
        prompt.setFont(new Font("Arial", Font.ITALIC, 20));
        form.add(prompt, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        JLabel lblId = new JLabel("ID Number:");
        lblId.setFont(new Font("Arial", Font.PLAIN, 18));
        form.add(lblId, gbc);

        gbc.gridx = 1;
        form.add(txtId, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblPw = new JLabel("Password:");
        lblPw.setFont(new Font("Arial", Font.PLAIN, 18));
        form.add(lblPw, gbc);

        gbc.gridx = 1;
        form.add(txtPw, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        form.add(btnLogin, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(form, BorderLayout.CENTER);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void showMessage(String message, String title, int messageType) {
        JLabel msgLabel = new JLabel("<html><body style='width: 350px;'>" + message + "</body></html>");
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JOptionPane pane = new JOptionPane(msgLabel, messageType);
        JDialog dialog = pane.createDialog(this, title);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
