package com.system.models;

/**
 * هذا الكلاس يمثل المستخدم في النظام (سواء كان مدير أو طالب).
 */
public class User {
    private String username;
    private String password;
    private String role; // "ADMIN" أو "USER"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}