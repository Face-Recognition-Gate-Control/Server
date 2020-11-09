package no.fractal.socket;

import no.fractal.server.ClientService;
import no.fractal.socket.meta.Segment;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Reads segments from FRACTAL requests and writes them to file.
 */
public class SegmentReader {

    /**
     * The folder where we store files temporary when they arrive.
     */
    private static final String TEMP_FOLDER = ClientService.getInstance().imageTmpSaveDir.getAbsolutePath();

    /**
     * Writes a segment to temp a temp folder and return the file reference when complete.
     *
     * @param in steram to read the segment from.
     * @param segment the segment description.
     * @return return the written file.
     */
    public File writeSegmentToTemp(InputStream in, Segment segment) {
        checkAndcreateTempDir();
        String path = Path.of(TEMP_FOLDER, segment.getFilename()).toString();
        StreamUtil.writeStreamToFile(in, path, segment.getSize());
        return new File(path);
    }

    /**
     * Check the temp dir for existence, if it exist, do nothing. Else create
     * directory tree.
     */
    private void checkAndcreateTempDir() {
        File directory = new File(TEMP_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}
