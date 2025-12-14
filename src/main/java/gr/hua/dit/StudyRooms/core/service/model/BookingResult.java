package gr.hua.dit.StudyRooms.core.service.model;



import gr.hua.dit.StudyRooms.core.model.Booking;


// DTO/Record που κρατάει το αποτέλεσμα της κράτησης

public record BookingResult(
        boolean success,
        String message,
        Booking booking
) {

    public static BookingResult success(Booking booking){
        return new BookingResult(true, null, booking);

    }


    public static BookingResult failed(String message){
        return new BookingResult(false, message, null);

    }
}
