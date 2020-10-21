package no.fractal.socket.messages.recive;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.database.Datatypes.TensorData;
import no.fractal.database.Datatypes.User;
import no.fractal.database.TensorSearcher;
import no.fractal.server.ClientRequestDatabaseInterface;
import no.fractal.socket.messages.send.UserIdentefiedMessage;
import no.fractal.socket.messages.send.UserNotIdentifiedMessage;
import no.fractal.socket.send.AbstractMessage;

import java.util.UUID;

public class UserAuthorizationPayload extends PayloadBase {

    private UUID session_id;

    private double[] face_features;

    public UserAuthorizationPayload() {
    }

    /**
     * @return the session_id
     */
    public UUID getSession_id() {
        return session_id;
    }

    /**
     * @return the face_features
     */
    public double[] getFace_features() {
        return face_features;
    }

    /**
     * Executes the payload instructions for this payload. The meta includes all
     * data for reading the payload.
     */
    @Override
    public void execute() {
        TensorData face_featuresData = new TensorData(face_features);

        try {

            ComparisonResult closest = TensorSearcher.getInstance().getClosestMatch(face_featuresData);
            AbstractMessage returnMessage = null;
            if (closest.diffValue < 0.6) {
                User user = ClientRequestDatabaseInterface.getInstance().getUser(closest.id);
                var userThumbnail = user.getUserImage();
                if (userThumbnail != null) {
                    returnMessage = new UserIdentefiedMessage(userThumbnail, session_id, "you can go throgh", true);
                }
            } else {
                ClientRequestDatabaseInterface.getInstance().registerUserInQue(session_id, face_featuresData,
                        UUID.fromString(client.getClientID()));
                String registrationUrl = ClientRequestDatabaseInterface.getInstance().getNewRegistrationURL(session_id);
                returnMessage = new UserNotIdentifiedMessage(session_id, registrationUrl);
            }

            if (returnMessage != null) {
                dispatcher.addMessage(returnMessage);
            }


        } catch (Exception e) {
            System.out.println(e);
            // TODO; Implement login failure handlin
        }

    }
}
