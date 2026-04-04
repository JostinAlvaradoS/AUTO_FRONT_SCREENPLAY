package com.sofka.automation.tasks;

import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Task: Navega a la página pública del evento (/events/{eventId}).
 * Usado en escenarios donde el evento está agotado y se muestra el banner de waitlist.
 */
public class NavegarAlEventoAgotado implements Task {

    private static final String BASE_URL = System.getProperty("frontend.url", "http://localhost:3000");
    private final String eventId;

    public NavegarAlEventoAgotado(String eventId) {
        this.eventId = eventId;
    }

    public static NavegarAlEventoAgotado conId(String eventId) {
        return instrumented(NavegarAlEventoAgotado.class, eventId);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(BASE_URL + "/events/" + eventId)
        );
    }
}
