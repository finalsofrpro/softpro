package org.example;


import com.system.gui.ModernLoginFrame;
import com.system.models.Appointment;
import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import com.system.services.BookingService;
import com.system.strategies.RangeDurationStrategy;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.time.LocalDateTime;

// كلاس التشغيل الرئيسي المحدث ليعمل بواجهة رسومية (GUI)

public class Main {
    // جعل الخدمات static لتمكين الوصول إليها من الشاشات المختلفة إذا لزم الأمر
    public static AppointmentRepository repo = new AppointmentRepository();
    public static AuthenticationService authService = new AuthenticationService();
    public static BookingService bookingService = new BookingService(repo, new RangeDurationStrategy());

    public static void main(String[] args) {
        // 1. تهيئة بعض المواعيد الابتدائية (اختياري، لأن الملف سيحمل المواعيد القديمة)
        if (repo.getAvailableAppointments().isEmpty()) {
            repo.addAppointment(new Appointment(1, LocalDateTime.now().plusDays(1).withHour(10).withMinute(0), 30));
        }

        // 2. تحسين شكل الواجهة لتناسب نظام التشغيل
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. تشغيل شاشة الدخول الفيروزية (GUI)
        SwingUtilities.invokeLater(() -> {
            new ModernLoginFrame().setVisible(true);
        });

        /* ملاحظة: الكود القديم (Scanner) تم إيقافه ليعمل النظام بالواجهات الرسومية.
           كل الوظائف (إضافة موعد، حجز، إلخ) سنقوم بنقلها لأزرار داخل الـ Dashboards.
        */
    }
}