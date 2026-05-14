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

    // تعريف الكائنات كـ static لضمان مشاركتها بين كل الشاشات
    public static final AppointmentRepository repo = new AppointmentRepository(true);
    public static final AuthenticationService authService = new AuthenticationService();
    // ربط الـ BookingService بالـ repo مباشرة مع استراتيجية بسيطة
    public static final BookingService bookingService = new BookingService(repo, app -> true);

    public static void main(String[] args) {
        // 1. تحميل البيانات من الملفات فور تشغيل البرنامج
        repo.loadFromFile();

        // 2. تعريف خدمة الإيميل (Observer)
        EmailService emailService = new EmailService();

        // 3. ✅ ربط الإيميل بـ Authentication (عشان يوصل إيميل ترحيبي وقت الـ Sign up)
        authService.addObserver(emailService);

        // 4. ✅ ربط الإيميل بـ BookingService (عشان يوصل إيميل وقت الحجز والإلغاء)
        bookingService.addObserver(emailService);

        // 5. ضبط شكل الواجهات (Look and Feel) مع معالجة الأخطاء للسونار
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to set UI LookAndFeel", e);
        }

        // 6. تشغيل واجهة الدخول
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