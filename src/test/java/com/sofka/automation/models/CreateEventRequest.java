package com.sofka.automation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    private String name;
    private String description;
    private String eventDate;
    private String venue;
    private int maxCapacity;
    private BigDecimal basePrice;
}
