package no.fractal.socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class FractalProtocol {

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

	public int getIdHeaderBytesLength() {
		return ID_LENGTH;
	}

	public int getMetaHeaderByteLength() {
		return META_LENGTH;
	}

	/**
	 * Reads the header data from the input stream. This operation is blocking.
	 * 
	 * @param in stream reader
	 * @throws IOException thrown if stream is closed
	 */
	public void readHeader(BufferedInputStream in) throws IOException {
		this.readId(in);
		this.readMeta(in);
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
