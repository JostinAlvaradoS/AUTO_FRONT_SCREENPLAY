package com.sofka.automation.api.contracts;

public interface InventoryApiClient {
    
    String createReservation(String seatId, String customerId);
}
