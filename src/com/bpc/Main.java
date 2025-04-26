package com.bpc;

import com.bpc.model.BPC_Booking;
import com.bpc.model.BPC_Patient;
import com.bpc.model.BPC_Physiotherapist;
import com.bpc.system.BPC_Clinic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;

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

        // Add patients
        BPC_Patient patient1 = system.addPatient(
                "Alice Brown",
                "321 Pine St",
                "555-0201"
        );

        BPC_Patient patient2 = system.addPatient(
                "Bob Wilson",
                "654 Maple Dr",
                "555-0202"
        );

        // Add sample bookings
        LocalDateTime day1 = LocalDateTime.of(2025, 6, 2, 10, 0);
        LocalDateTime day2 = LocalDateTime.of(2025, 6, 4, 14, 0);
        LocalDateTime day3 = LocalDateTime.of(2025, 6, 6, 11, 0);

        try {
            // Today at 10:00
            BPC_Booking booking1 = system.createBooking(
                    patient1.getId(),
                    physio1.getId(),
                    "Sport Injury Assessment",
                    day1
            );

            // Tomorrow at 14:00
            BPC_Booking booking2 = system.createBooking(
                    patient2.getId(),
                    physio2.getId(),
                    "Back Pain Treatment",
                    day2
            );

            // Next week at 11:00
            BPC_Booking booking3 = system.createBooking(
                    patient1.getId(),
                    physio2.getId(),
                    "Neck Pain Follow-up",
                    day3
            );

            // Mark first booking as attended
            system.attendBooking(booking1.getId());

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

    // In Main class
    private static void listAllPatients() {
        System.out.println("\n=== PATIENTS ===");
        for (BPC_Patient patient : system.getPatients().values()) {
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
        System.out.println("0. Exit");
    }

}
