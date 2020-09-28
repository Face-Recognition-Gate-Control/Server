package no.fractal.socket.payload;

/**
 * Describes an exception where the is invalid/ not possible to parse
 */
public class InvalidPayloadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidPayloadException() {
	}

	public InvalidPayloadException(String message) {
		super(message);
	}

	public InvalidPayloadException(String message, Throwable cause) {
		super(message, cause);
	}

}
