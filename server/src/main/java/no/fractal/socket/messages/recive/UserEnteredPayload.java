package no.fractal.socket.messages.recive;

import no.fractal.server.ClientRequestDatabaseInterface;
import no.fractal.socket.payload.PayloadBase;

import java.util.UUID;

public class UserEnteredPayload extends PayloadBase {

    private String session_id;

    public UserEnteredPayload() {
    }

    /**
     * Executes the payload instructions for this payload. The meta includes all
     * data for reading the payload.
     */
    @Override
    public void execute() {
        try {
            ClientRequestDatabaseInterface.getInstance().registerUserEntering(UUID.fromString(session_id), this.client.getGateId());
        } catch (Exception e){
            // TODO; Implement login failure handlin
        }


    }
}
