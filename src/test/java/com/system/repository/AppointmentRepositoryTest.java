package com.system.repository;

import com.system.models.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentRepositoryTest {

    @Test
    void testAddAppointment() {
        AppointmentRepository repo = new AppointmentRepository();

        LocalDateTime time = LocalDateTime.now();
        Appointment a = new Appointment(200, time, 30);

        repo.addAppointment(a);

        assertTrue(repo.getAvailableAppointments().size() > 0);
    }

    @Test
    void testDuplicateId() {
        AppointmentRepository repo = new AppointmentRepository();

        LocalDateTime time = LocalDateTime.now();

        Appointment a1 = new Appointment(201, time, 30);
        repo.addAppointment(a1);

        // نفس ID بس وقت مختلف (عشان ما يصير conflict)
        Appointment a2 = new Appointment(201, time.plusHours(1), 30);

        assertThrows(IllegalArgumentException.class, () -> repo.addAppointment(a2));
    }

    @Test
    void testTimeConflict() {
        AppointmentRepository repo = new AppointmentRepository();

        LocalDateTime time = LocalDateTime.now();

        Appointment a1 = new Appointment(202, time, 30);
        repo.addAppointment(a1);

        // هون بدنا conflict فعلي
        Appointment a2 = new Appointment(203, time.plusMinutes(10), 30);

        assertThrows(IllegalArgumentException.class, () -> repo.addAppointment(a2));
    }

    @Test
    void testDelete() {
        AppointmentRepository repo = new AppointmentRepository();

        LocalDateTime time = LocalDateTime.now();

        Appointment a = new Appointment(204, time, 30);
        repo.addAppointment(a);

        repo.deleteAppointment(204);

        boolean exists = repo.getAvailableAppointments()
                .stream()
                .anyMatch(x -> x.getId() == 204);

        assertFalse(exists);
    }

    @Test
    void testDeleteNonExisting() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.deleteAppointment(999); // ما لازم يكسر
    }
}