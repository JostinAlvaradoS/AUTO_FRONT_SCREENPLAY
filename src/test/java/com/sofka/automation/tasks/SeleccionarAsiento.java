package com.sofka.automation.tasks;

import com.sofka.automation.ui.EventPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class SeleccionarAsiento implements Task {

    private final String status;

    public SeleccionarAsiento(String status) {
        this.status = status;
    }

    public static SeleccionarAsiento conEstado(String status) {
        return Tasks.instrumented(SeleccionarAsiento.class, status);
    }

    public static SeleccionarAsiento disponible() {
        return Tasks.instrumented(SeleccionarAsiento.class, "Available");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            WaitUntil.the(EventPage.SEAT_BY_STATUS.of(status), isVisible()).forNoMoreThan(10).seconds(),
            Click.on(EventPage.SEAT_BY_STATUS.of(status)),
            WaitUntil.the(EventPage.RESERVE_BUTTON, isVisible()).forNoMoreThan(5).seconds(),
            Click.on(EventPage.RESERVE_BUTTON)
        );
    }
}
