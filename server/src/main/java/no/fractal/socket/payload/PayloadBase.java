package no.fractal.socket.payload;

import java.util.Map;

import no.fractal.socket.meta.Segment;

/**
 * Abstract class for all payloads. All payloads can have segments (files)
 * attached.
 */
public abstract class PayloadBase {

	transient Map<String, Segment> segments;

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
	 * Executes the payload instructions for this payload. The meta includes all
	 * data for reading the payload.
	 * 
	 * @param meta meta header for this payload
	 */
	public abstract void execute();

}
