package com.system.strategies;

import com.system.models.Appointment;

public class VirtualStrategy implements BookingStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        // القاعدة: إذا كان النوع Virtual، يجب أن تكون المدة 60 دقيقة (ساعة)
        return "Virtual".equalsIgnoreCase(appointment.getType()) && appointment.getDurationMinutes() == 60;
    }
}