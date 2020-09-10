package no.fractal.socket;

public class FractalProtocol {

	/**
	 * Byte size of the ID header
	 */
	private final int ID_LENGTH = 64;

	/**
	 * Byte size of the meta header
	 */
	private final int META_LENGTH = 4;

	private String id;

	private String meta;

	public String getMeta() {
		return meta;
	}

	public String getId() {
		return id;
	}

}
