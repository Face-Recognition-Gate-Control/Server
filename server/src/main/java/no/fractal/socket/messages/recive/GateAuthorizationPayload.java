package no.fractal.socket.messages.recive;

import no.fractal.server.ClientRequestDatabaseInterface;
import no.fractal.socket.messages.send.GateAuthorizedMessage;

import java.util.UUID;

public class GateAuthorizationPayload extends PayloadBase {

	private String login_key;
	private String station_uid;

	public GateAuthorizationPayload() {
	}

	@Override
	public void execute() {
		try {
			var uid = UUID.fromString(station_uid);
			boolean validationOk = ClientRequestDatabaseInterface.getInstance().stationManager.IsStationValid(uid,
					login_key);

			if (validationOk) {
				client.setAuthorized(validationOk);
				client.setClientID(uid.toString());
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
