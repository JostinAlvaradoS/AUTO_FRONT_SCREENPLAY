package com.sofka.automation.api.contracts;

import com.sofka.automation.api.dto.EventSeatmapDto;
import com.sofka.automation.models.CreateEventRequest;
import com.sofka.automation.models.GenerateSeatsRequest;

public interface CatalogApiClient {
    
    String createEvent(CreateEventRequest request);
    
    void generateSeats(String eventId, GenerateSeatsRequest request);
    
    EventSeatmapDto getSeatmap(String eventId);
    
    void deactivateEvent(String eventId);
}
