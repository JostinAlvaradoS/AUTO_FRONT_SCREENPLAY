package com.sofka.automation.api.contracts;

/**
 * Contract for Inventory API interactions.
 * Abstraction layer for creating seat reservations.
 */
public interface InventoryApiClient {
    
    /**
     * Creates a reservation for a seat
     * @param seatId Seat identifier
     * @param customerId Customer identifier
     * @return Created reservation ID
     */
    String createReservation(String seatId, String customerId);
}
