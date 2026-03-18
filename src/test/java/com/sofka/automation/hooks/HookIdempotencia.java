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

        // Determine if this is a negative scenario test
        boolean isInvalidReservationTest = scenario.getSourceTagNames().contains("@ReservaInvalida");
        String eventNamePrefix = isInvalidReservationTest ? "[INVALID_RESERVATION] " : "";

        // 1. Create Event
        CreateEventRequest eventRequest = CreateEventRequest.builder()
                .name(eventNamePrefix + "Automation Test Event " + System.currentTimeMillis())
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
        
        if (isInvalidReservationTest) {
            System.out.println("✅ Created INVALID_RESERVATION test event with ID: " + eventId);
        } else {
            System.out.println("✅ Created test event with ID: " + eventId);
        }

        // 3. For Negative Scenarios: Reserve one seat by other user if @ReservaInvalida tag is present
        if (isInvalidReservationTest) {
            System.out.println("⚙️  Scenario @ReservaInvalida detected. Pre-blocking seat for event " + eventId);
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

    try {
        // 1. Obtener el seatmap con los asientos disponibles
        System.out.println("🔍 Fetching seatmap from: " + catalogUrl + "/events/" + eventId + "/seatmap/");
        
        String response = given().baseUri(catalogUrl).when().get("/events/" + eventId + "/seatmap/")
                .then().statusCode(200).extract().asString();
        
        System.out.println("📊 Seatmap Response: " + response);
        
        io.restassured.path.json.JsonPath jsonPath = io.restassured.path.json.JsonPath.from(response);
        
        // Obtener los asientos disponibles del seatmap
        List<String> availableSeats = jsonPath.getList("seats.findAll { it.status == 'available' }.id");
        Double seatPrice = null;
        
        System.out.println("📋 Available seats found: " + availableSeats);
        
        // Si no encontramos con el filtro, obtenemos todos los asientos
        if (availableSeats == null || availableSeats.isEmpty()) {
            availableSeats = jsonPath.getList("seats.id");
            System.out.println("📋 All seats in seatmap: " + availableSeats);
        }
        
        try {
            seatPrice = jsonPath.getDouble("seats[0].basePrice");
        } catch (Exception e) {
            System.out.println("⚠️  Could not extract price, using default: " + e.getMessage());
            seatPrice = 50.0;
        }

        if (availableSeats != null && !availableSeats.isEmpty()) {
            String seatId = availableSeats.get(0);
            String randomBlockingUser = java.util.UUID.randomUUID().toString();
            
            System.out.println("🎫 Using seatId: " + seatId);
            System.out.println("👤 Blocking with customerId: " + randomBlockingUser);

            // POST a Inventory (SOLO seatId y customerId)
            String inventoryPayload = String.format("{\"seatId\": \"%s\", \"customerId\": \"%s\"}", 
                                           seatId, randomBlockingUser);
            
            System.out.println("📤 Sending to Inventory: POST " + inventoryUrl + "/reservations");
            System.out.println("   Payload: " + inventoryPayload);

            String reservationId = given()
                .baseUri(inventoryUrl)
                .contentType(ContentType.JSON)
                .body(inventoryPayload)
                .when().post("/reservations")
                .then().statusCode(oneOf(200, 201))
                .extract().path("reservationId");
            
            System.out.println("✅ Reservation created with ID: " + reservationId);
            Serenity.setSessionVariable("BLOCKED_SEAT_ID").to(seatId);
            System.out.println("✅ ÉXITO: Asiento " + seatId + " bloqueado para el test negativo.");
        } else {
            System.out.println("❌ ERROR: No se encontraron asientos en el seatmap del evento");
        }
    } catch (Exception e) {
        System.out.println("❌ ERROR en forzarAsientoReservado: " + e.getMessage());
        e.printStackTrace();
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
