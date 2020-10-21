package no.fractal.database.Datatypes;

import no.fractal.server.ClientRequestDatabaseInterface;

import java.io.File;
import java.util.UUID;

/**
 * user, to bee expanded
 */
public class User {

    public final UUID id;

    public final String firstName;

    public final String lastName;

    public transient final String thumbnail_name;

    public User(UUID id, String firstName, String lastName, String thumbnail_name) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.thumbnail_name = thumbnail_name;
    }

    /**
     * Returns the file for the user thumbnail or null if the thumbnail does not exist.
     *
     * @return file for thumbnail or null if not exists
     */
    public File getUserImage() {

        var thumbnail = new File(ClientRequestDatabaseInterface.getInstance().imagePermSaveDir, thumbnail_name);
        if (thumbnail.exists()) {
            return thumbnail;
        }
        return null;
    }
}
