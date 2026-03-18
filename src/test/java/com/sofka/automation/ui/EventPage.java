package com.sofka.automation.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class EventPage {

    /**
     * Target for a specific seat button based on its aria-label or status.
     * The component renders as: 
     * aria-label="Seat {sectionCode}{rowNumber}-{seatNumber}, ${price}, {status}"
     */
    public static final Target SEAT_BY_STATUS = Target.the("seat button by status")
            .locatedBy("//button[contains(@aria-label, '{0}')]");

    public static final Target AVAILABLE_SEAT = Target.the("available seat button")
            .locatedBy("//button[contains(@aria-label, 'Available')]");

    public static final Target DISABLED_SEAT = Target.the("disabled seat button")
            .locatedBy("//button[@disabled and contains(@aria-label, 'Reserved')]");

    public static final Target RESERVE_BUTTON = Target.the("reserve and add to cart button")
            .located(By.xpath("//button[contains(text(), 'Reserve & Add to Cart')]"));

    public static final Target REMOVE_FROM_CART_BUTTON = Target.the("remove from cart button")
            .located(By.xpath("//button[contains(text(), 'Remove from Cart')]"));

    public static final Target SEATMAP_CONTAINER = Target.the("seatmap container")
            .locatedBy(".flex.flex-col.gap-6");

    // Login UI Elements
    public static final Target USER_ID_INPUT = Target.the("user id input")
            .located(By.id("userId"));

    public static final Target LOGIN_BUTTON = Target.the("login button")
            .located(By.xpath("//button[@type='submit' and contains(text(), 'Sign In')]"));

    public static final Target COUNTDOWN_TIMER = Target.the("countdown timer in cart")
            .locatedBy(".font-mono.text-sm.font-medium.tabular-nums");
}
