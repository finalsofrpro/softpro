package com.system.models;

import java.time.LocalDateTime;

/**
 * Represents an appointment entity within the scheduling system.
 * This class stores appointment details including type, status, and participants.
 * * @author Raghad and Farah
 * @version 1.0
 */
public class Appointment {
    private int id;
    private LocalDateTime dateTime;
    private int durationMinutes;
    private String status; // "AVAILABLE" or "BOOKED"
    private int maxParticipants;
    private String type; // Supports US5.1 (e.g., Urgent, Virtual, Follow-up)
    private String bookedBy;

    /**
     * Compatibility Constructor to support existing legacy code.
     * * @param id              The unique identifier for the appointment.
     * @param dateTime        The date and time of the appointment.
     * @param durationMinutes The duration of the appointment in minutes.
     */
    public Appointment(int id, LocalDateTime dateTime, int durationMinutes) {
        this(id, dateTime, durationMinutes, 1, "General");
    }

    /**
     * Primary Constructor for creating detailed appointments.
     * * @param id              The unique identifier for the appointment.
     * @param dateTime        The date and time of the appointment.
     * @param durationMinutes The duration of the appointment in minutes.
     * @param maxParticipants The maximum number of participants allowed (US2.3).
     * @param type            The category of the appointment (US5.1).
     */
    public Appointment(int id, LocalDateTime dateTime, int durationMinutes, int maxParticipants, String type) {
        this.id = id;
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.maxParticipants = maxParticipants;
        this.type = type;
        this.status = "AVAILABLE";
        this.bookedBy = "";
    }

    // Getters and Setters with Javadoc

    /** @return The appointment ID. */
    public int getId() { return id; }

    /** @return The scheduled date and time. */
    public LocalDateTime getDateTime() { return dateTime; }

    /** @return The duration in minutes. */
    public int getDurationMinutes() { return durationMinutes; }

    /** @return The current status (AVAILABLE/BOOKED). */
    public String getStatus() { return status; }

    /** @param status The new status to be set. */
    public void setStatus(String status) { this.status = status; }

    /** @return The maximum participants limit. */
    public int getMaxParticipants() { return maxParticipants; }

    /** @param maxParticipants The maximum capacity allowed. */
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    /** @return The appointment type. */
    public String getType() { return type; }

    /** @param type The type to classify the appointment. */
    public void setType(String type) { this.type = type; }

    /** @return The username/email of the person who booked the slot. */
    public String getBookedBy() { return bookedBy; }

    /** @param bookedBy The user identifier to assign the booking to. */
    public void setBookedBy(String bookedBy) { this.bookedBy = bookedBy; }
}