package com.bpc;

import com.bpc.model.BPC_Booking;
import com.bpc.model.BPC_Patient;
import com.bpc.model.BPC_Physiotherapist;
import com.bpc.system.BPC_Clinic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final BPC_Clinic system = new BPC_Clinic();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        //create sample data
        createSampleData();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice){
                case 1:
                    addPatient();
                    break;
                case 2:
                    addPhysiotherapist();
                    break;
                case 3:
                    createBooking();
                    break;
                case 4:
                    cancelBooking();
                    break;
                case 5:
                    attendBooking();
                    break;
                case 6:
                    modifyBooking();
                    break;
                case 7:
                    generateReport();
                    break;
                case 8:
                    listAllBookings();
                    break;
                case 9:
                    listAllPatients();
                    break;
                case 10:
                    listAllPhysiotherapists();
                    break;
                case 11:
                    removePatient();
                    break;
                case 12:
                    reactivatePatient();
                    break;
                case 13:
                    searchByExpertise();
                    break;
                case 14:
                    searchByPhysiotherapistName();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting the system...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void createSampleData() {
        System.out.println("Initializing system with sample data...");

        // Add physiotherapists
        BPC_Physiotherapist physio1 = system.addPhysiotherapist(
                "John Smith",
                "123 Main St",
                "555-0101",
                Arrays.asList("Sports Injury", "Rehabilitation")
        );

        BPC_Physiotherapist physio2 = system.addPhysiotherapist(
                "Sarah Johnson",
                "456 Park Ave",
                "555-0102",
                Arrays.asList("Back Pain", "Neck Pain", "Posture")
        );
        BPC_Physiotherapist physio3 = system.addPhysiotherapist(
                "Emily Davis",
                "789 Elm St",
                "555-0103",
                Arrays.asList("Posture", "Sports Rehab")
        );
        BPC_Physiotherapist physio4 = system.addPhysiotherapist(
                "Michael Brown",
                "101 Oak St",
                "555-0104",
                Arrays.asList("Arthritis", "Massage Therapy")
        );
        BPC_Physiotherapist physio5 = system.addPhysiotherapist(
                "Jessica Wilson",
                "202 Birch St",
                "555-0105",
                Arrays.asList("Neck Pain", "Posture")
        );

        // Add 10 patients
        List<BPC_Patient> patients = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            BPC_Patient patient = system.addPatient(
                    "Patient " + i, "Address " + i, "07903033" + String.format("%02d", i)
            );
            patients.add(patient);
        }

        // Add bookings for 4 weeks
        LocalDateTime baseDate = LocalDateTime.of(2025, 6, 2, 9, 0);

        try {
            int patientIndex = 0; // <- track which patient to assign

            for (int day = 0; day < 5; day++) { // Monday to Friday
                LocalDateTime dayDate = baseDate.plusDays(day);

                // Only 1 slot per day for simplicity
                LocalDateTime bookingTime = dayDate.withHour(10); // 10:00 AM

                List<BPC_Physiotherapist> physios = List.of(physio1, physio2, physio3, physio4, physio5);
                BPC_Physiotherapist selectedPhysio = physios.get(new Random().nextInt(physios.size()));

                if (patientIndex < patients.size()) {
                    BPC_Patient selectedPatient = patients.get(patientIndex);
                    String treatmentName = selectedPhysio.getExpertise().get(0);

                    system.createBooking(
                            selectedPatient.getId(),
                            selectedPhysio.getId(),
                            treatmentName,
                            bookingTime
                    );
                    patientIndex++;
                }
            }

            System.out.println("Sample data created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating sample data: " + e.getMessage());
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void addPatient() {
        System.out.println("\n=== ADD PATIENT ===");
        String name = getStringInput("Enter patient name: ");
        String address = getStringInput("Enter patient address: ");
        String phone = getStringInput("Enter patient phone: ");

        try {
            BPC_Patient patient = system.addPatient(name, address, phone);
            System.out.println("Patient added successfully. ID: " + patient.getId());
        } catch (Exception e) {
            System.out.println("Error adding patient: " + e.getMessage());
        }
    }

    private static void removePatient() {
        System.out.println("\n=== REMOVE PATIENT ===");

        // List patients
        listAllPatients();

        String patientId = getStringInput("Enter patient ID to remove: ");

        try {
            BPC_Patient patient = system.getPatients().get(patientId);
            if (patient == null) {
                System.out.println("Patient not found.");
                return;
            }

            // Check if patient has active bookings
            if (patient.hasActiveBooking()) {
                System.out.println("Cannot remove patient with active bookings. Please cancel bookings first.");
                return;
            }

            // Deactivate patient
            patient.deactivate();
            System.out.println("Patient deactivated successfully.");
        } catch (Exception e) {
            System.out.println("Error removing patient: " + e.getMessage());
        }
    }

    private static void reactivatePatient() {
        System.out.println("\n=== REACTIVATE PATIENT ===");

        // List all patients
        listAllPatients();

        String patientId = getStringInput("Enter patient ID to reactivate: ");

        try {
            BPC_Patient patient = system.getPatients().get(patientId);
            if (patient == null) {
                System.out.println("Patient not found.");
                return;
            }

            if (patient.isActive()) {
                System.out.println("Patient is already active.");
                return;
            }

            patient.reactivate();
            System.out.println("Patient reactivated successfully.");
        } catch (Exception e) {
            System.out.println("Error reactivating patient: " + e.getMessage());
        }
    }


    private static void listAllPatients() {
        System.out.println("\n=== PATIENTS ===");

        List<BPC_Patient> sortedPatients = new ArrayList<>(system.getPatients().values());
        sortedPatients.sort(Comparator.comparing(BPC_Patient::getId));

        for (BPC_Patient patient : sortedPatients) {
            System.out.printf("%s | %s | %s | %s | Active: %s%n",
                    patient.getId(),
                    patient.getName(),
                    patient.getAddress(),
                    patient.getPhone(),
                    patient.isActive()
            );
        }
    }

    private static void addPhysiotherapist() {
        System.out.println("\n=== ADD PHYSIOTHERAPIST ===");
        String name = getStringInput("Enter physiotherapist name: ");
        String address = getStringInput("Enter physiotherapist address: ");
        String phone = getStringInput("Enter physiotherapist phone: ");
        String expertiseInput = getStringInput("Enter expertise (comma-separated): ");
        List<String> expertise = Arrays.asList(expertiseInput.split(","));

        try {
            BPC_Physiotherapist physiotherapist = system.addPhysiotherapist(name, address, phone, expertise);
            System.out.println("Physiotherapist added successfully. ID: " + physiotherapist.getId());
        } catch (Exception e) {
            System.out.println("Error adding physiotherapist: " + e.getMessage());
        }
    }

    private static void listAllPhysiotherapists() {
        System.out.println("\n=== PHYSIOTHERAPISTS ===");
        if (system.getPhysiotherapists().isEmpty()) {
            System.out.println("No physiotherapists found.");
            return;
        }

        for (BPC_Physiotherapist physio : system.getPhysiotherapists().values()) {
            System.out.printf("%s | %s | %s | %s | Active: %s | Expertise: %s%n",
                    physio.getId(),
                    physio.getName(),
                    physio.getAddress(),
                    physio.getPhone(),
                    physio.isActive(),
                    String.join(", ", physio.getExpertise())
            );
        }
    }

    private static void createBooking() {
        System.out.println("\n=== BOOK APPOINTMENT ===");

        while (true) {
            try {
                // List patients
                System.out.println("Available Patients:");
                listAllPatients();
                String patientId = getStringInput("Enter patient ID (or 0 to cancel): ");
                if (patientId.equals("0")) return;

                // List physiotherapists
                System.out.println("Available Physiotherapists:");
                listAllPhysiotherapists();
                String physiotherapistId = getStringInput("Enter physiotherapist ID (or 0 to cancel): ");
                if (physiotherapistId.equals("0")) return;

                String treatmentName = getStringInput("Enter treatment name (or 0 to cancel): ");
                if (treatmentName.equals("0")) return;

                String dateTimeStr = getStringInput("Enter date and time (yyyy-MM-dd HH:mm) (or 0 to cancel): ");
                if (dateTimeStr.equals("0")) return;

                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dateTimeFormatter);

                BPC_Booking booking = system.createBooking(patientId, physiotherapistId, treatmentName, dateTime);
                System.out.println("✅ Booking created successfully. ID: " + booking.getId());
                return; // exit to main menu after success

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            // Retry or return option
            String retry = getStringInput("Enter 1 to try again or 0 to return to main menu: ");
            if (retry.equals("0")) return;
        }
    }


    private static void attendBooking() {
        System.out.println("\n=== MARK BOOKING AS ATTENDED ===");
        String bookingId = getStringInput("Enter booking ID: ");

        try {
            system.attendBooking(bookingId);
            System.out.println("Booking marked as attended.");
        } catch (Exception e) {
            System.out.println("Error marking booking as attended: " + e.getMessage());
        }
    }

    private static void cancelBooking() {
        System.out.println("\n=== CANCEL BOOKING ===");
        String bookingId = getStringInput("Enter booking ID: ");

        try {
            system.cancelBooking(bookingId);
            System.out.println("Booking cancelled successfully.");
        } catch (Exception e) {
            System.out.println("Error cancelling booking: " + e.getMessage());
        }
    }
    private static void listAllBookings() {
        System.out.println("\n=== BOOKINGS ===");
        if (system.getBookings().isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (BPC_Booking booking : system.getBookings().values()) {
            System.out.printf("%s | %s | %s | %s | %s%n",
                    booking.getId(),
                    booking.getPatient().getName(),
                    booking.getTreatment().getName(),
                    booking.getTreatment().getDateTime().format(dateTimeFormatter),
                    booking.getTreatment().getPhysiotherapist().getName(),
                    booking.getStatus()
            );
        }
    }
    private static void modifyBooking() {
        System.out.println("\n=== MODIFY BOOKING ===");

        // List bookings
        System.out.println("Current Bookings:");
        listAllBookings();
        String bookingId = getStringInput("Enter booking ID to modify: ");

        // List physiotherapists
        System.out.println("Available Physiotherapists:");
        listAllPhysiotherapists();
        String physioId = getStringInput("Enter physiotherapist ID for the modified booking: ");

        String treatmentName = getStringInput("Enter treatment name: ");

        // Get date and time
        String dateTimeStr = getStringInput("Enter new date and time (YYYY-MM-DD HH:MM): ");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dateTimeFormatter);

            system.modifyBooking(bookingId, physioId, treatmentName, dateTime);
            System.out.println("Booking modified successfully.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD HH:MM");
        } catch (Exception e) {
            System.out.println("Error modifying booking: " + e.getMessage());
        }
    }

    private static void searchByExpertise() {
        System.out.println("\n=== SEARCH PHYSIOTHERAPISTS BY EXPERTISE ===");
        String expertise = getStringInput("Enter expertise area (e.g., Massage, Sports Injury): ");

        boolean found = false;
        for (BPC_Physiotherapist physio : system.getPhysiotherapists().values()) {
            for (String skill : physio.getExpertise()) {
                if (skill.equalsIgnoreCase(expertise.trim())) {
                    System.out.println("Physiotherapist: " + physio.getName());
                    List<LocalDateTime> availableSlots = physio.getAvailableAppointments();
                    if (availableSlots.isEmpty()) {
                        System.out.println("  No available slots.");
                    } else {
                        for (LocalDateTime slot : availableSlots) {
                            System.out.println("  Available slot: " + slot.format(dateTimeFormatter));
                        }
                    }
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("No physiotherapists found for the given expertise.");
        }
    }

    private static void searchByPhysiotherapistName() {
        System.out.println("\n=== SEARCH TREATMENTS BY PHYSIOTHERAPIST NAME ===");
        String name = getStringInput("Enter physiotherapist name: ");

        boolean found = false;
        for (BPC_Physiotherapist physio : system.getPhysiotherapists().values()) {
            if (physio.getName().equalsIgnoreCase(name.trim())) {
                System.out.println("Physiotherapist: " + physio.getName());
                System.out.println("Expertise Areas: " + String.join(", ", physio.getExpertise()));
                List<LocalDateTime> availableSlots = physio.getAvailableAppointments();
                if (availableSlots.isEmpty()) {
                    System.out.println("  No available slots.");
                } else {
                    for (LocalDateTime slot : availableSlots) {
                        System.out.println("  Available slot: " + slot.format(dateTimeFormatter));
                    }
                }
                found = true;
            }
        }
        if (!found) {
            System.out.println("No physiotherapist found with that name.");
        }
    }

    private static void generateReport() {
        System.out.println("\n=== GENERATE REPORT ===");
        String report = system.generateReport();
        System.out.println(report);
    }


    private static void displayMenu() {
        System.out.println("\n=== BOOST PHYSIO CLINIC SYSTEM ===");
        System.out.println("1. Add Patient");
        System.out.println("2. Add Physiotherapist");
        System.out.println("3. Book Appointment");
        System.out.println("4. Cancel Booking");
        System.out.println("5. Mark Booking as Attended");
        System.out.println("6. Modify Booking");
        System.out.println("7. Generate Report");
        System.out.println("8. List All Bookings");
        System.out.println("9. List All Patients");
        System.out.println("10. List All Physiotherapists");
        System.out.println("11. Remove Patient");
        System.out.println("12. Reactivate Patient");
        System.out.println("13. Search Physiotherapists by Expertise");
        System.out.println("14. Search Physiotherapist by Name");
        System.out.println("0. Exit");
    }

}
