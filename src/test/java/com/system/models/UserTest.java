package com.system.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User("farah", "1234", "ADMIN");

        assertEquals("farah", user.getUsername());
        assertEquals("1234", user.getPassword());
        assertEquals("ADMIN", user.getRole());
    }
}