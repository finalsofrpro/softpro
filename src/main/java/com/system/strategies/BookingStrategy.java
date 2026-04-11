package com.system.strategies;

import com.system.models.Appointment;

/**
 * Interface for the Strategy Pattern to validate appointment bookings.
 */
public interface BookingStrategy {
    boolean isValid(Appointment appointment);
}