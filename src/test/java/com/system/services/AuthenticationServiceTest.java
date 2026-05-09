package com.system.services;

import com.system.models.Appointment;
import com.system.models.Role;
import com.system.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationServiceTest {

    @Test
    void testRegisterUserSuccess() {
        AuthenticationService auth = new AuthenticationService(true);
        boolean result = auth.registerNewUser("user1", "1234", "u@test.com");
        assertTrue(result);
    }

    @Test
    void testDuplicateUser() {
        AuthenticationService auth = new AuthenticationService(true);
        auth.registerNewUser("user2", "1234", "u@test.com");
        boolean result = auth.registerNewUser("user2", "1234", "u@test.com");
        assertFalse(result);
    }

    @Test
    void testInvalidUsername() {
        AuthenticationService auth = new AuthenticationService(true);
        boolean result = auth.registerNewUser("user!!", "1234", "u@test.com");
        assertFalse(result);
    }

    @Test
    void testLoginSuccess() {
        AuthenticationService auth = new AuthenticationService(true);
        auth.registerNewUser("user3", "1234", "u@test.com");
        assertEquals(Role.USER, auth.login("user3", "1234"));
    }

    @Test
    void testLoginFail() {
        AuthenticationService auth = new AuthenticationService(true);
        assertEquals(Role.NONE, auth.login("wrong", "wrong"));
    }

    @Test
    void testRegisterAdmin() {
        AuthenticationService auth = new AuthenticationService(true);
        assertTrue(auth.registerNewAdmin("admin2", "1234"));
    }

    @Test
    void testInvalidAdmin() {
        AuthenticationService auth = new AuthenticationService(true);
        assertFalse(auth.registerNewAdmin("!!", "1234"));
    }

    @Test
    void testRegisterNullUsername() {
        AuthenticationService auth = new AuthenticationService(true);
        assertFalse(auth.registerNewUser(null, "123", "test@test.com"));
    }

    @Test
    void testLoginAdmin() {
        AuthenticationService auth = new AuthenticationService(true);
        assertEquals(Role.ADMIN, auth.login("admin", "admin123"));
    }

    @Test
    void testDuplicateAdmin() {
        AuthenticationService auth = new AuthenticationService(true);
        auth.registerNewAdmin("admin2", "123");
        assertFalse(auth.registerNewAdmin("admin2", "123"));
    }

    @Test
    void testLoginWrongPassword() {
        AuthenticationService auth = new AuthenticationService(true);
        auth.registerNewUser("user", "1234", "t@test.com");
        assertEquals(Role.NONE, auth.login("user", "wrong"));
    }

    @Test
    void testLoginAdminWrongPassword() {
        AuthenticationService auth = new AuthenticationService(true);
        assertEquals(Role.NONE, auth.login("admin", "wrong"));
    }
    @Test
    void testSaveToFileDoesNotCrash() {
        AppointmentRepository repo = new AppointmentRepository(true);

        Appointment a = new Appointment(300, LocalDateTime.now(), 30);
        repo.addAppointment(a);

        // إذا وصلنا لهون بدون exception = نجح
    }

    @Test
    void testLoadFromFileWhenFileNotExists() {
        AppointmentRepository repo = new AppointmentRepository(true);

        // ما في ملف → لازم ما يكسر
        assertDoesNotThrow(repo::loadFromFile);
    }

    @Test
    void testLoadFromFileWithData() {
        AppointmentRepository repo = new AppointmentRepository(true);

        Appointment a = new Appointment(400, LocalDateTime.now(), 30);
        repo.addAppointment(a);

        // reload
        repo.loadFromFile();

        assertFalse(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testGetAvailableAppointmentsWithFileMode() {
        AppointmentRepository repo = new AppointmentRepository(true);

        repo.addAppointment(new Appointment(500, LocalDateTime.now(), 30));

        assertFalse(repo.getAvailableAppointments().isEmpty());
    }

    @Test
    void testDeleteWithFileMode() {
        AppointmentRepository repo = new AppointmentRepository(true);

        Appointment a = new Appointment(600, LocalDateTime.now(), 30);
        repo.addAppointment(a);

        repo.deleteAppointment(600);

        boolean exists = repo.getAvailableAppointments()
                .stream().anyMatch(x -> x.getId() == 600);

        assertFalse(exists);
    }

    @Test
    void testEmptyLineHandlingInLoad() {
        AppointmentRepository repo = new AppointmentRepository(true);

        // بس نتأكد ما بكسر لو في سطور فاضية
        assertDoesNotThrow(repo::loadFromFile);
    }
}