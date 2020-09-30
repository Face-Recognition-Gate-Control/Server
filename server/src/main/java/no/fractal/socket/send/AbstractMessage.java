package no.fractal.socket.send;

import no.fractal.socket.StreamUtil;
import no.fractal.util.StreamUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public abstract class AbstractMessage {

    private final String messageType;
    private final Map<String, Segment> segments;

    protected AbstractMessage(String messageType) {
        this.messageType = messageType;
        this.segments = new LinkedHashMap<String, Segment>();
    }

    protected void addSegment(String name, Segment segment) {
        this.segments.put(name, segment);
    }

    public void writeToStream(BufferedOutputStream outputStream) throws IOException {

        byte[] typeBytes = this.messageType.getBytes();
        byte[] segmentBytes = getMainSegmentMeta().toString().getBytes();

        // byte count
        int byteCont = segments.values().stream().map(Segment::getBodyByteSize).reduce(Integer::sum).get();
        byteCont += segmentBytes.length;
        byteCont += typeBytes.length;
        byteCont += 14; // hederstuff

        // - payload tot size 4B
        StreamUtils.writeInt(outputStream, byteCont);

        // - id Size 2b and id
        StreamUtils.writeInt(outputStream, typeBytes.length);
        outputStream.write(typeBytes);

        // - segmentArr size 2b and arr
        StreamUtils.writeInt(outputStream, segmentBytes.length);
        outputStream.write(segmentBytes);

        for (Segment segment : segments.values()) {
            segment.writeToStream(outputStream);
        }
    }

    private JSONArray getMainSegmentMeta() {
        JSONArray ret = new JSONArray();
        var ob = new JsonObject();
        var gson = new Gson();
        segments.forEach((key, val) -> ob.addProperty(key, gson.toJson(val)));
        return ret;
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
