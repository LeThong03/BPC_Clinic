package com.bpc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {
    private BPC_Patient patient;
    private BPC_Physiotherapist physiotherapist;
    private BPC_Treatment treatment;
    private BPC_Booking booking;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        patient = new BPC_Patient("P1", "Test Patient", "123 Test St", "555-1234");
        physiotherapist = new BPC_Physiotherapist(
                "PHY1",
                "Test Physio",
                "456 Test Ave",
                "555-5678",
                Arrays.asList("Test Specialty", "Test Treatment")
        );
        dateTime = LocalDateTime.of(2025, 6, 16, 10, 0);
        treatment = new BPC_Treatment("Test Treatment", physiotherapist, dateTime);
        booking = new BPC_Booking("B1", patient, treatment);
    }

    @Test
    void testInitialState() {
        // A newly created booking should be in BOOKED status
        assertEquals(BPC_BookingStatus.BOOKED, booking.getStatus());
        assertEquals(patient, booking.getPatient());
        assertEquals(treatment, booking.getTreatment());
        assertEquals("B1", booking.getId());
        assertNotNull(booking.getBookingTime());
    }

    @Test
    void testCancelBooking() {
        // Given a booked appointment
        assertEquals(BPC_BookingStatus.BOOKED, booking.getStatus());

        // When cancelling the booking
        booking.cancelBooking();

        // Then the status should be CANCELLED
        assertEquals(BPC_BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void testAttend() {
        // Given a booked appointment
        assertEquals(BPC_BookingStatus.BOOKED, booking.getStatus());

        // When marking as attended
        booking.attend();

        // Then the status should be ATTENDED
        assertEquals(BPC_BookingStatus.ATTENDED, booking.getStatus());
    }

    @Test
    void testCantCancelAttendedBooking() {
        // Given an attended booking
        booking.attend();

        //throw exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            booking.cancelBooking();
        });

        // And the message should indicate the booking is not in BOOKED state
        assertTrue(exception.getMessage().contains("not confirmed") ||
                exception.getMessage().contains("not in BOOKED state"));
    }

    @Test
    void testCantAttendCancelledBooking() {
        // Given a cancelled booking
        booking.cancelBooking();

        // When trying to mark as attended
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            booking.attend();
        });

        // And the message should indicate the booking is not in BOOKED state
        assertTrue(exception.getMessage().contains("not confirmed") ||
                exception.getMessage().contains("not in BOOKED state"));
    }

    @Test
    void testChangeBooking() {
        // Given a booked appointment and a new treatment
        BPC_Treatment newTreatment = new BPC_Treatment(
                "New Treatment",
                physiotherapist,
                dateTime.plusHours(1)
        );

        // When changing the booking to the new treatment
        booking.changeBooking(newTreatment);

        // Then the booking should have the new treatment
        assertEquals(newTreatment, booking.getTreatment());
        assertEquals("New Treatment", booking.getTreatment().getName());
        assertEquals(dateTime.plusHours(1), booking.getTreatment().getDateTime());
    }

    @Test
    void testCantChangeAttendedBooking() {
        booking.attend();
        // And a new treatment
        BPC_Treatment newTreatment = new BPC_Treatment(
                "New Treatment",
                physiotherapist,
                dateTime.plusHours(1)
        );

        //throw exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            booking.changeBooking(newTreatment);
        });

        // And the message should indicate the booking cannot be changed
        assertTrue(exception.getMessage().contains("Only active bookings") ||
                exception.getMessage().contains("not in BOOKED state"));
    }

    @Test
    void testCantChangeCancelledBooking() {
        // Given a cancelled booking
        booking.cancelBooking();

        // And a new treatment
        BPC_Treatment newTreatment = new BPC_Treatment(
                "New Treatment",
                physiotherapist,
                dateTime.plusHours(1)
        );

        //when trying to change the booking, throw an exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            booking.changeBooking(newTreatment);
        });

        // And the message should indicate the booking cannot be changed
        assertTrue(exception.getMessage().contains("Only active bookings") ||
                exception.getMessage().contains("not in BOOKED state"));
    }
}
