package no.fractal.socket.meta;

/**
 * A segment is a description of a packet segemnt in the FRACTAL protocol. A
 * segment has a size (total size to read/write to the stream), and a mimetype
 * describing the type of data the segment is.
 */
public class Segment {

	private int size;

	private String mime;

	public Segment() {
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}
}
