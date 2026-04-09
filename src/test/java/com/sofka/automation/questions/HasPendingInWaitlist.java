package com.sofka.automation.questions;

import com.sofka.automation.utils.ApiConstants;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HasPendingInWaitlist implements Question<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(HasPendingInWaitlist.class);

    private final String eventId;
    private final String waitlistUrl;

    public HasPendingInWaitlist(String eventId) {
        this.eventId = eventId;
        this.waitlistUrl = ApiConstants.DEFAULT_WAITLIST_API_URL;
    }

    public static HasPendingInWaitlist paraEvento(String eventId) {
        return new HasPendingInWaitlist(eventId);
    }

    @Override
    public Boolean answeredBy(net.serenitybdd.screenplay.Actor actor) {
        try {
            Response response = RestAssured.given()
                    .baseUri(waitlistUrl)
                    .queryParam("eventId", eventId)
                    .get(ApiConstants.WAITLIST_HAS_PENDING_ENDPOINT);

            boolean result = response.jsonPath().getBoolean("hasPending");
            logger.info("has-pending para evento {}: {}", eventId, result);
            return result;
        } catch (Exception e) {
            logger.error("Error consultando has-pending para evento {}: {}", eventId, e.getMessage());
            return true;
        }
    }
}
