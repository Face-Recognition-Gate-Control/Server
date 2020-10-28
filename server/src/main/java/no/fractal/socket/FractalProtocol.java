package no.fractal.socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import no.fractal.socket.messages.recive.PayloadBase;
import no.fractal.socket.meta.JsonMetaParser;
import no.fractal.socket.meta.Segment;
import no.fractal.socket.payload.InvalidPayloadException;
import no.fractal.util.Parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class FractalProtocol {

    private static final Parser parser = new JsonMetaParser();
    /**
     * Total size of the payload
     */
    private final int PAYLOAD_LENGTH = 4;
    /**
     * Byte size of the ID header
     */
    private final int ID_LENGTH = 4;
    /**
     * Byte size of the segment header
     */
    private final int SEGMENT_LENGTH = 4;
    /**
     * Byte size of the json payload
     */
    private final int JSON_LENGTH = 4;
    /**
     * Bytes remaing for this payload to read
     */
    private int remainingPayloadBytes = 0;

    public FractalProtocol() {
    }

    public int getIdHeaderBytesLength() {
        return ID_LENGTH;
    }

    public int getMetaHeaderByteLength() {
        return SEGMENT_LENGTH;
    }

    /**
     * Reads the full payload from the input stream. This operation is blocking.
     *
     * @param inputStream stream reader
     *
     * @throws IOException thrown if stream is closed
     */
    public PayloadData readPayloadData(BufferedInputStream inputStream) throws IOException {
        this.remainingPayloadBytes = this.readByteSize(inputStream, PAYLOAD_LENGTH);

        String id             = this.readHeader(inputStream, ID_LENGTH);
        String segmentsString = this.readHeader(inputStream, SEGMENT_LENGTH);
        String jsonPayload    = this.readHeader(inputStream, JSON_LENGTH);

        return new PayloadData(this.readSegmentFiles(inputStream, segmentsString), id, segmentsString, jsonPayload);
    }

    /**
     * Reads the rest of the stream, if there is more data left for this payload.
     * This makes sure we dont start the next payload read at a position where we
     * read left over data from last payload, where an error etc has occured.
     * <p>
     * Blocking operation
     *
     * @param in input stream to read from
     *
     * @throws IOException when IO goes wrong
     */
    public void clearStream(InputStream in) throws IOException {
        if (this.remainingPayloadBytes > 0) {
            in.readNBytes(this.remainingPayloadBytes);
        }
    }

    private void reduceRemaingPayloadBytes(int bytesRed) {
        this.remainingPayloadBytes -= bytesRed;
    }

    /**
     * Reads N bytes from the stream and returns the size of the bytes.
     *
     * @param in     input stream to read from
     * @param length how many bytes to read
     *
     * @return bytes as int
     * @throws IOException if stream closes or reading fails.
     */
    private int readByteSize(BufferedInputStream in, int length) throws IOException {
        byte[] input     = new byte[length];
        int    bytesRead = in.read(input, 0, input.length);
        if (bytesRead <= 0) {
            throw new IOException("Bytestream closed");
        }
        reduceRemaingPayloadBytes(bytesRead);
        int size = 1;
        switch (length) {
            case 1:
                size = input[0];
                break;
            case 2:
                size = ByteBuffer.wrap(input).getShort();
                break;
            case 4:
                size = ByteBuffer.wrap(input).getInt();
                break;

        }
        return size;
    }

    private String readHeader(BufferedInputStream in, int headerSize) throws IOException {
        int    size  = readByteSize(in, headerSize);
        byte[] input = new byte[size];
        input = new byte[size];
        int bytesRead = in.read(input, 0, input.length);
        if (bytesRead <= 0) {
            throw new IOException("Bytestream closed");
        }
        reduceRemaingPayloadBytes(bytesRead);
        return new String(input, StandardCharsets.UTF_8).trim();
    }

    /**
     * Returns a list of all file segments from a request.
     *
     * @param inputStream input stream for reading files
     *
     * @return map of all segments
     */
    private Map<String, Segment> readSegmentFiles(BufferedInputStream inputStream, String segmentStr) {
        Map<String, Segment> segments = getParsedSegments(segmentStr);

        var sr = new SegmentReader();
        segments.forEach((key, segment) -> {
            segment.setFile(sr.writeSegmentToTemp(inputStream, segment));
            reduceRemaingPayloadBytes(segment.getSize());
        });

        return segments;
    }

    /**
     * Returns a map of all the segments in the payload header. The key of the map
     * is the field name of the segment, for identification.
     *
     * @return map of segment header
     */
    private Map<String, Segment> getParsedSegments(String segmentStr) {
        var keyedSegments = new LinkedHashMap<String, Segment>();
        try {
            JsonArray segmentsArray = JsonParser.parseString(segmentStr).getAsJsonArray();
            segmentsArray.forEach((segment) -> {
                var segmentObject = segment.getAsJsonObject();
                var key           = (String) segmentObject.keySet().toArray()[0];
                keyedSegments.put(key, new Segment(segmentObject.get(key).getAsJsonObject()));

            });

        } catch (IndexOutOfBoundsException e) {
            throw new InvalidPayloadException("The payload is missing segment identifier: key for the segment");
        } catch (IllegalStateException e) {
            throw new InvalidPayloadException("The payload is not a valid \n " + e.getMessage());
        }
        return keyedSegments;
    }

    /**
     * Creates a payload object from the defined type, and inject data from segment,
     * and all file segments.
     *
     * @param <E> type of the payload
     * @param t   type of the payload
     *
     * @return payload object
     */
    public static <E extends PayloadBase> E BuildPayloadObject(Class<E> t, PayloadData payloadData) {
        E payload = parser.parse(t, payloadData.jsonPayloadString);
        payload.setSegments(payloadData.segments);
        return payload;
    }


    public class PayloadData {

        private final Map<String, Segment> segments;

        private final String id;

        private final String segmentString;

        private final String jsonPayloadString;

        public PayloadData(Map<String, Segment> segments, String id, String segmentString, String jsonPayloadString) {
            this.segments          = segments;
            this.id                = id;
            this.segmentString     = segmentString;
            this.jsonPayloadString = jsonPayloadString;
        }

        /**
         * Returns the packet id from the data header
         *
         * @return packet id from the data header
         */
        public String getId() {
            return this.id;
        }


    }

}
