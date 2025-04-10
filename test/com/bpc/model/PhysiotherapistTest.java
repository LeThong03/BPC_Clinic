package com.bpc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PhysiotherapistTest {
    private BPC_Physiotherapist physiotherapist;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        physiotherapist = new BPC_Physiotherapist(
                "PHY1",
                "Test Physio",
                "456 Test Ave",
                "555-5678",
                List.of("Test Specialty"));
        dateTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    }

    @Test
    void testInitializeActiveStatus(){
        assertTrue(physiotherapist.isAvailable());
    }

    @Test
    void testGetAvailableAppointments() {
        // Given the physiotherapist is available
        List<LocalDateTime> availableAppointments = physiotherapist.getAvailableAppointments();

        // Check if the list is not empty
        assertFalse(availableAppointments.isEmpty());

        // Define expected start and end dates for April 2025
        LocalDateTime expectedStart = LocalDateTime.of(2025, 4, 1, 9, 0);
        LocalDateTime expectedEnd = LocalDateTime.of(2025, 5, 1, 0, 0); // May 1, midnight

        // Check if all appointments are within April
        for (LocalDateTime appointment : availableAppointments) {
            assertTrue(appointment.isEqual(expectedStart) || appointment.isAfter(expectedStart),
                    "Appointment should be on or after April 1");
            assertTrue(appointment.isBefore(expectedEnd),
                    "Appointment should be before May 1");
        }

        // If the physiotherapist is not available, the list should be empty
        physiotherapist.deactivateAppointment();
        availableAppointments = physiotherapist.getAvailableAppointments();
        assertTrue(availableAppointments.isEmpty());
    }

    @Test
    void testIsAvailable(){
        // Given a test date/time
        // When checking availability
        boolean available = physiotherapist.isAvailable(dateTime);

        // Then it should be available initially
        assertTrue(available);
    }

    @Test
    void testAssignAppointment() {
        // Given a test date/time
        // When assigning an appointment
        physiotherapist.assignAppointment(dateTime);

        // Then the appointment should be marked as unavailable
        assertFalse(physiotherapist.isAvailable(dateTime));

        // When trying to assign the same appointment again
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            physiotherapist.assignAppointment(dateTime);
        });

        // Then it should throw an exception
        assertEquals("Slot is not available", exception.getMessage());
    }

    @Test
    void testActivateDeactivate() {
        physiotherapist.deactivateAppointment();
        assertFalse(physiotherapist.isAvailable());
        assertTrue(physiotherapist.getAvailableAppointments().isEmpty());

        physiotherapist.activateAppointment();
        assertTrue(physiotherapist.isAvailable());
        assertFalse(physiotherapist.getAvailableAppointments().isEmpty());
    }
}
