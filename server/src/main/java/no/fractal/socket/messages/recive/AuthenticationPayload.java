package no.fractal.socket.messages.recive;

import com.google.gson.JsonElement;

import no.fractal.socket.meta.Segment;
import no.fractal.socket.messages.send.ValidationResponseMessage;

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

		var msg = new ValidationResponseMessage(this.getIdentificationId(), 28739);
		dispatcher.addMessage(msg);

	}

	public String getIdentificationId() {
		return identificationId;
	}

}
