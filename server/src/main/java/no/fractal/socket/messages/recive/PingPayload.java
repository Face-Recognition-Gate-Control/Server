package no.fractal.socket.messages.recive;

import com.google.gson.JsonElement;
import no.fractal.server.ClientRequestDatabaseInterface;
import no.fractal.socket.messages.send.PongMessage;
import no.fractal.socket.meta.Segment;
import no.fractal.socket.payload.PayloadBase;

import java.util.UUID;

public class PingPayload extends PayloadBase {


    @Override
    public void execute() {
        this.dispatcher.addMessage(new PongMessage());
    }
}
