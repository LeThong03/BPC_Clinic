package com.bpc.system;

import com.bpc.model.*;
import com.bpc.util.BPC_IdGenerator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BPC_Clinic {
    private Map<String, BPC_Physiotherapist> physiotherapists;
    private Map<String, BPC_Patient> patients;
    private Map<String, BPC_Booking> bookings;

    public BPC_Clinic() {
        physiotherapists = new HashMap<>();
        patients = new HashMap<>();
        bookings = new HashMap<>();
    }

    //patient management
    public BPC_Patient addPatient(String name, String address, String phone){
        String id = BPC_IdGenerator.generatePatientId();
        BPC_Patient patient = new BPC_Patient(id, name, address, phone);
        patients.put(id, patient);
        return patient;
    }

    //physiotherapist management
    public BPC_Physiotherapist addPhysiotherapist(String name, String address, String phone, List<String> expertise) {
        String id = BPC_IdGenerator.generatePhysiotherapistId();
        BPC_Physiotherapist physiotherapist = new BPC_Physiotherapist(id, name, address, phone, expertise);
        physiotherapists.put(id, physiotherapist);
        return physiotherapist;
    }

    //booking management
    public BPC_Booking createBooking(String patientId, String physiotherapistId,
                                     String treatmentName, LocalDateTime dateTime) {
        BPC_Patient patient = patients.get(patientId);
        BPC_Physiotherapist physiotherapist = physiotherapists.get(physiotherapistId);

        if (!physiotherapist.isAvailable(dateTime)) {
            throw new IllegalStateException("Physiotherapist is not available at this time");
        }
        String bookingId = BPC_IdGenerator.generateBookingId();
        BPC_Treatment treatment = new BPC_Treatment(treatmentName, physiotherapist, dateTime);
        BPC_Booking booking = new BPC_Booking(bookingId, patient, treatment);

        patient.addBooking(booking);
        bookings.put(bookingId, booking);

        return booking;
    }

    public void cancelBooking(String bookingId) {
        BPC_Booking booking = bookings.get(bookingId);
        booking.cancelBooking();
    }

    public void attendBooking(String bookingId) {
        BPC_Booking booking = bookings.get(bookingId);
        booking.attend();
    }

    public void modifyBooking(String bookingId, String physiotherapistId, String treatmentName, LocalDateTime dateTime) {
        BPC_Booking booking = getBooking(bookingId);
        BPC_Physiotherapist physiotherapist = getPhysiotherapist(physiotherapistId);

        if (!physiotherapist.isAvailable(dateTime)) {
            throw new IllegalStateException("Physiotherapist is not available at this time");
        }
        BPC_Treatment newTreatment = new BPC_Treatment(treatmentName, physiotherapist, dateTime);
        booking.changeBooking(newTreatment);
    }

    //report functionality
    public Map<BPC_Physiotherapist, List<BPC_Booking>> getPhysiotherapistBookings(){
        return bookings.values().stream()
                .collect(Collectors.groupingBy(booking -> booking.getTreatment().getPhysiotherapist()));
    }

    public List<Map.Entry<BPC_Physiotherapist, Long>> getPhysiotherapistByAttendedBookings(){
        Map<BPC_Physiotherapist, Long> attendedCounts = bookings.values().stream()
                .filter(b -> b.getStatus() == BPC_BookingStatus.ATTENDED)
                .collect(Collectors.groupingBy(b -> b.getTreatment().getPhysiotherapist(), Collectors.counting()));

        // Add .reversed() to sort in descending order
        return attendedCounts.entrySet().stream()
                .sorted(Map.Entry.<BPC_Physiotherapist, Long>comparingByValue().reversed())
                .collect(Collectors.toList());
    }


    public String generateReport(){
        StringBuilder report = new StringBuilder();
        report.append("BOOST PHYSIO CLINIC - REPORT\n");
        report.append("==============================\n\n");

        // List all treatment appointments for each physiotherapist
        report.append("APPOINTMENTS BY PHYSIOTHERAPIST\n");
        report.append("------------------------------\n");
        Map<BPC_Physiotherapist, List<BPC_Booking>> physioBookings = getPhysiotherapistBookings();

        for (Map.Entry<BPC_Physiotherapist, List<BPC_Booking>> entry : physioBookings.entrySet()) {
            BPC_Physiotherapist physio = entry.getKey();
            List<BPC_Booking> appointments = entry.getValue();

            report.append(String.format("%s (%s)\n", physio.getName(), physio.getId()));

            if (appointments.isEmpty()) {
                report.append("  No appointments\n");
            } else {
                for (BPC_Booking booking : appointments) {
                    report.append(String.format("  %s | %s | %s | %s | %s\n",
                            booking.getId(),
                            booking.getTreatment().getDateTime().toString(),
                            booking.getTreatment().getName(),
                            booking.getPatient().getName(),
                            booking.getStatus()
                    ));
                }
            }
            report.append("\n");
        }
        // List physiotherapists by number of attended appointments
        report.append("PHYSIOTHERAPISTS BY ATTENDED APPOINTMENTS\n");
        report.append("----------------------------------------\n");
        List<Map.Entry<BPC_Physiotherapist, Long>> ranked = getPhysiotherapistByAttendedBookings();

        for (Map.Entry<BPC_Physiotherapist, Long> entry : ranked) {
            report.append(String.format("%s (%s): %d attended appointments\n",
                    entry.getKey().getName(),
                    entry.getKey().getId(),
                    entry.getValue()
            ));
        }

        return report.toString();
    }

    private BPC_Physiotherapist getPhysiotherapist(String physiotherapistId) {
        BPC_Physiotherapist physiotherapist = physiotherapists.get(physiotherapistId);
        if (physiotherapist == null) {
            throw new IllegalArgumentException("Physiotherapist not found: " + physiotherapistId);
        }
        if (!physiotherapist.isActive()) {
            throw new IllegalArgumentException("Physiotherapist is not active: " + physiotherapistId);
        }
        return physiotherapist;
    }

    private BPC_Booking getBooking(String bookingId) {
        BPC_Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found" + bookingId);
        }
        return booking;
    }

    //getters and setters
    public Map<String, BPC_Physiotherapist> getPhysiotherapists(){
        return new HashMap<>(physiotherapists);
    }

    public Map<String, BPC_Patient> getPatients(){
        return new HashMap<>(patients);
    }

    public Map<String, BPC_Booking> getBookings(){
        return new HashMap<>(bookings);
    }

}
