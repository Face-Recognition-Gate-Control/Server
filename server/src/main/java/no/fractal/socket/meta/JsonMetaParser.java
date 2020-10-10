package no.fractal.socket.meta;

import com.google.gson.Gson;
import no.fractal.util.Parser;

public class JsonMetaParser implements Parser {

    Gson gson = new Gson();

    @Override
    public <T> T parse(Class<? extends T> type, String metaString) {
        return gson.fromJson(metaString, type);
    }
}