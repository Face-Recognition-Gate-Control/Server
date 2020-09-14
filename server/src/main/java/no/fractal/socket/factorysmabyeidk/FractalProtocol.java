package no.fractal.socket.factorysmabyeidk;


import no.fractal.socket.factorysmabyeidk.request.FractalRequestMeta;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 2 bytes id_size
 * -- id --
 * 3 bytes meta_size
 * -- meta json --
 *
 *
 *
 *
 */
public class FractalProtocol {

    private static final int ID_SIZE = 2;
    private static final int META_BYTE_SIZE = 3;


    public static FractalRequestMeta parseRequestMeta(InputStream inputStream){
        try {

            int mimeByteSize = ByteBuffer.wrap(inputStream.readNBytes(ID_SIZE)).getShort();
            String mimeType = new String(inputStream.readNBytes(mimeByteSize), StandardCharsets.UTF_8).trim();

            long metaByteSize = ByteBuffer.wrap(inputStream.readNBytes(META_BYTE_SIZE)).getLong();
            JSONArray segmentsMeta = new JSONArray(new JSONTokener(new String(inputStream.readNBytes(mimeByteSize), StandardCharsets.UTF_8).trim()));

            return new FractalRequestMeta(segmentsMeta,mimeType);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }




}
