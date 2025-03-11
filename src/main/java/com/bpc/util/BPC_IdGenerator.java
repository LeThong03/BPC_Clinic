package main.java.com.bpc.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class BPC_IdGenerator {
    private static final AtomicInteger patientCounter = new AtomicInteger(1);
    private static final AtomicInteger physiotherapistCounter = new AtomicInteger(1);
    private static final AtomicInteger bookingCounter = new AtomicInteger(1);

    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    //Format: PAT_YYYYMMDD_XXXX (e.g. PAT_20250304_0001)
    public static String generatePatientId(){
        String timestamp = getCurrentTimestamp();
        String sequence = String.format("%04d", patientCounter.getAndIncrement());
        return String.format("PAT_%s_%s", timestamp, sequence);
    }

    //Format: PHY_YYYYMMDD_XXXX (e.g. PHY_20250304_0001)
    public static String generatePhysiotherapistId(){
        String timestamp = getCurrentTimestamp();
        String sequence = String.format("%04d", physiotherapistCounter.getAndIncrement());
        return String.format("PHY_%s_%s", timestamp, sequence);
    }

    public static String generateBookingId(){
        String timestamp = getCurrentTimestamp();
        String sequence = String.format("%04d", bookingCounter.getAndIncrement());
        return String.format("BOOK_%s_%s", timestamp, sequence);
    }

    //testing purposes
    public static void resetCounters(){
        patientCounter.set(1);
        physiotherapistCounter.set(1);
        bookingCounter.set(1);
    }

    public static boolean isValidPatientId(String id){
        return id.matches("PAT_\\d{8}_\\d{4}");
    }

    public static boolean isValidPhysiotherapistId(String id){
        return id.matches("PHY_\\d{8}_\\d{4}");
    }

    public static boolean isValidBookingId(String id){
        return id.matches("BOOK_\\d{8}_\\d{4}");
    }
}
