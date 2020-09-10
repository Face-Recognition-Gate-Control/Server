package no.fractal.socket;

import java.io.IOException;

import java.net.Socket;
import java.util.logging.Logger;

public class FractalClient extends Client {

	// Handles logging for the FractalClient
	private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

	public FractalClient(Socket clientSocket, TcpServer server) throws IOException {
		super(clientSocket, server);

	}

	@Override
	public void run() {

	}

	@Override
	protected void read() {

	}

}
