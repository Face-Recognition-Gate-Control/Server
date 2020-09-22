package no.fractal.socket;

import java.io.File;
import java.io.InputStream;

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
	 * The data stream we read segments from
	 */
	private InputStream in;

	/**
	 * Helper class for writing data stream to file.
	 */
	private StreamUtil fileWriter = new StreamUtil();

	public SegmentReader(InputStream in) {
		this.in = in;
	}

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
	 * 
	 * @param segments the segment array from the request
	 * @return
	 */
	public File[] read(Segment[] segments) {
		checkAndcreateTempDir();
		File[] fileSegments = new File[segments.length];
		for (int i = 0; i < segments.length; i++) {
			Segment segment = segments[i];
			fileWriter.writeStreamToFile(in, segment.getFilename(), TEMP_FOLDER, segment.getSize());
			fileSegments[i] = new File(TEMP_FOLDER + segment.getFilename());
		}
		return fileSegments;
	}

}
