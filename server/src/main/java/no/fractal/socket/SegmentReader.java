package no.fractal.socket;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import no.fractal.socket.meta.Segment;

/**
 * Reads segments from FRACTAL requests.
 */
public class SegmentReader {

	/**
	 * The folder where we store files temporary when they arrive.
	 */
	private static final String TEMP_FOLDER = "./tmp/";

	/**
	 * Check the temp dir for existance, if it exist, do nothing. Else create
	 * directory tree.
	 */
	private void checkAndcreateTempDir() {
		File directory = new File(TEMP_FOLDER);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	/**
	 * Extract all segments from the request, and store them in a temp folder. An
	 * array of all the segments are returned, when all segments are red to
	 * destination.
	 * 
	 * @param segments the segment array from the request
	 * @return file for the segment uploaded
	 */
	public File writeSegmentToTemp(InputStream in, Segment segment) {
		checkAndcreateTempDir();
		String path = Path.of(TEMP_FOLDER, segment.getFilename()).toString();
		StreamUtil.writeStreamToFile(in, path, segment.getSize());
		return new File(path.toString());
	}

}
