package com.sofka.automation.services;

import com.sofka.automation.api.contracts.CatalogApiClient;
import com.sofka.automation.api.contracts.InventoryApiClient;
import com.sofka.automation.api.dto.EventSeatmapDto;
import com.sofka.automation.exceptions.SeatReservationException;
import com.sofka.automation.utils.ApiConstants;
import net.serenitybdd.core.Serenity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for blocking seats for negative test scenarios.
 * Single Responsibility: Managing seat reservation for test setup.
 * Follows Dependency Inversion Principle: depends on abstractions, not concrete implementations.
 */
public class SeatBlockingService {
    
    private static final Logger logger = LoggerFactory.getLogger(SeatBlockingService.class);
    private final CatalogApiClient catalogApi;
    private final InventoryApiClient inventoryApi;

    public SeatBlockingService(CatalogApiClient catalogApi, InventoryApiClient inventoryApi) {
        this.catalogApi = catalogApi;
        this.inventoryApi = inventoryApi;
    }

    /**
     * Blocks the first available seat in an event for invalid reservation test scenarios
     * @param eventId Event identifier
     * @throws SeatReservationException if no seats are available or reservation fails
     */
    public void blockFirstAvailableSeat(String eventId) {
        try {
            logger.info("Attempting to block first available seat for event: {}", eventId);
            
            EventSeatmapDto seatmap = catalogApi.getSeatmap(eventId);
            
            if (seatmap == null || seatmap.getSeats() == null) {
                throw new SeatReservationException(
                    "Seatmap response is null for event: " + eventId
                );
            }
            
            List<String> availableSeats = seatmap.getAvailableSeats();
            
            if (availableSeats == null || availableSeats.isEmpty()) {
                throw new SeatReservationException(
                    "No available seats found to block in event: " + eventId
                );
            }

            String seatIdToBlock = availableSeats.get(0);
            String blockingUserId = UUID.randomUUID().toString();
            
            logger.info("Blocking seat {} for invalid reservation test", seatIdToBlock);
            
            String reservationId = inventoryApi.createReservation(seatIdToBlock, blockingUserId);
            
            storeBlockingSessionVariables(seatIdToBlock, blockingUserId);
            
            logger.info("Seat successfully blocked - reservationId: {}, seatId: {}", 
                reservationId, seatIdToBlock);
            
        } catch (SeatReservationException e) {
            logger.error("Seat blocking failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while blocking seat for event: {}", eventId, e);
            throw new SeatReservationException("Unexpected error while blocking seat", e);
        }
    }

    /**
     * Stores blocking information in Serenity session for test assertions
     * @param seatId Blocked seat identifier
     * @param blockingUserId User who blocked the seat
     */
    private void storeBlockingSessionVariables(String seatId, String blockingUserId) {
        Serenity.setSessionVariable(ApiConstants.SESSION_BLOCKED_SEAT_ID).to(seatId);
        Serenity.setSessionVariable(ApiConstants.SESSION_BLOCKING_USER_ID).to(blockingUserId);
    }
}
