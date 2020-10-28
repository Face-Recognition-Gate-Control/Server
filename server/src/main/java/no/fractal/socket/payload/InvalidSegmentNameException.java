package no.fractal.socket.payload;

/**
 * Describes an exception when a segment name is invalid or cant be found
 */
public class InvalidSegmentNameException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidSegmentNameException() {
    }

    public InvalidSegmentNameException(String message) {
        super(message);
    }

    public InvalidSegmentNameException(String message, Throwable cause) {
        super(message, cause);
    }

}
