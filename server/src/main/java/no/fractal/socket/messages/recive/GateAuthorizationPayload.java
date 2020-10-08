package no.fractal.socket.messages.recive;

import no.fractal.server.ClientRequestDatabaseInterface;
import no.fractal.socket.messages.send.GateAuthorizedMessage;
import no.fractal.socket.payload.PayloadBase;

import java.util.UUID;

public class GateAuthorizationPayload extends PayloadBase {

	private String login_key;
	private String station_uid;

	public GateAuthorizationPayload() {
	}

	@Override
	public void execute() {
		try {
			boolean validationOk = ClientRequestDatabaseInterface.getInstance().stationManager
					.IsStationValid(UUID.fromString(station_uid), login_key);

			if (validationOk) {
				GateAuthorizedMessage gateAuthorizedMessage = new GateAuthorizedMessage("mabye remove bro");
				this.dispatcher.addMessage(gateAuthorizedMessage);
			}

		} catch (Exception e) {
			// TODO; Implement login failure handlin
		}
	}

	/**
	 * @return the login_key
	 */
	public String getLogin_key() {
		return login_key;
	}

	/**
	 * @return the station_uid
	 */
	public String getStation_uid() {
		return station_uid;
	}
}
