package com.system.services;

import com.system.models.Role;
import com.system.observers.NotificationObserver;
import java.io.*;
import java.util.*;

public class AuthenticationService {

    private static final String USER_FILE = "users.txt";
    private static final String ADMIN_FILE = "admins.txt";

    private static final String DEFAULT_ADMIN = "admin";
    private static final String DEFAULT_ADMIN_PASS = "admin123";

    private Map<String, String> userAccounts = new HashMap<>();
    private Map<String, String> userEmails = new HashMap<>();
    private Map<String, String> adminAccounts = new HashMap<>();

    private List<NotificationObserver> observers = new ArrayList<>();

    private boolean testMode = false;

    public AuthenticationService() {
        loadAccounts(USER_FILE, userAccounts);
        loadAccounts(ADMIN_FILE, adminAccounts);
        initializeDefaultAdmin();
    }

    public AuthenticationService(NotificationObserver observer) {
        this.testMode = true;
        initEmptyMaps();
        if (observer != null) {
            addObserver(observer);
        }
        initializeDefaultAdmin();
    }

    public AuthenticationService(boolean testMode) {
        this.testMode = testMode;
        initEmptyMaps();
        initializeDefaultAdmin();
    }

    private void initEmptyMaps() {
        userAccounts = new HashMap<>();
        userEmails = new HashMap<>();
        adminAccounts = new HashMap<>();
    }

    private void initializeDefaultAdmin() {
        if (!adminAccounts.containsKey(DEFAULT_ADMIN)) {
            adminAccounts.put(DEFAULT_ADMIN, DEFAULT_ADMIN_PASS);
            if (!testMode) {
                saveAccount(ADMIN_FILE, DEFAULT_ADMIN, DEFAULT_ADMIN_PASS);
            }
        }
    }

    // ميثود جلب الإيميل الحقيقي لليوزر
    public String getUserEmail(String username) {
        if (username == null) return "";
        String email = userEmails.get(username.trim());
        if (email != null && !email.isEmpty()) {
            return email;
        }
        return username.trim() + "@gmail.com";
    }

    public void addObserver(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    private void notifyObservers(String email, String message) {
        for (NotificationObserver observer : observers) {
            observer.update(email, message);
        }
    }

    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9._]+$");
    }

    public boolean registerNewUser(String username, String password, String email) {
        if (!isValidNewUser(username, userAccounts)) return false;

        userAccounts.put(username, password);
        userEmails.put(username, email);

        // ✅ إرسال إيميل ترحيبي فوراً عند التسجيل
        notifyObservers(email, "Welcome " + username + "! Your account has been created successfully in our Appointment System.");

        if (!testMode) {
            saveAccount(USER_FILE, username, password + "," + email);
        }

        return true;
    }

    public boolean registerNewAdmin(String username, String password) {
        if (!isValidNewUser(username, adminAccounts)) return false;

        adminAccounts.put(username, password);

        if (!testMode) {
            saveAccount(ADMIN_FILE, username, password);
        }

        return true;
    }

    private boolean isValidNewUser(String username, Map<String, String> map) {
        return isValidUsername(username) && !map.containsKey(username);
    }

    public Role login(String username, String password) {
        String u = (username != null) ? username.trim() : "";
        String p = (password != null) ? password.trim() : "";

        if (isValidLogin(adminAccounts, u, p)) {
            return Role.ADMIN;
        }

        if (isValidLogin(userAccounts, u, p)) {
            return Role.USER;
        }

        return Role.NONE;
    }

    private boolean isValidLogin(Map<String, String> map, String username, String password) {
        return map.containsKey(username) && map.get(username).equals(password);
    }

    private void saveAccount(String filePath, String user, String data) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.println(user + "," + data);
        } catch (IOException e) {
            System.err.println("Error saving to " + filePath + ": " + e.getMessage());
        }
    }

    private void loadAccounts(String filePath, Map<String, String> map) {
        if (testMode) return;

        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    map.put(username, password);
                    if (filePath.equals(USER_FILE) && parts.length >= 3) {
                        userEmails.put(username, parts[2].trim());
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