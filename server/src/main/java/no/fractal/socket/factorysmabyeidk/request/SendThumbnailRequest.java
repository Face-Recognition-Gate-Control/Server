package no.fractal.socket.factorysmabyeidk.request;

import no.fractal.socket.factorysmabyeidk.Segment;
import no.fractal.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SendThumbnailRequest extends AbstractRequest {

    private String fileName = "";

    public SendThumbnailRequest(FractalRequestMeta requestMeta) {
        super(requestMeta);
    }

    @Override
    public void doAction() {
        System.out.println(fileName); // vil da ver verdien fra som trilla inn fra get segments

    }

    @Override
    protected ArrayList<Segment> getSegments() {
        ArrayList<Segment> segments = new ArrayList<>();

        segments.add(new Segment((inputStream, segment_meta) -> {
            String fileName = segment_meta.get("IMNAME");
            int fileSize = Integer.parseInt(segment_meta.get("File_size"));

            this.fileName = fileName;
            File writefile = new File(fileName);
            try {
                StreamUtils.readImageToFile(inputStream, fileSize, writefile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addMetaValue("IMNAME"));

        segments.add(new Segment((inputStream, stringStringHashMap) -> {
            // json parsing elns //
        }).addMetaValue("meta param 1")
        .addMetaValue("metaparam 2 osv.."));

        return segments;
    }
}
