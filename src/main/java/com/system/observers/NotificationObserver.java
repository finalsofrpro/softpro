package com.system.observers;

/**
 * Interface defining the Observer in the Observer Pattern.
 * Used to decouple the booking system from notification channels (US3.1).
 * * @author Raghad and Farah
 * @version 1.0
 */
public interface NotificationObserver {
    /**
     * Updates the observer with a new message for a specific recipient.
     * * @param recipient The email or contact of the user to be notified.
     * @param message   The content of the notification message.
     */
    void update(String recipient, String message);
}