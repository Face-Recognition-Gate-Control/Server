package no.fractal.database.Datatypes;

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
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public File getUserImage(){
        return null;
    }
}
