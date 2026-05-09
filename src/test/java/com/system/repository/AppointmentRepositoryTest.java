package com.system.repository;

import com.system.models.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentRepositoryTest {

    private final LocalDateTime t1 = LocalDateTime.of(2025,1,1,10,0);
    private final LocalDateTime t2 = LocalDateTime.of(2025,1,1,11,0);
    private final LocalDateTime t3 = LocalDateTime.of(2025,1,1,12,0);

    @Test
    void testAddAppointment() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(200, t1, 30));
        assertFalse(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testDuplicateId() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(201, t1, 30));

        assertThrows(IllegalArgumentException.class,
                () -> repo.addAppointment(new Appointment(201, t2, 30)));
    }

    @Test
    void testTimeConflict() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(202, t1, 30));

        assertThrows(IllegalArgumentException.class,
                () -> repo.addAppointment(new Appointment(203, t1.plusMinutes(10), 30)));
    }

    @Test
    void testDelete() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(204, t1, 30));
        repo.deleteAppointment(204);

        assertTrue(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testMultipleAppointmentsNoConflict() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(10, t1, 30));
        repo.addAppointment(new Appointment(11, t2, 30));

        assertEquals(2, repo.getAvailableAppointments().size());
    }

    @Test
    void testConflictMessage() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(50, t1, 30));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> repo.addAppointment(new Appointment(51, t1.plusMinutes(10), 30)));

        assertTrue(ex.getMessage().contains("Time Conflict"));
    }

    // 🔥 FILE MODE (بدون تخريب التستات)

    @Test
    void testSaveToFile() {
        AppointmentRepository repo = new AppointmentRepository(true);

        repo.deleteAppointment(300); // تنظيف
        repo.addAppointment(new Appointment(300, t3, 30));

        assertDoesNotThrow(repo::saveToFile);
    }

    @Test
    void testLoadFileNotExists() {
        AppointmentRepository repo = new AppointmentRepository(true);
        assertDoesNotThrow(repo::loadFromFile);
    }

    @Test
    void testGetAvailableAppointmentsFileMode() {
        AppointmentRepository repo = new AppointmentRepository(true);

        repo.deleteAppointment(500);
        repo.addAppointment(new Appointment(500, t2, 30));

        assertFalse(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testDeleteWithFileMode() {
        AppointmentRepository repo = new AppointmentRepository(true);

        repo.deleteAppointment(600);
        repo.addAppointment(new Appointment(600, t3, 30));
        repo.deleteAppointment(600);

        boolean exists = repo.getAvailableAppointments()
                .stream()
                .anyMatch(x -> x.getId() == 600);

        assertFalse(exists);
    }
}