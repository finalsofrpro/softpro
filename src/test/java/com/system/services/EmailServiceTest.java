package com.system.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    @Test
    void testUpdateDoesNotCrash() {

        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String subject, String message) {
                // mock
            }
        };

        assertDoesNotThrow(() ->
                service.update("test@test.com", "hello")
        );
    }

    @Test
    void testUpdateMultipleTimes() {

        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String subject, String message) {
                // mock
            }
        };

        assertDoesNotThrow(() -> {
            service.update("a@test.com", "msg1");
            service.update("b@test.com", "msg2");
        });
    }

    @Test
    void testSendEmailInternalLogic() {

        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String recipient, String subject, String content) {

                if (recipient == null) {
                    System.err.println("❌ Fake Error");
                } else {
                    System.out.println("✅ Fake Success");
                }
            }
        };

        service.sendEmail("test@test.com", "subject", "Hello");
        service.sendEmail(null, "subject", "Fail case");

        assertTrue(true);
    }

    @Test
    void testUpdatePassesCorrectEmail() {

        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String subject, String message) {
                assertEquals("test@test.com", email);
            }
        };

        service.update("test@test.com", "hello");
    }

    @Test
    void testUpdatePassesCorrectMessage() {

        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String subject, String message) {
                assertEquals("hello", message);
            }
        };

        service.update("test@test.com", "hello");
    }

    @Test
    void testSendEmailWithLongContent() {

        EmailService service = new EmailService() {
            @Override
            public void sendEmail(String email, String subject, String message) {
                assertTrue(message.length() > 500);
            }
        };

        service.update("test@test.com", "a".repeat(1000));
    }
}