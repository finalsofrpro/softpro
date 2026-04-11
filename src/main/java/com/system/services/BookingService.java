package com.system.services;

import com.system.models.Appointment;
import com.system.observers.NotificationObserver;
import com.system.repository.AppointmentRepository;
import com.system.strategies.BookingStrategy;
import com.system.strategies.FollowUpStrategy;
import com.system.strategies.UrgentStrategy;
import com.system.strategies.VirtualStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class that manages appointment booking and notifies observers.
 */
public class BookingService {
    private AppointmentRepository repository;
    private BookingStrategy strategy;
    private List<NotificationObserver> observers = new ArrayList<>();

    public BookingService(AppointmentRepository repository, BookingStrategy strategy)
    {
        this.repository = repository;
        this.strategy = strategy;
        // إضافة الـ EmailService تلقائياً كمراقب
        this.observers.add(new EmailService());

    }

    public void setStrategy(BookingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Attempts to book an appointment and notifies users via Observers.
     */
    // داخل كلاس BookingService.java

    // داخل BookingService.java

    public void cancel(Appointment appointment, String userEmail) {
        appointment.setStatus("AVAILABLE");
        appointment.setBookedBy(""); // مسح الإيميل عشان يرجع الموعد فاضي
        repository.saveToFile();

        // نص الإيميل عند الإلغاء
        String content = "مرحباً،\n\nتم إلغاء موعدك بنجاح.\n" +
                "تفاصيل الموعد الملغى:\n" +
                "- النوع: " + appointment.getType() + "\n" +
                "- الوقت: " + appointment.getDateTime().toString().replace("T", " ") + "\n" +
                "نتمنى رؤيتك قريباً!";

        notifyObservers(userEmail, content);
    }

    public boolean book(Appointment appointment, String userEmail) {
        if (strategy.isValid(appointment)) {
            appointment.setStatus("BOOKED");
            appointment.setBookedBy(userEmail);
            repository.saveToFile();

            // نص الإيميل عند الحجز مع التفاصيل
            String content = "تم تأكيد حجز موعدك!\n" +
                    "التفاصيل:\n" +
                    "- النوع: " + appointment.getType() + "\n" +
                    "- الوقت: " + appointment.getDateTime().toString().replace("T", " ") + "\n" +
                    "- المدة: " + appointment.getDurationMinutes() + " دقيقة.";

            notifyObservers(userEmail, content);
            return true;
        }
        return false;
    }
    private void notifyObservers(String email, String message) {
        for (NotificationObserver observer : observers) {
            observer.update(email, message);
        }
    }
}