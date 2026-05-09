package com.system.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class EmailServiceTest {

    @Test
    void testUpdateDoesNotCrash() {
        EmailService emailService = new EmailService() {
            @Override
            public void sendEmail(String email, String message) {
                // do nothing
            }
        };
        assertDoesNotThrow(() ->
                emailService.update("test@test.com", "hello")
        );
    }

    @Test
    void testSendEmailDoesNotCrash() {
        EmailService emailService = new EmailService() {
            @Override
            public void sendEmail(String email, String message) {
                // do nothing
            }
        };
        assertDoesNotThrow(() ->
                emailService.sendEmail("test@test.com", "hello")
        );
    }

    @Test
    void testUpdateMultipleTimes() {
        EmailService emailService = new EmailService() {
            @Override
            public void sendEmail(String email, String message) {
                // do nothing
            }
        };
        assertDoesNotThrow(() -> {
            emailService.update("a@test.com", "msg1");
            emailService.update("b@test.com", "msg2");
        });
    }
}