package no.fractal.socket.messages.send;

import com.google.gson.JsonObject;
import no.fractal.socket.send.AbstractMessage;

import java.util.UUID;

public class UserNotIdentifiedMessage extends AbstractMessage {
    public static final String MESSAGE_TYPE = "user_unidentified";

    /**
     * builds the not identefied message
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
