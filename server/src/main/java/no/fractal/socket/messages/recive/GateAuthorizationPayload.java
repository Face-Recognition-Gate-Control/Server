package no.fractal.socket.messages.recive;

import no.fractal.server.ClientService;
import no.fractal.socket.messages.send.GateAuthorizedMessage;

import java.util.UUID;

/**
 * Sent from the a gate station to authorize itself with the server.
 * The station is authorized with its id and a secret.
 */
public class GateAuthorizationPayload extends PayloadBase {

    private String login_key;
    private String station_uid;

    /**
     * Empty constructor is required for JSON parser
     */
    public GateAuthorizationPayload() {
    }

    /**
     * @return the login_key
     */
    public String getLogin_key() {
        return login_key;
    }

    /**
     * @return the station_uid
     */
    public String getStation_uid() {
        return station_uid;
    }

    @Override
    public void execute() {
        try {
            var uid = UUID.fromString(station_uid);
            boolean validationOk = ClientService.getInstance().stationManager.IsStationValid(uid,
                    login_key
            );
            if (validationOk) {
                client.setClientID(uid.toString());
                client.setAuthorized(validationOk);
                GateAuthorizedMessage gateAuthorizedMessage = new GateAuthorizedMessage("Fisk");
                this.dispatcher.addMessage(gateAuthorizedMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO; Implement login failure handlin
        }
    }
}
