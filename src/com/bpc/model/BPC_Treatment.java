package com.bpc.model;

import java.time.LocalDateTime;

public class BPC_Treatment {
    private String name;
    private LocalDateTime dateTime;
    private BPC_Physiotherapist physiotherapist;
    private boolean booked;

    /**
     * Creates a new treatment
     *
     * @param name Name of the treatment
     * @param physiotherapist Physiotherapist assigned to the treatment
     * @param dateTime Date and time of the treatment
     * @throws IllegalArgumentException if any parameter is null or the dateTime is in the past
     */
    public BPC_Treatment(String name, BPC_Physiotherapist physiotherapist, LocalDateTime dateTime) {
        // Validate parameters are not null
        if (name == null || physiotherapist == null || dateTime == null) {
            throw new IllegalArgumentException("All parameters must be non-null");
        }

        // Validate that appointment time is not in the past
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Treatment date cannot be in the past");
        }

        this.name = name;
        this.dateTime = dateTime;
        this.physiotherapist = physiotherapist;
        this.booked = false;
    }

    /**
     * Checks if the treatment is available for booking
     *
     * @return true if the slot is available, false otherwise
     */
    public boolean isAvailable() {
        return !booked && physiotherapist.isAvailable(dateTime);
    }

    /**
     * Marks the treatment as booked and assigns the appointment to the physiotherapist
     *
     * @throws IllegalArgumentException if the treatment is already booked
     * @throws IllegalStateException if the treatment date is now in the past
     */
    public void markAsBooked() {
        if (!isAvailable()) {
            throw new IllegalArgumentException("Treatment is already booked");
        }

        // Double-check the date is not in the past at booking time
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot book a treatment in the past");
        }

        booked = true;
        physiotherapist.assignAppointment(dateTime);
    }

    /**
     * Marks the treatment as unbooked and frees the appointment slot
     */
    public void markAsUnbooked() {
        if (booked) {
            booked = false;
            physiotherapist.freeAppointment(dateTime);
        }
    }

    // Getters
    public String getName() { return name; }
    public BPC_Physiotherapist getPhysiotherapist() { return physiotherapist; }
    public LocalDateTime getDateTime() { return dateTime; }
    public boolean isBooked() { return booked; }
}