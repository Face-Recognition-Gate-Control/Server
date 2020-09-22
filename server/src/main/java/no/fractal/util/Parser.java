package no.fractal.util;

public interface Parser {
	public <T> T parse(Class<? extends T> type, String stringToParse);

}