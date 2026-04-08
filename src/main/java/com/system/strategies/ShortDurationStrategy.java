package com.system.strategies;

import com.system.models.Appointment;

/**
 * قانون: الموعد يجب أن لا يتجاوز 60 دقيقة.
 */
public class ShortDurationStrategy implements BookingStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        // إذا المدة أقل أو تساوي 60 دقيقة، القانون بيحكي "تم"
        return appointment.getDurationMinutes() <= 60;
    }
}