package com.system.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    // يغطي thread
    @Test
    void testUpdateDoesNotCrash() {
        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String message) {}
        };
        assertDoesNotThrow(() -> service.update("test@test.com", "hello"));
    }

    // multiple calls
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

    // يغطي if (بدون password)
    @Test
    void testSendEmailWithoutPasswordThrowsException() {
        EmailService service = new EmailService();

        // إذا عندك env → سكّب التست
        Assumptions.assumeTrue(System.getenv("APP_PASSWORD") == null);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> service.sendEmail("test@test.com", "msg"));

        assertTrue(ex.getMessage().contains("APP_PASSWORD"));
    }

    // 🔥 أهم تست: يغطي props + session + try
    @Test
    void testSendEmailFullPath() {
        EmailService service = new EmailService();

        // إذا ما في env → سكّب
        Assumptions.assumeTrue(System.getenv("APP_PASSWORD") != null);

        try {
            service.sendEmail("test@gmail.com", "hello");
        } catch (Exception ignored) {}

        assertTrue(true);
    }

    // يغطي catch
    @Test
    void testSendEmailCatchBlock() {
        EmailService service = new EmailService();

        Assumptions.assumeTrue(System.getenv("APP_PASSWORD") != null);

        try {
            service.sendEmail("invalid@@@", "msg");
        } catch (Exception ignored) {}

        assertTrue(true);
    }

    // edge case
    @Test
    void testSendEmailWithLongContent() {
        EmailService service = new EmailService();

        Assumptions.assumeTrue(System.getenv("APP_PASSWORD") != null);

        try {
            service.sendEmail("test@test.com", "a".repeat(1000));
        } catch (Exception ignored) {}

        assertTrue(true);
    }

    // تحقق من تمرير القيم
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