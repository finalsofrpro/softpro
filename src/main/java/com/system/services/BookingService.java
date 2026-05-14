package com.system.services;

import com.system.models.Appointment;
import com.system.observers.NotificationObserver;
import com.system.repository.AppointmentRepository;
import com.system.strategies.BookingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class responsible for managing appointment booking and cancellation operations.
 * @author Raghad and Farah
 * @version 1.2
 */
public class BookingService {
    private static final Logger LOGGER = Logger.getLogger(BookingService.class.getName());
    private static final String STATUS_AVAILABLE = "AVAILABLE";
    private static final String STATUS_BOOKED = "BOOKED";
    private static final String TYPE_URGENT = "Urgent";

    private AppointmentRepository repository;
    private BookingStrategy strategy;
    private List<NotificationObserver> observers = new ArrayList<>();

    public BookingService(AppointmentRepository repository, BookingStrategy strategy) {
        this.repository = repository;
        this.strategy = strategy;
    }

    public void addObserver(NotificationObserver observer) {
        this.observers.add(observer);
    }

    public void setStrategy(BookingStrategy strategy) {
        this.strategy = strategy;
    }

    public void cancel(Appointment appointment, String userEmail) {
        appointment.setStatus(STATUS_AVAILABLE);
        appointment.setBookedBy("");
        repository.saveToFile();

        String content = buildCancelMessage(appointment);
        notifyObservers(userEmail, content);
        LOGGER.log(Level.INFO, "Appointment {0} cancelled for {1}", new Object[]{appointment.getId(), userEmail});
    }

    public boolean book(Appointment appointment, String userEmail) {
        if (!STATUS_AVAILABLE.equals(appointment.getStatus())) {
            return false;
        }

        // ✅ قاعدة الـ 15 دقيقة
        if (TYPE_URGENT.equalsIgnoreCase(appointment.getType()) && appointment.getDurationMinutes() > 15) {
            LOGGER.log(Level.WARNING, "Booking failed: Urgent appointment exceeds 15 minutes.");
            return false;
        }

        if (strategy.isValid(appointment)) {
            appointment.setStatus(STATUS_BOOKED);
            appointment.setBookedBy(userEmail);
            repository.saveToFile();

            String content = buildBookingMessage(appointment);
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

    private String buildBookingMessage(Appointment appointment) {
        return "Your booking is confirmed!\n"
                + "Details:\n"
                + "- Type: " + appointment.getType() + "\n"
                + "- Time: " + formatDate(appointment) + "\n"
                + "- Duration: " + appointment.getDurationMinutes() + " minutes.";
    }

    private String buildCancelMessage(Appointment appointment) {
        return "Hello,\n\nYour appointment has been successfully cancelled.\n"
                + "Details:\n"
                + "- Type: " + appointment.getType() + "\n"
                + "- Time: " + formatDate(appointment);
    }

    private String formatDate(Appointment appointment) {
        return appointment.getDateTime().toString().replace("T", " ");
    }
}