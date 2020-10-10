package no.fractal.util;

public interface Parser {
    <T> T parse(Class<? extends T> type, String stringToParse);

}