package main.java.com.bpc.model;

import java.time.LocalDateTime;

public class BPC_Booking {
    private String id;
    private BPC_Patient patient;
    private BPC_Treatment treatment;
    private BPC_BookingStatus status;
    private LocalDateTime bookingTime;

    public BPC_Booking(String id, BPC_Patient patient, BPC_Treatment treatment, BPC_BookingStatus status, LocalDateTime bookingTime) {
        this.id = id;
        this.patient = patient;
        this.treatment = treatment;
        this.status = BPC_BookingStatus.BOOKED;
        this.bookingTime = LocalDateTime.now();
        treatment.markAsBooked();
    }

    public void cancelBooking(){
        if(status == BPC_BookingStatus.BOOKED){
            throw new IllegalArgumentException("Booking is not confirmed");
        }
        status = BPC_BookingStatus.CANCELLED;
    }

    public void attend(){
        if (status != BPC_BookingStatus.BOOKED) {
            throw new IllegalArgumentException("Booking is not confirmed");
        }
        status = BPC_BookingStatus.ATTENDED;
    }

    public void changeBooking(BPC_Treatment newTreatment){
        if(status != BPC_BookingStatus.BOOKED){
            throw new IllegalArgumentException("Booking is not confirmed");
        }
        this.treatment = treatment;
        newTreatment.markAsBooked();
    }

    public String getId(){  return id;  }
    public BPC_Patient getPatient(){  return patient;  }
    public BPC_Treatment getTreatment(){  return treatment;  }
    public BPC_BookingStatus getStatus(){  return status;  }
    public LocalDateTime getBookingTime(){  return bookingTime;  }
}
