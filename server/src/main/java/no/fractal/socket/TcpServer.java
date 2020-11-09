package no.fractal.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for a TCP server.
 * It creates a welcome socket on a given port, and accept incomming connections.
 * A connected and handshake client is moved to a new thread, so the server can continue accepting new connections.
 */
public class TcpServer {

    private static final Logger LOGGER = Logger.getLogger(TcpServer.class.getName());

    private static final int MAX_THREADS = 10;

    private final Map<String, Client> authorizedClients = new HashMap<>();
    private int port = 9876;
    private String host = "localhost";
    private ServerSocket welcomeSocket;

    public TcpServer() {
    }

    public TcpServer(int port) {
        this.port = port;
    }

    public TcpServer(int port, String host) {
        this(port);
        this.host = host;

    }

    /**
     * Initializes the server socket, and starts accepting clients.
     */
    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
        try {
            this.welcomeSocket = new ServerSocket(port);
            LOGGER.log(Level.INFO, String.format("Server started on port: %s", port));

            while (true) {
                try {
                    Socket clientSocket = this.welcomeSocket.accept();

                    pool.execute(new ClientHandler(clientSocket, this, this::addFractalClient));
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void addFractalClient(Client fractalClient) {
        this.authorizedClients.put(fractalClient.getClientID(), fractalClient);
        LOGGER.log(Level.INFO, String.format("Authorized client with id: %s", fractalClient.getClientID()));
    }

}
