package com.system.strategies;

import com.system.models.Appointment;

public class UrgentStrategy implements BookingStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        // القاعدة: إذا كان النوع Urgent، يجب أن تكون المدة 15 دقيقة حصراً
        return "Urgent".equalsIgnoreCase(appointment.getType()) && appointment.getDurationMinutes() == 15;
    }
}