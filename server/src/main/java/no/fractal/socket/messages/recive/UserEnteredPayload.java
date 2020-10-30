package no.fractal.socket.messages.recive;

import no.fractal.server.ClientRequestDatabaseInterface;

import java.util.UUID;

public class UserEnteredPayload extends PayloadBase {

    private String session_id;

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
            ClientRequestDatabaseInterface.getInstance().registerUserEntering(UUID.fromString(session_id),
                    UUID.fromString(client.getClientID()));

        } catch (Exception e) {
            e.printStackTrace();
            // TODO; Implement login failure handlin
        }

    }
}
