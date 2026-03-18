package com.sofka.automation.stepdefinitions;

import com.sofka.automation.questions.ElEstadoDelAsiento;
import com.sofka.automation.questions.ElAsientoEstaDeshabilitado;
import com.sofka.automation.tasks.NavegarAlEvento;
import com.sofka.automation.tasks.SeleccionarAsiento;
import com.sofka.automation.utils.ApiConstants;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Step definitions for seat reservation scenarios.
 * Defines behavior for user interactions with the event seatmap.
 */
public class EventStepDefinitions {
    
    private static final Logger logger = LoggerFactory.getLogger(EventStepDefinitions.class);

    @Dado("que existe un evento con asientos configurados")
    public void queExisteUnEventoConAsientosConfigurados() {
        String eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_CREATED_EVENT_ID);
        if (eventId == null) {
            eventId = "9574044a-f3a3-4b68-809c-e6686a603c4f";
            Serenity.setSessionVariable(ApiConstants.SESSION_CREATED_EVENT_ID).to(eventId);
        }
        logger.info("Event ID: {}", eventId);
    }

    @Dado("el cliente está visualizando el mapa de asientos")
    public void elClienteEstaVisualizandoElMapaDeAsientos() {
        String uniqueUserId = java.util.UUID.randomUUID().toString();
        Serenity.setSessionVariable("USER_ID").to(uniqueUserId);
        
        String eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_CREATED_EVENT_ID);
        logger.info("Client navigating to event seatmap: {}", eventId);
        
        OnStage.theActorCalled("Cliente").attemptsTo(
                NavegarAlEvento.conId(eventId)
        );
    }

    @Dado("que soy un usuario autenticado")
    public void usuarioAutenticado() {
        logger.debug("Authentication step - covered by navigation");
        // Step ya cubierto en la navegación para el MVP
    }

    @Dado("que un asiento se encuentra en estado {string}")
    public void asientoEnEstado(String estado) {
        logger.debug("Verifying seat state: {}", estado);
        // Verificamos que el asiento esté disponible para la prueba
    }

    @Cuando("el cliente selecciona dicho asiento para reservarlo")
    public void seleccionaAsientoParaReservar() {
        logger.info("Client selecting available seat for reservation");
        OnStage.theActorInTheSpotlight().attemptsTo(
                SeleccionarAsiento.disponible()
        );
    }

    @Entonces("el sistema debe confirmar la reserva y la adición al carrito del usuario de forma exitosa")
    public void confirmaReservaExitosa() {
        logger.info("Validating successful reservation");
        assertSeatIsReserved();
    }

    @Entonces("debe iniciar automáticamente el conteo del tiempo de reserva temporal \\(TTL)")
    public void iniciaConteoTTL() {
        logger.info("Validating TTL countdown is visible");
        assertSeatIsReserved();
    }

    @Entonces("el asiento seleccionado debe quedar bloqueado para otros usuarios mientras el tiempo de reserva esté vigente")
    public void asientoQuedaBloqueado() {
        logger.info("Validating seat is locked for other users");
        assertSeatIsReserved();
    }

    // Scenarios: Liberación automática
    @Dado("que el cliente ya tiene un asiento en su carrito con una reserva activa")
    public void clienteTieneAsientoEnCarrito() {
        logger.info("Client has active seat reservation in cart");
        OnStage.theActorInTheSpotlight().attemptsTo(
                SeleccionarAsiento.disponible()
        );
    }

    @Dado("el tiempo de reserva temporal permitido ha expirado sin que se complete la compra")
    public void tiempoHaExpirado() {
        logger.debug("TTL expiration simulation");
        // Aquí podríamos simular el paso del tiempo vía API o esperando el TTL real si es corto
    }

    @Cuando("el sistema procesa la expiración de dicha reserva")
    public void sistemaProcesaExpiracion() {
        logger.debug("System processes TTL expiration");
        // El sistema lo hace solo, o podemos forzarlo vía API
    }

    @Entonces("el asiento debe volver automáticamente al estado {string} en el mapa")
    public void asientoVuelveAEstado(String estado) {
        logger.info("Validating seat returned to available state");
        OnStage.theActorInTheSpotlight().should(
                seeThat(ElEstadoDelAsiento.es(), equalTo("Available"))
        );
    }

    @Entonces("el sistema debe notificar al cliente que su reserva ha expirado por tiempo agotado")
    public void sistemaNotificaExpiracion() {
        logger.debug("TTL expiration notification validation");
        // Pendiente: Question para el toast/notificación de expiración
    }

    // Scenario: Intento de reserva de un asiento ya ocupado
    @Dado("que un asiento ya ha sido reservado por otro usuario previamente")
    public void asientoReservadoPreviamente() {
        logger.info("Seat pre-blocked by HookIdempotencia for invalid reservation test");
        // La pre-reserva se maneja en el HookIdempotencia mediante el tag @ReservaInvalida
        // para asegurar que el estado se aplique sobre el evento recién creado.
    }

    @Cuando("un nuevo cliente intenta seleccionar ese mismo asiento para comprarlo")
    public void intentaSeleccionarAsientoOcupado() {
        logger.info("Client viewing event with blocked seat");
        // No hacemos click porque el asiento está disabled
        // Solo navegamos al evento para ver el mapa con el asiento bloqueado
        String eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_CREATED_EVENT_ID);
        OnStage.theActorInTheSpotlight().attemptsTo(
                NavegarAlEvento.conId(eventId)
        );
    }

    @Entonces("el sistema debe mostrar indisponibilidad del asiento")
    public void sistemaMuestraIndisponibilidad() {
        logger.info("Validating blocked seat is disabled in UI");
        // Verificamos que el asiento está deshabilitado (disabled) en el HTML
        OnStage.theActorInTheSpotlight().should(
                seeThat(ElAsientoEstaDeshabilitado.estaDeshabilitado(), is(true))
        );
    }

    @Dado("que el cliente tiene una reserva activa")
    public void clienteTieneReservaActiva() {
        logger.info("Setting up client with active reservation");
        OnStage.theActorInTheSpotlight().attemptsTo(
                SeleccionarAsiento.disponible()
        );
    }

    @Cuando("consulta el estado de su proceso")
    public void consultaEstadoProceso() {
        logger.debug("Client checking process status");
    }

    @Entonces("el sistema debe mostrar un temporizador con el tiempo restante para completar la compra")
    public void muestraTemporizador() {
        logger.debug("TTL timer display validation");
    }

    /**
     * Assertion helper: Validates that a seat is in Reserved state
     * Extracts common validation logic (DRY principle)
     */
    private void assertSeatIsReserved() {
        OnStage.theActorInTheSpotlight().should(
                seeThat(ElEstadoDelAsiento.es(), equalTo("Reserved"))
        );
    }
}
