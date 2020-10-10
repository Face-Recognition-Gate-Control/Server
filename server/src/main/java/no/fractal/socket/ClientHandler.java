package no.fractal.socket;

import no.fractal.socket.messages.recive.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
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

	private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

	/**
	 * Callback function for when a client has successfully authorized
	 */
	private Consumer<FractalClient> authorizedCallback;

	ClientHandler(Socket clientSocket, TcpServer server, Consumer<FractalClient> authorizedCallback) {
		this.server = server;
		this.clientSocket = clientSocket;
		this.authorizedCallback = authorizedCallback;
	}

	@Override
	public void run() {
		try {
			final var unauthorizedClient = new FractalClient(clientSocket, server);
			unauthorizedClient.setPayloadBuilder(ClientHandler::unauthorizedClientPayloadBuilder);
			LOGGER.log(Level.INFO, "Unautorized client connected...");

			Runnable task = () -> {
				unauthorizedClient.closeClient();
				LOGGER.log(Level.INFO, "Closed client for not authorizing...");
			};

			ScheduledFuture future = ClientHandler.scheduledExecutor.schedule(task, UNATHORIZED_CONNECTION_TIME, TimeUnit.MILLISECONDS);

			while (!unauthorizedClient.isAuthorized() && !unauthorizedClient.getClientSocket().isClosed()) {
				unauthorizedClient.read();
			}

			future.cancel(false);

			if (unauthorizedClient.isAuthorized()) {
				unauthorizedClient.setPayloadBuilder(ClientHandler::verifiedClientPayloadBuilder);
				this.authorizedCallback.accept(unauthorizedClient);
			}

		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Socket IO error", e);
		}
	}

	public static PayloadBase verifiedClientPayloadBuilder(FractalProtocol.PayloadData payloadData){
		return switch (payloadData.getId()) {
			case "gate_authorization" -> FractalProtocol.BuildPayloadObject(GateAuthorizationPayload.class, payloadData);
			case "user_authorization" -> FractalProtocol.BuildPayloadObject(UserAuthorizationPayload.class, payloadData);
			case "user_thumbnail" -> FractalProtocol.BuildPayloadObject(UserThumbnailPayload.class, payloadData);
			case "user_entered" -> FractalProtocol.BuildPayloadObject(UserEnteredPayload.class, payloadData);
			case "gate_ping" -> FractalProtocol.BuildPayloadObject(PingPayload.class, payloadData);
			default -> null;
		};
	}
	public static PayloadBase unauthorizedClientPayloadBuilder(FractalProtocol.PayloadData payloadData){
		return switch (payloadData.getId()) {
			case "gate_authorization" -> FractalProtocol.BuildPayloadObject(GateAuthorizationPayload.class, payloadData);
			default -> null;
		};
	}




}
