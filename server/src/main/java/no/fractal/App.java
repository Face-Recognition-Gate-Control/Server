package no.fractal;

/**
 * Hello world!
 *
 */
    /*
     * Change the format of the java util logger to single line message in format:
     * [Data time] [type] message @ class
     */
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tb %1$td %1$tY] [%1$tT] %2$s%n%4$s: %5$s%n");
    }
    }
}
