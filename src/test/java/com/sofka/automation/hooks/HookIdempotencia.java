package com.sofka.automation.hooks;

import com.sofka.automation.models.CreateEventRequest;
import com.sofka.automation.models.GenerateSeatsRequest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.http.ContentType;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.oneOf;

public class HookIdempotencia {

    private String getApiBaseUrl() {
        return Serenity.environmentVariables().optionalProperty("catalog.api.url")
                .orElse("http://localhost:50001");
    }

    @Before(order = 0)
    public void setupStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Before(order = 1)
    public void setupEventForTest(Scenario scenario) {
        String baseUrl = getApiBaseUrl();

        // 1. Create Event
        CreateEventRequest eventRequest = CreateEventRequest.builder()
                .name("Automation Test Event " + System.currentTimeMillis())
                .description("Event created for automated testing of seat reservation")
                .eventDate(LocalDateTime.now().plusDays(7).toString())
                .venue("Virtual Theater")
                .maxCapacity(100)
                .basePrice(new BigDecimal("50.00"))
                .build();

        String eventId = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(eventRequest)
                .when()
                .post("/admin/events")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        Serenity.setSessionVariable("CREATED_EVENT_ID").to(eventId);

        // 2. Generate Seats (5 seats in one row)
        GenerateSeatsRequest.SeatSectionConfiguration section = GenerateSeatsRequest.SeatSectionConfiguration.builder()
                .sectionCode("A")
                .rows(1)
                .seatsPerRow(5)
                .priceMultiplier(new BigDecimal("1.0"))
                .build();

        GenerateSeatsRequest generateSeatsRequest = GenerateSeatsRequest.builder()
                        .sectionConfigurations(List.of(section))
                        .build();

        given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(generateSeatsRequest)
                .when()
                .post("/admin/events/" + eventId + "/seats")
                .then()
                .statusCode(200);
        
        System.out.println("Created test event with ID: " + eventId);

        // 3. For Negative Scenarios: Reserve one seat by other user if @ReservaInvalida tag is present
        if (scenario.getSourceTagNames().contains("@ReservaInvalida")) {
            System.out.println("DEBUG: Scenario @ReservaInvalida detected via Scenario object. Pre-blocking seat for event " + eventId);
            forzarAsientoReservado(eventId);
        }
    }

    private String getInventoryApiBaseUrl() {
        return Serenity.environmentVariables().optionalProperty("inventory.api.url")
                .orElse("http://localhost:50005");
    }

public static void forzarAsientoReservado(String eventId) {
    String catalogUrl = "http://localhost:50001";
    String inventoryUrl = "http://localhost:50002";
    String orderingUrl = "http://localhost:5003";

    // 1. Obtener los IDs de los asientos (Mantenemos tu lógica de JsonPath que está perfecta)
    String response = given().baseUri(catalogUrl).when().get("/admin/events/" + eventId)
            .then().statusCode(200).extract().asString();
    
    // Simplificación de path (ajusta según la estructura real de tu JSON de Catalog)
    List<String> seatIds = io.restassured.path.json.JsonPath.from(response).getList("sections.seats.id.flatten()");
    Double seatPrice = io.restassured.path.json.JsonPath.from(response).getDouble("sections[0].seats[0].basePrice");

    if (seatIds != null && !seatIds.isEmpty()) {
        String seatId = seatIds.get(0); 
        String randomBlockingUser = java.util.UUID.randomUUID().toString();

        // 1. POST a Inventory (SOLO seatId y customerId)
        String inventoryPayload = String.format("{\"seatId\": \"%s\", \"customerId\": \"%s\"}", 
                                       seatId, randomBlockingUser);

        String reservationId = given()
            .baseUri(inventoryUrl)
            .contentType(ContentType.JSON)
            .body(inventoryPayload)
            .when().post("/reservations")
            .then().statusCode(oneOf(200, 201)) // Aceptamos ambos por seguridad
            .extract().path("reservationId");

        // 2. POST a Ordering (Debe incluir seatId, price y userId)
        String orderingPayload = String.format(
            "{\"reservationId\": \"%s\", \"seatId\": \"%s\", \"price\": %f, \"userId\": \"%s\"}", 
            reservationId, seatId, seatPrice, randomBlockingUser);

        given()
            .baseUri(orderingUrl)
            .contentType(ContentType.JSON)
            .body(orderingPayload)
            .when().post("/cart/add")
            .then().statusCode(200);

        Serenity.setSessionVariable("BLOCKED_SEAT_ID").to(seatId);
        System.out.println("✅ ÉXITO: Asiento " + seatId + " bloqueado para el test negativo.");
    }
}
    @After
    public void cleanupTestEvent() {
        // Disabling cleanup as requested to avoid 400 errors blocking the run
        /*
        String eventId = Serenity.sessionVariableCalled("CREATED_EVENT_ID");
        if (eventId != null) {
            String baseUrl = getApiBaseUrl();
            
            given()
                .baseUri(baseUrl)
                .when()
                .post("/admin/events/" + eventId + "/deactivate")
                .then()
                .statusCode(200);
                
            System.out.println("Deactivated test event with ID: " + eventId);
        }
        */
    }
}
