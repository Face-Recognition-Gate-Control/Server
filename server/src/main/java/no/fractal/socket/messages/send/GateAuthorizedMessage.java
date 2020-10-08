package no.fractal.socket.messages.send;

import com.google.gson.JsonObject;
import no.fractal.socket.send.AbstractMessage;

public class GateAuthorizedMessage extends AbstractMessage {
    public static final String MESSAGE_TYPE = "gate_authorized";

    /**
     * builds the gate authorised message
     *
     * @param stationName the name of the station to authorise
     */
    public GateAuthorizedMessage(String stationName) {
        super(MESSAGE_TYPE);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("station_name", stationName);
        this.addJsonBody(jsonObject);

    }
}
