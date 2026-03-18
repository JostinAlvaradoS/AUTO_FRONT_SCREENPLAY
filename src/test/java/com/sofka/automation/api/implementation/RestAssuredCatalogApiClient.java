package com.sofka.automation.api.implementation;

import com.sofka.automation.api.contracts.CatalogApiClient;
import com.sofka.automation.api.dto.EventSeatmapDto;
import com.sofka.automation.models.CreateEventRequest;
import com.sofka.automation.models.GenerateSeatsRequest;
import com.sofka.automation.utils.ApiConstants;
import io.restassured.http.ContentType;
import net.serenitybdd.core.Serenity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * RestAssured implementation of CatalogApiClient.
 * Handles all HTTP calls to Catalog API with proper error handling and logging.
 */
public class RestAssuredCatalogApiClient implements CatalogApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(RestAssuredCatalogApiClient.class);
    private final String baseUrl;
    
    public RestAssuredCatalogApiClient() {
        this.baseUrl = Serenity.environmentVariables()
            .optionalProperty(ApiConstants.CATALOG_API_URL_KEY)
            .orElse(ApiConstants.DEFAULT_CATALOG_API_URL);
        logger.info("CatalogApiClient initialized with baseUrl: {}", baseUrl);
    }
    
    public RestAssuredCatalogApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        logger.info("CatalogApiClient initialized with explicit baseUrl: {}", baseUrl);
    }
    
    @Override
    public String createEvent(CreateEventRequest request) {
        logger.info("Creating event: {}", request.getName());
        
        String eventId = given()
            .baseUri(baseUrl)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(ApiConstants.ADMIN_EVENTS_ENDPOINT)
            .then()
            .statusCode(ApiConstants.CREATED_STATUS)
            .extract()
            .path("id");
        
        logger.info("Event created successfully with ID: {}", eventId);
        return eventId;
    }
    
    @Override
    public void generateSeats(String eventId, GenerateSeatsRequest request) {
        logger.info("Generating seats for event: {}", eventId);
        
        String endpoint = ApiConstants.ADMIN_EVENTS_SEATS_ENDPOINT.replace("{eventId}", eventId);
        
        given()
            .baseUri(baseUrl)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(endpoint)
            .then()
            .statusCode(ApiConstants.OK_STATUS);
        
        logger.info("Seats generated successfully for event: {}", eventId);
    }
    
    @Override
    public EventSeatmapDto getSeatmap(String eventId) {
        logger.info("Fetching seatmap for event: {}", eventId);
        
        String endpoint = ApiConstants.SEATMAP_ENDPOINT.replace("{eventId}", eventId);
        
        EventSeatmapDto seatmap = given()
            .baseUri(baseUrl)
            .when()
            .get(endpoint)
            .then()
            .statusCode(ApiConstants.OK_STATUS)
            .extract()
            .as(EventSeatmapDto.class);
        
        logger.info("Seatmap retrieved: {} seats found", seatmap.getSeats().size());
        return seatmap;
    }
    
    @Override
    public void deactivateEvent(String eventId) {
        logger.info("Deactivating event: {}", eventId);
        
        String endpoint = ApiConstants.DEACTIVATE_EVENT_ENDPOINT.replace("{eventId}", eventId);
        
        try {
            int statusCode = given()
                .baseUri(baseUrl)
                .when()
                .post(endpoint)
                .getStatusCode();
            
            if (statusCode == ApiConstants.OK_STATUS) {
                logger.info("Event deactivated successfully: {}", eventId);
            } else {
                logger.warn("Deactivate event returned unexpected status code: {}. Event ID: {}", statusCode, eventId);
            }
        } catch (Exception e) {
            logger.warn("Deactivate event failed with error (non-blocking): {}", e.getMessage());
            // No relanzar excepción - cleanup no debe romper tests
        }
    }
}
