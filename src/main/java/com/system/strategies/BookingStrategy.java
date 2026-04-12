package com.system.strategies;

import com.system.models.Appointment;

/**
 * Strategy interface for appointment rule enforcement.
 * Part of the Strategy Pattern (US5.2) to allow flexible booking rules.
 * * @author Raghad and Farah
 * @version 1.0
 */
public interface BookingStrategy {
    /**
     * Validates an appointment based on specific business rules.
     * * @param appointment The appointment to validate.
     * @return true if the booking satisfies the strategy rules, false otherwise.
     */
    boolean isValid(Appointment appointment);
}