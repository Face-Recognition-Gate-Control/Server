package no.fractal.socket.messages.recive;

import no.fractal.socket.Client;
import no.fractal.socket.meta.Segment;
import no.fractal.socket.send.MessageDispatcher;

import java.util.Map;

/**
 * Abstract class for all payloads. All payloads can have segments (files)
 * attached.
 */
public abstract class PayloadBase {

    protected transient MessageDispatcher dispatcher;

    protected transient Client client;

    /**
     * Holds all segments attached to this payload
     */
    protected transient Map<String, Segment> segments;

    /**
     * Empty constructor is required for JSON parser
     */
    public PayloadBase() {
    }

    public void setDispatcher(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Sets the segments for this payload
     *
     * @param segments
     */
    public void setSegments(Map<String, Segment> segments) {
        this.segments = segments;
    }

    /**
     * Executes the payload instructions for this payload.
     */
    public abstract void execute();

}
