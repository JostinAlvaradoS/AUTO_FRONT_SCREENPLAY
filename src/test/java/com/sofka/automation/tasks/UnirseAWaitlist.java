package com.sofka.automation.tasks;

import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.InputField;
import net.serenitybdd.core.annotations.findby.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class UnirseAWaitlist implements Task {

    private final String email;

    public UnirseAWaitlist(String email) {
        this.email = email;
    }

    public static UnirseAWaitlist conEmail(String email) {
        return instrumented(UnirseAWaitlist.class, email);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(Button.withText("Join the Waitlist")),
                Enter.theValue(email).into(InputField.withNameOrId("waitlist-email")),
                Click.on(Button.withText("Join Waitlist"))
        );
    }
}
