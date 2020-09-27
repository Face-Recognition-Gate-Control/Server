package no.fractal.socket.send.messages;

import no.fractal.socket.send.AbstractMessage;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ValidationResponseMessage extends AbstractMessage {
    public static final String MESSAGE_TYPE = "val_resp";
    public ValidationResponseMessage(String uid, int magic
    ) {
        super(MESSAGE_TYPE);

        var a = new HashMap<String, String>();
        a.put("param_1", "val_1");
        a.put("param_2", uid);
        a.put("param_3", String.valueOf(magic));

        // -- example -- //
        this.addSegment(new JsonSegment(new JSONObject(a)));
        this.addSegment(new FileSegment(new File("")));
        this.addSegment(new ByteSegment("a;lkdfj".getBytes()));
    }
}
