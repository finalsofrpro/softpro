package org.example;

import com.system.gui.ModernLoginFrame;
import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import com.system.services.BookingService;
import com.system.services.EmailService;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The main entry point for the Appointment Scheduling System.
 * This class initializes all core components, including repositories and services,
 * and sets up the observer pattern for notifications.
 * * @author Raghad and Farah
 * @version 1.0
 */
public class Main {

    /** Global repository for appointment data management. */
    public static final AppointmentRepository repo = new AppointmentRepository();

    /** Global service for user authentication and account management. */
    public static AuthenticationService authService = new AuthenticationService();

    /** * Global booking service initialized with a flexible strategy
     * that accepts all appointment types for testing purposes.
     */
    public static BookingService bookingService = new BookingService(repo, appointment -> true);

    /**
     * Main method that launches the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // 1. Load existing appointment records from storage
        repo.loadFromFile();

        // 2. CRITICAL: Register the EmailService as an observer to enable email notifications
        bookingService.addObserver(new EmailService());

        // 3. Set the system look and feel for the GUI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Start the application by displaying the login frame
        SwingUtilities.invokeLater(() -> {
            new ModernLoginFrame().setVisible(true);
        });
    }
}