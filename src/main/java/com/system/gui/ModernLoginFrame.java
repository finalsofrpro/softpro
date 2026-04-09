package com.system.gui;

import com.system.models.Role;
import com.system.services.AuthenticationService;
import org.example.Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Raghd Mansour
 * Modern Turquoise Sign-In Interface (Full Pattern Integration)
 */
public class ModernLoginFrame extends JFrame {

    private AuthenticationService authService = Main.authService;
    private Role selectedRole = Role.USER;

    private final Color TURQUOISE_COLOR = new Color(0, 188, 212);
    private final Color TEXT_DARK_RED = new Color(128, 0, 0);

    private JLabel footerLabel;
    private JLabel signUpLink;
    private JButton userBtn;
    private JButton adminBtn;

    public ModernLoginFrame() {
        setTitle("Sign in");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // --- Header Section ---
        JLabel titleLabel = new JLabel("Sign in");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setBounds(40, 40, 200, 50);
        add(titleLabel);

        JLabel subtitleLabel = new JLabel("Enter your credentials to continue.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(40, 90, 300, 20);
        add(subtitleLabel);

        // --- Role Selection (Modified to match pattern) ---
        JLabel roleTitle = new JLabel("I AM A:");
        roleTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleTitle.setForeground(TEXT_DARK_RED);
        roleTitle.setBounds(40, 140, 100, 20);
        add(roleTitle);

        userBtn = new JButton("User");
        adminBtn = new JButton("Administrator");

        userBtn.setBounds(40, 170, 155, 50);
        adminBtn.setBounds(205, 170, 155, 50);

        userBtn.addActionListener(e -> {
            selectedRole = Role.USER;
            updateRoleButtons();
        });

        adminBtn.addActionListener(e -> {
            selectedRole = Role.ADMIN;
            updateRoleButtons();
        });

        add(userBtn);
        add(adminBtn);

        // --- Username Section ---
        JLabel userTitle = new JLabel("USERNAME");
        userTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTitle.setForeground(TEXT_DARK_RED);
        userTitle.setBounds(40, 240, 100, 20);
        add(userTitle);

        JTextField userField = new JTextField("e.g. username");
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userField.setForeground(Color.LIGHT_GRAY);
        userField.setBounds(40, 270, 320, 35);
        userField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        userField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (userField.getText().equals("e.g. username")) {
                    userField.setText("");
                    userField.setForeground(Color.BLACK);
                }
            }
        });
        add(userField);

        // --- Password Section ---
        JLabel passTitle = new JLabel("PASSWORD");
        passTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passTitle.setForeground(TEXT_DARK_RED);
        passTitle.setBounds(40, 325, 100, 20);
        add(passTitle);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(40, 355, 320, 35);
        passField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        add(passField);

        // --- Sign In Button ---
        JButton signInBtn = new JButton("Sign In  →");
        signInBtn.setBackground(Color.WHITE);
        signInBtn.setForeground(TURQUOISE_COLOR);
        signInBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signInBtn.setBorder(BorderFactory.createLineBorder(TURQUOISE_COLOR, 3));
        signInBtn.setBounds(40, 420, 320, 55);
        signInBtn.setFocusPainted(false);
        signInBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signInBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            Role role = authService.login(username, password);

            if (role != Role.NONE && role == selectedRole) {
                if (role == Role.USER) {
                    new UserDashboard(username).setVisible(true);
                } else {
                    new AdminDashboardFrame(username).setVisible(true);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials for " + selectedRole, "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(signInBtn);

        // --- Bottom Link ---
        footerLabel = new JLabel("Don't have an account? ");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBounds(90, 510, 150, 20);
        add(footerLabel);

        signUpLink = new JLabel("<html><u>Sign up</u></html>");
        signUpLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signUpLink.setForeground(TURQUOISE_COLOR);
        signUpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpLink.setBounds(245, 510, 60, 20);
        signUpLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new SignUpFrame().setVisible(true);
                dispose();
            }
        });
        add(signUpLink);

        updateRoleButtons();
    }

    private void updateRoleButtons() {
        if (selectedRole == Role.USER) {
            applySelectedStyle(userBtn);
            applyUnselectedStyle(adminBtn);
            footerLabel.setVisible(true);
            signUpLink.setVisible(true);
        } else {
            applySelectedStyle(adminBtn);
            applyUnselectedStyle(userBtn);
            footerLabel.setVisible(false);
            signUpLink.setVisible(false);
        }
    }

    // النمط عند الاختيار: خلفية رمادية فاتحة جداً لتمييزه + الحفاظ على الخط والإطار الفيروزي
    private void applySelectedStyle(JButton btn) {
        btn.setBackground(new Color(230, 247, 250)); // لون فيروزي فاتح جداً (خلفية)
        btn.setForeground(TURQUOISE_COLOR); // يبقى الخط فيروزي
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBorder(BorderFactory.createLineBorder(TURQUOISE_COLOR, 4)); // إطار أسمك شوي للتمييز
        btn.setFocusPainted(false);
    }

    // النمط عند عدم الاختيار: خلفية بيضاء سادة
    private void applyUnselectedStyle(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(TURQUOISE_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBorder(BorderFactory.createLineBorder(TURQUOISE_COLOR, 2)); // إطار أنحف
        btn.setFocusPainted(false);
    }
}