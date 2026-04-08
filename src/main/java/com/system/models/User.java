package com.system.models;

/**
 * هذا الكلاس يمثل المستخدم في النظام (سواء كان مدير أو طالب).
 * Javadoc: @author اسمك واسم زميلك
 */
public class User {
    private String username; // اسم الدخول
    private String password; // كلمة السر
    private String role;     // الرتبة: "ADMIN" أو "USER"

    // هذا هو الـ Constructor (المسؤول عن إنشاء مستخدم جديد)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // هذه ميثودز (Getters) لجلب البيانات من الكلاس
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}