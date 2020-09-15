package no.fractal.socket.meta;

import com.google.gson.Gson;

import no.fractal.util.Parser;

public class JsonMetaParser<T> implements Parser<T> {

	Gson gson = new Gson();

	private String metaString;

	public JsonMetaParser(String meta) {
		this.metaString = meta;
	}

	@Override
	public T parse(Class<T> type) {
		return gson.fromJson(metaString, type);
	}
}