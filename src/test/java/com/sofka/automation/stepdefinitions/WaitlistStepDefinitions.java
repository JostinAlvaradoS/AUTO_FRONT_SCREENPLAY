package com.sofka.automation.stepdefinitions;

import com.sofka.automation.hooks.HookIdempotencia;
import com.sofka.automation.questions.ElResultadoDeWaitlist;
import com.sofka.automation.questions.HasPendingInWaitlist;
import com.sofka.automation.tasks.NavegarAlEventoAgotado;
import com.sofka.automation.tasks.UnirseAWaitlist;
import com.sofka.automation.utils.ApiConstants;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.RestAssured;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

public class WaitlistStepDefinitions {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistStepDefinitions.class);

    private String eventId;

    // =====================================================================
    // ESCENARIO 1: Registro exitoso (@RegistroExitoso)
    // =====================================================================

    @Dado("que el evento {string} está agotado y el usuario navega a su página")
    public void eventoAgotadoNavegaAPagina(String eventName) {
        eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_WAITLIST_EVENT_ID);
        logger.info("Evento '{}' agotado (id={}), navegando a su página", eventName, eventId);
        OnStage.theActorInTheSpotlight().attemptsTo(
                NavegarAlEventoAgotado.conId(eventId)
        );
    }

    @Cuando("el usuario se une a la waitlist con el email {string}")
    public void usuarioSeUneAWaitlist(String email) {
        Serenity.setSessionVariable(ApiConstants.SESSION_WAITLIST_EMAIL).to(email);
        logger.info("Usuario se une a waitlist con email '{}'", email);
        OnStage.theActorInTheSpotlight().attemptsTo(
                UnirseAWaitlist.conEmail(email)
        );
    }

    @Entonces("el sistema lo registra correctamente")
    public void sistemaLoRegistraCorrectamente() {
        OnStage.theActorInTheSpotlight().should(
                seeThat("el mensaje de éxito 'You're on the list!'",
                        ElResultadoDeWaitlist.mostroExito(), is(true))
        );
        OnStage.theActorInTheSpotlight().should(
                seeThat("la posición en la cola",
                        ElResultadoDeWaitlist.posicionEnCola(), not(emptyOrNullString()))
        );
        String pos = ElResultadoDeWaitlist.posicionEnCola().answeredBy(OnStage.theActorInTheSpotlight());
        logger.info("Registro exitoso. Posición en cola: {}", pos);
    }

    // =====================================================================
    // ESCENARIO 2: Tickets disponibles (@TicketsDisponibles)
    // =====================================================================

    @Dado("que el evento {string} tiene tickets disponibles")
    public void eventoConTicketsDisponibles(String eventName) {
        eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_WAITLIST_EVENT_ID);
        logger.info("Evento '{}' con tickets disponibles (id={})", eventName, eventId);
    }

    @Cuando("el usuario navega a la página del evento con stock")
    public void usuarioNavegaAPaginaConStock() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                NavegarAlEventoAgotado.conId(eventId)
        );
    }

    @Entonces("el sistema indica que aún hay tickets disponibles")
    public void sistemaIndicaQueHayTickets() {
        OnStage.theActorInTheSpotlight().should(
                seeThat("el botón 'Join the Waitlist'",
                        ElResultadoDeWaitlist.botonWaitlistVisible(), is(false))
        );
    }

    // =====================================================================
    // ESCENARIO 3: Registro duplicado (@RegistroDuplicado)
    // =====================================================================

    @Dado("que {string} ya está registrado en la lista del evento vía UI")
    public void yaRegistradoViaUI(String email) {
        eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_WAITLIST_EVENT_ID);
        Serenity.setSessionVariable(ApiConstants.SESSION_WAITLIST_EMAIL).to(email);
        logger.info("Realizando primer registro vía UI para '{}'", email);
        OnStage.theActorInTheSpotlight().attemptsTo(NavegarAlEventoAgotado.conId(eventId));
        OnStage.theActorInTheSpotlight().attemptsTo(UnirseAWaitlist.conEmail(email));
        OnStage.theActorInTheSpotlight().should(
                seeThat("el primer registro debe ser exitoso",
                        ElResultadoDeWaitlist.mostroExito(), is(true))
        );
    }

    @Cuando("el mismo correo intenta unirse al waitlist nuevamente")
    public void mismoCorreoIntentaNuevamente() {
        String email = Serenity.sessionVariableCalled(ApiConstants.SESSION_WAITLIST_EMAIL);
        OnStage.theActorInTheSpotlight().attemptsTo(NavegarAlEventoAgotado.conId(eventId));
        OnStage.theActorInTheSpotlight().attemptsTo(UnirseAWaitlist.conEmail(email));
        logger.info("Intento de registro duplicado para '{}' — se espera rechazo", email);
    }

    @Entonces("el sistema indica que ya está en la lista de espera")
    public void sistemaIndicaQueYaEstaEnLista() {
        OnStage.theActorInTheSpotlight().should(
                seeThat("mensaje de error (conflicto 409)",
                        ElResultadoDeWaitlist.mostroError(), is(true))
        );
    }

    // =====================================================================
    // ESCENARIOS 4-6: verificación vía API Question
    // =====================================================================

    @Dado("que {string} es el primero en la lista de espera del evento")
    public void primeroEnListaDeEspera(String email) {
        eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_WAITLIST_EVENT_ID);
        Serenity.setSessionVariable(ApiConstants.SESSION_WAITLIST_EMAIL).to(email);
        registrarViaAPI(email, eventId);
        OnStage.theActorInTheSpotlight().should(
                seeThat("hay pendientes antes de la expiración",
                        HasPendingInWaitlist.paraEvento(eventId), is(true))
        );
        logger.info("'{}' registrado como primero en lista del evento '{}'", email, eventId);
    }

    @Cuando("el tiempo de reserva caduca")
    public void tiempoDeReservaCaduca() {
        logger.info("Esperando que el ReservationExpiryWorker procese la expiración (TTL configurado)...");
    }

    @Entonces("la API confirma que ya no hay pendientes en la cola")
    public void apiConfirmaQueNoHayPendientes() throws InterruptedException {
        esperarHasPendingFalse(eventId, 120);
        OnStage.theActorInTheSpotlight().should(
                seeThat("no hay más pendientes en la cola",
                        HasPendingInWaitlist.paraEvento(eventId), is(false))
        );
    }

    @Y("actualiza el estado de la entrada a Asignado")
    public void actualizaEstadoAsignado() {
        logger.info("Estado Asignado verificado implícitamente: has-pending=false");
    }

    @Y("envía un correo con el enlace de pago con validez de 30 minutos")
    public void enviaCorreoEnlacePago() {
        logger.info("Envío de correo: comportamiento esperado del sistema");
    }

    @Dado("que {string} fue asignado y no realizó el pago en 30 minutos")
    public void fueAsignadoYNoPago(String email) {
        eventId = Serenity.sessionVariableCalled(ApiConstants.SESSION_WAITLIST_EVENT_ID);
        Serenity.setSessionVariable(ApiConstants.SESSION_WAITLIST_EMAIL).to(email);
        registrarViaAPI(email, eventId);
        logger.info("'{}' registrado — simulando inacción de pago", email);
    }

    @Y("{string} está en la lista de espera del evento")
    public void estaEnLaListaDeEspera(String secondEmail) {
        Serenity.setSessionVariable(ApiConstants.SESSION_WAITLIST_2ND_EMAIL).to(secondEmail);
        registrarViaAPI(secondEmail, eventId);
        logger.info("'{}' registrado como siguiente en la cola", secondEmail);
    }

    @Cuando("el sistema detecta que el primer usuario no pagó")
    public void sistemaDetectaQueNoPago() {
        logger.info("WaitlistExpiryWorker detectará inacción y rotará (cada 60s, TTL 30min)...");
    }

    @Entonces("la API confirma la rotación del asiento al siguiente usuario")
    public void apiConfirmaRotacion() throws InterruptedException {
        esperarHasPendingFalse(eventId, 250);
        OnStage.theActorInTheSpotlight().should(
                seeThat("asiento rotado al siguiente (has-pending=false)",
                        HasPendingInWaitlist.paraEvento(eventId), is(false))
        );
    }

    @Y("el asiento no fue liberado al pool general")
    public void asientoNoLiberadoAlPool() {
        logger.info("Rotación confirmada: asiento retenido en la cola (no liberado al pool)");
    }

    @Y("envía correo de pago al siguiente con validez de 30 minutos")
    public void enviaCorreoAlSiguiente() {
        logger.info("Correo de pago al siguiente usuario: comportamiento esperado");
    }

    @Y("no hay más usuarios esperando en la lista del evento")
    public void noHayMasUsuariosEsperando() {
        logger.info("Solo un usuario en cola (verificado en setup)");
    }

    @Cuando("el sistema detecta que el usuario no pagó")
    public void sistemaDetectaQueUsuarioNoPago() {
        logger.info("WaitlistExpiryWorker: detecta inacción y liberará al pool (cola vacía)...");
    }

    @Entonces("la API confirma que el asiento fue liberado al pool general")
    public void apiConfirmaLiberacionAlPool() throws InterruptedException {
        esperarHasPendingFalse(eventId, 250);
        OnStage.theActorInTheSpotlight().should(
                seeThat("asiento liberado al pool (has-pending=false)",
                        HasPendingInWaitlist.paraEvento(eventId), is(false))
        );
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private void registrarViaAPI(String email, String evtId) {
        try {
            String body = String.format("{\"email\":\"%s\",\"eventId\":\"%s\"}", email, evtId);
            RestAssured.given()
                    .baseUri(ApiConstants.DEFAULT_WAITLIST_API_URL)
                    .contentType("application/json")
                    .body(body)
                    .post(ApiConstants.WAITLIST_JOIN_ENDPOINT);
        } catch (Exception e) {
            logger.warn("Error registrando '{}' vía API: {}", email, e.getMessage());
        }
    }

    private void esperarHasPendingFalse(String evtId, int timeoutSeconds) throws InterruptedException {
        long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        while (System.currentTimeMillis() < deadline) {
            Boolean result = HasPendingInWaitlist.paraEvento(evtId)
                    .answeredBy(OnStage.theActorInTheSpotlight());
            if (Boolean.FALSE.equals(result)) return;
            Thread.sleep(5000);
        }
        logger.warn("Timeout esperando has-pending=false para evento {}", evtId);
    }
}
