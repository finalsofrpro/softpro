package com.system.services;

import com.system.models.Role;
import com.system.observers.NotificationObserver;
import java.io.*;
import java.util.*;

/**
 * Service class responsible for user authentication and account management.
 * This class handles registration, login, and data persistence for users and admins.
 * * @author Raghad and Farah
 * @version 1.0
 */
public class AuthenticationService {
    private Map<String, String> userAccounts = new HashMap<>();
    private Map<String, String> userEmails = new HashMap<>();
    private Map<String, String> adminAccounts = new HashMap<>();
    private List<NotificationObserver> observers = new ArrayList<>();

    private final String USER_FILE = "users.txt";
    private final String ADMIN_FILE = "admins.txt";

    /**
     * Initializes the service by loading existing accounts and setting up observers.
     */
    public AuthenticationService() {
        loadAccounts(USER_FILE, userAccounts);
        loadAccounts(ADMIN_FILE, adminAccounts);

        addObserver(new EmailService());

        if (adminAccounts.isEmpty()) {
            adminAccounts.put("admin", "admin123");
            saveAccount(ADMIN_FILE, "admin", "admin123");
        }
    }

    /**
     * Registers a new observer for authentication events.
     * * @param observer The observer to be added.
     */
    public void addObserver(NotificationObserver observer) {
        if (observer != null) observers.add(observer);
    }

    /**
     * Notifies observers about registration or login events.
     * * @param email   The user's email.
     * @param message The message to be sent.
     */
    private void notifyObservers(String email, String message) {
        for (NotificationObserver observer : observers) {
            observer.update(email, message);
        }
    }

    /**
     * Validates if the username contains only allowed characters.
     * * @param username The username to check.
     * @return true if valid, false otherwise.
     */
    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9._]+$");
    }

    /**
     * Registers a new user and sends a welcome notification.
     * * @param username The chosen username.
     * @param password The user's password.
     * @param email    The user's email address.
     * @return true if registration succeeds, false if the username exists or is invalid.
     */
    public boolean registerNewUser(String username, String password, String email) {
        if (!isValidUsername(username)) return false;

        for (String existingUser : userAccounts.keySet()) {
            if (existingUser.equalsIgnoreCase(username)) return false;
        }

        userAccounts.put(username, password);
        userEmails.put(username, email);
        saveAccount(USER_FILE, username, password + "," + email);

        String welcomeMessage = "Hello " + username + ",\n\nYour account has been created successfully!";
        new Thread(() -> notifyObservers(email, welcomeMessage)).start();

        return true;
    }

    /**
     * Registers a new administrator.
     * * @param username The admin username.
     * @param password The admin password.
     * @return true if registration succeeds.
     */
    public boolean registerNewAdmin(String username, String password) {
        if (!isValidUsername(username)) return false;

        for (String existingAdmin : adminAccounts.keySet()) {
            if (existingAdmin.equalsIgnoreCase(username)) return false;
        }

        adminAccounts.put(username, password);
        saveAccount(ADMIN_FILE, username, password);
        return true;
    }

    /**
     * Authenticates a user or admin based on credentials.
     * * @param username The input username.
     * @param password The input password.
     * @return The Role associated with the credentials (ADMIN, USER, or NONE).
     */
    public Role login(String username, String password) {
        for (Map.Entry<String, String> entry : adminAccounts.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(username) && entry.getValue().equals(password)) {
                return Role.ADMIN;
            }
        }
        for (Map.Entry<String, String> entry : userAccounts.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(username) && entry.getValue().equals(password)) {
                return Role.USER;
            }
        }
        return Role.NONE;
    }

    /**
     * Saves account details to a file.
     * * @param filePath The destination file path.
     * @param user     The username.
     * @param data     The data (password/email) to save.
     */
    private void saveAccount(String filePath, String user, String data) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.println(user + "," + data);
        } catch (IOException e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }

    /**
     * Loads account details from a text file into memory.
     * * @param filePath The source file path.
     * @param map      The map to populate with credentials.
     */
    private void loadAccounts(String filePath, Map<String, String> map) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    map.put(parts[0], parts[1]);
                    if (filePath.equals(USER_FILE) && parts.length == 3) {
                        userEmails.put(parts[0], parts[2]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading: " + filePath);
        }
    }

    /**
     * Closes the current user session.
     */
    public void logout() {
        System.out.println("[System]: Logged out.");
    }
}