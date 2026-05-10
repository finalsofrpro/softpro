package com.system.services;

import com.system.models.Appointment;
import com.system.observers.NotificationObserver;
import com.system.repository.AppointmentRepository;
import com.system.strategies.BookingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing appointment booking and cancellation operations.
 * It coordinates between the repository layer, booking validation strategies,
 * and notification observers (e.g., email services).
 *
 * <p>This class follows the Strategy pattern for flexible booking validation
 * and the Observer pattern for sending notifications.</p>
 *
 * @author Raghad and Farah
 * @version 1.0
 */
public class BookingService {

    // ✅ Constants to avoid magic strings
    private static final String STATUS_AVAILABLE = "AVAILABLE";
    private static final String STATUS_BOOKED = "BOOKED";

    /** Repository responsible for storing and retrieving appointments */
    private AppointmentRepository repository;

    /** Strategy used to validate booking rules */
    private BookingStrategy strategy;

    /** List of observers to be notified on booking/cancellation events */
    private List<NotificationObserver> observers = new ArrayList<>();

    /**
     * Constructs a BookingService with a specific repository and booking strategy.
     *
     * @param repository the repository used for data persistence
     * @param strategy the strategy used to validate booking rules
     */
    public BookingService(AppointmentRepository repository, BookingStrategy strategy) {
        this.repository = repository;
        this.strategy = strategy;
    }

    /**
     * Adds an observer to the notification list.
     *
     * @param observer the observer (e.g., EmailService) to be notified of changes
     */
    public void addObserver(NotificationObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Updates the booking strategy at runtime.
     *
     * @param strategy the new BookingStrategy to be applied
     */
    public void setStrategy(BookingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Cancels an existing appointment, resets its status,
     * and notifies the user via registered observers.
     *
     * @param appointment the appointment to be cancelled
     * @param userEmail the email of the user who owns the booking
     */
    public void cancel(Appointment appointment, String userEmail) {
        appointment.setStatus(STATUS_AVAILABLE);
        appointment.setBookedBy("");
        repository.saveToFile();

        String content = buildCancelMessage(appointment);
        notifyObservers(userEmail, content);
    }

    /**
     * Attempts to book an appointment if it is available and passes validation.
     *
     * @param appointment the appointment to be booked
     * @param userEmail the email of the user making the booking
     * @return true if the booking was successful, false otherwise
     */
    public boolean book(Appointment appointment, String userEmail) {
        if (!STATUS_AVAILABLE.equals(appointment.getStatus())) {
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

    /**
     * Notifies all registered observers about a booking or cancellation event.
     *
     * @param email the recipient's email
     * @param message the content of the notification
     */
    private void notifyObservers(String email, String message) {
        for (NotificationObserver observer : observers) {
            observer.update(email, message);
        }
    }

    /**
     * Builds a confirmation message for a successful booking.
     *
     * @param appointment the booked appointment
     * @return formatted booking confirmation message
     */
    private String buildBookingMessage(Appointment appointment) {
        return "Your booking is confirmed!\n"
                + "Details:\n"
                + "- Type: " + appointment.getType() + "\n"
                + "- Time: " + formatDate(appointment) + "\n"
                + "- Duration: " + appointment.getDurationMinutes() + " minutes.";
    }

    /**
     * Builds a notification message for a cancelled appointment.
     *
     * @param appointment the cancelled appointment
     * @return formatted cancellation message
     */
    private String buildCancelMessage(Appointment appointment) {
        return "Hello,\n\nYour appointment has been successfully cancelled.\n"
                + "Details:\n"
                + "- Type: " + appointment.getType() + "\n"
                + "- Time: " + formatDate(appointment);
    }

    /**
     * Formats the appointment date into a readable string.
     *
     * @param appointment the appointment to format
     * @return formatted date string
     */
    private String formatDate(Appointment appointment) {
        return appointment.getDateTime().toString().replace("T", " ");
    }
}