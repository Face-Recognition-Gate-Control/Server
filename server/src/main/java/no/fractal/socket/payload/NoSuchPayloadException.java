package no.fractal.socket.payload;

/**
 * Describes an exception where the payload do not exist.
 */
public class NoSuchPayloadException extends RuntimeException {

	public NoSuchPayloadException() {
	}

	public NoSuchPayloadException(String message) {
		super(message);
	}

	public NoSuchPayloadException(String message, Throwable cause) {
		super(message, cause);
	}

}
