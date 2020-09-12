package no.fractal.socket.payload;

import no.fractal.socket.Client;
import no.fractal.socket.meta.AuthenticationMeta;
import no.fractal.socket.meta.Meta;

public class AuthenticationPayload extends PayloadBase<AuthenticationMeta> {

	private AuthenticationMeta meta;

	public AuthenticationPayload(Client client) {
		super(client);
	}

	@Override
	public Class<AuthenticationMeta> getMetaType() {
		return AuthenticationMeta.class;
	}

	@Override
	public void execute(Meta inputMeta) {
		if (inputMeta instanceof AuthenticationMeta) {
			this.meta = (AuthenticationMeta) meta;
		}
		System.out.println("AUTHENTICATION IS EXECUTED");
	}

}
