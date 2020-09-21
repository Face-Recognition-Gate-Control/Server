package no.fractal.socket.payload;

/**
 * Describes an exception where the payload do not exist.
 */
public class NoSuchPayloadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchPayloadException() {
	}

	public NoSuchPayloadException(String message) {
		super(message);
	}

	public NoSuchPayloadException(String message, Throwable cause) {
		super(message, cause);
	}

}
