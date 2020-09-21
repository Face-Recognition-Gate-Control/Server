package no.fractal.socket.payload;

import no.fractal.socket.Client;

import no.fractal.socket.meta.AuthenticationMeta;

public class AuthenticationPayload extends PayloadBase<AuthenticationMeta> {

	private AuthenticationMeta meta;

	public AuthenticationPayload(Client client, AuthenticationMeta meta) {
		super(client);
		setMeta(meta);
	}

	private void setMeta(AuthenticationMeta meta) {
		if (meta == null)
			throw new IllegalArgumentException("Meta can not be null");

		this.meta = meta;
	}

	@Override
	public void execute() {
		System.out.println("AUTHENTICATION IS EXECUTED");
		System.out.println("I have authfield: " + meta.getUsername());

	}

}
