package com.sofka.automation.utils;

import com.sofka.automation.models.CreateEventRequest;
import com.sofka.automation.models.GenerateSeatsRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Builder for creating test event data
 * Follows Builder pattern for flexible test data creation
 */
public class EventTestDataBuilder {
    
    private String eventName;
    private String description;
    private String venue;
    private int maxCapacity;
    private BigDecimal basePrice;
    private LocalDateTime eventDate;
    
    public EventTestDataBuilder() {
        this.eventName = "Automation Test Event " + System.currentTimeMillis();
        this.description = "Event created for automated testing of seat reservation";
        this.venue = ApiConstants.DEFAULT_VENUE;
        this.maxCapacity = ApiConstants.DEFAULT_MAX_CAPACITY;
        this.basePrice = new BigDecimal("50.00");
        this.eventDate = LocalDateTime.now().plusDays(ApiConstants.DAYS_IN_FUTURE_FOR_EVENT);
    }
    
    public EventTestDataBuilder withName(String eventName) {
        this.eventName = eventName;
        return this;
    }
    
    public EventTestDataBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public EventTestDataBuilder withInvalidReservationPrefix() {
        this.eventName = "[INVALID_RESERVATION] " + this.eventName;
        return this;
    }
    
    public EventTestDataBuilder withVenue(String venue) {
        this.venue = venue;
        return this;
    }
    
    public EventTestDataBuilder withMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }
    
    public EventTestDataBuilder withBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
        return this;
    }
    
    public EventTestDataBuilder withEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
        return this;
    }
    
    public CreateEventRequest buildCreateEventRequest() {
        return CreateEventRequest.builder()
            .name(eventName)
            .description(description)
            .eventDate(eventDate.toString())
            .venue(venue)
            .maxCapacity(maxCapacity)
            .basePrice(basePrice)
            .build();
    }
    
    public GenerateSeatsRequest buildGenerateSeatsRequest() {
        GenerateSeatsRequest.SeatSectionConfiguration section = 
            GenerateSeatsRequest.SeatSectionConfiguration.builder()
                .sectionCode(ApiConstants.DEFAULT_SECTION_CODE)
                .rows(ApiConstants.DEFAULT_ROWS)
                .seatsPerRow(ApiConstants.DEFAULT_SEATS_PER_ROW)
                .priceMultiplier(new BigDecimal("1.0"))
                .build();
        
        return GenerateSeatsRequest.builder()
            .sectionConfigurations(List.of(section))
            .build();
    }
}
