package no.fractal.socket.messages.recive;

import no.fractal.server.ClientRequestDatabaseInterface;
import no.fractal.socket.FractalClient;

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
            if (this.client instanceof FractalClient) {
                var client = (FractalClient) this.client;
                ClientRequestDatabaseInterface.getInstance().registerUserEntering(UUID.fromString(session_id),
                                                                                  client.getGateId()
                );
            }

        } catch (Exception e) {
            // TODO; Implement login failure handlin
        }

    }
}
