package com.system.strategies;

import com.system.models.Appointment;

/**
 * هذا هو القالب لأي قانون حجز مستقبلي.
 */
public interface BookingStrategy {
    // ميثود بترجع true إذا الموعد مسموح، و false إذا مرفوض
    boolean isValid(Appointment appointment);
}