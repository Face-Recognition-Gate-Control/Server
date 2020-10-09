package no.fractal.socket.messages.recive;

import no.fractal.server.ClientRequestDatabaseInterface;

import java.util.UUID;

public class UserEnteredPayload extends PayloadBase {

    private String session_id;

    public UserEnteredPayload() {
    }

    @Override
    public void execute() {
        try {
            ClientRequestDatabaseInterface.getInstance().registerUserEntering(UUID.fromString(session_id),
                    this.client.getGateId());
        } catch (Exception e) {
            // TODO; Implement login failure handlin
        }

    }

    /**
     * @return the session_id
     */
    public String getSession_id() {
        return session_id;
    }
}
