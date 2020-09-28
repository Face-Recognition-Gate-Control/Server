package no.fractal.database.Datatypes;

import java.io.File;
import java.util.UUID;

/**
 * user, to bee expanded
 */
public class User {
    public final UUID id;
    public final String name;

    public User(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public File getUserImage(){
        return null;
    }
}
