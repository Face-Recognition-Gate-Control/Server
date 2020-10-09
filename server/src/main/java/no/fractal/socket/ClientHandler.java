package no.fractal.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client handler is responsible for creating an incomming client, and handle
 * the authorization of an authorized client. It runs in its own thread, so it
 * can handle reading of clients. An un-authorized client is diconnected after a
 * set timeout,to drop clients which fails to authorize. When an un-authorized
 * client has authorized itself, it will be upgraded to a FractalClient and
 * passed to a authorized callaback function. Clients started from this handler
 * will stay on this thread, when given back through the callback.
 */
public class ClientHandler implements Runnable {

	private static Logger LOGGER = Logger.getLogger(TcpServer.class.getName());

	/**
	 * How many milliseconds a client can stay unathorized before the socket is
	 * closed
	 */
	private static final long UNATHORIZED_CONNECTION_TIME = 3000L;

	private TcpServer server;

	private Socket clientSocket;

	ExecutorService pool;

	/**
	 * Callback function for when a client has successfully authorized
	 */
	Consumer<FractalClient> authorizedCallback;

	ClientHandler(Socket clientSocket, TcpServer server, Consumer<FractalClient> authorizedCallback) {
		this.server = server;
		this.clientSocket = clientSocket;
		this.authorizedCallback = authorizedCallback;
	}

	@Override
	public void run() {
		try {
			final var unauthorizedClient = new UnauthorizedClient(clientSocket, server);
			LOGGER.log(Level.INFO, "Unautorized client connected...");

			TimerTask task = new TimerTask() {
				public void run() {
					unauthorizedClient.closeClient();
					LOGGER.log(Level.INFO, "Closed client for not authorizing...");
				}
			};
			Timer timer = new Timer("UnauthorizedTimer");
			timer.schedule(task, UNATHORIZED_CONNECTION_TIME);

			while (!unauthorizedClient.isAuthorized() && !unauthorizedClient.getClientSocket().isClosed()) {
				unauthorizedClient.read();
			}
			timer.cancel();
			if (unauthorizedClient.isAuthorized()) {
				var authorizedClient = new FractalClient(clientSocket, server);
				authorizedClient.setClientID(unauthorizedClient.getClientID());
				authorizedClient.setAuthorized(unauthorizedClient.isAuthorized());
				this.authorizedCallback.accept(authorizedClient);
			}

		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Socket IO error", e);
		}
	}

}
