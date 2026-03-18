package com.sofka.automation.questions;

import com.sofka.automation.ui.EventPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Attribute;

public class ElEstadoDelAsiento implements Question<String> {

    @Override
    public String answeredBy(Actor actor) {
        if (EventPage.COUNTDOWN_TIMER.resolveFor(actor).isVisible()) {
            return "Reserved";
        }
        return "Available";
    }

    public static ElEstadoDelAsiento es() {
        return new ElEstadoDelAsiento();
    }
}
