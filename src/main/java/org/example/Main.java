package org.example;

import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import com.system.services.BookingService;
import com.system.services.EmailService;
import com.system.gui.ModernLoginFrame;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Entry point.
 * Robust configuration for Phase 2: Observer pattern, Persistence, and Logger.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static final AppointmentRepository repo = new AppointmentRepository(true);
    public static final AuthenticationService authService = new AuthenticationService();
    public static final BookingService bookingService = new BookingService(repo, app -> true);

    public static void main(String[] args) {
        repo.loadFromFile();

        EmailService emailService = new EmailService();

        authService.addObserver(emailService);

        bookingService.addObserver(emailService);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to set UI LookAndFeel", e);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new ModernLoginFrame().setVisible(true);
                LOGGER.log(Level.INFO, "System started successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Critical error during startup", e);
            }
        });
    }
}