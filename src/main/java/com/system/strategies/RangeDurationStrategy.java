package com.system.strategies;

import com.system.models.Appointment;
import java.util.Arrays;
import java.util.List;

/**
 * قانون: مدة الحجز يجب أن تكون حصراً (15، 30، 45، أو 60 دقيقة).
 */
public class RangeDurationStrategy implements BookingStrategy {
    private final List<Integer> validDurations = Arrays.asList(15, 30, 45, 60);

    @Override
    public boolean isValid(Appointment appointment) {
        // نتحقق إذا كانت مدة الموعد موجودة ضمن القائمة المسموحة
        return validDurations.contains(appointment.getDurationMinutes());
    }
}