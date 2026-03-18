package com.sofka.automation.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.core.Serenity;

public class NavegarAlEvento implements Task {

    private final String eventId;

    public NavegarAlEvento(String eventId) {
        this.eventId = eventId;
    }

    public static NavegarAlEvento conId(String eventId) {
        return Tasks.instrumented(NavegarAlEvento.class, eventId);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = Serenity.environmentVariables().optionalProperty("webdriver.base.url")
                .orElse("http://localhost:3000");
        String url = String.format("%s/events/%s", baseUrl, eventId);
        actor.attemptsTo(
            Open.url(url)
        );
    }
}
