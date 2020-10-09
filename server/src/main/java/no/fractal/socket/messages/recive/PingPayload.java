package no.fractal.socket.messages.recive;

import no.fractal.socket.messages.send.PongMessage;

public class PingPayload extends PayloadBase {

    @Override
    public void execute() {
        this.dispatcher.addMessage(new PongMessage());
    }
}
