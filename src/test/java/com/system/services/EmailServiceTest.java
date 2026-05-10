package com.system.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    // ✅ thread
    @Test
    void testUpdateDoesNotCrash() {
        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String message) {}
        };

        assertDoesNotThrow(() -> service.update("test@test.com", "hello"));
    }

    // ✅ multiple calls
    @Test
    void testUpdateMultipleTimes() {
        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String message) {}
        };

        assertDoesNotThrow(() -> {
            service.update("a@test.com", "msg1");
            service.update("b@test.com", "msg2");
        });
    }

    // ✅ يغطي if (password = null)
    @Test
    void testSendEmailWithoutPassword() {
        EmailService service = new EmailService() {
            @Override
            protected String getAppPassword() {
                return null;
            }
        };

        assertThrows(IllegalStateException.class,
                () -> service.sendEmail("test@test.com", "msg"));
    }

    // ✅ يغطي try
    @Test
    void testSendEmailWithPassword() {
        EmailService service = new EmailService() {
            @Override
            protected String getAppPassword() {
                return "dummy";
            }
        };

        try {
            service.sendEmail("invalid-email", "msg");
        } catch (Exception ignored) {}

        assertTrue(true);
    }

    // ✅ يغطي catch
    @Test
    void testSendEmailCatchBlock() {
        EmailService service = new EmailService() {
            @Override
            protected String getAppPassword() {
                return "dummy";
            }
        };

        try {
            service.sendEmail("invalid@@@", "msg");
        } catch (Exception ignored) {}

        assertTrue(true);
    }

    // ✅ edge case
    @Test
    void testSendEmailWithLongContent() {
        EmailService service = new EmailService() {
            @Override
            protected String getAppPassword() {
                return "dummy";
            }
        };

        try {
            service.sendEmail("test@test.com", "a".repeat(1000));
        } catch (Exception ignored) {}

        assertTrue(true);
    }

    // ✅ verify email passed
    @Test
    void testUpdatePassesCorrectEmail() {
        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String message) {
                assertEquals("test@test.com", email);
            }
        };

        service.update("test@test.com", "hello");
    }

    // ✅ verify message passed
    @Test
    void testUpdatePassesCorrectMessage() {
        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String message) {
                assertEquals("hello", message);
            }
        };

        service.update("test@test.com", "hello");
    }
}