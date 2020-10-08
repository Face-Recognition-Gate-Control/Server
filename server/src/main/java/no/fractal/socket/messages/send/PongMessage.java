package no.fractal.socket.messages.send;

import no.fractal.socket.send.AbstractMessage;

public class PongMessage extends AbstractMessage {
    public static final String MESSAGE_TYPE = "pong";

    /**
     * makes the pong message
     *
     * @ping @pong @ping @pong
     */
    public PongMessage() {
        super(MESSAGE_TYPE);
    }
}
