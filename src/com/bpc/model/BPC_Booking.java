package com.bpc.model;

import java.time.LocalDateTime;

public class BPC_Booking {
    private String id;
    private BPC_Patient patient;
    private BPC_Treatment treatment;
    private BPC_BookingStatus status;
    private LocalDateTime bookingTime;

    public BPC_Booking(String id, BPC_Patient patient, BPC_Treatment treatment) {
        this.id = id;
        this.patient = patient;
        this.treatment = treatment;
        this.status = BPC_BookingStatus.BOOKED;
        this.bookingTime = LocalDateTime.now();
        treatment.markAsBooked();
    }

    private void validateBookingStatus(BPC_BookingStatus expected) {
        if (status != expected) {
            throw new IllegalStateException("Booking is not in " + expected + " state");
        }
    }

    public void cancelBooking(){
        validateBookingStatus(BPC_BookingStatus.BOOKED);
        status = BPC_BookingStatus.CANCELLED;
        // Free up the physiotherapist's time slot
        treatment.getPhysiotherapist().freeAppointment(treatment.getDateTime());
    }

    public void attend(){
        validateBookingStatus(BPC_BookingStatus.BOOKED);
        status = BPC_BookingStatus.ATTENDED;
    }

    public void changeBooking(BPC_Treatment newTreatment){
        validateBookingStatus(BPC_BookingStatus.BOOKED);
        // Free up the physiotherapist's time slot for the old treatment
        treatment.getPhysiotherapist().freeAppointment(treatment.getDateTime());
        this.treatment = newTreatment;
        newTreatment.markAsBooked();
    }

    public String getId(){  return id;  }
    public BPC_Patient getPatient(){  return patient;  }
    public BPC_Treatment getTreatment(){  return treatment;  }
    public BPC_BookingStatus getStatus(){  return status;  }
    public LocalDateTime getBookingTime(){  return bookingTime;  }
}
