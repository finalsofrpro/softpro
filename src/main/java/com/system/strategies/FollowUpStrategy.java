package com.system.strategies;

import com.system.models.Appointment;

public class FollowUpStrategy implements BookingStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        // فرضا المتابعة تكون 30 دقيقة
        return "Follow-up".equalsIgnoreCase(appointment.getType()) && appointment.getDurationMinutes() == 30;
    }
}