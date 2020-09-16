package no.fractal.socket.meta;

/**
 * Metaheader for authnetication payload
 */
public class AuthenticationMeta extends Meta {

	/**
	 * id for authentication verification by the server
	 */
	private String username;

	public AuthenticationMeta() {
		super();
	}

	public String getUsername() {
		return username;
	}
}
