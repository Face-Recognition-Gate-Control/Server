package no.fractal.util;

public interface Parser<T> {
	public T parse(Class<T> type);
}