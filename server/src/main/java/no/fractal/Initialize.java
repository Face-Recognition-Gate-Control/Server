package no.fractal;

import no.fractal.server.ClientRequestDatabaseInterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Setup neccessary application parts for it
 * to function properly
 */
public class Initialize {

    private static final Logger LOGGER = Logger.getLogger(Initialize.class.getName());

    public Initialize() throws InitException {
        setupThumbnailFolders();
        setupTestImage();
    }

    /**
     * Saves the test user image to perm storage
     */
    private void setupTestImage() throws InitException {
        var userImageName = "00000000-0000-0000-0000-000000000000.jpg";
        var resourcePath = "/images/" + userImageName;

        String tempDir = System.getProperty("java.io.tmpdir");
        try {
            File file = new File(ClientRequestDatabaseInterface.getInstance().imagePermSaveDir, userImageName);
            if (!file.exists()) {
                var is = (getClass().getResourceAsStream(resourcePath));
                Files.copy(is, file.getAbsoluteFile().toPath());
            }
        } catch (NullPointerException | IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new InitException("Could not setup admin image");
        }
    }

    /**
     * Make sure we can write and folders for thumbnails are setup
     */
    private void setupThumbnailFolders() throws InitException {
        var i = ClientRequestDatabaseInterface.getInstance();
        if (!i.imagePermSaveDir.exists()) {
            try {
                Files.createDirectories(i.imagePermSaveDir.toPath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new InitException("Thumbnail permanent storage could not be created");
            }
        }
        if (!i.imageTmpSaveDir.exists()) {
            try {
                Files.createDirectories(i.imageTmpSaveDir.toPath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new InitException("Thumbnail temporary storage could not be created");
            }
        }
    }

}
