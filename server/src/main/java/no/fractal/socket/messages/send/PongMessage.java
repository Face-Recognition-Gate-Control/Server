package no.fractal.socket.messages.send;

import no.fractal.socket.send.AbstractMessage;

/**
 * Pong message :D
 */
public class PongMessage extends AbstractMessage {
    public static final String MESSAGE_TYPE = "pong";

    public PongMessage() {
        super(MESSAGE_TYPE);
    }
}