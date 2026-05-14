package com.system.gui;

import com.system.models.Appointment;
import com.system.services.BookingService;
import org.example.Main;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * User Dashboard interface for managing personal appointments.
 * Features include visual status indicators (Green/Red), filtering, and booking actions.
 * * @author Raghad and Farah
 * @version 1.0
 */
public class UserDashboard extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(UserDashboard.class.getName());
    private final Color TURQUOISE_COLOR = new Color(0, 188, 212);
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> durationFilter;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String currentUsername;
    private boolean showOnlyMyBookings = false;

    public UserDashboard(String username) {
        Main.repo.loadFromFile();
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
        topPanel.setBackground(Color.WHITE);
        topPanel.add(new JLabel("Filter Duration (Min): ")); topPanel.add(durationFilter);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Date & Time", "Duration", "Type", "Status", "Booked By"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setRowHeight(35);

        appointmentTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("AVAILABLE".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(0, 150, 0));
                } else if ("BOOKED".equalsIgnoreCase(status)) {
                    c.setForeground(Color.RED);
                }
                return c;
            }
        });

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
        b.setFocusPainted(false);
    }

    private void refreshTableData() {
        Main.repo.loadFromFile();
        tableModel.setRowCount(0);
        String filter = (String) durationFilter.getSelectedItem();

        for (Appointment app : Main.repo.getAvailableAppointments()) {
            boolean durMatch = "All".equals(filter) || String.valueOf(app.getDurationMinutes()).equals(filter);
            if (durMatch) addRow(app);
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
            if (Main.bookingService.book(selected, currentUsername + "@gmail.com")) {
                JOptionPane.showMessageDialog(this, "Booking Successful!");
                refreshTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Booking Failed: This type has special rules (e.g., Urgent max 15 mins).");
            }
        }
    }

    private void handleCancel() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        Appointment selected = Main.repo.getAvailableAppointments().stream().filter(a -> a.getId() == id).findFirst().orElse(null);

        if (selected != null && "BOOKED".equalsIgnoreCase(selected.getStatus())) {
            Main.bookingService.cancel(selected, currentUsername + "@gmail.com");
            refreshTableData();
            JOptionPane.showMessageDialog(this, "Cancelled!");
        }
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}