package com.system.gui;

import com.system.services.AuthenticationService;
import org.example.Main;
import javax.swing.*;
import java.awt.*;

public class SignUpFrame extends JFrame {
    private final Color TURQUOISE_COLOR = new Color(0, 188, 212);
    private AuthenticationService authService = Main.authService;

    public SignUpFrame() {
        setTitle("Create New Account");
        setSize(450, 650); // زدنا الطول شوي عشان وسع حقل الإيميل
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // --- Header ---
        JLabel title = new JLabel("Sign Up");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setBounds(40, 30, 200, 40);
        add(title);

        // --- Username ---
        JLabel userLbl = new JLabel("USERNAME");
        styleLabel(userLbl, 100);
        add(userLbl);

        JTextField userField = new JTextField();
        userField.setBounds(40, 125, 350, 35);
        add(userField);

        // --- Password ---
        JLabel passLbl = new JLabel("PASSWORD");
        styleLabel(passLbl, 180);
        add(passLbl);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(40, 205, 350, 35);
        add(passField);

        // --- Email (الحقل الجديد) ---
        JLabel emailLbl = new JLabel("EMAIL ADDRESS");
        styleLabel(emailLbl, 260);
        add(emailLbl);

        JTextField emailField = new JTextField("example@mail.com");
        emailField.setForeground(Color.LIGHT_GRAY);
        emailField.setBounds(40, 285, 350, 35);
        // حركة لطيفة لتفريغ النص عند الضغط
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (emailField.getText().equals("example@mail.com")) {
                    emailField.setText("");
                    emailField.setForeground(Color.BLACK);
                }
            }
        });
        add(emailField);

        // --- Create Account Button (بنفس نمطكم الفخم) ---
        JButton signUpBtn = new JButton("Create Account");
        signUpBtn.setBackground(Color.WHITE);
        signUpBtn.setForeground(TURQUOISE_COLOR);
        signUpBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signUpBtn.setBorder(BorderFactory.createLineBorder(TURQUOISE_COLOR, 3));
        signUpBtn.setBounds(40, 380, 350, 55);
        signUpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signUpBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            String email = emailField.getText();

            if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            // استدعاء ميثود التسجيل المعدلة اللي بتستقبل 3 باراميترات
            boolean success = authService.registerNewUser(user, pass, email);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account Created! A welcome email will be sent to: " + email);
                new ModernLoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration Failed!\n- Check if username exists.\n- Use only letters, numbers, dots or underscores.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(signUpBtn);

        // --- Back to Login ---
        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setBounds(40, 450, 350, 30);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(Color.GRAY);
        backBtn.addActionListener(e -> {
            new ModernLoginFrame().setVisible(true);
            dispose();
        });
        add(backBtn);
    }

    private void styleLabel(JLabel lbl, int y) {
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(128, 0, 0)); // نفس اللون الأحمر الغامق اللي بملف اللوجن
        lbl.setBounds(40, y, 150, 20);
    }
}