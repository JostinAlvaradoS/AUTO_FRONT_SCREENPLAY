package com.sofka.automation.exceptions;

/**
 * Custom exception for seat reservation related errors
 * Allows catching specific business logic errors for seat operations
 */
public class SeatReservationException extends RuntimeException {
    
    public SeatReservationException(String message) {
        super(message);
    }

    public SeatReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}
