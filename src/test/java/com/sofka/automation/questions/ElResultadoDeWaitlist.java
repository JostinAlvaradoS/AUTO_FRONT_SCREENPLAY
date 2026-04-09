package com.sofka.automation.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ElResultadoDeWaitlist {

    private static final Target SUCCESS_MESSAGE = Target.the("mensaje de éxito waitlist")
            .located(By.xpath("//*[contains(text(),\"You're on the list!\")]"));

    private static final Target ERROR_MESSAGE = Target.the("mensaje de error waitlist")
            .located(By.cssSelector("p.text-sm.text-destructive"));

    private static final Target WAITLIST_BUTTON = Target.the("botón Join the Waitlist")
            .located(By.xpath("//button[contains(text(), 'Join the Waitlist')]"));

    private static final Target POSITION_TEXT = Target.the("posición en la cola")
            .located(By.cssSelector("span.font-bold.text-accent.text-base"));

    public static Question<Boolean> mostroExito() {
        return actor -> SUCCESS_MESSAGE.resolveFor(actor).isDisplayed();
    }

    public static Question<Boolean> mostroError() {
        return actor -> {
            try {
                return ERROR_MESSAGE.resolveFor(actor).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        };
    }

    public static Question<String> textoDelError() {
        return actor -> {
            try {
                return ERROR_MESSAGE.resolveFor(actor).getText();
            } catch (Exception e) {
                return "";
            }
        };
    }

    public static Question<Boolean> botonWaitlistVisible() {
        return actor -> {
            try {
                return WAITLIST_BUTTON.resolveFor(actor).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        };
    }

    public static Question<String> posicionEnCola() {
        return actor -> {
            try {
                return POSITION_TEXT.resolveFor(actor).getText();
            } catch (Exception e) {
                return "";
            }
        };
    }

    private ElResultadoDeWaitlist() {
        
    }
}
