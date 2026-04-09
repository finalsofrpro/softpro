package com.system.models;

import java.time.LocalDateTime;

 //كلاس الموعد المحدث ليشمل النوع والسعة

public class Appointment {
    private int id;
    private LocalDateTime dateTime;
    private int durationMinutes;
    private String status; // "AVAILABLE" أو "BOOKED"
    private int maxParticipants; // US2.3
    private String type; // US5.1 (مثلاً: Urgent, Virtual)
    private String bookedBy = ""; // أضيفي هذا الحقل مع Getter و Setter

    // الـ Constructor المحدث
    public Appointment(int id, LocalDateTime dateTime, int durationMinutes) {
        this.id = id;
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.status = "AVAILABLE";
        this.maxParticipants = 1; // القيمة الافتراضية شخص واحد
        this.type = "General";    // النوع الافتراضي
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

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }
}