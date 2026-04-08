package com.system.repository;

import com.system.models.Appointment;
import java.util.ArrayList;
import java.util.List;

/**
 * هذا الكلاس هو "المخزن" للمواعيد.
 * بدل قاعدة البيانات، بنستخدم ArrayList في الذاكرة.
 */
public class AppointmentRepository {
    private List<Appointment> appointments = new ArrayList<>();

    // ميثود لإضافة موعد جديد للمخزن
    public void addAppointment(Appointment app) {
        appointments.add(app);
    }

    // ميثود لجلب كل المواعيد المتاحة فقط (Status = AVAILABLE)
    public List<Appointment> getAvailableAppointments() {
        List<Appointment> available = new ArrayList<>();
        for (Appointment app : appointments) {
            if ("AVAILABLE".equals(app.getStatus())) {
                available.add(app);
            }
        }
        return available;
    }
}