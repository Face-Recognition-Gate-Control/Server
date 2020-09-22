package no.fractal.socket;

import no.fractal.socket.meta.*;
import no.fractal.socket.payload.AuthenticationPayload;
import no.fractal.socket.payload.NoSuchPayloadException;
import no.fractal.socket.payload.PayloadBase;

import java.io.BufferedInputStream;

import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonSyntaxException;

public class FractalClient extends Client {

	// Handles logging for the FractalClient
	private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

	private FractalProtocol<Meta> protocol = new FractalProtocol<Meta>(new JsonMetaParser());

	public FractalClient(Socket clientSocket, TcpServer server) throws IOException {
		super(clientSocket, server);
	}

	@Override
	public void run() {
		// initial setup
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
				FractalProtocol<Meta>.PayloadBuilder payloadBuilder = protocol.readPayload(in);

				// Headers extract
				String payloadName = protocol.getId();

				try {
					PayloadBase payload = switch (payloadName) {
						case "authentication" -> payloadBuilder.createPayloadObject(AuthenticationPayload.class);
						default -> null;
					};

					if (payload == null) {
						throw new NoSuchPayloadException("Can not find the payload with name: " + payloadName);
					}
					// Execute the payload

					payload.execute();
				} catch (JsonSyntaxException e) {
					// SEND INVALID META FOR PAYLOAD
					LOGGER.log(Level.INFO, String.format("Invalid meta for: %s", payloadName));
				} catch (NoSuchPayloadException e) {
					// SEND INVALID PAYLOAD NAME
					LOGGER.log(Level.INFO, String.format("%s", e.getMessage()));
				}
			}

			this.getClientSocket().close();
			LOGGER.log(Level.INFO, String.format("Client disconnected: %s", this.getClientID()));
		} catch (SocketException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

	}

}
