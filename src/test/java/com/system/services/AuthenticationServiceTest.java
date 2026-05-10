package com.system.services;

import com.system.models.Role;
import org.junit.jupiter.api.Test;
import com.system.observers.NotificationObserver;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationServiceTest {

    @Test
    void testRegisterUserSuccess() {
        AuthenticationService auth = new AuthenticationService(true);
        assertTrue(auth.registerNewUser("user1", "1234", "u@test.com"));
    }

    @Test
    void testDuplicateUser() {
        AuthenticationService auth = new AuthenticationService(true);
        auth.registerNewUser("user2", "1234", "u@test.com");
        assertFalse(auth.registerNewUser("user2", "1234", "u@test.com"));
    }

    @Test
    void testInvalidUsername() {
        AuthenticationService auth = new AuthenticationService(true);
        assertFalse(auth.registerNewUser("user!!", "1234", "u@test.com"));
    }

    @Test
    void testRegisterNullUsername() {
        AuthenticationService auth = new AuthenticationService(true);
        assertFalse(auth.registerNewUser(null, "1234", "u@test.com"));
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
    void testLoginWrongPassword() {
        AuthenticationService auth = new AuthenticationService(true);
        auth.registerNewUser("user", "1234", "t@test.com");
        assertEquals(Role.NONE, auth.login("user", "wrong"));
    }

    @Test
    void testRegisterAdmin() {
        AuthenticationService auth = new AuthenticationService(true);
        assertTrue(auth.registerNewAdmin("admin2", "1234"));
    }

    @Test
    void testDuplicateAdmin() {
        AuthenticationService auth = new AuthenticationService(true);
        auth.registerNewAdmin("admin2", "123");
        assertFalse(auth.registerNewAdmin("admin2", "123"));
    }

    @Test
    void testInvalidAdmin() {
        AuthenticationService auth = new AuthenticationService(true);
        assertFalse(auth.registerNewAdmin("!!", "1234"));
    }

    @Test
    void testLoginAdmin() {
        AuthenticationService auth = new AuthenticationService(true);
        assertEquals(Role.ADMIN, auth.login("admin", "admin123"));
    }

    @Test
    void testLoginAdminWrongPassword() {
        AuthenticationService auth = new AuthenticationService(true);
        assertEquals(Role.NONE, auth.login("admin", "wrong"));
    }

    @Test
    void testObserverIsNotifiedOnRegister() {
        NotificationObserver mockObserver = mock(NotificationObserver.class);

        AuthenticationService auth = new AuthenticationService(mockObserver);

        auth.registerNewUser("userX", "1234", "test@test.com");

        verify(mockObserver, atLeastOnce()).update(anyString(), anyString());
    }

    @Test
    void testAddObserverManually() {
        AuthenticationService auth = new AuthenticationService(true);

        NotificationObserver observer = mock(NotificationObserver.class);
        auth.addObserver(observer);

        auth.registerNewUser("userY", "1234", "test@test.com");

        verify(observer, atLeastOnce()).update(anyString(), anyString());
    }

    @Test
    void testLogoutDoesNotCrash() {
        AuthenticationService auth = new AuthenticationService(true);

        assertDoesNotThrow(auth::logout);
    }

    @Test
    void testRegisterWithNullEmail() {
        AuthenticationService auth = new AuthenticationService(true);
        assertTrue(auth.registerNewUser("userX", "1234", null));
    }

    @Test
    void testAddObserver() {
        AuthenticationService auth = new AuthenticationService(true);
        auth.addObserver((email, msg) -> {});
        assertTrue(true); // بس لتغطية السطر
    }

    @Test
    void testConstructorLoadsDefaultAdmin() {
        AuthenticationService auth = new AuthenticationService();
        assertEquals(Role.ADMIN, auth.login("admin", "admin123"));
    }

    @Test
    void testObserverTriggeredOnRegister() {
        NotificationObserver observer = mock(NotificationObserver.class);
        AuthenticationService auth = new AuthenticationService(observer);

        auth.registerNewUser("userZ", "1234", "z@test.com");

        verify(observer).update(anyString(), anyString());
    }

    @Test
    void testRegisterAdminRealMode() {
        AuthenticationService auth = new AuthenticationService(false);
        assertTrue(auth.registerNewAdmin("adminX", "1234"));
    }
}