package com.system.repository;

import com.system.models.Appointment;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class responsible for managing appointment data persistence.
 * It handles saving to and loading from a text file, ensuring data integrity
 * and preventing time conflicts.
 * * @author Raghad and Farah
 * @version 1.0
 */
public class AppointmentRepository {
    private List<Appointment> appointments = new ArrayList<>();
    private final String FILE_NAME = "appointments.txt";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructs the repository and automatically loads existing appointments from the file.
     */
    public AppointmentRepository() {
        loadFromFile();
    }

    /**
     * Adds a new appointment to the system after validating ID uniqueness
     * and checking for schedule overlaps.
     * * @param newApp The new appointment object to be added.
     * @throws IllegalArgumentException If the ID exists or if there is a time conflict.
     */
    public void addAppointment(Appointment newApp) {
        LocalDateTime newStart = newApp.getDateTime();
        LocalDateTime newEnd = newStart.plusMinutes(newApp.getDurationMinutes());

        for (Appointment existing : appointments) {
            // Check for duplicate ID
            if (existing.getId() == newApp.getId()) {
                throw new IllegalArgumentException("Appointment ID already exists!");
            }

            // Check for Overlapping time
            LocalDateTime existingStart = existing.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                throw new IllegalArgumentException("Time Conflict! This slot overlaps with an existing appointment ("
                        + existingStart.format(formatter) + " for " + existing.getDurationMinutes() + " min).");
            }
        }

        appointments.add(newApp);
        saveToFile();
    }

    /**
     * Removes an appointment from the list based on its ID and updates the storage.
     * * @param id The unique identifier of the appointment to be deleted.
     */
    public void deleteAppointment(int id) {
        appointments.removeIf(a -> a.getId() == id);
        saveToFile();
    }

    /**
     * Retrieves all available appointments by refreshing the data from the file.
     * * @return A list of all stored appointments.
     */
    public List<Appointment> getAvailableAppointments() {
        appointments.clear();
        loadFromFile();
        return appointments;
    }

    /**
     * Synchronizes the current list of appointments to the text file.
     * Data is stored using the Pipe (|) delimiter for structured parsing.
     */
    public void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Appointment app : appointments) {
                // Format: ID|DateTime|Duration|Status|MaxParticipants|Type|BookedBy
                out.println(app.getId() + "|" +
                        app.getDateTime() + "|" +
                        app.getDurationMinutes() + "|" +
                        app.getStatus() + "|" +
                        app.getMaxParticipants() + "|" +
                        app.getType() + "|" +
                        app.getBookedBy());
            }
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }

    /**
     * Loads appointment records from the text file into the in-memory list.
     * Parses each line and reconstructs Appointment objects.
     */
    public void loadFromFile() {
        appointments.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split("\\|");

                if (p.length >= 6) {
                    int id = Integer.parseInt(p[0]);
                    LocalDateTime dt = LocalDateTime.parse(p[1]);
                    int dur = Integer.parseInt(p[2]);
                    String status = p[3];
                    int max = Integer.parseInt(p[4]);
                    String type = p[5];
                    String bookedBy = (p.length > 6) ? p[6] : "";

                    Appointment app = new Appointment(id, dt, dur, max, type);
                    app.setStatus(status);
                    app.setBookedBy(bookedBy);
                    appointments.add(app);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }
}