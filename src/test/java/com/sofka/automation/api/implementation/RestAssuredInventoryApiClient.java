package com.sofka.automation.api.implementation;

import com.sofka.automation.api.contracts.InventoryApiClient;
import com.sofka.automation.utils.ApiConstants;
import io.restassured.http.ContentType;
import net.serenitybdd.core.Serenity;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * RestAssured implementation of InventoryApiClient.
 * Handles all HTTP calls to Inventory API with proper error handling and logging.
 */
public class RestAssuredInventoryApiClient implements InventoryApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(RestAssuredInventoryApiClient.class);
    private final String baseUrl;
    
    public RestAssuredInventoryApiClient() {
        this.baseUrl = Serenity.environmentVariables()
            .optionalProperty(ApiConstants.INVENTORY_API_URL_KEY)
            .orElse(ApiConstants.DEFAULT_INVENTORY_API_URL);
        logger.info("InventoryApiClient initialized with baseUrl: {}", baseUrl);
    }
    
    public RestAssuredInventoryApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        logger.info("InventoryApiClient initialized with explicit baseUrl: {}", baseUrl);
    }
    
    @Override
    public String createReservation(String seatId, String customerId) {
        logger.info("Creating reservation for seatId: {}, customerId: {}", seatId, customerId);
        
        String payload = String.format(
            "{\"seatId\": \"%s\", \"customerId\": \"%s\"}", 
            seatId, customerId
        );
        
        String reservationId = given()
            .baseUri(baseUrl)
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post(ApiConstants.RESERVATIONS_ENDPOINT)
            .then()
            .statusCode(Matchers.oneOf(ApiConstants.OK_STATUS, ApiConstants.CREATED_STATUS))
            .extract()
            .path("reservationId");
        
        logger.info("Reservation created successfully with ID: {}", reservationId);
        return reservationId;
    }
}
