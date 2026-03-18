package com.sofka.automation.api.contracts;

import com.sofka.automation.api.dto.EventSeatmapDto;
import com.sofka.automation.models.CreateEventRequest;
import com.sofka.automation.models.GenerateSeatsRequest;

/**
 * Contract for Catalog API interactions.
 * Abstraction layer following Dependency Inversion Principle (DIP).
 * Implementation can be swapped for testing or different environments.
 */
public interface CatalogApiClient {
    
    /**
     * Creates a new event for testing
     * @param request Event creation request
     * @return Created event ID
     */
    String createEvent(CreateEventRequest request);
    
    /**
     * Generates seats for an event
     * @param eventId Event identifier
     * @param request Seat generation configuration
     */
    void generateSeats(String eventId, GenerateSeatsRequest request);
    
    /**
     * Retrieves seatmap with current seat states
     * @param eventId Event identifier
     * @return Seatmap with seat information
     */
    EventSeatmapDto getSeatmap(String eventId);
    
    /**
     * Deactivates an event (cleanup)
     * @param eventId Event identifier
     */
    void deactivateEvent(String eventId);
}
