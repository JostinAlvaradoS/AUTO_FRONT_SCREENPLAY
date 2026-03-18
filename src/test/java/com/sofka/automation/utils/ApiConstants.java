package com.sofka.automation.utils;

/**
 * Constants for API configuration and endpoints
 * Centralized configuration to avoid hardcoded values
 */
public class ApiConstants {
    
    // Default API URLs
    public static final String DEFAULT_CATALOG_API_URL = "http://localhost:50001";
    public static final String DEFAULT_INVENTORY_API_URL = "http://localhost:50002";
    
    // Environment variable keys
    public static final String CATALOG_API_URL_KEY = "catalog.api.url";
    public static final String INVENTORY_API_URL_KEY = "inventory.api.url";
    
    // API Endpoints
    public static final String ADMIN_EVENTS_ENDPOINT = "/admin/events";
    public static final String ADMIN_EVENTS_SEATS_ENDPOINT = "/admin/events/{eventId}/seats";
    public static final String SEATMAP_ENDPOINT = "/events/{eventId}/seatmap/";
    public static final String DEACTIVATE_EVENT_ENDPOINT = "/admin/events/{eventId}/deactivate";
    public static final String RESERVATIONS_ENDPOINT = "/reservations";
    
    // HTTP Status Codes
    public static final int CREATED_STATUS = 201;
    public static final int OK_STATUS = 200;
    
    // Seat Configuration
    public static final String DEFAULT_SECTION_CODE = "A";
    public static final int DEFAULT_ROWS = 1;
    public static final int DEFAULT_SEATS_PER_ROW = 5;
    
    // Seat Status Values
    public static final String SEAT_STATUS_AVAILABLE = "available";
    public static final String SEAT_STATUS_RESERVED = "reserved";
    public static final String SEAT_STATUS_SOLD = "sold";
    
    // Event Configuration
    public static final int DEFAULT_MAX_CAPACITY = 100;
    public static final int DAYS_IN_FUTURE_FOR_EVENT = 7;
    public static final String DEFAULT_VENUE = "Virtual Theater";
    
    // Session Variable Keys
    public static final String SESSION_CREATED_EVENT_ID = "CREATED_EVENT_ID";
    public static final String SESSION_BLOCKED_SEAT_ID = "BLOCKED_SEAT_ID";
    public static final String SESSION_BLOCKING_USER_ID = "BLOCKING_USER_ID";
    
    // Test Tags
    public static final String TAG_INVALID_RESERVATION = "@ReservaInvalida";
    public static final String TAG_VALID_RESERVATION = "@ReservaValida";
    
    private ApiConstants() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
