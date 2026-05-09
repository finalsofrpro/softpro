package com.system.services;

import com.system.models.Role;
import com.system.observers.NotificationObserver;
import java.io.*;
import java.util.*;

public class AuthenticationService {

    private Map<String, String> userAccounts = new HashMap<>();
    private Map<String, String> userEmails = new HashMap<>();
    private Map<String, String> adminAccounts = new HashMap<>();
    private List<NotificationObserver> observers = new ArrayList<>();

    private final String USER_FILE = "users.txt";
    private final String ADMIN_FILE = "admins.txt";

    private boolean testMode = false;

    // ✅ تشغيل طبيعي (GUI)
    public AuthenticationService() {
        loadAccounts(USER_FILE, userAccounts);
        loadAccounts(ADMIN_FILE, adminAccounts);

        addObserver(new EmailService());

        if (adminAccounts.isEmpty()) {
            adminAccounts.put("admin", "admin123");
            saveAccount(ADMIN_FILE, "admin", "admin123");
        }
    }

    // ✅ تشغيل التست (بدون ملفات)
    public AuthenticationService(boolean testMode) {
        this.testMode = testMode;

        // 🔥 أهم تعديل (يمنع أي بيانات قديمة)
        userAccounts = new HashMap<>();
        userEmails = new HashMap<>();
        adminAccounts = new HashMap<>();

        addObserver(new EmailService());
        adminAccounts.put("admin", "admin123");
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
        if (!isValidUsername(username)) return false;

        // ✅ أسرع وأضمن
        if (userAccounts.containsKey(username)) return false;

        userAccounts.put(username, password);
        userEmails.put(username, email);

        if (!testMode) {
            saveAccount(USER_FILE, username, password + "," + email);
        }

        return true;
    }

    public boolean registerNewAdmin(String username, String password) {
        if (!isValidUsername(username)) return false;

        if (adminAccounts.containsKey(username)) return false;

        adminAccounts.put(username, password);

        if (!testMode) {
            saveAccount(ADMIN_FILE, username, password);
        }

        return true;
    }

    public Role login(String username, String password) {
        if (adminAccounts.containsKey(username) &&
                adminAccounts.get(username).equals(password)) {
            return Role.ADMIN;
        }

        if (userAccounts.containsKey(username) &&
                userAccounts.get(username).equals(password)) {
            return Role.USER;
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
        if (testMode) return;

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