package no.fractal.socket.send;

import no.fractal.util.StreamUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class AbstractMessage {

    private final String messageType;
    private final Map<String, Segment> segments;
    private JsonObject body = new JsonObject();

    protected AbstractMessage(String messageType) {
        this.messageType = messageType;
        this.segments = new LinkedHashMap<String, Segment>();
    }

    protected void addSegment(String name, Segment segment) {
        this.segments.put(name, segment);
    }

    protected void addJsonBody(JsonObject body) {
        this.body = body;
    }

    public void writeToStream(BufferedOutputStream outputStream) throws IOException {

        byte[] typeBytes = this.messageType.getBytes();
        byte[] segmentBytes = getSegmentMetaAsJsonString().getBytes();
        byte[] bodyBytes = this.body.toString().getBytes();

        // byte count
        int payloadSize = 0;
        if (!segments.isEmpty())
            payloadSize += segments.values().stream().map(Segment::getBodyByteSize).reduce(Integer::sum).get();
        payloadSize += segmentBytes.length;
        payloadSize += typeBytes.length;
        payloadSize += 16; // 4B(Payload Size) + 4B(Payload ID Size) + 4B(Segment Size) + 4B (Body Size)

        // - payload tot size 4B
        StreamUtils.writeInt(outputStream, payloadSize);

        // - id Size 4B and id
        StreamUtils.writeInt(outputStream, typeBytes.length);
        outputStream.write(typeBytes);

        // - segmentArr size 4B and arr
        StreamUtils.writeInt(outputStream, segmentBytes.length);
        outputStream.write(segmentBytes);

        // JsonBody size 4B
        StreamUtils.writeInt(outputStream, bodyBytes.length);
        outputStream.write(bodyBytes);

        for (Segment segment : segments.values()) {
            segment.writeToStream(outputStream);
        }
    }

    private String getSegmentMetaAsJsonString() {
        JsonArray segmentArray = new JsonArray();
        var gson = new Gson();
        segments.forEach((key, val) -> {
            var jObject = new JsonObject();
            jObject.add(key, gson.toJsonTree(val.getSegmentMeta()));
            segmentArray.add(jObject);
        });
        return gson.toJson(segmentArray);
    }

    private abstract static class Segment {
        protected final Map<String, String> segmentMeta;

        protected Segment(Map<String, String> segmentMeta) {
            this.segmentMeta = segmentMeta;
        }

        Map<String, String> getSegmentMeta() {
            return segmentMeta;
        }

        public int getBodyByteSize() {
            return Integer.parseInt(segmentMeta.get(keys.size.name()));
        }

        public abstract void writeToStream(BufferedOutputStream outputStream) throws IOException;

        protected enum keys {
            size, mime_type, file_name
        }
    }

    public static class FileSegment extends Segment {
        public FileSegment(File file) {
            super(new HashMap<>());
            this.file = file;

            try {
                this.segmentMeta.put(keys.size.name(), String.valueOf(file.length()));
                this.segmentMeta.put(keys.mime_type.name(), String.valueOf(Files.probeContentType(file.toPath())));
                this.segmentMeta.put(keys.file_name.name(), file.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private final File file;

        @Override
        public void writeToStream(BufferedOutputStream outputStream) throws IOException {
            StreamUtils.writeFileToStream(outputStream, file);
        }
    }

    public static class JsonSegment extends Segment {
        private final byte[] bytes;

        public JsonSegment(JSONObject jsonObject) {
            super(new HashMap<>());
            this.bytes = jsonObject.toString().getBytes();

            this.segmentMeta.put(keys.size.name(), String.valueOf(bytes.length));
            this.segmentMeta.put(keys.mime_type.name(), "application/json");

        }

        @Override
        public void writeToStream(BufferedOutputStream outputStream) throws IOException {
            outputStream.write(bytes);
        }
    }

    public static class ByteSegment extends Segment {
        private final byte[] blob;

        public ByteSegment(byte[] blob) {
            super(new HashMap<>());
            this.blob = blob;

            this.segmentMeta.put(keys.size.name(), String.valueOf(blob.length));
            this.segmentMeta.put(keys.mime_type.name(), "application/octet-stream");
        }

        @Override
        public void writeToStream(BufferedOutputStream outputStream) throws IOException {
            outputStream.write(blob);
        }
    }

}
