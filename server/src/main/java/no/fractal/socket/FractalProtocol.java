package no.fractal.socket;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import no.fractal.socket.meta.Meta;
import no.fractal.socket.payload.PayloadBase;
import no.fractal.util.Parser;

public class FractalProtocol<T> {

	/**
	 * Byte size of the ID header
	 */
	private final int ID_LENGTH = 2;

	/**
	 * Byte size of the meta header
	 */
	private final int META_LENGTH = 4;

	private String id;

	private String meta;

	private Parser metaParser;

	public FractalProtocol(Parser metaParser) {
		this.metaParser = metaParser;
	}

	public int getIdHeaderBytesLength() {
		return ID_LENGTH;
	}

	public int getMetaHeaderByteLength() {
		return META_LENGTH;
	}

	public class PayloadBuilder {

		File[] segments;

		public PayloadBuilder(File[] segments) {
			this.segments = segments;
		}

		/**
		 * Creates a payload object from the defined type, and inject data from meta,
		 * and all file segments.
		 * 
		 * @param <E> type of the payload
		 * @param t   type of the payload
		 * @return
		 */
		public <E extends PayloadBase> E createPayloadObject(Class<E> t) {
			E payload = metaParser.parse(t, getMeta());
			payload.setSegments(segments);
			return payload;
		}
	}

	/**
	 * Reads the header data from the input stream. This operation is blocking.
	 * 
	 * @param in stream reader
	 * @throws IOException thrown if stream is closed
	 */
	public PayloadBuilder readPayload(BufferedInputStream in) throws IOException {
		this.readId(in);
		this.readMeta(in);
		return new PayloadBuilder(this.readSegments(in));
	}

	/**
	 * Returns a list of all file segments from a request.
	 * 
	 * @param in input stream for reading files
	 * @return list of all file segments
	 */
	private File[] readSegments(BufferedInputStream in) {
		return new SegmentReader(in).read(getParsedMeta().getSegments());

	}

	private String readHeader(BufferedInputStream in, int headerSize) throws IOException {
		byte[] input = new byte[headerSize];
		int bytesRead = in.read(input, 0, input.length);
		int size = 1;

		switch (headerSize) {
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
		input = new byte[size];
		bytesRead = in.read(input, 0, input.length);
		if (bytesRead <= 0)
			throw new IOException("Bytestream closed");
		return new String(input, StandardCharsets.UTF_8).trim();
	}

	private void readId(BufferedInputStream in) throws IOException {
		this.id = this.readHeader(in, ID_LENGTH);
	}

	private void readMeta(BufferedInputStream in) throws IOException {
		this.meta = this.readHeader(in, META_LENGTH);
	}

	/**
	 * Returns the parsed meta object by the Parser provided on object
	 * initialization. Returns null if there is no meta data to parse.
	 * 
	 * @param Class type to parse too
	 * @return returns parsed class or null
	 */
	public T getParsedMeta(Class<? extends T> t) {
		String meta = this.getMeta();
		if (meta == null) {
			return null;
		}

		return metaParser.parse(t, meta);
	}

	/**
	 * Returns the Meta object from header. Returns null if meta is not set.
	 * 
	 * @return returns parsed Meta or null
	 */
	public Meta getParsedMeta() {
		String meta = this.getMeta();
		if (meta == null) {
			return null;
		}

		return metaParser.parse(Meta.class, meta);
	}

	/**
	 * Returns the meta data fetched from the data header
	 * 
	 * @return meta data fetched from data header
	 */
	public String getMeta() {
		return this.meta;
	}

	/**
	 * Returns the packet id from the data header
	 * 
	 * @return packet id from the data header
	 */
	public String getId() {
		return this.id;
	}

}
