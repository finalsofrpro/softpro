package com.system.gui;

import com.system.models.Appointment;
import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import org.example.Main;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AdminDashboardFrame extends JFrame {
    private final Color TURQUOISE_COLOR = new Color(0, 188, 212);
    private AuthenticationService authService = Main.authService;
    private AppointmentRepository appointmentRepo = new AppointmentRepository();
    private JPanel contentPanel;
    private String currentSubMode = "ADD";

    public AdminDashboardFrame(String adminName) {
        setTitle("Admin Panel - " + adminName);
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // الجانب الأيسر
        JPanel sidePanel = new JPanel(new GridLayout(8, 1, 10, 10));
        sidePanel.setBackground(TURQUOISE_COLOR);
        sidePanel.setPreferredSize(new Dimension(230, 750));

        JLabel logo = new JLabel("ADMIN PANEL", SwingConstants.CENTER);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        sidePanel.add(logo);

        JButton btnManageApp = createMenuButton("Manage Appointments");
        JButton btnViewSlots = createMenuButton("View All Slots");
        JButton btnManageUsers = createMenuButton("Manage Users");
        JButton btnAddAdmin = createMenuButton("Add New Admin");
        JButton btnLogout = new JButton("Logout");

        btnManageApp.addActionListener(e -> showManageAppointments());
        btnViewSlots.addActionListener(e -> showAllAppointments());
        btnManageUsers.addActionListener(e -> showManageUsers());
        btnAddAdmin.addActionListener(e -> showAddAdminDialog());
        btnLogout.addActionListener(e -> { new ModernLoginFrame().setVisible(true); dispose(); });

        sidePanel.add(btnManageApp);
        sidePanel.add(btnViewSlots);
        sidePanel.add(btnManageUsers);
        sidePanel.add(btnAddAdmin);
        sidePanel.add(new JLabel(""));
        sidePanel.add(btnLogout);

        add(sidePanel, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        showWelcome(adminName);
    }

    // ميثود لتنسيق الأزرار (الخط الكتكوت/الوسط)
    private void applyStyledButton(JButton btn, int fontSize, int borderWeight) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(TURQUOISE_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBorder(BorderFactory.createLineBorder(TURQUOISE_COLOR, borderWeight));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showManageAppointments() {
        contentPanel.removeAll();

        // أزرار المود (Add, Edit, Delete) بخط كتكوت (14)
        JPanel togglePanel = new JPanel(new GridLayout(1, 3, 20, 0));
        togglePanel.setBackground(Color.WHITE);
        togglePanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));

        JButton addMode = new JButton("Add Mode");
        JButton editMode = new JButton("Edit Mode");
        JButton deleteMode = new JButton("Delete Mode");

        applyStyledButton(addMode, 14, 2);
        applyStyledButton(editMode, 14, 2);
        applyStyledButton(deleteMode, 14, 2);

        addMode.addActionListener(e -> { currentSubMode = "ADD"; showManageAppointments(); });
        editMode.addActionListener(e -> { currentSubMode = "EDIT"; showManageAppointments(); });
        deleteMode.addActionListener(e -> { currentSubMode = "DELETE"; showManageAppointments(); });

        togglePanel.add(addMode); togglePanel.add(editMode); togglePanel.add(deleteMode);
        contentPanel.add(togglePanel, BorderLayout.NORTH);

        JPanel workArea = new JPanel(new GridLayout(8, 1, 10, 10));
        workArea.setBackground(Color.WHITE);
        workArea.setBorder(BorderFactory.createEmptyBorder(20, 150, 50, 150));

        JTextField idF = new JTextField();
        JTextField dateF = new JTextField("2026-04-15 10:00");
        JTextField durF = new JTextField("30");

        JButton actionBtn = new JButton("Confirm " + currentSubMode);
        applyStyledButton(actionBtn, 20, 3); // خط أكبر للأكشن الأساسي

        if (currentSubMode.equals("ADD")) {
            workArea.add(new JLabel("Enter New ID:")); workArea.add(idF);
            workArea.add(new JLabel("Time (yyyy-MM-dd HH:mm):")); workArea.add(dateF);
            workArea.add(new JLabel("Duration (Min):")); workArea.add(durF);
        } else if (currentSubMode.equals("EDIT")) {
            workArea.add(new JLabel("Target ID (Cannot be changed):")); workArea.add(idF);
            workArea.add(new JLabel("New Time:")); workArea.add(dateF);
            workArea.add(new JLabel("New Duration:")); workArea.add(durF);
        } else {
            workArea.add(new JLabel("Enter ID to Delete:")); workArea.add(idF);
            workArea.add(new JLabel("")); workArea.add(new JLabel("Warning: This ID will be removed from system."));
        }

        workArea.add(actionBtn);
        actionBtn.addActionListener(e -> handleAppAction(idF.getText(), dateF.getText(), durF.getText()));

        contentPanel.add(workArea, BorderLayout.CENTER);
        refresh();
    }

    private void handleAppAction(String idS, String dateS, String durS) {
        try {
            int id = Integer.parseInt(idS);
            boolean exists = appointmentRepo.getAvailableAppointments().stream().anyMatch(a -> a.getId() == id);

            if (currentSubMode.equals("ADD")) {
                LocalDateTime dt = LocalDateTime.parse(dateS, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                appointmentRepo.addAppointment(new Appointment(id, dt, Integer.parseInt(durS)));
                JOptionPane.showMessageDialog(this, "Appointment Added Successfully!");
            } else if (currentSubMode.equals("EDIT")) {
                if (!exists) {
                    JOptionPane.showMessageDialog(this, "Error: Appointment ID not found!", "Fail", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // تعديل الموعد مع الحفاظ على نفس الـ ID
                LocalDateTime dt = LocalDateTime.parse(dateS, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                appointmentRepo.deleteAppointment(id); // نحذف القديم
                appointmentRepo.addAppointment(new Appointment(id, dt, Integer.parseInt(durS))); // نضيف الجديد بنفس الـ ID
                JOptionPane.showMessageDialog(this, "Appointment Updated!");
            } else if (currentSubMode.equals("DELETE")) {
                if (!exists) {
                    JOptionPane.showMessageDialog(this, "Error: Appointment ID not found!", "Fail", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                appointmentRepo.deleteAppointment(id);
                JOptionPane.showMessageDialog(this, "Appointment Deleted!");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void showManageUsers() {
        contentPanel.removeAll();
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(Color.WHITE);

        String[] cols = {"User Name", "Account Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        ArrayList<String> allUsernames = new ArrayList<>();

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 1) {
                    model.addRow(new Object[]{p[0], "CLIENT"});
                    allUsernames.add(p[0]);
                }
            }
        } catch (Exception e) { }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        userPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel deleteUserArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        deleteUserArea.setBackground(Color.WHITE);

        JLabel label = new JLabel("Enter Username to Delete:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16)); // تكبير الجملة شوي

        JTextField deleteUserField = new JTextField(15);
        deleteUserField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton btnDeleteUser = new JButton("Delete User");
        applyStyledButton(btnDeleteUser, 18, 2); // خط وسط كتكوت (18)

        deleteUserArea.add(label);
        deleteUserArea.add(deleteUserField);
        deleteUserArea.add(btnDeleteUser);

        btnDeleteUser.addActionListener(e -> {
            String target = deleteUserField.getText().trim();
            if(!allUsernames.contains(target)) {
                JOptionPane.showMessageDialog(this, "User '" + target + "' not found in system!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                deleteUserFromFile(target);
                showManageUsers();
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            }
        });

        userPanel.add(deleteUserArea, BorderLayout.SOUTH);
        contentPanel.add(userPanel, BorderLayout.CENTER);
        refresh();
    }

    // بقية الميثودات (deleteUserFromFile, showAllAppointments, etc.) تبقى كما هي مع تحديث Refresh
    private void deleteUserFromFile(String username) {
        java.util.List<String> lines = new ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(username + ",")) lines.add(line);
            }
        } catch (Exception e) { }
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("users.txt"))) {
            for (String l : lines) pw.println(l);
        } catch (Exception e) { }
    }

    private void showWelcome(String name) {
        contentPanel.removeAll();
        JLabel lbl = new JLabel("Welcome back, " + name, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        contentPanel.add(lbl, BorderLayout.CENTER);
        refresh();
    }

    private void showAllAppointments() {
        contentPanel.removeAll();
        String[] cols = {"ID", "Time", "Duration", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Appointment a : appointmentRepo.getAvailableAppointments()) {
            model.addRow(new Object[]{a.getId(), a.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), a.getDurationMinutes(), a.getStatus()});
        }
        JTable t = new JTable(model); t.setRowHeight(25);
        contentPanel.add(new JScrollPane(t), BorderLayout.CENTER);
        refresh();
    }

    private void showAddAdminDialog() {
        JTextField u = new JTextField(); JPasswordField p = new JPasswordField();
        Object[] m = {"User:", u, "Pass:", p};
        if (JOptionPane.showConfirmDialog(this, m, "Add Admin", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            authService.registerNewAdmin(u.getText(), new String(p.getPassword()));
        }
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(TURQUOISE_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private void refresh() { contentPanel.revalidate(); contentPanel.repaint(); }
}