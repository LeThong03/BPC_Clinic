package com.bpc.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Objects;

public class BPC_Physiotherapist {
    //declare physiotherapist variables
    private String id;
    private String name;
    private String address;
    private String phone;
    private List <String> expertise;
    private Map<LocalDateTime, Boolean> timetable; //true: available, false: booked
    private boolean isActive;  //true: available, false: not available

    public BPC_Physiotherapist(String id, String name, String address, String phone, List<String> expertise) {
        //initialize physiotherapist variables
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.expertise = new ArrayList<>(expertise);
        this.timetable = initializeTimetable();
        this.isActive = true; //default to available
    }

    private Map<LocalDateTime, Boolean> initializeTimetable() {
        Map<LocalDateTime, Boolean> slots = new TreeMap<>();

        // Set fixed start date to April 1, 2025
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 9, 0);

        // Calculate number of days in April (30)
        int daysInJune = 30;

        // Initialize timetable for April
        for (int day = 0; day < daysInJune; day++) {
            LocalDateTime currentDay = start.plusDays(day);

            // Skip weekends (Saturday = 6, Sunday = 7)
            if (currentDay.getDayOfWeek().getValue() >= 6) {
                continue;
            }

            // 9am to 5pm, 1 hour slots
            for (int hour = 9; hour < 17; hour++) {
                slots.put(currentDay.withHour(hour), true);
            }
        }
        return slots;
    }

    public List<LocalDateTime> getAvailableAppointments() {
        if (!isActive) {
            return new ArrayList<>();
        }
        return timetable.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }

    public boolean isAvailable(LocalDateTime dateTime) {
        return isActive && timetable.getOrDefault(dateTime, false);
    }

    public void assignAppointment(LocalDateTime dateTime) {
        if (!isActive) {
            throw new IllegalStateException("Physiotherapist is not available");
        }
        if (!isAvailable(dateTime)) {
            throw new IllegalStateException("Slot is not available");
        }
        timetable.put(dateTime, false);
    }

    public void freeAppointment(LocalDateTime dateTime) {
        if (timetable.containsKey(dateTime)) {
            timetable.put(dateTime, true);
        }
    }

    // Renamed methods for clarity
    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BPC_Physiotherapist that = (BPC_Physiotherapist) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //Getter and Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public List<String> getExpertise() { return new ArrayList<>(expertise); }
    public boolean isActive() { return isActive; }
}
