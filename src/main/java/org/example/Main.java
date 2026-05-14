package org.example;

import com.system.gui.ModernLoginFrame;
import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import com.system.services.BookingService;
import com.system.services.EmailService;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Appointment System.
 * Configured for Phase 2 Static Analysis and Persistence.
 * @author Raghad and Farah
 */
public class Main {

    // استخدام الـ Repository مع تفعيل الملفات لضمان حفظ البيانات
    public static final AppointmentRepository repo = new AppointmentRepository(true);
    public static AuthenticationService authService = new AuthenticationService();
    public static BookingService bookingService = new BookingService(repo, appointment -> true);

    public static void main(String[] args) {
        // تحميل المواعيد من الملف فور تشغيل البرنامج
        repo.loadFromFile();

        // ربط الـ Observer لإرسال الإيميلات (مطلب أساسي في الفيز 2)
        bookingService.addObserver(new EmailService());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> {
            new ModernLoginFrame().setVisible(true);
        });
    }
}