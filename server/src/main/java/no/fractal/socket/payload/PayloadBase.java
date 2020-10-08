package no.fractal.socket.payload;

import java.util.Map;

import no.fractal.socket.FractalClient;
import no.fractal.socket.meta.Segment;
import no.fractal.socket.send.MessageDispatcher;

/**
 * Abstract class for all payloads. All payloads can have segments (files)
 * attached.
 */
public abstract class PayloadBase {

	protected transient MessageDispatcher dispatcher;

	public void setDispatcher(MessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	protected FractalClient client;

	public void setClient(FractalClient client) {
		this.client = client;
	}

	/**
	 * Holds all segments attached to this payload
	 */
	protected transient Map<String, Segment> segments;

	public PayloadBase() {
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
