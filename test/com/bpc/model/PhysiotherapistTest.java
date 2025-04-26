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
        dateTime = LocalDateTime.of(2025, 6, 2, 10, 0);
    }

    @Test
    void testInitializeActiveStatus(){
        assertTrue(physiotherapist.isActive());
    }

    @Test
    void testGetAvailableAppointments() {
        // Given the physiotherapist is available
        List<LocalDateTime> availableAppointments = physiotherapist.getAvailableAppointments();

        // Check if the list is not empty
        assertFalse(availableAppointments.isEmpty());

        // Define expected start and end dates for April 2025
        LocalDateTime expectedStart = LocalDateTime.of(2025, 6, 2, 9, 0);
        LocalDateTime expectedEnd = LocalDateTime.of(2025, 7, 1, 0, 0);

        // Check if all appointments are within April
        for (LocalDateTime appointment : availableAppointments) {
            assertTrue(appointment.isEqual(expectedStart) || appointment.isAfter(expectedStart),
                    "Appointment should be on or after June 1");
            assertTrue(appointment.isBefore(expectedEnd),
                    "Appointment should be before July 1");
            // Ensure appointments are only on weekdays
            assertTrue(appointment.getDayOfWeek().getValue() < 6,
                    "Appointment should be on a weekday");
        }
        // If the physiotherapist is not available, the list should be empty
        physiotherapist.deactivate();
        availableAppointments = physiotherapist.getAvailableAppointments();
        assertTrue(availableAppointments.isEmpty());
    }

    @Test
    void testIsAvailable(){
        // Given a test date/time in April 2025
        boolean available = physiotherapist.isAvailable(dateTime);

        // Then it should be available initially
        assertTrue(available);

        // When assigned
        physiotherapist.assignAppointment(dateTime);

        // Then it should not be available
        assertFalse(physiotherapist.isAvailable(dateTime));

        // When freed
        physiotherapist.freeAppointment(dateTime);

        // Then it should be available again
        assertTrue(physiotherapist.isAvailable(dateTime));
    }

    @Test
    void testActivateDeactivate() {
        // Initially active
        assertTrue(physiotherapist.isActive());

        // When deactivated
        physiotherapist.deactivate();

        // Then not active and no available appointments
        assertFalse(physiotherapist.isActive());
        assertTrue(physiotherapist.getAvailableAppointments().isEmpty());

        // When activated
        physiotherapist.activate();

        // Then active again and has available appointments
        assertTrue(physiotherapist.isActive());
        assertFalse(physiotherapist.getAvailableAppointments().isEmpty());
    }
}
