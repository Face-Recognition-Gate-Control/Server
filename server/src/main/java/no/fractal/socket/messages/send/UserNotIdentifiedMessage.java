package no.fractal.socket.messages.send;

import com.google.gson.JsonObject;
import no.fractal.socket.send.AbstractMessage;

import java.util.UUID;

/**
 * Message for when a user is not identified.
 * The response returns the session id for authorization session from the gate station, and a registration URL where
 * the user can register itself with the system.
 */
public class UserNotIdentifiedMessage extends AbstractMessage {

    public static final String MESSAGE_TYPE = "user_unidentified";

    /**
     * builds the not identified message
     *
     * @param sessionId       the id of the reg user -> is ok -> answer exchange
     * @param registrationUrl the url to show for registration
     */
    public UserNotIdentifiedMessage(UUID sessionId, String registrationUrl) {
        super(MESSAGE_TYPE);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("session_id", sessionId.toString());
        jsonObject.addProperty("registration_url", registrationUrl);
        this.addJsonBody(jsonObject);

    }
}