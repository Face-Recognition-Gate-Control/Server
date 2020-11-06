package no.fractal.socket.messages.send;

import com.google.gson.JsonObject;
import no.fractal.socket.send.AbstractMessage;

import java.io.File;
import java.util.UUID;

/**
 * Message for when a user is idnetified on the server.
 * The message includes the thumbnail of the user, the session id(user id), a message for the identification and
 * if the user is allowed access of not.
 */
public class UserIdentefiedMessage extends AbstractMessage {
    public static final String MESSAGE_TYPE = "user_identified";

    /**
     * builds the user is identefied message
     *
     * @param thumbnail the thumbnail file
     * @param sessionId the id of the of the reg user -> is ok -> answer exchange
     * @param message   the message to show
     * @param allowed   wheter or not to allow the user throghhghgrohgohgo
     */
    public UserIdentefiedMessage(File thumbnail, UUID sessionId, String message, boolean allowed) {
        super(MESSAGE_TYPE);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("session_id", sessionId.toString());
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("access_granted", allowed);
        this.addJsonBody(jsonObject);

        FileSegment fileSegment = new FileSegment(thumbnail);

        this.addSegment("thumbnail", fileSegment);

    }
}

