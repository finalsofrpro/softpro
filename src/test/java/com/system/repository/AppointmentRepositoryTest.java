package com.system.repository;

import com.system.models.Appointment;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentRepositoryTest {

    @BeforeEach
    void cleanFile() {
        new java.io.File("appointments.txt").delete();
    }

    @Test
    void testAddAppointment() {
        AppointmentRepository repo = new AppointmentRepository();
        Appointment a = new Appointment(200, LocalDateTime.now(), 30);
        repo.addAppointment(a);
        assertFalse(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testTimeConflict() {
        AppointmentRepository repo = new AppointmentRepository();
        LocalDateTime time = LocalDateTime.now();

        repo.addAppointment(new Appointment(202, time, 30));

        Appointment app = new Appointment(203, time.plusMinutes(10), 30);

        assertThrows(IllegalArgumentException.class, () -> repo.addAppointment(app));
    }

    @Test
    void testGetAvailableAppointments() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(1, LocalDateTime.now(), 30));

        assertFalse(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testMultipleAppointmentsNoConflict() {
        AppointmentRepository repo = new AppointmentRepository();
        LocalDateTime time = LocalDateTime.now();

        repo.addAppointment(new Appointment(10, time, 30));
        repo.addAppointment(new Appointment(11, time.plusHours(1), 30));

        assertEquals(2, repo.getAvailableAppointments().size());
    }

    @Test
    void testConflictMessage() {
        AppointmentRepository repo = new AppointmentRepository();
        LocalDateTime time = LocalDateTime.now();

        repo.addAppointment(new Appointment(50, time, 30));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                repo.addAppointment(new Appointment(51, time.plusMinutes(10), 30))
        );

        assertTrue(ex.getMessage().contains("Time Conflict"));
    }

    // 🔥🔥🔥 هاي أهم الإضافات لرفع الكفرج

    @Test
    void testDeleteAppointment() {
        AppointmentRepository repo = new AppointmentRepository();

        repo.addAppointment(new Appointment(5, LocalDateTime.now(), 30));
        repo.deleteAppointment(5);

        boolean exists = repo.getAvailableAppointments()
                .stream()
                .anyMatch(a -> a.getId() == 5);

        assertFalse(exists);
    }

    @Test
    void testFileModeSaveAndLoad() {
        AppointmentRepository repo = new AppointmentRepository(true);

        Appointment a = new Appointment(300, LocalDateTime.now().plusHours(5), 30);
        repo.addAppointment(a);

        // reload
        repo.loadFromFile();

        assertFalse(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testLoadFromFileWhenFileNotExists() {
        AppointmentRepository repo = new AppointmentRepository(true);

        assertDoesNotThrow(repo::loadFromFile);
    }

    @Test
    void testGetAvailableAppointmentsFileMode() {
        AppointmentRepository repo = new AppointmentRepository(true);

        repo.addAppointment(new Appointment(400, LocalDateTime.now().plusHours(2), 30));

        assertFalse(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testDeleteWithFileMode() {
        AppointmentRepository repo = new AppointmentRepository(true);

        repo.addAppointment(new Appointment(500, LocalDateTime.now(), 30));
        repo.deleteAppointment(500);

        boolean exists = repo.getAvailableAppointments()
                .stream()
                .anyMatch(a -> a.getId() == 500);

        assertFalse(exists);
    }

    @Test
    void testDeleteNonExisting() {
        AppointmentRepository repo = new AppointmentRepository();

        assertDoesNotThrow(() -> repo.deleteAppointment(123));
    }

    @Test
    void testDeleteNonExistingDoesNotAffectData() {
        AppointmentRepository repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(1, LocalDateTime.now(), 30));

        repo.deleteAppointment(999);

        assertEquals(1, repo.getAvailableAppointments().size());
    }

    @Test
    void testDeleteNonExistingDoesNotThrow() {
        AppointmentRepository repo = new AppointmentRepository();

        assertDoesNotThrow(() -> repo.deleteAppointment(123));
    }

    @Test
    void testEmptyAppointments() {
        AppointmentRepository repo = new AppointmentRepository();

        assertTrue(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testDuplicateId() {
        AppointmentRepository repo = new AppointmentRepository();

        LocalDateTime time = LocalDateTime.now();

        repo.addAppointment(new Appointment(1, time, 30));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                repo.addAppointment(new Appointment(1, time.plusHours(1), 30))
        );

        assertTrue(ex.getMessage().contains("ID"));
    }
}