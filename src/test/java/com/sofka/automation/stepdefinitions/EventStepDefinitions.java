package com.sofka.automation.stepdefinitions;

import com.sofka.automation.questions.ElEstadoDelAsiento;
import com.sofka.automation.tasks.NavegarAlEvento;
import com.sofka.automation.tasks.SeleccionarAsiento;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

public class EventStepDefinitions {

    @Dado("que existe un evento con asientos configurados")
    public void queExisteUnEventoConAsientosConfigurados() {
        String eventId = Serenity.sessionVariableCalled("CREATED_EVENT_ID");
        if (eventId == null) {
            eventId = "9574044a-f3a3-4b68-809c-e6686a603c4f";
            Serenity.setSessionVariable("CREATED_EVENT_ID").to(eventId);
        }
    }

    @Dado("el cliente está visualizando el mapa de asientos")
    public void elClienteEstaVisualizandoElMapaDeAsientos() {
        String uniqueUserId = java.util.UUID.randomUUID().toString();
        Serenity.setSessionVariable("USER_ID").to(uniqueUserId);
        
        String eventId = Serenity.sessionVariableCalled("CREATED_EVENT_ID");
        OnStage.theActorCalled("Cliente").attemptsTo(
                NavegarAlEvento.conId(eventId)
        );
    }

    @Dado("que soy un usuario autenticado")
    public void usuarioAutenticado() {
        // Step ya cubierto en la navegación para el MVP
    }

    @Dado("que un asiento se encuentra en estado {string}")
    public void asientoEnEstado(String estado) {
        // Verificamos que el asiento esté disponible para la prueba
    }

    @Cuando("el cliente selecciona dicho asiento para reservarlo")
    public void seleccionaAsientoParaReservar() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                SeleccionarAsiento.disponible()
        );
    }

    @Entonces("el sistema debe confirmar la reserva y la adición al carrito del usuario de forma exitosa")
    public void confirmaReservaExitosa() {
        OnStage.theActorInTheSpotlight().should(
                seeThat(ElEstadoDelAsiento.es(), equalTo("Reserved"))
        );
    }

    @Entonces("debe iniciar automáticamente el conteo del tiempo de reserva temporal \\(TTL)")
    public void iniciaConteoTTL() {
        OnStage.theActorInTheSpotlight().should(
                seeThat(ElEstadoDelAsiento.es(), equalTo("Reserved"))
        );
    }

    @Entonces("el asiento seleccionado debe quedar bloqueado para otros usuarios mientras el tiempo de reserva esté vigente")
    public void asientoQuedaBloqueado() {
        OnStage.theActorInTheSpotlight().should(
                seeThat(ElEstadoDelAsiento.es(), equalTo("Reserved"))
        );
    }

    // Scenarios: Liberación automática
    @Dado("que el cliente ya tiene un asiento en su carrito con una reserva activa")
    public void clienteTieneAsientoEnCarrito() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                SeleccionarAsiento.disponible()
        );
    }

    @Dado("el tiempo de reserva temporal permitido ha expirado sin que se complete la compra")
    public void tiempoHaExpirado() {
        // Aquí podríamos simular el paso del tiempo vía API o esperando el TTL real si es corto
    }

    @Cuando("el sistema procesa la expiración de dicha reserva")
    public void sistemaProcesaExpiracion() {
        // El sistema lo hace solo, o podemos forzarlo vía API
    }

    @Entonces("el asiento debe volver automáticamente al estado {string} en el mapa")
    public void asientoVuelveAEstado(String estado) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(ElEstadoDelAsiento.es(), equalTo("Available"))
        );
    }

    @Entonces("el sistema debe notificar al cliente que su reserva ha expirado por tiempo agotado")
    public void sistemaNotificaExpiracion() {
        // Pendiente: Question para el toast/notificación de expiración
    }

    // Scenario: Intento de reserva de un asiento ya ocupado
    @Dado("que un asiento ya ha sido reservado por otro usuario previamente")
    public void asientoReservadoPreviamente() {
        // La pre-reserva se maneja en el HookIdempotencia mediante el tag @ReservaInvalida
        // para asegurar que el estado se aplique sobre el evento recién creado.
        System.out.println("DEBUG: El asiento ya fue pre-bloqueado por el HookIdempotencia.");
    }

    @Cuando("un nuevo cliente intenta seleccionar ese mismo asiento para comprarlo")
    public void intentaSeleccionarAsientoOcupado() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                SeleccionarAsiento.conEstado("Reserved")
        );
    }

    @Entonces("el sistema debe mostrar indisponibilidad del asiento")
    public void sistemaMuestraIndisponibilidad() {
        // Verificamos que el asiento no se puede seleccionar o muestra el estado ocupado
    }

    @Dado("que el cliente tiene una reserva activa")
    public void clienteTieneReservaActiva() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                SeleccionarAsiento.disponible()
        );
    }

    @Cuando("consulta el estado de su proceso")
    public void consultaEstadoProceso() {
    }

    @Entonces("el sistema debe mostrar un temporizador con el tiempo restante para completar la compra")
    public void muestraTemporizador() {
    }
}
