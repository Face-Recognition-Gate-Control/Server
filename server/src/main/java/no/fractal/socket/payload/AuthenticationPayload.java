package no.fractal.socket.payload;

import com.google.gson.JsonElement;

import no.fractal.socket.meta.Segment;

public class AuthenticationPayload extends PayloadBase {

	private String identificationId;

	public AuthenticationPayload() {
	}

	@Override
	public void execute() {
		System.out.println("AUTHENTICATION IS EXECUTED");

		System.out.println("I have ID: " + this.getIdentificationId());

		Segment vectorFileSegment = this.segments.get("vectorfile");
		System.out.println(vectorFileSegment.getFile().getName());

		JsonElement randomKey = vectorFileSegment.get("random_key");
		if (randomKey != null) {
			System.out.println(randomKey.getAsString());
		}

	}

	public String getIdentificationId() {
		return identificationId;
	}

}
