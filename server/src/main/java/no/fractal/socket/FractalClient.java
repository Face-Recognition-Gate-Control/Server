package no.fractal.socket;

import com.google.gson.JsonSyntaxException;
import no.fractal.socket.messages.recive.NoSuchPayloadException;
import no.fractal.socket.messages.recive.PayloadBase;
import no.fractal.socket.payload.InvalidPayloadException;
import no.fractal.socket.send.MessageDispatcher;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FractalClient extends Client {

    private MessageDispatcher dispatcher;
    private UUID              GateId;

    private Function<FractalProtocol.PayloadData, PayloadBase> payloadBuilder = payloadData -> null;

    public UUID getGateId() {
        return GateId;
    }

    public void setGateId(UUID gateId) {
        this.GateId = gateId;
    }

    // Handles logging for the FractalClient
    private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

    private FractalProtocol protocol = new FractalProtocol();

    public FractalClient(Socket clientSocket, TcpServer server) throws IOException {
        super(clientSocket, server);
    }

    public void setPayloadBuilder(Function<FractalProtocol.PayloadData, PayloadBase> payloadBuilder) {
        this.payloadBuilder = payloadBuilder;
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
            this.dispatcher = new MessageDispatcher(this.getOutputStream());

            boolean reading = true;
            while (reading) {

                // Blocks here until all header fields are red.
                FractalProtocol.PayloadData payloadData = protocol.readPayloadData(in);


                try {
                    PayloadBase payload = payloadBuilder.apply(payloadData);

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


}
