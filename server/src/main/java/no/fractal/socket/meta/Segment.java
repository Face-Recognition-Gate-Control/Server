package no.fractal.socket.meta;

import java.io.File;
import java.time.LocalDateTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A segment is a description of a packet segemnt in the FRACTAL protocol. A
 * segment has a size (total size to read/write to the stream), and a mimetype
 * describing the type of data the segment is.
 */
public class Segment {

	private final String SEGMENT_SIZE = "size";
	private final String MIME_TYPE = "mime_type";
	private final String FILE_NAME = "file_name";

	private JsonObject segmentObject;
	transient private File file;

	public Segment(JsonObject segmentObject) {
		this.segmentObject = segmentObject;
	}

	public void setFile(File segmentFile) {
		this.file = segmentFile;
	}

	public File getFile() {
		return this.file;
	}

	public int getSize() {
		return this.segmentObject.get(SEGMENT_SIZE).getAsInt();
	}

	public String getMime() {
		return this.segmentObject.get(MIME_TYPE).getAsString();
	}

	public String getFilename() {
		JsonElement fileNameElement = this.segmentObject.get(FILE_NAME);
		if (fileNameElement == null) {
			String filename = "TMP_" + LocalDateTime.now().toString() + "_" + Math.random();
			this.segmentObject.addProperty(FILE_NAME, filename);
		}
		return this.segmentObject.get(FILE_NAME).getAsString();
	}

	public JsonElement get(String name) {
		return this.segmentObject.get(name);
	}

}
