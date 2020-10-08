package no.fractal.socket;

import no.fractal.socket.meta.*;
import no.fractal.socket.messages.recive.GateAuthorizationPayload;
import no.fractal.socket.messages.recive.PingPayload;
import no.fractal.socket.messages.recive.UserAuthorizationPayload;
import no.fractal.socket.messages.recive.UserEnteredPayload;
import no.fractal.socket.payload.InvalidPayloadException;
import no.fractal.socket.payload.NoSuchPayloadException;
import no.fractal.socket.payload.PayloadBase;
import no.fractal.socket.messages.recive.UserThumbnailPayload;

import java.io.BufferedInputStream;

import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonSyntaxException;
import no.fractal.socket.send.MessageDispatcher;

public class FractalClient extends Client {

	private MessageDispatcher dispatcher;
	private UUID GateId;

	public UUID getGateId() {
		return GateId;
	}

	public void setGateId(UUID gateId) {
		this.GateId = gateId;
	}

	// Handles logging for the FractalClient
	private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

	private FractalProtocol protocol = new FractalProtocol(new JsonMetaParser());

	public FractalClient(Socket clientSocket, TcpServer server) throws IOException {
		super(clientSocket, server);
	}

	@Override
	public void run() {
		this.dispatcher = new MessageDispatcher(this.getOutputStream());
		this.read();
	}

	/**
	 * Reads all incoming headers and routes the payloads to the approporiate
	 * payload handlers.
	 */
	@Override
	protected void read() {

		try {

			BufferedInputStream in = this.getInputReader();

			boolean reading = true;
			while (reading) {

				// Blocks here until all header fields are red.
				FractalProtocol.PayloadBuilder payloadBuilder = protocol.readPayload(in);

				// Headers extract
				String payloadName = protocol.getId();

				try {
					PayloadBase payload = switch (payloadName) {
						case "gate_authorization" -> payloadBuilder.createPayloadObject(GateAuthorizationPayload.class);
						case "user_authorization" -> payloadBuilder.createPayloadObject(UserAuthorizationPayload.class);
						case "user_thumbnail" -> payloadBuilder.createPayloadObject(UserThumbnailPayload.class);
						case "user_entered" -> payloadBuilder.createPayloadObject(UserEnteredPayload.class);
						case "gate_ping" -> payloadBuilder.createPayloadObject(PingPayload.class);
						default -> null;
					};

					if (payload == null) {
						throw new NoSuchPayloadException("Can not find the payload with name: " + payloadName);
					}
					// Execute the payload
					payload.setDispatcher(dispatcher);
					payload.setClient(this);
					payload.execute();
				} catch (JsonSyntaxException e) {
					// SEND INVALID META FOR PAYLOAD
					LOGGER.log(Level.INFO, String.format("Invalid meta for: %s", payloadName));
				} catch (NoSuchPayloadException | InvalidPayloadException e) {
					// SEND INVALID PAYLOAD NAME
					LOGGER.log(Level.INFO, String.format("%s", e.getMessage()));
				}

				/**
				 * Make sure all data for payload is cleared.
				 */
				protocol.clearStream(in);
			}

			this.getClientSocket().close();
			LOGGER.log(Level.INFO, String.format("Client disconnected: %s", this.getClientID()));
		} catch (SocketException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

	}

}
