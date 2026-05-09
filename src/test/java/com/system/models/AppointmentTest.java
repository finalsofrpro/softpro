package com.system.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {

    @Test
    void testDefaultStatusIsAvailable() {
        Appointment a = new Appointment(1, LocalDateTime.now(), 30);
        assertEquals("AVAILABLE", a.getStatus());
    }

    @Test
    void testStatusChange() {
        Appointment a = new Appointment(2, LocalDateTime.now(), 30);
        a.setStatus("BOOKED");
        assertEquals("BOOKED", a.getStatus());
    }

    @Test
    void testBookedBy() {
        Appointment a = new Appointment(3, LocalDateTime.now(), 30);
        a.setBookedBy("test@test.com");
        assertEquals("test@test.com", a.getBookedBy());
    }

    @Test
    void testBookedByInitiallyNull() {
        Appointment a = new Appointment(4, LocalDateTime.now(), 30);
        assertEquals("", a.getBookedBy());    }

    @Test
    void testInitialValues() {
        LocalDateTime time = LocalDateTime.now();
        Appointment a = new Appointment(5, time, 45);

        assertEquals(5, a.getId());
        assertEquals(time, a.getDateTime());
        assertEquals(45, a.getDurationMinutes());    }

    @Test
    void testSetType() {
        Appointment a = new Appointment(1, LocalDateTime.now(), 30);
        a.setType("Urgent");
        assertEquals("Urgent", a.getType());
    }
}