package no.fractal.socket.factorysmabyeidk.request;

import no.fractal.socket.factorysmabyeidk.Segment;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public abstract class AbstractRequest {

    private ArrayList<Segment> segments;
    private FractalRequestMeta requestMeta;

    public AbstractRequest(FractalRequestMeta requestMeta) {
        this.requestMeta = requestMeta;
        this.segments = this.getSegments();
    }

    // nams are like supershit

    public boolean finishIO(BufferedInputStream inputStream){
        boolean suc = true;
        for (int i = 0; i < segments.size(); i++) {
            JSONObject segmentMeta = requestMeta.getSegmentMetaData().getJSONObject(i);
            segments.get(i).parseMeta(segmentMeta);
            suc = suc && segments.get(i).readSegment(inputStream);
            if (!suc) break;
        }
        return suc;
    }

    public abstract void doAction();
    protected abstract ArrayList<Segment> getSegments();


    // -- utillity read funcs -- //







}
