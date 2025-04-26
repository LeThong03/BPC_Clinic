package com.bpc.system;


import com.bpc.model.BPC_Booking;
import com.bpc.model.BPC_BookingStatus;
import com.bpc.model.BPC_Patient;
import com.bpc.model.BPC_Physiotherapist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClinicSystemTest {
    private BPC_Clinic system;
    private BPC_Patient testPatient;
    private BPC_Physiotherapist testPhysio;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        system = new BPC_Clinic();

        // Add test patient
        testPatient = system.addPatient("Test Patient", "123 Test St", "555-1234");

        // Add test physiotherapist with expertise that matches the treatments in tests
        List<String> expertise = List.of("Test Specialty", "Treatment", "Massage", "Rehabilitation");
        testPhysio = system.addPhysiotherapist("Test Physio", "456 Test Ave", "555-5678", expertise);

        // Set test date/time to April 15, 2025, 10:00 AM
        testDateTime = LocalDateTime.of(2025, 6, 2, 10, 0);
    }

    @Test
    void testAddPatient() {
        // Given a name, address, and phone
        String name = "John Doe";
        String address = "789 New St";
        String phone = "555-9012";

        // When adding a patient
        BPC_Patient patient = system.addPatient(name, address, phone);

        // Then the patient should be added correctly
        assertNotNull(patient);
        assertNotNull(patient.getId());
        assertEquals(name, patient.getName());
        assertEquals(address, patient.getAddress());
        assertEquals(phone, patient.getPhone());
        assertTrue(patient.isActive());
        assertTrue(system.getPatients().containsKey(patient.getId()));
    }

    @Test
    void testAddPhysiotherapist() {
        // Given a name, address, phone, and expertise
        String name = "Jane Smith";
        String address = "321 Clinic Rd";
        String phone = "555-3456";
        List<String> expertise = Arrays.asList("Sports", "Rehabilitation");

        // When adding a physiotherapist
        BPC_Physiotherapist physio = system.addPhysiotherapist(name, address, phone, expertise);

        // Then the physiotherapist should be added correctly
        assertNotNull(physio);
        assertNotNull(physio.getId());
        assertEquals(name, physio.getName());
        assertEquals(address, physio.getAddress());
        assertEquals(phone, physio.getPhone());
        assertEquals(expertise.size(), physio.getExpertise().size());
        for (String skill : expertise) {
            assertTrue(physio.getExpertise().contains(skill));
        }
        assertTrue(physio.isActive());
        assertTrue(system.getPhysiotherapists().containsKey(physio.getId()));
    }

    @Test
    void testCreateBooking() {
        // Given a patient, physiotherapist, treatment name, and date time
        String treatmentName = "Test Treatment";

        // When creating a booking
        BPC_Booking booking = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                treatmentName,
                testDateTime
        );

        // Then the booking should be created correctly
        assertNotNull(booking);
        assertNotNull(booking.getId());
        assertEquals(testPatient, booking.getPatient());
        assertEquals(testPhysio, booking.getTreatment().getPhysiotherapist());
        assertEquals(treatmentName, booking.getTreatment().getName());
        assertEquals(testDateTime, booking.getTreatment().getDateTime());
        assertEquals(BPC_BookingStatus.BOOKED, booking.getStatus());
        assertTrue(system.getBookings().containsKey(booking.getId()));
    }

    @Test
    void testCancelBooking() {
        // Given an existing booking
        BPC_Booking booking = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "Test Treatment",
                testDateTime
        );
        String bookingId = booking.getId();

        // When cancelling the booking
        system.cancelBooking(bookingId);

        // Then the booking status should be CANCELLED
        assertEquals(BPC_BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void testModifyBooking() {
        // Given an existing booking
        BPC_Booking booking = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "Initial Treatment",
                testDateTime
        );

        // And a new treatment name and time
        String newTreatment = "Modified Treatment";
        LocalDateTime newDateTime = testDateTime.plusDays(1);

        // When modifying the booking
        system.modifyBooking(booking.getId(), testPhysio.getId(), newTreatment, newDateTime);

        // Then the booking should be updated
        assertEquals(newTreatment, booking.getTreatment().getName());
        assertEquals(newDateTime, booking.getTreatment().getDateTime());
    }



    @Test
    void testCantBookOverlappingAppointments() {
        // Given a patient with an existing booking
        system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "First Treatment",
                testDateTime
        );

        // When trying to book another appointment at the same time
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            system.createBooking(
                    testPatient.getId(),
                    testPhysio.getId(),
                    "Second Treatment",
                    testDateTime
            );
        });

        // Then an exception should be thrown
        assertTrue(exception.getMessage().contains("not available") ||
                exception.getMessage().contains("overlapping booking"));
    }

    @Test
    void testDifferentPatientsCanBookSameTimeWithDifferentPhysios() {
        // Given two physiotherapists
        BPC_Physiotherapist secondPhysio = system.addPhysiotherapist(
                "Second Physio",
                "Another Address",
                "555-9999",
                List.of("Treatment", "Another Specialty")
        );

        // And a second patient
        BPC_Patient secondPatient = system.addPatient(
                "Second Patient",
                "Another Patient Address",
                "555-8888"
        );

        // When booking appointments at the same time with different physiotherapists
        BPC_Booking booking1 = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "Treatment with first physio",
                testDateTime
        );

        BPC_Booking booking2 = system.createBooking(
                secondPatient.getId(),
                secondPhysio.getId(),
                "Treatment with second physio",
                testDateTime
        );

        // Then both bookings should be created successfully
        assertNotNull(booking1);
        assertNotNull(booking2);
        assertEquals(testDateTime, booking1.getTreatment().getDateTime());
        assertEquals(testDateTime, booking2.getTreatment().getDateTime());
    }

    @Test
    void testGenerateReport() {
        // Given a system with bookings
        BPC_Booking booking1 = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "Treatment 1",
                testDateTime
        );

        BPC_Booking booking2 = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "Treatment 2",
                testDateTime.plusHours(1)
        );

        // Mark one booking as attended
        system.attendBooking(booking1.getId());

        // When generating a report
        String report = system.generateReport();

        // Then the report should contain booking information
        assertTrue(report.contains("BOOST PHYSIO CLINIC"));
        assertTrue(report.contains(testPhysio.getName()));
        assertTrue(report.contains("Treatment 1"));
        assertTrue(report.contains("Treatment 2"));
        assertTrue(report.contains("ATTENDED"));
        assertTrue(report.contains("BOOKED"));
    }

    @Test
    void testGetPhysiotherapistBookings() {
        // Given some bookings
        BPC_Booking booking1 = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "Treatment 1",
                testDateTime
        );

        BPC_Booking booking2 = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "Treatment 2",
                testDateTime.plusHours(1)
        );

        // When getting physiotherapist bookings
        Map<BPC_Physiotherapist, List<BPC_Booking>> physioBookings = system.getPhysiotherapistBookings();

        // Then the map should contain the correct data
        assertTrue(physioBookings.containsKey(testPhysio));
        List<BPC_Booking> bookings = physioBookings.get(testPhysio);
        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void testGetPhysiotherapistsByAttendedBookings() {
        // Given another physiotherapist
        BPC_Physiotherapist physio2 = system.addPhysiotherapist(
                "Second Physio",
                "Another Address",
                "555-9999",
                List.of("Treatment", "Another Specialty")
        );

        // And bookings with different attendance status
        BPC_Booking booking1 = system.createBooking(
                testPatient.getId(),
                testPhysio.getId(),
                "Treatment 1",
                testDateTime
        );

        BPC_Booking booking2 = system.createBooking(
                testPatient.getId(),
                physio2.getId(),
                "Treatment 2",
                testDateTime.plusHours(1)
        );

        BPC_Booking booking3 = system.createBooking(
                testPatient.getId(),
                physio2.getId(),
                "Treatment 3",
                testDateTime.plusHours(2)
        );

        // Mark bookings as attended
        system.attendBooking(booking1.getId()); // 1 for testPhysio
        system.attendBooking(booking2.getId()); // 1 for physio2
        system.attendBooking(booking3.getId()); // 2 for physio2

        // When getting physiotherapists by attended bookings
        List<Map.Entry<BPC_Physiotherapist, Long>> ranked = system.getPhysiotherapistByAttendedBookings();

        // Then the list should be ordered correctly with physio2 first (2 attended) and testPhysio second (1 attended)
        assertNotNull(ranked);
        assertEquals(2, ranked.size());
        assertEquals(physio2.getId(), ranked.get(0).getKey().getId()); // First position (most attended)
        assertEquals(2L, ranked.get(0).getValue().longValue());
        assertEquals(testPhysio.getId(), ranked.get(1).getKey().getId());
        assertEquals(1L, ranked.get(1).getValue().longValue());
    }
}
