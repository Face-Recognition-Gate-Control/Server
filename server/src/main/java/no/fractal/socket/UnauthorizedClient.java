package no.fractal.socket;

import no.fractal.socket.meta.*;

import no.fractal.socket.messages.recive.GateAuthorizationPayload;
import no.fractal.socket.payload.InvalidPayloadException;
import no.fractal.socket.messages.recive.NoSuchPayloadException;
import no.fractal.socket.messages.recive.PayloadBase;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.JsonSyntaxException;
import no.fractal.socket.send.MessageDispatcher;

public class UnauthorizedClient extends Client {

	private MessageDispatcher dispatcher;

	private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

	private FractalProtocol protocol = new FractalProtocol(new JsonMetaParser());

	public UnauthorizedClient(Socket clientSocket, TcpServer server) throws IOException {
		super(clientSocket, server);
		this.dispatcher = new MessageDispatcher(this.getOutputStream());

	}

	@Override
	public void run() {
		this.read();
	}

	/**
	 * Reads all incoming headers and routes the payloads to the approporiate
	 * payload handlers.
	 */
	@Override
	public void read() {
		try {
			BufferedInputStream in = this.getInputReader();
			// Blocks here until all header fields are red.
			FractalProtocol.PayloadBuilder payloadBuilder = protocol.readPayload(in);
			// Headers extract
			String payloadName = protocol.getId();
			try {
				PayloadBase payload = switch (payloadName) {
					case "gate_authorization" -> payloadBuilder.createPayloadObject(GateAuthorizationPayload.class);
					default -> null;
				};

				if (payload == null) {
					throw new NoSuchPayloadException("Can not find the payload with name: " + payloadName);
				}
				// Execute the payload
				payload.setDispatcher(dispatcher);
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
