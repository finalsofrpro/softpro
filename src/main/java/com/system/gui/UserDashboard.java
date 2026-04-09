package com.system.gui;

import com.system.models.Appointment;
import org.example.Main;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;


 // تحديث واجهة المستخدم: إصلاح زر الإلغاء وتعديل الألوان

 public class UserDashboard extends JFrame {
    private final Color TURQUOISE_COLOR = new Color(0, 188, 212);
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> durationFilter;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String currentUsername;
    private boolean showOnlyMyBookings = false;

    public UserDashboard(String username) {
        this.currentUsername = username.trim(); // تنظيف الفراغات
        setTitle("User Dashboard - " + username);
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(TURQUOISE_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        JButton allAppsBtn = createMenuButton("All Appointments");
        JButton myBookingsBtn = createMenuButton("Booked by me");
        JButton logoutBtn = createMenuButton("Logout");

        allAppsBtn.addActionListener(e -> { showOnlyMyBookings = false; refreshTableData(); });
        myBookingsBtn.addActionListener(e -> { showOnlyMyBookings = true; refreshTableData(); });
        logoutBtn.addActionListener(e -> { new ModernLoginFrame().setVisible(true); dispose(); });

        sidebar.add(Box.createVerticalStrut(100));
        sidebar.add(allAppsBtn);
        sidebar.add(myBookingsBtn);
        sidebar.add(logoutBtn);
        add(sidebar, BorderLayout.WEST);

        // --- Main Panel ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Filter Area
        durationFilter = new JComboBox<>(new String[]{"All", "15", "30", "45", "60"});
        durationFilter.addActionListener(e -> refreshTableData());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(new JLabel("Filter Duration: "));
        topPanel.add(durationFilter);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table Configuration
        tableModel = new DefaultTableModel(new String[]{"ID", "Date & Time", "Duration", "Status", "Booked By"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setRowHeight(35);

        // تلوين عمود الحالة
        appointmentTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int col) {
                Component c = super.getTableCellRendererComponent(t, v, isSel, hasF, r, col);
                if ("BOOKED".equals(v)) {
                    c.setForeground(Color.RED);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    c.setForeground(new Color(0, 150, 0));
                    c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                }
                return c;
            }
        });

        mainPanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        // --- Bottom Buttons (Confirm & Cancel) ---
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setPreferredSize(new Dimension(0, 70));

        // تنسيق زر التأكيد حسب طلبك (خط فيروزي عريض)
        // داخل الكونسكرتور تبع UserDashboard
        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.setBackground(Color.WHITE);
        bookBtn.setForeground(TURQUOISE_COLOR); // خط فيروزي
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bookBtn.setBorder(BorderFactory.createLineBorder(TURQUOISE_COLOR, 3)); // إطار فيروزي عريض

        JButton cancelBtn = new JButton("Cancel Booking");
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setForeground(TURQUOISE_COLOR); // خط فيروزي
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cancelBtn.setBorder(BorderFactory.createLineBorder(TURQUOISE_COLOR, 3));

        bookBtn.addActionListener(e -> handleBooking());
        cancelBtn.addActionListener(e -> handleCancel());

        actionPanel.add(bookBtn);
        actionPanel.add(cancelBtn);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        refreshTableData();
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        String filter = (String) durationFilter.getSelectedItem();
        List<Appointment> apps = Main.repo.getAvailableAppointments();

        for (Appointment app : apps) {
            boolean durMatch = filter.equals("All") || String.valueOf(app.getDurationMinutes()).equals(filter);
            if (!durMatch) continue;

            if (showOnlyMyBookings) {
                // فحص دقيق للاسم والحالة
                if ("BOOKED".equalsIgnoreCase(app.getStatus()) && currentUsername.equalsIgnoreCase(app.getBookedBy())) {
                    addRow(app);
                }
            } else {
                addRow(app);
            }
        }
    }

    private void addRow(Appointment app) {
        tableModel.addRow(new Object[]{
                app.getId(),
                app.getDateTime().format(formatter),
                app.getDurationMinutes(),
                app.getStatus(),
                app.getBookedBy()
        });
    }

    private void handleBooking() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        for (Appointment app : Main.repo.getAvailableAppointments()) {
            if (app.getId() == id) {
                if ("AVAILABLE".equalsIgnoreCase(app.getStatus())) {
                    app.setStatus("BOOKED");
                    app.setBookedBy(currentUsername);
                    Main.repo.saveToFile();
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Booked Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Already Booked!");
                }
                return;
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
        // سحب اسم صاحب الموعد من الجدول مباشرة للتأكد
        String ownerInTable = (String) tableModel.getValueAt(row, 4);

        for (Appointment app : Main.repo.getAvailableAppointments()) {
            if (app.getId() == id) {
                // الفحص الجوهري: هل الحالة محجوزة وهل الاسم يطابق اسمك الحالي؟
                if ("BOOKED".equalsIgnoreCase(app.getStatus()) && currentUsername.equalsIgnoreCase(app.getBookedBy())) {
                    app.setStatus("AVAILABLE");
                    app.setBookedBy(""); // تصفير المالك
                    Main.repo.saveToFile();
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Booking Cancelled!");
                } else {
                    JOptionPane.showMessageDialog(this, "You can only cancel your own bookings!\n(Owner: " + ownerInTable + ")", "Access Denied", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }
        }
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }
}