package com.system.services;

import com.system.models.Appointment;
import com.system.observers.NotificationObserver;
import com.system.repository.AppointmentRepository;
import com.system.strategies.BookingStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class that manages the booking and cancellation of appointments.
 * It coordinates between the repository, booking strategies, and notification observers.
 * * @author Raghad and Farah
 * @version 1.0
 */
public class BookingService {
    private AppointmentRepository repository;
    private BookingStrategy strategy;
    private List<NotificationObserver> observers = new ArrayList<>();

    /**
     * Constructs a BookingService with a specific repository and strategy.
     * * @param repository The repository used for data persistence.
     * @param strategy   The strategy used to validate booking rules.
     */
    public BookingService(AppointmentRepository repository, BookingStrategy strategy) {
        this.repository = repository;
        this.strategy = strategy;
    }

    /**
     * Adds an observer to the notification list.
     * * @param observer The observer (e.g., EmailService) to be notified of changes.
     */
    public void addObserver(NotificationObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Updates the booking strategy at runtime.
     * * @param strategy The new BookingStrategy to be applied.
     */
    public void setStrategy(BookingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Cancels an existing appointment and notifies the user.
     * * @param appointment The appointment to be cancelled.
     * @param userEmail   The email of the user who owns the booking.
     */
    public void cancel(Appointment appointment, String userEmail) {
        appointment.setStatus("AVAILABLE");
        appointment.setBookedBy("");
        repository.saveToFile();

        String content = "Hello,\n\nYour appointment has been successfully cancelled.\n" +
                "Details:\n" +
                "- Type: " + appointment.getType() + "\n" +
                "- Time: " + appointment.getDateTime().toString().replace("T", " ");

        notifyObservers(userEmail, content);
    }

    /**
     * Books an appointment if it passes the strategy validation.
     * * @param appointment The appointment to be booked.
     * @param userEmail   The email of the user making the booking.
     * @return true if the booking was successful, false if it failed validation.
     */
    public boolean book(Appointment appointment, String userEmail) {
        if (strategy.isValid(appointment)) {
            appointment.setStatus("BOOKED");
            appointment.setBookedBy(userEmail);
            repository.saveToFile();

            String content = "Your booking is confirmed!\n" +
                    "Details:\n" +
                    "- Type: " + appointment.getType() + "\n" +
                    "- Time: " + appointment.getDateTime().toString().replace("T", " ") + "\n" +
                    "- Duration: " + appointment.getDurationMinutes() + " minutes.";

            notifyObservers(userEmail, content);
            return true;
        }
        return false;
    }

    /**
     * Notifies all registered observers about an event.
     * * @param email   The recipient's email.
     * @param message The content of the notification.
     */
    private void notifyObservers(String email, String message) {
        for (NotificationObserver observer : observers) {
            observer.update(email, message);
        }
    }
}