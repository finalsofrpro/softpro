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

    // ✅ تشغيل طبيعي (يقرأ الملفات عند البدء)
    public AuthenticationService() {
        loadAccounts(USER_FILE, userAccounts);
        loadAccounts(ADMIN_FILE, adminAccounts);
        initializeDefaultAdmin();
    }

    // ✅ مشغل للتست مع Observer
    public AuthenticationService(NotificationObserver observer) {
        this.testMode = true;
        initEmptyMaps();
        if (observer != null) {
            addObserver(observer);
        }
        initializeDefaultAdmin();
    }

    // ✅ مشغل للتست العام
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

    public boolean registerNewUser(String username, String password, String email) {
        if (!isValidNewUser(username, userAccounts)) return false;

        userAccounts.put(username, password);
        userEmails.put(username, email);

        notifyObservers(email, "Welcome " + username);

        if (!testMode) {
            // يتم الحفظ بتنسيق: username,password,email
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
        // استخدام trim() للتخلص من أي مسافات زائدة قد تسبب فشل اللوجن
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
                processLine(filePath, map, line);
            }
        } catch (IOException e) {
            System.err.println("Error loading: " + filePath);
        }
    }

    private void processLine(String filePath, Map<String, String> map, String line) {
        String[] parts = line.split(",");

        if (parts.length >= 2) {
            String username = parts[0].trim();
            String password = parts[1].trim();

            map.put(username, password);

            // إذا كان ملف يوزرز وفيه إيميل (الخيار الثالث)
            if (filePath.equals(USER_FILE) && parts.length >= 3) {
                userEmails.put(username, parts[2].trim());
            }
        }
    }

    public void logout() {
        System.out.println("[System]: Logged out.");
    }
}