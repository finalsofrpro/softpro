package com.system.strategies;

import com.system.models.Appointment;

/**
 * Strategy for Urgent appointments.
 * Rule: Duration must be exactly 15 minutes.
 */
public class UrgentStrategy implements BookingStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        return "Urgent".equalsIgnoreCase(appointment.getType()) && appointment.getDurationMinutes() == 15;
    }
}