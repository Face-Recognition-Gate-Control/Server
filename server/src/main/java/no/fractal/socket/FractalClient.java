package no.fractal.socket;

import no.fractal.socket.meta.*;
import no.fractal.socket.payload.AuthenticationPayload;
import no.fractal.socket.payload.NoSuchPayloadException;
import no.fractal.socket.payload.PayloadLoader;
import no.fractal.socket.payload.PayloadBase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class FractalClient extends Client {

	// Handles logging for the FractalClient
	private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

	private FractalProtocol protocol = new FractalProtocol();

	PayloadLoader<PayloadBase<? extends Meta>> payloads;

	public FractalClient(Socket clientSocket, TcpServer server) throws IOException {
		super(clientSocket, server);

		Class<?> constructorTypes[] = { Client.class };
		payloads = new PayloadLoader<PayloadBase<? extends Meta>>(constructorTypes);
	}

	@Override
	public void run() {
		// initial setup
		this.registerPayloads();
		this.read();
	}

	/**
	 * Register all payloads with name and class type to the loader
	 */
	private void registerPayloads() {
		payloads.addSubClass("authentication", AuthenticationPayload.class);
	}

	/**
	 * Reads all incoming headers and routes the payloads to the approporiate
	 * payload handlers.
	 */
	@Override
	protected void read() {

		try {
			try {


				Gson gson = new Gson();
				BufferedInputStream in = this.getInputReader();
				BufferedOutputStream out = this.getOutputStream();
				boolean reading = true;
				while (reading) {
					// Blocks here until all header fields are red.
					protocol.readHeader(in);
					// Headers extract
					String payloadName = protocol.getId();
					String metaString = protocol.getMeta();

					try {

						Object constructorArgumets[] = { this };
						PayloadBase<? extends Meta> payload = payloads.getnewInstance(payloadName, constructorArgumets);
						if (payload == null) {
							throw new NoSuchPayloadException("Can not find the payload with name: " + payloadName);
						}
						// Parse meta string to meta object type
						Meta meta = gson.fromJson(metaString, payload.getMetaType());

						// Execute the payload
						payload.execute(meta);
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
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

	}

}
