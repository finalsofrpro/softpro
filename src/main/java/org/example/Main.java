package org.example;

import com.system.gui.ModernLoginFrame;
import com.system.models.Appointment;
import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import com.system.services.BookingService;
import com.system.strategies.UrgentStrategy; // استيراد الاستراتيجية المحدثة

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.time.LocalDateTime;

public class Main {
    // 1. تهيئة المستودع والخدمات (Static لسهولة الوصول إليها من كل الشاشات)
    public static AppointmentRepository repo = new AppointmentRepository();
    public static AuthenticationService authService = new AuthenticationService();

    // 2. تحديث الـ Strategy هنا: نبدأ بـ UrgentStrategy أو أي استراتيجية افتراضية
    public static BookingService bookingService = new BookingService(repo, new UrgentStrategy());

    public static void main(String[] args) {
        // 3. تحميل البيانات من الملف فور بدء البرنامج
        repo.loadFromFile();

        // 4. تحسين شكل الواجهة
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5. تشغيل واجهة الدخول
        SwingUtilities.invokeLater(() -> {
            new ModernLoginFrame().setVisible(true);
        });
    }
}