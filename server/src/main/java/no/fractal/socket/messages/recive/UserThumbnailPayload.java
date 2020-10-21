package no.fractal.socket.messages.recive;

import no.fractal.server.ClientRequestDatabaseInterface;
import no.fractal.socket.meta.Segment;

import java.util.UUID;

public class UserThumbnailPayload extends PayloadBase {

    private UUID session_id;

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
        Segment segment = this.segments.get("thumbnail");
        try {
            ClientRequestDatabaseInterface.getInstance().registerImageToUser(session_id,
                    segment.getFile()
            );
        } catch (Exception e) {
            System.out.println(e);
            // TODO; Implement login failure handlin
        }
    }

}
