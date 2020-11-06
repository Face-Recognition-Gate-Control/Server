package no.fractal.socket.messages.recive;

import no.fractal.socket.messages.send.PongMessage;

/**
 * Ping payload, which responds with a pong message to the sender.
 */
public class PingPayload extends PayloadBase {

    @Override
    public void execute() {
        this.dispatcher.addMessage(new PongMessage());
    }
}
