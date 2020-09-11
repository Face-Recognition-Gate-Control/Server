package no.fractal.socket;

import java.io.BufferedInputStream;

import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FractalClient extends Client {

	// Handles logging for the FractalClient
	private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

	private FractalProtocol protocol = new FractalProtocol();

	public FractalClient(Socket clientSocket, TcpServer server) throws IOException {
		super(clientSocket, server);
	}

	@Override
	public void run() {
		// initial setup
		this.read();
	}

	@Override
	protected void read() {
		try {
			try {

				BufferedInputStream in = this.getInputReader();
				boolean reading = true;
				while (reading) {
					// Blocks here until all header fields are red.
					protocol.readHeader(in);
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
