package no.fractal.socket.messages.recive;

import no.fractal.server.ClientService;

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
            ClientService.getInstance().createUserEnteredEvent(UUID.fromString(session_id),
                    UUID.fromString(client.getClientID()));

        } catch (Exception e) {
            e.printStackTrace();
            // TODO; Implement login failure handlin
        }

    }
}
