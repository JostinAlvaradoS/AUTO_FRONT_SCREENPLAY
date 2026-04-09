package com.sofka.automation.services;

import com.sofka.automation.api.contracts.CatalogApiClient;
import com.sofka.automation.models.GenerateSeatsRequest;
import com.sofka.automation.utils.ApiConstants;
import com.sofka.automation.utils.EventTestDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSetupService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventSetupService.class);
    private final CatalogApiClient catalogApi;

    public EventSetupService(CatalogApiClient catalogApi) {
        this.catalogApi = catalogApi;
    }

    public String createEventWithSeats(boolean isInvalidReservationTest) {
        logger.info("Setting up event for {} scenario", 
            isInvalidReservationTest ? "INVALID_RESERVATION" : "VALID_RESERVATION");
        
        EventTestDataBuilder builder = new EventTestDataBuilder();
        if (isInvalidReservationTest) {
            builder.withInvalidReservationPrefix();
        }
        
        String eventId = catalogApi.createEvent(builder.buildCreateEventRequest());
        logger.debug("Event created with ID: {}", eventId);

        generateSeats(eventId, builder);
        logger.info("Event setup completed: {}", eventId);

        return eventId;
    }

    private void generateSeats(String eventId, EventTestDataBuilder builder) {
        logger.info("Generating {} seats for event: {}", ApiConstants.DEFAULT_SEATS_PER_ROW, eventId);
        
        GenerateSeatsRequest generateSeatsRequest = builder.buildGenerateSeatsRequest();
        catalogApi.generateSeats(eventId, generateSeatsRequest);
        
        logger.debug("Seats generation completed for event: {}", eventId);
    }
}
