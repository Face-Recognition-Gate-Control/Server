package no.fractal.socket.messages.recive;

import no.fractal.server.ClientService;

import java.util.UUID;

/**
 * Payload received when a users enters the gate station after validation/authorization.
 * It registers the user entrance in the system to the gate a user entered on.
 */
public class UserEnteredPayload extends PayloadBase {

    private String session_id;

    /**
     * Empty constructor is required for JSON parser
     */
    public UserEnteredPayload() {
    }

    /**
     * @return the session_id
     */
    public String getSession_id() {
        return session_id;
    }

    @Override
    public void execute() {
        try {
            ClientService.getInstance().createUserEnteredEvent(UUID.fromString(session_id),
                    UUID.fromString(client.getClientID()));

        } catch (Exception e) {
            e.printStackTrace();
            // TODO; Implement login failure handlin
        }

    }
}
