package main.java.com.bpc.model;

import java.time.LocalDateTime;
import java.util.*;

public class BPC_Physiotherapist {
    //declare physiotherapist variables
    private String id;
    private String name;
    private String address;
    private String phone;
    private List <String> expertise;
    private Map<LocalDateTime, Boolean> timetable; //true: available, false: booked

    public BPC_Physiotherapist(String id, String name, String address, String phone, List<String> expertise) {
        //initialize physiotherapist variables
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.expertise = new ArrayList<>(expertise);
        this.timetable = initializeTimetable();
    }

    private Map<LocalDateTime, Boolean> initializeTimetable() {
        Map<LocalDateTime, Boolean> slots = new TreeMap<>();
        LocalDateTime start = LocalDateTime.now().withHour(9).withMinute(0);

        //Initialize timetable for 4 weeks
        for (int day = 0; day < 28; day++) {
            LocalDateTime currentDay = start.plusDays(day);

            // 9am to 5pm, 1 hour slots
            for (int hour = 9; hour < 17; hour++) {
                slots.put(currentDay.withHour(hour), true);
            }
        }
        return slots;
    }

    public List<LocalDateTime> getAvailableAppointments() {
        return timetable.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }

    public boolean isAvailable(LocalDateTime dateTime) {
        return timetable.getOrDefault(dateTime, false);
    }

    public void assignAppointment(LocalDateTime dateTime) {
        if (!isAvailable(dateTime)) {
            throw new IllegalArgumentException("Time slot is not available");
        }
        timetable.put(dateTime, false);
    }

    //Getter and Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public List<String> getExpertise() { return new ArrayList<>(expertise); }
}
