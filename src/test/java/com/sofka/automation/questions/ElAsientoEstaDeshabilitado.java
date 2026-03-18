package com.sofka.automation.questions;

import com.sofka.automation.ui.EventPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

import java.util.List;

public class ElAsientoEstaDeshabilitado implements Question<Boolean> {

    @Override
    public Boolean answeredBy(Actor actor) {
        try {
            // Attempt: Look for button with disabled attribute and Reserved in aria-label
            List<WebElementFacade> disabledButtons = EventPage.DISABLED_SEAT.resolveAllFor(actor);
            
            if (disabledButtons != null && !disabledButtons.isEmpty()) {
                System.out.println("✅ Found " + disabledButtons.size() + " disabled seat button(s)");
                for (WebElementFacade btn : disabledButtons) {
                    System.out.println("   - Aria-label: " + btn.getAttribute("aria-label"));
                    System.out.println("   - Disabled: " + btn.getAttribute("disabled"));
                }
                return true;
            }
            
            System.out.println("❌ No disabled seat found using DISABLED_SEAT target");
            return false;
            
        } catch (Exception e) {
            System.out.println("❌ Error checking disabled seat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static ElAsientoEstaDeshabilitado estaDeshabilirtado() {
        return new ElAsientoEstaDeshabilitado();
    }
}


