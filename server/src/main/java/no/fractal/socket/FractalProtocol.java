package no.fractal.socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import no.fractal.socket.meta.Segment;
import no.fractal.socket.payload.InvalidPayloadException;
import no.fractal.socket.messages.recive.PayloadBase;
import no.fractal.util.Parser;

public class FractalProtocol {

	/**
	 * Total size of the payload
	 */
	private final int PAYLOAD_LENGTH = 4;

	/**
	 * Byte size of the ID header
	 */
	private final int ID_LENGTH = 4;

	/**
	 * Byte size of the segment header
	 */
	private final int SEGMENT_LENGTH = 4;

	/**
	 * Byte size of the json payload
	 */
	private final int JSON_LENGTH = 4;

	/**
	 * Bytes remaing for this payload to read
	 */
	private int remainingPayloadBytes = 0;

	private String id;

	private String segments;

	private String jsonPayload;

	private Parser parser;

	public FractalProtocol(Parser parser) {
		this.parser = parser;
	}

	public int getIdHeaderBytesLength() {
		return ID_LENGTH;
	}

	public int getMetaHeaderByteLength() {
		return SEGMENT_LENGTH;
	}

	/**
	 * Reads the full payload from the input stream. This operation is blocking.
	 * 
	 * @param in stream reader
	 * @throws IOException thrown if stream is closed
	 */
	public PayloadBuilder readPayload(BufferedInputStream in) throws IOException {
		this.remainingPayloadBytes = this.readByteSize(in, PAYLOAD_LENGTH);
		this.id = this.readHeader(in, ID_LENGTH);
		this.segments = this.readHeader(in, SEGMENT_LENGTH);
		this.jsonPayload = this.readHeader(in, JSON_LENGTH);
		return new PayloadBuilder(this.readSegmentFiles(in));
	}

	private void reduceRemaingPayloadBytes(int bytesRed) {
		this.remainingPayloadBytes -= bytesRed;
	}

	/**
	 * Reads N bytes from the stream and returns the size of the bytes.
	 * 
	 * @param in     input stream to read from
	 * @param length how many bytes to read
	 * @return bytes as int
	 * @throws IOException if stream closes or reading fails.
	 */
	private int readByteSize(BufferedInputStream in, int length) throws IOException {
		byte[] input = new byte[length];
		int bytesRead = in.read(input, 0, input.length);
		if (bytesRead <= 0)
			throw new IOException("Bytestream closed");
		reduceRemaingPayloadBytes(bytesRead);
		int size = 1;
		switch (length) {
			case 1:
				size = input[0];
				break;
			case 2:
				size = ByteBuffer.wrap(input).getShort();
				break;
			case 4:
				size = ByteBuffer.wrap(input).getInt();
				break;

		}
		return size;
	}

	private String readHeader(BufferedInputStream in, int headerSize) throws IOException {
		int size = readByteSize(in, headerSize);
		byte[] input = new byte[size];
		input = new byte[size];
		int bytesRead = in.read(input, 0, input.length);
		if (bytesRead <= 0)
			throw new IOException("Bytestream closed");
		reduceRemaingPayloadBytes(bytesRead);
		return new String(input, StandardCharsets.UTF_8).trim();
	}

	/**
	 * Reads the rest of the stream, if there is more data left for this payload.
	 * This makes sure we dont start the next payload read at a position where we
	 * read left over data from last payload, where an error etc has occured.
	 * 
	 * Blocking operation
	 * 
	 * @param in input stream to read from
	 * @throws IOException when IO goes wrong
	 */
	public void clearStream(InputStream in) throws IOException {
		if (this.remainingPayloadBytes > 0) {
			in.readNBytes(this.remainingPayloadBytes);
		}
	}

	/**
	 * Returns a list of all file segments from a request.
	 * 
	 * @param in input stream for reading files
	 * @return map of all segments
	 */
	private Map<String, Segment> readSegmentFiles(BufferedInputStream in) {
		Map<String, Segment> segments = getParsedSegments();

		var sr = new SegmentReader();
		segments.forEach((key, segment) -> {
			segment.setFile(sr.writeSegmentToTemp(in, segment));
			reduceRemaingPayloadBytes(segment.getSize());
		});

		return segments;
	}

	/**
	 * Returns a map of all the segments in the payload header. The key of the map
	 * is the field name of the segment, for identification.
	 * 
	 * @return map of segment header
	 */
	private Map<String, Segment> getParsedSegments() {
		var keyedSegments = new LinkedHashMap<String, Segment>();
		try {
			String segments = this.getSegments();
			JsonArray segmentsArray = JsonParser.parseString(segments).getAsJsonArray();
			segmentsArray.forEach((segment) -> {
				var segmentObject = segment.getAsJsonObject();
				var key = (String) segmentObject.keySet().toArray()[0];
				keyedSegments.put(key, new Segment(segmentObject.get(key).getAsJsonObject()));

			});

		} catch (IndexOutOfBoundsException e) {
			throw new InvalidPayloadException("The payload is missing segment identifier: key for the segment");
		} catch (IllegalStateException e) {
			throw new InvalidPayloadException("The payload is not a valid \n " + e.getMessage());
		}
		return keyedSegments;
	}

	/**
	 * Returns the segment data fetched from the data header
	 * 
	 * @return segment data fetched from data header
	 */
	public String getSegments() {
		return this.segments;
	}

	/**
	 * Returns the json string payload
	 * 
	 * @return json string payload
	 */
	public String getJsonPayload() {
		return this.jsonPayload;
	}

	/**
	 * Returns the packet id from the data header
	 * 
	 * @return packet id from the data header
	 */
	public String getId() {
		return this.id;
	}

	public class PayloadBuilder {

		Map<String, Segment> segments;

		public PayloadBuilder(Map<String, Segment> segments) {
			this.segments = segments;
		}

		/**
		 * Creates a payload object from the defined type, and inject data from segment,
		 * and all file segments.
		 * 
		 * @param <E> type of the payload
		 * @param t   type of the payload
		 * @return payload object
		 */
		public <E extends PayloadBase> E createPayloadObject(Class<E> t) {
			E payload = parser.parse(t, getJsonPayload());
			payload.setSegments(segments);
			return payload;
		}
	}

}
