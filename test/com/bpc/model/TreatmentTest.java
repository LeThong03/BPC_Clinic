package com.bpc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BPC_Treatment.
 */
public class TreatmentTest {
    private BPC_Physiotherapist physiotherapist;
    private BPC_Treatment treatment;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        physiotherapist = new BPC_Physiotherapist(
                "PHY1",
                "Test Physio",
                "123 Test Ave",
                "555-1234",
                Arrays.asList("Test Specialty", "Test Treatment")
        );

        dateTime = LocalDateTime.of(2025, 6, 16, 10, 0);
        treatment = new BPC_Treatment("Test Treatment", physiotherapist, dateTime);
    }

    @Test
    void testInitialState() {
        // A newly created treatment should not be booked
        assertTrue(treatment.isAvailable());
        assertFalse(treatment.isBooked());
        assertEquals("Test Treatment", treatment.getName());
        assertEquals(physiotherapist, treatment.getPhysiotherapist());
        assertEquals(dateTime, treatment.getDateTime());
    }

    @Test
    void testMarkAsBooked() {
        // Given an available treatment
        assertTrue(treatment.isAvailable());

        // When marking as booked
        treatment.markAsBooked();

        // Then the treatment should be booked
        assertFalse(treatment.isAvailable());
        assertTrue(treatment.isBooked());
    }

    @Test
    void testCantMarkUnavailableTreatmentAsBooked() {
        // Given a treatment already marked as booked
        treatment.markAsBooked();

        // When trying to mark it as booked again
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            treatment.markAsBooked();
        });

        // And the message should indicate the treatment is already booked
        assertTrue(exception.getMessage().contains("already booked"));
    }

    @Test
    void testTreatmentAvailabilityDependsOnPhysiotherapistAvailability() {
        // Given a physiotherapist who becomes unavailable
        physiotherapist.deactivate();

        // Then the treatment should not be available
        assertFalse(treatment.isAvailable());

        // And when the physiotherapist becomes available again
        physiotherapist.activate();

        // Then the treatment should be available again
        assertTrue(treatment.isAvailable());
    }

    @Test
    void testTreatmentWithUnavailableTimeSlot() {
        // Given a physiotherapist with an assigned appointment
        physiotherapist.assignAppointment(dateTime);

        // Then a treatment for that time slot should not be available
        assertFalse(treatment.isAvailable());
    }

    @Test
    void testMarkAsUnbooked() {
        // Given a booked treatment
        treatment.markAsBooked();
        assertTrue(treatment.isBooked());

        // When marking as unbooked
        treatment.markAsUnbooked();

        // Then the treatment should be available again
        assertFalse(treatment.isBooked());
        assertTrue(treatment.isAvailable());
    }

    @Test
    void testConstructorWithNullParameters() {
        // When creating a treatment with null parameters
        // Then exceptions should be thrown

        assertThrows(IllegalArgumentException.class, () -> {
            new BPC_Treatment(null, physiotherapist, dateTime);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new BPC_Treatment("Test Treatment", null, dateTime);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new BPC_Treatment("Test Treatment", physiotherapist, null);
        });
    }

    @Test
    void testConstructorWithPastDateTime() {
        // When creating a treatment with a past date
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);

        // Then an exception should be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            new BPC_Treatment("Test Treatment", physiotherapist, pastDateTime);
        });
    }
}
