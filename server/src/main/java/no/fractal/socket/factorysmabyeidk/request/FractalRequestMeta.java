package no.fractal.socket.factorysmabyeidk.request;

import org.json.JSONArray;

public class FractalRequestMeta {

    private final JSONArray segmentMetaData;
    private final String requestId;

    public FractalRequestMeta(JSONArray segmentMetaData, String requestId) {
        this.segmentMetaData = segmentMetaData;
        this.requestId = requestId;
    }

    public JSONArray getSegmentMetaData() {
        return segmentMetaData;
    }

    public String getRequestId() {
        return requestId;
    }
}
