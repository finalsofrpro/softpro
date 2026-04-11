package com.system.observers;

/**
 * Interface to support the Observer pattern for notifications.
 *  @author Raghad  and Farah
 */
public interface NotificationObserver {
    /**
     * Sends a notification to the specified recipient.
     * @param recipient The email address of the user.
     * @param message The message body.
     */
    void update(String recipient, String message);
}