package no.fractal.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

    /**
     * How many milliseconds a client can stay unathorized before the socket is
     * closed
     */
    private static final long UNATHORIZED_CONNECTION_TIME = 3000L;
    private static final Logger LOGGER = Logger.getLogger(TcpServer.class.getName());
    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private final TcpServer server;
    private final Socket clientSocket;
    private ScheduledFuture<?> notAuthorizedTask;
    /**
     * Callback function for when a client has successfully authorized
     */
    private final Consumer<Client> authorizedCallback;

    ClientHandler(Socket clientSocket, TcpServer server, Consumer<Client> authorizedCallback) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.authorizedCallback = authorizedCallback;
    }

    @Override
    public void run() {
        try {
            final var unauthorizedClient = new FractalClient(clientSocket, server, (Client client) -> {
                if (client.isAuthorized()) {
                    notAuthorizedTask.cancel(true);
                    authorizedCallback.accept(client);
                }
            });
            LOGGER.log(Level.INFO, "Unauthorized client connected...");
            Runnable task = () -> {
                if (!unauthorizedClient.isAuthorized()) {
                    unauthorizedClient.closeClient();
                    LOGGER.log(Level.INFO, "Closed client for not authorizing...");
                }
            };
            notAuthorizedTask = ClientHandler.scheduledExecutor.schedule(task,
                    UNATHORIZED_CONNECTION_TIME,
                    TimeUnit.MILLISECONDS
            );
            unauthorizedClient.run();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Socket IO error", e);
        }
    }


}
