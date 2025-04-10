package com.bpc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatientTest {
    private BPC_Patient patient;
    private BPC_Physiotherapist physio;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        patient = new BPC_Patient("P1", "Test Patient", "123 Test St", "555-1234");
        physio = new BPC_Physiotherapist("PHY1", "Test Physio", "456 Test Ave", "555-5678",
                List.of("Test Specialty"));
        dateTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    }

    @Test
    void testAddBooking() {
        BPC_Treatment treatment = new BPC_Treatment("Test Treatment", physio, dateTime);
        BPC_Booking booking = new BPC_Booking("B1", patient, treatment, BPC_BookingStatus.BOOKED, LocalDateTime.now());

        patient.addBooking(booking);

        assertEquals(1, patient.getBookings().size());
        assertEquals(booking, patient.getBookings().get(0));
    }

    @Test
    void testHasActiveBookings() {
        // Create a booking and check if the patient has active bookings
        assertFalse(patient.hasActiveBooking());

        // Create a booking
        BPC_Treatment treatment = new BPC_Treatment("Test Treatment", physio, dateTime);
        BPC_Booking booking = new BPC_Booking("B1", patient, treatment, BPC_BookingStatus.BOOKED, LocalDateTime.now());
        patient.addBooking(booking);

        //The patient should now have an active booking
        assertTrue(patient.hasActiveBooking());

        //not if the booking is cancelled
        booking.cancelBooking();
        assertFalse(patient.hasActiveBooking());
    }

    @Test
    void testDeactivatePatient() {
        // Deactivate the patient
        assertTrue(patient.isActive());

        //when deactivate
        patient.deactivate();

        //patient should be inactive
        assertFalse(patient.isActive());
    }

    @Test
    void testReactivatePatient() {
        // Deactivate the patient
        patient.deactivate();
        assertFalse(patient.isActive());

        // Reactivate the patient
        patient.reactivate();

        //patient should be active
        assertTrue(patient.isActive());
    }

    @Test
    void testCannotDeactivateWithActiveBookings(){
        //Given a patient with an active booking
        BPC_Treatment treatment = new BPC_Treatment("Test Treatment", physio, dateTime);
        BPC_Booking booking = new BPC_Booking("B1", patient, treatment, BPC_BookingStatus.BOOKED, LocalDateTime.now());
        patient.addBooking(booking);

        //When deactivating the patient
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            patient.deactivate();
        });

        assertTrue(exception.getMessage().contains("active booking"));

        //The patient should still be active
        assertTrue(patient.hasActiveBooking());
    }

    @Test
    void testCannotAddBookingToInactivePatient() {
        // Given an inactive patient
        patient.deactivate();
        assertFalse(patient.isActive());

        // Create a booking
        BPC_Treatment treatment = new BPC_Treatment("Test Treatment", physio, dateTime);
        BPC_Booking booking = new BPC_Booking("B1", patient, treatment, BPC_BookingStatus.BOOKED, LocalDateTime.now());

        // Attempt to add the booking to the inactive patient
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            patient.addBooking(booking);
        });

        assertTrue(exception.getMessage().contains("inactive patient"));
    }
}
