package no.fractal.socket.meta;

/**
 * A Meta header is included in every request/response from the FRACTAL
 * protocol. A meta header includes atlease a segment array, defining all
 * segemnts in the package (Json data, files, other types etc). All data that is
 * to be red/written has to be defined in a segment in order in which it is to
 * be red/written from the stream.
 */
public class Meta {

    /**
     * All segments in the meta payload
     */
    private Segment[] segments;

    /**
     * Required for JSON marshalling
     */
    public Meta() {
    }

    public Segment[] getSegments() {
        return segments;
    }

    public void setSegments(Segment[] segments) {
        this.segments = segments;
    }
}
