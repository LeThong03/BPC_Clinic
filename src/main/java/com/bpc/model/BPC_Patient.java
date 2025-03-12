package main.java.com.bpc.model;

import java.util.ArrayList;
import java.util.List;

public class BPC_Patient {
    //declare patient variables
    private String id;
    private String name;
    private String address;
    private String phone;
    private List<BPC_Booking> bookings;
    private boolean active;

    public BPC_Patient(String id, String name, String address, String phone) {
        //initialize patient variables
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.bookings = new ArrayList<>();
        this.active = true;
    }

    public void addBooking(BPC_Booking booking) {
        //add booking to patient
        if (!active) {
            throw new IllegalArgumentException("Cannot add booking to inactive patient");
        }
        if (hasOverlappingBooking(booking)) {
            throw new IllegalArgumentException("Patient already has a booking at this time");
        }
        bookings.add(booking);
    }

    public boolean hasOverlappingBooking(BPC_Booking newBooking) {
        return bookings.stream()
                .filter(b -> b.getStatus() == BPC_BookingStatus.BOOKED)
                .anyMatch(b -> b.getTreatment().getDateTime()
                        .equals(newBooking.getTreatment().getDateTime()));
    }

    public boolean hasActiveBooking() {
        return bookings.stream()
                .anyMatch(b -> b.getStatus() == BPC_BookingStatus.BOOKED);
    }

    public void deactivate() {
        //deactivate patient
        if (hasActiveBooking()) {
            throw new IllegalArgumentException("Cannot deactivate patient with active booking");
        }
        this.active = false;
    }

    public void reactivate() {
        //reactivate patient
        this.active = true;
    }

    //Getter and Setter
    public String getId() { return id;  }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public List<BPC_Booking> getBookings() { return bookings; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
