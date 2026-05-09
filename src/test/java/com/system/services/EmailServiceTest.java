package com.system.services;

import org.junit.jupiter.api.Test;

public class EmailServiceTest {

    @Test
    void testUpdateDoesNotCrash() {
        EmailService emailService = new EmailService();
        emailService.update("test@test.com", "hello");
    }
}