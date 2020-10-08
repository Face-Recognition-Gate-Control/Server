package no.fractal.socket.messages.recive;

import no.fractal.socket.messages.send.PongMessage;
import no.fractal.socket.payload.PayloadBase;

public class PingPayload extends PayloadBase {

    @Override
    public void execute() {
        this.dispatcher.addMessage(new PongMessage());
    }
}
