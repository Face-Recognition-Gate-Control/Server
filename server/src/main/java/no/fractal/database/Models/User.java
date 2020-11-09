package no.fractal.database.Models;

import no.fractal.server.ClientService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Model class for a User.
 * A user has an ID, firstname, lastname, and a thumbnail image.
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
        var tmpdir = ClientService.getInstance().imageTmpSaveDir;
        var permDir = ClientService.getInstance().imagePermSaveDir;

        var thumbnail = new File(permDir, thumbnail_name);
        var tmpThumbnail = new File(tmpdir, thumbnail_name);
        if (tmpThumbnail.exists()) {
            try {
                Files.move(Path.of(tmpThumbnail.toURI()), Path.of(thumbnail.toURI()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (thumbnail.exists()) {
            return thumbnail;
        }

        return null;
    }
}
