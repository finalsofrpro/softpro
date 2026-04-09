package com.system.services;

import com.system.models.Role;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Raghd Mansour
 * خدمة التحقق من الهوية - تدعم الرموز المسموحة وتجاهل حالة الأحرف
 */
public class AuthenticationService {
    private Map<String, String> userAccounts = new HashMap<>();
    private Map<String, String> adminAccounts = new HashMap<>();

    private final String USER_FILE = "users.txt";
    private final String ADMIN_FILE = "admins.txt";

    public AuthenticationService() {
        // تحميل الحسابات من الملفات فور تشغيل النظام
        loadAccounts(USER_FILE, userAccounts);
        loadAccounts(ADMIN_FILE, adminAccounts);

        // إذا كان ملف الإدارة فارغاً، ننشئ الأدمن الافتراضي
        if (adminAccounts.isEmpty()) {
            adminAccounts.put("admin", "admin123");
            saveAccount(ADMIN_FILE, "admin", "admin123");
        }
    }

    /**
     * فحص صحة صيغة اسم المستخدم:
     * يسمح فقط بالأحرف (A-Z, a-z)، الأرقام (0-9)، النقطة (.)، والشرطة السفلية (_)
     */
    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9._]+$");
    }

    /**
     * ميثود التسجيل العامة لليوزر
     */
    public boolean register(String username, String password) {
        return registerNewUser(username, password);
    }

    // تسجيل مستخدم جديد (فحص الصيغة + فحص التكرار بغض النظر عن الحالة)
    public boolean registerNewUser(String username, String password) {
        // 1. فحص الرموز المسموحة
        if (!isValidUsername(username)) {
            System.err.println("Invalid username format!");
            return false;
        }

        // 2. فحص التكرار (Ignore Case)
        for (String existingUser : userAccounts.keySet()) {
            if (existingUser.equalsIgnoreCase(username)) {
                return false;
            }
        }

        userAccounts.put(username, password);
        saveAccount(USER_FILE, username, password);
        return true;
    }

    // تسجيل أدمن جديد (فحص الصيغة + فحص التكرار بغض النظر عن الحالة)
    public boolean registerNewAdmin(String username, String password) {
        // 1. فحص الرموز المسموحة
        if (!isValidUsername(username)) {
            System.err.println("Invalid admin username format!");
            return false;
        }

        // 2. فحص التكرار (Ignore Case)
        for (String existingAdmin : adminAccounts.keySet()) {
            if (existingAdmin.equalsIgnoreCase(username)) {
                return false;
            }
        }

        adminAccounts.put(username, password);
        saveAccount(ADMIN_FILE, username, password);
        return true;
    }

    // فحص بيانات الدخول (اليوزرنيم غير حساس للحالة | الباسورد حساس للحالة)
    public Role login(String username, String password) {
        // 1. فحص في قائمة الأدمن
        for (Map.Entry<String, String> entry : adminAccounts.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(username)) {
                if (entry.getValue().equals(password)) {
                    return Role.ADMIN;
                }
            }
        }

        // 2. فحص في قائمة اليوزرز
        for (Map.Entry<String, String> entry : userAccounts.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(username)) {
                if (entry.getValue().equals(password)) {
                    return Role.USER;
                }
            }
        }

        return Role.NONE;
    }

    public void logout() {
        System.out.println("[System]: Logged out successfully.");
    }

    private void saveAccount(String filePath, String user, String pass) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.println(user + "," + pass);
        } catch (IOException e) {
            System.err.println("Error saving account: " + e.getMessage());
        }
    }

    private void loadAccounts(String filePath, Map<String, String> map) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading file: " + filePath);
        }
    }
}