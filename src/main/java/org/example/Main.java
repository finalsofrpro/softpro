package org.example;

import com.system.gui.ModernLoginFrame;
import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import com.system.services.BookingService;
import com.system.services.EmailService;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for the Appointment System.
 * Configured for Phase 2 Static Analysis and Persistence.
 * @author Raghad and Farah
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static final AppointmentRepository repo = new AppointmentRepository(true);
    public static AuthenticationService authService = new AuthenticationService();
    public static BookingService bookingService = new BookingService(repo, appointment -> true);

    public static void main(String[] args) {
        repo.loadFromFile();
        bookingService.addObserver(new EmailService());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to set UI LookAndFeel", e);
        }

        SwingUtilities.invokeLater(() -> new ModernLoginFrame().setVisible(true));
    }
}