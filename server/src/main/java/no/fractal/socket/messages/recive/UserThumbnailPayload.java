package no.fractal.socket.messages.recive;

import no.fractal.server.ClientService;
import no.fractal.socket.meta.Segment;
import no.fractal.socket.payload.InvalidSegmentNameException;

import java.util.UUID;

/**
 * Payload is received after a gate station has received a {@link no.fractal.socket.messages.send.UserNotIdentifiedMessage}.
 * The payload contains the session id used for authorization and the thumbnail image gathered when extracting face features
 * of the user. The session id used to link the thumbnail to the unidentified user.
 */
public class UserThumbnailPayload extends PayloadBase {

    private UUID session_id;

    private static final String THUMBNAIL_SEGMENT_NAME = "thumbnail";

    /**
     * Empty constructor is required for JSON parser
     */
    public UserThumbnailPayload() {
    }

    /**
     * @return the session_id
     */
    public UUID getSession_id() {
        return session_id;
    }


    @Override
    public void execute() {
        try {
            Segment segment = this.segments.get(THUMBNAIL_SEGMENT_NAME);
            if (segment == null) {
                throw new InvalidSegmentNameException("Segment: " + THUMBNAIL_SEGMENT_NAME + " is NULL");
            }
            System.out.println(segment.getFile().getAbsoluteFile());
            ClientService.getInstance().addThumbnailToNewUserInRegistrationQueue(session_id,
                    segment.getFile()
            );
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
