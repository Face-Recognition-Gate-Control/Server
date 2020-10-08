package no.fractal.socket.messages.recive;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.database.Datatypes.TensorData;
import no.fractal.database.Datatypes.User;
import no.fractal.database.DbTensorCache;
import no.fractal.server.ClientRequestDatabaseInterface;
import no.fractal.socket.messages.send.UserIdentefiedMessage;
import no.fractal.socket.messages.send.UserNotIdentifiedMessage;
import no.fractal.socket.payload.PayloadBase;
import no.fractal.socket.send.AbstractMessage;

import java.math.BigDecimal;
import java.util.UUID;

public class UserAuthorizationPayload extends PayloadBase {

    private String session_id;

    private BigDecimal[] tensor;

    public UserAuthorizationPayload() {
    }

    /**
     * Executes the payload instructions for this payload. The meta includes all
     * data for reading the payload.
     */
    @Override
    public void execute() {
        TensorData tensorData = new TensorData(tensor);

        try {
            ComparisonResult closest = DbTensorCache.getInstance().getClosestMatch(tensorData);
            AbstractMessage returnMessage = null;
            if (closest.diffValue < 0.6) {
                User user = ClientRequestDatabaseInterface.getInstance().getUser(closest.id);
                returnMessage = new UserIdentefiedMessage(user.getUserImage(), UUID.fromString(session_id),
                        "you can go throgh", true);
            } else {
                UUID uuid = UUID.randomUUID();
                String registrationUrl = ClientRequestDatabaseInterface.getInstance().getNewRegistrationURL(uuid);
                returnMessage = new UserNotIdentifiedMessage(uuid, registrationUrl);
            }

            dispatcher.addMessage(returnMessage);

        } catch (Exception e) {
            // TODO; Implement login failure handlin
        }

    }

    /**
     * @return the session_id
     */
    public String getSession_id() {
        return session_id;
    }

    /**
     * @return the tensor
     */
    public BigDecimal[] getTensor() {
        return tensor;
    }
}
