package com.sofka.automation.hooks;

import com.sofka.automation.api.implementation.RestAssuredCatalogApiClient;
import com.sofka.automation.api.implementation.RestAssuredInventoryApiClient;
import com.sofka.automation.services.EventSetupService;
import com.sofka.automation.services.SeatBlockingService;
import com.sofka.automation.utils.ApiConstants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hook para configurar el ambiente antes de cada escenario de prueba.
 * 
 * Responsabilidad única: Gestionar ciclo de vida de tests (@Before/@After).
 * Las operaciones específicas de API están delegadas a servicios.
 * 
 * Implementa Dependency Inversion Principle:
 * - Las dependencias son inyectadas a través de constructores
 * - Depende de abstracciones (interfaces), no implementaciones concretas
 */
public class HookIdempotencia {
    
    private static final Logger logger = LoggerFactory.getLogger(HookIdempotencia.class);
    private final EventSetupService eventSetupService;
    private final SeatBlockingService seatBlockingService;

    public HookIdempotencia() {
        this.eventSetupService = new EventSetupService(new RestAssuredCatalogApiClient());
        this.seatBlockingService = new SeatBlockingService(
            new RestAssuredCatalogApiClient(),
            new RestAssuredInventoryApiClient()
        );
    }

    // Constructor para testing con dependencias inyectadas
    public HookIdempotencia(EventSetupService eventSetupService, SeatBlockingService seatBlockingService) {
        this.eventSetupService = eventSetupService;
        this.seatBlockingService = seatBlockingService;
    }

    @Before(order = 0)
    public void setupStage() {
        logger.debug("Setting up Serenity stage");
        OnStage.setTheStage(new OnlineCast());
    }

    @Before(order = 1)
    public void setupEventForTest(Scenario scenario) {
        logger.info("Setting up test for scenario: {}", scenario.getName());
        
        boolean isInvalidReservationTest = isInvalidReservationScenario(scenario);
        String eventId = eventSetupService.createEventWithSeats(isInvalidReservationTest);
        
        Serenity.setSessionVariable(ApiConstants.SESSION_CREATED_EVENT_ID).to(eventId);
        
        if (isInvalidReservationTest) {
            logger.info("Pre-blocking seat for invalid reservation test");
            seatBlockingService.blockFirstAvailableSeat(eventId);
        }
        
        logger.info("Test setup completed for event: {}", eventId);
    }

    @After
    public void cleanupTestEvent() {
        String eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_CREATED_EVENT_ID);
        if (eventId != null) {
            try {
                logger.info("Cleaning up test event: {}", eventId);
                new RestAssuredCatalogApiClient().deactivateEvent(eventId);
                logger.info("Event cleanup completed: {}", eventId);
            } catch (Exception e) {
                logger.error("Error during cleanup for event: {}", eventId, e);
            }
        }
    }

    /**
     * Checks if current scenario is for invalid reservation testing
     * @param scenario Cucumber scenario
     * @return true if scenario has @ReservaInvalida tag
     */
    private boolean isInvalidReservationScenario(Scenario scenario) {
        return scenario.getSourceTagNames().contains(ApiConstants.TAG_INVALID_RESERVATION);
    }
}
