package no.fractal.socket.payload;

public class AuthenticationPayload extends PayloadBase {

	private String username;

	public AuthenticationPayload() {
	}

	@Override
	public void execute() {
		System.out.println("AUTHENTICATION IS EXECUTED");
		System.out.println("I have authfield: " + this.getUsername());

	}

	public String getUsername() {
		return username;
	}

}
