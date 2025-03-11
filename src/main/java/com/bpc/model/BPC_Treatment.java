package main.java.com.bpc.model;

import java.time.LocalDateTime;

public class BPC_Treatment {
    private String name;
    private LocalDateTime dateTime;
    private BPC_Physiotherapist physiotherapist;
    private boolean booked;

    /**
     *
     * @param name Name of the treatment
     * @param physiotherapist Physiotherapist assigned to the treatment
     * @param dateTime Date and time of the treatment
     */

    public BPC_Treatment(String name, BPC_Physiotherapist physiotherapist, LocalDateTime dateTime) {
        this.name = name;
        this.dateTime = dateTime;
        this.physiotherapist = physiotherapist;
        this.booked = false;
    }

    /**
     * Checked if the timetable slot is booked
     *
     * @return true if the slot is booked, false otherwise
     */
    public boolean isAvailable() {
        return !booked && physiotherapist.isAvailable(dateTime);
    }

    /**
     * Marks the treatment as booked and assigns the appointment to the physiotherapist
     *
     * @throws IllegalArgumentException if the treatment is already booked
     */
    public void markAsBooked() {
        if (!isAvailable()) {
            throw new IllegalArgumentException("Treatment is already booked");
        }
        booked = true;
        physiotherapist.assignAppointment(dateTime);
    }

    //getters
    public String getName(){  return name;  }
    public BPC_Physiotherapist getPhysiotherapist(){  return physiotherapist;  }
    public LocalDateTime getDateTime(){  return dateTime;  }
    public boolean isBooked(){  return booked;  }
}
