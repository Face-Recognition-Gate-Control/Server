package no.fractal.socket;

import com.google.gson.JsonSyntaxException;
import no.fractal.socket.messages.recive.*;
import no.fractal.socket.payload.InvalidPayloadException;
import no.fractal.socket.send.MessageDispatcher;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FractalClient extends Client {

    // Handles logging for the FractalClient
    private static final Logger LOGGER = Logger.getLogger(FractalClient.class.getName());
    private final FractalProtocol protocol = new FractalProtocol();
    private MessageDispatcher dispatcher;
    private UUID GateId;

    public FractalClient(Socket clientSocket, TcpServer server, Consumer<Client> authCallback) throws IOException {
        super(clientSocket, server, authCallback);
    }

    public UUID getGateId() {
        return GateId;
    }

    public void setGateId(UUID gateId) {
        this.GateId = gateId;
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
    protected void read() {
        try {

            BufferedInputStream in = this.getInputReader();
            this.dispatcher = new MessageDispatcher(this.getOutputStream());

            boolean reading = true;
            while (reading) {

                // Blocks here until all header fields are red.
                FractalProtocol.PayloadData payloadData = protocol.readPayloadData(in);

                try {
                    PayloadBase payload = this.isAuthorized() ? authorizedPayloads(payloadData)
                            : unauthorizedPayloads(payloadData);

                    if (payload == null) {
                        throw new NoSuchPayloadException("Can not find the payload with name: " + payloadData.getId());
                    }
                    // Execute the payload
                    payload.setDispatcher(dispatcher);
                    payload.setClient(this);
                    payload.execute();
                } catch (JsonSyntaxException e) {
                    // SEND INVALID META FOR PAYLOAD
                    LOGGER.log(Level.INFO, String.format("Invalid meta for: %s", payloadData.getId()));
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

    private PayloadBase authorizedPayloads(FractalProtocol.PayloadData payloadData) {
        return switch (payloadData.getId()) {
            case "gate_authorization" -> FractalProtocol.BuildPayloadObject(GateAuthorizationPayload.class,
                    payloadData);
            case "user_authorization" -> FractalProtocol.BuildPayloadObject(UserAuthorizationPayload.class,
                    payloadData);
            case "user_thumbnail" -> FractalProtocol.BuildPayloadObject(UserThumbnailPayload.class, payloadData);
            case "user_entered" -> FractalProtocol.BuildPayloadObject(UserEnteredPayload.class, payloadData);
            case "gate_ping" -> FractalProtocol.BuildPayloadObject(PingPayload.class, payloadData);
            default -> null;
        };
    }

    private PayloadBase unauthorizedPayloads(FractalProtocol.PayloadData payloadData) {
        return switch (payloadData.getId()) {
            case "gate_authorization" -> FractalProtocol.BuildPayloadObject(GateAuthorizationPayload.class,
                    payloadData);
            default -> null;
        };
    }

}
