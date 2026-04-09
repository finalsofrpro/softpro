package com.system.gui;

import com.system.services.AuthenticationService;
import org.example.Main; // تأكدي إن البكج مطابق للمين عندك
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


 //Modern Turquoise Sign Up Interface

public class SignUpFrame extends JFrame {

    private AuthenticationService authService = Main.authService;
    private final Color TURQUOISE_COLOR = new Color(0, 188, 212);
    private final Color TEXT_DARK_RED = new Color(128, 0, 0);

    public SignUpFrame() {
        // إعدادات النافذة
        setTitle("Create Account");
        setSize(400, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // --- Header Section ---
        JLabel titleLabel = new JLabel("Sign up");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setBounds(40, 40, 200, 50);
        add(titleLabel);

        JLabel subtitleLabel = new JLabel("Join us to manage your appointments.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(40, 90, 320, 20);
        add(subtitleLabel);

        // --- Full Name Field ---
        createLabel("FULL NAME", 150);
        JTextField nameField = createStyledField("e.g. Raghd Mansour", 180);
        add(nameField);

        // --- Username Field ---
        createLabel("USERNAME", 240);
        JTextField userField = createStyledField("e.g. raghd_23", 270);
        add(userField);

        // --- Password Field ---
        createLabel("PASSWORD", 330);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(40, 360, 320, 35);
        passField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        add(passField);

        // --- Confirm Password Field ---
        createLabel("CONFIRM PASSWORD", 420);
        JPasswordField confirmField = new JPasswordField();
        confirmField.setBounds(40, 450, 320, 35);
        confirmField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        add(confirmField);

        // --- Register Button ---
        JButton registerBtn = new JButton("Register Now");
        registerBtn.setBackground(TURQUOISE_COLOR);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerBtn.setBounds(40, 520, 320, 50);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || password.isEmpty() || !password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Please check your inputs!");
                return;
            }

            // محاولة التسجيل وفحص النتيجة
            boolean success = authService.register(username, password);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                new ModernLoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username '" + username + "' is already taken. Try another one.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(registerBtn);

        // --- Back to Login Link ---
        JLabel loginLink = new JLabel("<html>Already have an account? <u><font color='#00BCD4'>Login</font></u></html>");
        loginLink.setBounds(100, 580, 250, 20);
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new ModernLoginFrame().setVisible(true);
                dispose();
            }
        });
        add(loginLink);
    }

    // ميثود مساعدة لإنشاء العناوين الحمراء
    private void createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK_RED);
        label.setBounds(40, y, 200, 20);
        add(label);
    }

    // ميثود مساعدة لإنشاء حقول الإدخال مع Placeholder
    private JTextField createStyledField(String placeholder, int y) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(Color.LIGHT_GRAY);
        field.setBounds(40, y, 320, 35);
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
        });
        return field;
    }
}