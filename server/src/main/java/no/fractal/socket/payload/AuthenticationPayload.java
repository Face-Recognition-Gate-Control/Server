package no.fractal.socket.payload;

import no.fractal.socket.Client;

import no.fractal.socket.meta.AuthenticationMeta;
import no.fractal.util.Parser;

public class AuthenticationPayload extends PayloadBase<AuthenticationMeta> {

	AuthenticationMeta meta = this.getMeta(AuthenticationMeta.class);

	public AuthenticationPayload(Client client, Parser<AuthenticationMeta> mf) {
		super(client, mf);
	}

	@Override
	public void execute() {
		System.out.println("AUTHENTICATION IS EXECUTED");
		System.out.println("I have authfield: " + meta.getUsername());

	}

}
