package com.sofka.automation.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventSeatmapDto {
    private String eventId;
    private List<SeatDto> seats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeatDto {
        private String id;
        private String status;
        private Double basePrice;
    }
    
    public List<String> getAvailableSeats() {
        if (seats == null || seats.isEmpty()) {
            return List.of();
        }
        return seats.stream()
            .filter(s -> s != null && "available".equalsIgnoreCase(s.getStatus()))
            .map(SeatDto::getId)
            .collect(Collectors.toList());
    }
    
    public String getFirstAvailableSeat() {
        List<String> available = getAvailableSeats();
        return available.isEmpty() ? null : available.get(0);
    }
}
