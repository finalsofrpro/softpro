package com.system.models;

import java.time.LocalDateTime;

/**
 * كلاس الموعد المحدث ليشمل النوع والسعة وتفاصيل الحجز.
 */
public class Appointment {
    private int id;
    private LocalDateTime dateTime;
    private int durationMinutes;
    private String status; // "AVAILABLE" أو "BOOKED"
    private int maxParticipants;
    private String type; // US5.1 (مثلاً: Urgent, Virtual, Follow-up)
    private String bookedBy;

    // 1. الـ Constructor التوافقي (للإبقاء على توافقية الكود القديم)
    public Appointment(int id, LocalDateTime dateTime, int durationMinutes) {
        this(id, dateTime, durationMinutes, 1, "General");
    }

    // 2. الـ Constructor الأساسي الجديد (المستخدم في الـ Repository والخدمات الجديدة)
    public Appointment(int id, LocalDateTime dateTime, int durationMinutes, int maxParticipants, String type) {
        this.id = id;
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.maxParticipants = maxParticipants;
        this.type = type;
        this.status = "AVAILABLE";
        this.bookedBy = "";
    }

    // Getters and Setters
    public int getId() { return id; }
    public LocalDateTime getDateTime() { return dateTime; }
    public int getDurationMinutes() { return durationMinutes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBookedBy() { return bookedBy; }
    public void setBookedBy(String bookedBy) { this.bookedBy = bookedBy; }
}