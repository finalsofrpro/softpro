package com.system.gui;

import com.system.models.Appointment;
import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import com.system.services.EmailService;
import org.example.Main;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Advanced Admin Dashboard for managing system appointments.
 * Features automated notifications when appointments are cancelled by the admin.
 * @author Raghad and Farah
 * @version 1.2
 */
public class AdminDashboardFrame extends JFrame {
    private final Color TURQUOISE_COLOR = new Color(0, 188, 212);
    private transient AuthenticationService authService = Main.authService;
    // رجعنا للتعريف الأصلي المستقر
    private AppointmentRepository appointmentRepo = Main.repo;
    private JPanel contentPanel;
    private String currentSubMode = "ADD";

    public AdminDashboardFrame(String adminName) {
        setTitle("Admin Panel - " + adminName);
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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

        JPanel workArea = new JPanel(new BorderLayout(10, 10));
        workArea.setBackground(Color.WHITE);

        JPanel inputFields = new JPanel(new GridLayout(0, 1, 5, 5));
        inputFields.setBackground(Color.WHITE);
        inputFields.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 150));

        JTextField idF = new JTextField();
        JTextField dateF = new JTextField("2026-04-15 10:00");
        JTextField durF = new JTextField("30");
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"General", "Urgent", "Virtual"});

        if (currentSubMode.equals("ADD")) {
            inputFields.add(new JLabel("Enter New ID:")); inputFields.add(idF);
            inputFields.add(new JLabel("Time (yyyy-MM-dd HH:mm):")); inputFields.add(dateF);
            inputFields.add(new JLabel("Duration (Min):")); inputFields.add(durF);
            inputFields.add(new JLabel("Type:")); inputFields.add(typeBox);
        } else if (currentSubMode.equals("EDIT")) {
            inputFields.add(new JLabel("Target ID:")); inputFields.add(idF);
            inputFields.add(new JLabel("New Time:")); inputFields.add(dateF);
            inputFields.add(new JLabel("New Duration:")); inputFields.add(durF);
            inputFields.add(new JLabel("New Type:")); inputFields.add(typeBox);
        } else {
            inputFields.add(new JLabel("ID to Delete:")); inputFields.add(idF);
        }

        JButton actionBtn = new JButton("Confirm " + currentSubMode);
        applyStyledButton(actionBtn, 20, 3);
        inputFields.add(new JLabel(""));
        inputFields.add(actionBtn);
        actionBtn.addActionListener(e -> handleAppAction(idF.getText(), dateF.getText(), durF.getText(), typeBox.getSelectedItem().toString()));

        workArea.add(inputFields, BorderLayout.NORTH);

        String[] cols = {"ID", "Time", "Duration", "Type", "Status", "Booked By"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Appointment a : appointmentRepo.getAvailableAppointments()) {
            model.addRow(new Object[]{a.getId(), a.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    a.getDurationMinutes(), a.getType(), a.getStatus(), a.getBookedBy()});
        }
        JTable previewTable = new JTable(model);

        // ربط الجدول بالحقول (النسخة اللي طلبتيها للتسهيل)
        previewTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && previewTable.getSelectedRow() != -1) {
                int row = previewTable.getSelectedRow();
                idF.setText(model.getValueAt(row, 0).toString());
                dateF.setText(model.getValueAt(row, 1).toString());
                durF.setText(model.getValueAt(row, 2).toString());
                typeBox.setSelectedItem(model.getValueAt(row, 3).toString());
            }
        });

        workArea.add(new JScrollPane(previewTable), BorderLayout.CENTER);
        contentPanel.add(workArea, BorderLayout.CENTER);
        refresh();
    }

    private void handleAppAction(String idS, String dateS, String durS, String typeS) {
        try {
            int id = Integer.parseInt(idS);
            int duration = currentSubMode.equals("DELETE") ? 0 : Integer.parseInt(durS);

            // نجيب الموعد القديم
            Appointment oldAppointment = appointmentRepo
                    .getAvailableAppointments()
                    .stream()
                    .filter(a -> a.getId() == id)
                    .findFirst()
                    .orElse(null);

            // ✅ validation للمدة حسب النوع
            if (currentSubMode.equals("ADD") || currentSubMode.equals("EDIT")) {

                if (typeS.equalsIgnoreCase("Urgent") && duration > 15) {
                    JOptionPane.showMessageDialog(this, "❌ Urgent max is 15 minutes");
                    return;
                }

                if (typeS.equalsIgnoreCase("General") && duration > 30) {
                    JOptionPane.showMessageDialog(this, "❌ General max is 30 minutes");
                    return;
                }

                if (typeS.equalsIgnoreCase("Virtual") && duration > 60) {
                    JOptionPane.showMessageDialog(this, "❌ Virtual max is 60 minutes");
                    return;
                }
            }

            if (currentSubMode.equals("ADD")) {

                LocalDateTime dt = LocalDateTime.parse(dateS,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                appointmentRepo.addAppointment(new Appointment(id, dt, duration, 1, typeS));

            } else if (currentSubMode.equals("DELETE")) {

                if (oldAppointment != null && "BOOKED".equalsIgnoreCase(oldAppointment.getStatus())) {
                    String email = oldAppointment.getBookedBy();

                    new EmailService().sendEmail(
                            email,
                            "Appointment Cancelled",
                            "Hello,\n\nYour appointment has been cancelled.\n" +
                                    "Details:\n" +
                                    "- Type: " + oldAppointment.getType() + "\n" +
                                    "- Time: " + oldAppointment.getDateTime().toString().replace("T", " ")
                    );
                }

                appointmentRepo.deleteAppointment(id);

            } else if (currentSubMode.equals("EDIT")) {

                String email = "";
                boolean wasBooked = false;

                if (oldAppointment != null && "BOOKED".equalsIgnoreCase(oldAppointment.getStatus())) {
                    email = oldAppointment.getBookedBy();
                    wasBooked = true;
                }

                appointmentRepo.deleteAppointment(id);

                LocalDateTime dt = LocalDateTime.parse(dateS,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                Appointment updated = new Appointment(id, dt, duration, 1, typeS);

                // إذا كان محجوز نحافظ عليه
                if (wasBooked) {
                    updated.setStatus("BOOKED");
                    updated.setBookedBy(email);

                    new EmailService().sendEmail(
                            email,
                            "Appointment Updated",
                            "Hello,\n\nYour appointment has been updated.\n" +
                                    "New Details:\n" +
                                    "- Type: " + typeS + "\n" +
                                    "- Time: " + dt.toString().replace("T", " ") + "\n" +
                                    "- Duration: " + duration + " minutes."
                    );
                }

                appointmentRepo.addAppointment(updated);
            }

            appointmentRepo.saveToFile();
            JOptionPane.showMessageDialog(this, "Success!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        showManageAppointments();
    }

    private void showManageUsers() {
        contentPanel.removeAll();
        JPanel userPanel = new JPanel(new BorderLayout());
        String[] cols = {"User Name", "Account Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 1) model.addRow(new Object[]{p[0], "CLIENT"});
            }
        } catch (Exception e) { }
        userPanel.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        contentPanel.add(userPanel, BorderLayout.CENTER);
        refresh();
    }

    private void showAllAppointments() {
        contentPanel.removeAll();
        String[] cols = {"ID", "Time", "Duration", "Type", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Appointment a : appointmentRepo.getAvailableAppointments()) {
            model.addRow(new Object[]{a.getId(), a.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), a.getDurationMinutes(), a.getType(), a.getStatus()});
        }
        contentPanel.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        refresh();
    }

    private void showWelcome(String name) {
        contentPanel.removeAll();
        JLabel lbl = new JLabel("Welcome back, " + name, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        contentPanel.add(lbl, BorderLayout.CENTER);
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
        btn.setForeground(Color.WHITE); btn.setBackground(TURQUOISE_COLOR);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void refresh() { contentPanel.revalidate(); contentPanel.repaint(); }
}