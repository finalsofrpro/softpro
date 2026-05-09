package com.system.strategies;

import com.system.models.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StrategyTest {

    @Test
    void testUrgentStrategy() {
        UrgentStrategy s = new UrgentStrategy();

        Appointment a = new Appointment(1, LocalDateTime.now(), 15, 1, "Urgent");

        assertTrue(s.isValid(a));
    }

    @Test
    void testVirtualStrategyFail() {
        VirtualStrategy s = new VirtualStrategy();

        Appointment a = new Appointment(2, LocalDateTime.now(), 30, 1, "Virtual");

        assertFalse(s.isValid(a));
    }

    @Test
    void testUrgentStrategyFail() {
        UrgentStrategy s = new UrgentStrategy();
        Appointment a = new Appointment(5, LocalDateTime.now(), 30, 1, "Urgent");
        assertFalse(s.isValid(a));
    }

    @Test
    void testVirtualStrategySuccess() {
        VirtualStrategy s = new VirtualStrategy();
        Appointment a = new Appointment(10, LocalDateTime.now(), 60, 1, "Virtual");
        assertTrue(s.isValid(a));
    }

    @Test
    void testFollowUpStrategySuccess() {
        FollowUpStrategy s = new FollowUpStrategy();
        Appointment a = new Appointment(11, LocalDateTime.now(), 30, 1, "Follow-up");
        assertTrue(s.isValid(a));
    }

    @Test
    void testFollowUpStrategyFail() {
        FollowUpStrategy s = new FollowUpStrategy();
        Appointment a = new Appointment(12, LocalDateTime.now(), 20, 1, "Follow-up");
        assertFalse(s.isValid(a));
    }

    @Test
    void testRangeDurationStrategy() {
        RangeDurationStrategy s = new RangeDurationStrategy();
        Appointment a = new Appointment(13, LocalDateTime.now(), 45);
        assertTrue(s.isValid(a));
    }

    @Test
    void testRangeDurationStrategyFail() {
        RangeDurationStrategy s = new RangeDurationStrategy();
        Appointment a = new Appointment(14, LocalDateTime.now(), 25);
        assertFalse(s.isValid(a));
    }

    @Test
    void testShortDurationStrategy() {
        ShortDurationStrategy s = new ShortDurationStrategy();
        Appointment a = new Appointment(15, LocalDateTime.now(), 60);
        assertTrue(s.isValid(a));
    }

    @Test
    void testRangeDurationValid() {
        RangeDurationStrategy s = new RangeDurationStrategy();
        Appointment a = new Appointment(1, LocalDateTime.now(), 30);
        assertTrue(s.isValid(a));
    }

    @Test
    void testShortDurationFail() {
        ShortDurationStrategy s = new ShortDurationStrategy();
        Appointment a = new Appointment(2, LocalDateTime.now(), 90);
        assertFalse(s.isValid(a));
    }
}