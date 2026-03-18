package com.sofka.automation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateSeatsRequest {
    private List<SeatSectionConfiguration> sectionConfigurations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatSectionConfiguration {
        private String sectionCode;
        private int rows;
        private int seatsPerRow;
        private BigDecimal priceMultiplier;
    }
}
