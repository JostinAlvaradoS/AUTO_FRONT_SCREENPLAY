package com.sofka.automation.hooks;

import com.sofka.automation.api.implementation.RestAssuredCatalogApiClient;
import com.sofka.automation.api.implementation.RestAssuredInventoryApiClient;
import com.sofka.automation.models.CreateEventRequest;
import com.sofka.automation.models.GenerateSeatsRequest;
import com.sofka.automation.services.EventSetupService;
import com.sofka.automation.services.SeatBlockingService;
import com.sofka.automation.utils.ApiConstants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Hooks para los escenarios de Waitlist en AUTO_FRONT_SCREENPLAY.
 *
 * Separa el ciclo de vida de los tests de waitlist del HookIdempotencia existente.
 * - @RegistroExitoso, @RegistroDuplicado, @AsignacionAutomatica, etc.:
 *   Crea evento + todos los asientos bloqueados (evento agotado)
 * - @TicketsDisponibles:
 *   Crea evento + asientos sin bloquear (stock disponible)
 */
public class WaitlistHook {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistHook.class);

    private static final List<String> SOLD_OUT_TAGS = List.of(
            "@RegistroExitoso", "@RegistroDuplicado",
            "@AsignacionAutomatica", "@LiberacionConSiguiente", "@LiberacionSinCola"
    );

    private final RestAssuredCatalogApiClient catalogApi;
    private final RestAssuredInventoryApiClient inventoryApi;
    private final EventSetupService eventSetupService;

    public WaitlistHook() {
        this.catalogApi        = new RestAssuredCatalogApiClient();
        this.inventoryApi      = new RestAssuredInventoryApiClient();
        this.eventSetupService = new EventSetupService(catalogApi);
    }

    @Before(order = 0)
    public void setupStage() {
        OnStage.setTheStage(new OnlineCast());
        OnStage.theActorCalled("El Usuario");
    }

    @Before(order = 1)
    public void setupWaitlistEvent(Scenario scenario) {
        boolean needsSoldOut = scenario.getSourceTagNames().stream()
                .anyMatch(SOLD_OUT_TAGS::contains);

        String eventId;
        if (needsSoldOut) {
            // Crear evento y bloquear TODOS los asientos (evento agotado)
            eventId = eventSetupService.createEventWithSeats(false);
            blockAllSeats(eventId);
        } else {
            // Crear evento con asientos disponibles
            eventId = eventSetupService.createEventWithSeats(false);
        }

        Serenity.setSessionVariable(ApiConstants.SESSION_WAITLIST_EVENT_ID).to(eventId);
        logger.info("Evento waitlist listo: {} (agotado={})", eventId, needsSoldOut);

        // Para @RegistroDuplicado: pre-registrar el email via API para que el test
        // solo deba intentar registrarse de nuevo y verificar el rechazo.
        if (scenario.getSourceTagNames().contains("@RegistroDuplicado")) {
            String preEmail = "jostin@example.com";
            preRegistrarEmail(preEmail, eventId);
            Serenity.setSessionVariable(ApiConstants.SESSION_WAITLIST_EMAIL).to(preEmail);
            logger.info("Email '{}' pre-registrado via API para escenario @RegistroDuplicado", preEmail);
        }
    }

    @After
    public void cleanupWaitlistEvent() {
        String eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_WAITLIST_EVENT_ID);
        if (eventId != null) {
            try {
                catalogApi.deactivateEvent(eventId);
                logger.info("Evento waitlist desactivado: {}", eventId);
            } catch (Exception e) {
                logger.warn("Error desactivando evento waitlist: {}", e.getMessage());
            }
        }
    }

    /**
     * Bloquea todos los asientos del evento para simular un evento completamente agotado.
     */
    private void blockAllSeats(String eventId) {
        try {
            var seatmap = catalogApi.getSeatmap(eventId);
            if (seatmap == null || seatmap.getSeats() == null) return;

            for (String seatId : seatmap.getAvailableSeats()) {
                String customerId = java.util.UUID.randomUUID().toString();
                String body = String.format("{\"seatId\":\"%s\",\"customerId\":\"%s\"}", seatId, customerId);
                RestAssured.given()
                        .baseUri(ApiConstants.DEFAULT_INVENTORY_API_URL)
                        .contentType("application/json")
                        .body(body)
                        .post(ApiConstants.RESERVATIONS_ENDPOINT);
                logger.info("Asiento {} bloqueado para evento agotado", seatId);
            }
        } catch (Exception e) {
            logger.warn("Error bloqueando todos los asientos: {}", e.getMessage());
        }
    }

    /**
     * Pre-registra un email en la waitlist via API directa.
     * Usado en @RegistroDuplicado para que el test solo verifique el rechazo.
     */
    private void preRegistrarEmail(String email, String eventId) {
        try {
            String body = String.format("{\"email\":\"%s\",\"eventId\":\"%s\"}", email, eventId);
            RestAssured.given()
                    .baseUri(ApiConstants.DEFAULT_WAITLIST_API_URL)
                    .contentType("application/json")
                    .body(body)
                    .post(ApiConstants.WAITLIST_JOIN_ENDPOINT);
        } catch (Exception e) {
            logger.warn("Error pre-registrando email '{}' en waitlist: {}", email, e.getMessage());
        }
    }
}
