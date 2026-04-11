package com.system.services;

import com.system.models.Role;
import com.system.observers.NotificationObserver;
import java.io.*;
import java.util.*;

/**
 * Service class responsible for user authentication and account management.
 * This class acts as the 'Subject' in the Observer pattern.
 * @author Raghd and Farah
 * @version 1.0
 */
public class AuthenticationService {
    private Map<String, String> userAccounts = new HashMap<>(); // Username -> Password
    private Map<String, String> userEmails = new HashMap<>();   // Username -> Email
    private Map<String, String> adminAccounts = new HashMap<>();

    // قائمة المراقبين (Observer Pattern)
    private List<NotificationObserver> observers = new ArrayList<>();

    private final String USER_FILE = "users.txt";
    private final String ADMIN_FILE = "admins.txt";

    public AuthenticationService() {
        loadAccounts(USER_FILE, userAccounts);
        loadAccounts(ADMIN_FILE, adminAccounts);

        // إضافة الـ EmailService كمراقب تلقائياً
        addObserver(new EmailService());

        if (adminAccounts.isEmpty()) {
            adminAccounts.put("admin", "admin123");
            saveAccount(ADMIN_FILE, "admin", "admin123");
        }
    }

    public void addObserver(NotificationObserver observer) {
        if (observer != null) observers.add(observer);
    }

    private void notifyObservers(String email, String message) {
        for (NotificationObserver observer : observers) {
            observer.update(email, message);
        }
    }

    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9._]+$");
    }

    /**
     * تسجيل مستخدم جديد
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
     * تسجيل أدمن جديد (الميثود التي كانت تسبب الإيرور)
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

    private void saveAccount(String filePath, String user, String data) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.println(user + "," + data);
        } catch (IOException e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }

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

    public void logout() {
        System.out.println("[System]: Logged out.");
    }
}