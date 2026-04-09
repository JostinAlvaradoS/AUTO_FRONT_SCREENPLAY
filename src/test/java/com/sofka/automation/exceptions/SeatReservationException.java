package com.sofka.automation.exceptions;

public class SeatReservationException extends RuntimeException {
    
    public SeatReservationException(String message) {
        super(message);
    }

    public SeatReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}
