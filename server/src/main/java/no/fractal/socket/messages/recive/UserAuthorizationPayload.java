package no.fractal.socket.messages.recive;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.database.Models.TensorData;
import no.fractal.database.Models.User;
import no.fractal.database.TensorSearcher;
import no.fractal.server.ClientService;
import no.fractal.socket.messages.send.UserIdentefiedMessage;
import no.fractal.socket.messages.send.UserNotIdentifiedMessage;
import no.fractal.socket.send.AbstractMessage;

import java.util.UUID;

/**
 * Payload received when a user tries to authorize and access a gate station
 * The payload includes the users face features, and an ID of the authorization session created by the gate station.
 * If a user is recognized and validated, it responds with an {@link UserIdentefiedMessage},
 * else a new user registration entry is added to the database and a {@link UserNotIdentifiedMessage} response is sent in return.
 */
public class UserAuthorizationPayload extends PayloadBase {

    private UUID session_id;

    private double[] face_features;

    /**
     * Empty constructor is required for JSON parser
     */
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

    @Override
    public void execute() {
        TensorData face_featuresData = new TensorData(face_features);

        try {

            ComparisonResult closest = TensorSearcher.getInstance().getClosestMatch(face_featuresData);
            AbstractMessage returnMessage = null;
            if (closest.diffValue < 0.6) {
                User user = ClientService.getInstance().getUserById(closest.id);
                var userThumbnail = user.getUserImage();
                if (userThumbnail != null) {
                    System.out.println(user.firstName);
                    returnMessage = new UserIdentefiedMessage(userThumbnail, user.id, "Access", true);

                }
            } else {
                ClientService.getInstance().addNewUserToRegistrationQueue(session_id, face_featuresData,
                        UUID.fromString(client.getClientID()));
                String registrationUrl = ClientService.getInstance().getNewRegistrationURL(session_id);
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
