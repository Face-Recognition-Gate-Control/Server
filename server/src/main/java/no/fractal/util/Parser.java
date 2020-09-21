package no.fractal.util;

public interface Parser<T> {
	public T parse(Class<? extends T> type, String stringToParse);
}