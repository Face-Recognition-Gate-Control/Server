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

    public User(UUID id, String firstName, String lastName) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
    }


    public File getUserImage() {
        return new File(ClientRequestDatabaseInterface.getInstance().imagePermSaveDir, id.toString());
    }
}
