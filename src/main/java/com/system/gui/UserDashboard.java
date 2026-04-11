package com.system.gui;

import com.system.models.Appointment;
import org.example.Main;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserDashboard extends JFrame {
    private final Color TURQUOISE_COLOR = new Color(0, 188, 212);
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> durationFilter;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String currentUsername;
    private boolean showOnlyMyBookings = false;

    public UserDashboard(String username) {
        this.currentUsername = username.trim();
        setTitle("User Dashboard - " + username);
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(TURQUOISE_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        JButton allAppsBtn = createMenuButton("All Appointments");
        JButton myBookingsBtn = createMenuButton("My Bookings");
        JButton logoutBtn = createMenuButton("Logout");

        allAppsBtn.addActionListener(e -> { showOnlyMyBookings = false; refreshTableData(); });
        myBookingsBtn.addActionListener(e -> { showOnlyMyBookings = true; refreshTableData(); });
        logoutBtn.addActionListener(e -> { new ModernLoginFrame().setVisible(true); dispose(); });

        sidebar.add(Box.createVerticalStrut(100));
        sidebar.add(allAppsBtn); sidebar.add(myBookingsBtn); sidebar.add(logoutBtn);
        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        durationFilter = new JComboBox<>(new String[]{"All", "15", "30", "45", "60"});
        durationFilter.addActionListener(e -> refreshTableData());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter Duration: ")); topPanel.add(durationFilter);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Date & Time", "Duration", "Type", "Status", "Booked By"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setRowHeight(35);
        mainPanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton bookBtn = new JButton("Confirm Booking");
        JButton cancelBtn = new JButton("Cancel Booking");

        formatBtn(bookBtn); formatBtn(cancelBtn);

        bookBtn.addActionListener(e -> handleBooking());
        cancelBtn.addActionListener(e -> handleCancel());

        actionPanel.add(bookBtn); actionPanel.add(cancelBtn);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        refreshTableData();
    }

    private void formatBtn(JButton b) {
        b.setForeground(TURQUOISE_COLOR);
        b.setFont(new Font("Segoe UI", Font.BOLD, 22));
        b.setBorder(BorderFactory.createLineBorder(TURQUOISE_COLOR, 3));
        b.setBackground(Color.WHITE);
    }



    private void refreshTableData() {
        tableModel.setRowCount(0);
        String filter = (String) durationFilter.getSelectedItem();
        String userEmail = getUserEmail(currentUsername); // نحتاج الإيميل هنا

        for (Appointment app : Main.repo.getAvailableAppointments()) {
            boolean durMatch = filter.equals("All") || String.valueOf(app.getDurationMinutes()).equals(filter);
            if (!durMatch) continue;

            if (showOnlyMyBookings) {
                // نقارن الإيميل بالإيميل
                if ("BOOKED".equalsIgnoreCase(app.getStatus()) && userEmail.equalsIgnoreCase(app.getBookedBy())) {
                    addRow(app);
                }
            } else {
                addRow(app);
            }
        }
    }

    private void addRow(Appointment app) {
        tableModel.addRow(new Object[]{app.getId(), app.getDateTime().format(formatter), app.getDurationMinutes(), app.getType(), app.getStatus(), app.getBookedBy()});
    }

    private void handleBooking() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        Appointment selected = Main.repo.getAvailableAppointments().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
        if (selected != null && "AVAILABLE".equalsIgnoreCase(selected.getStatus())) {
            String email = getUserEmail(currentUsername);
            if (Main.bookingService.book(selected, email)) {
                JOptionPane.showMessageDialog(this, "Success!");
                refreshTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Rules not met for " + selected.getType());
            }
        }
    }

    private void handleCancel() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        Appointment selected = Main.repo.getAvailableAppointments().stream()
                .filter(a -> a.getId() == id).findFirst().orElse(null);

        // الخطوة الأهم: جلب إيميل المستخدم الحالي للمقارنة
        String userEmail = getUserEmail(currentUsername);

        // نقارن إيميل المستخدم الحالي مع الإيميل المخزن في خانة BookedBy
        if (selected != null && "BOOKED".equalsIgnoreCase(selected.getStatus()) &&
                userEmail.equalsIgnoreCase(selected.getBookedBy())) {

            // استدعاء خدمة الإلغاء
            Main.bookingService.cancel(selected, userEmail);

            refreshTableData();
            JOptionPane.showMessageDialog(this, "Booking Cancelled! Email sent to: " + userEmail);
        } else {
            JOptionPane.showMessageDialog(this, "You can only cancel your own bookings!");
        }
    }

    private String getUserEmail(String username) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 3 && p[0].trim().equalsIgnoreCase(username)) return p[2].trim();
            }
        } catch (Exception e) {}
        return username + "@gmail.com";
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return btn;
    }
}