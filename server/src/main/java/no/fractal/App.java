package no.fractal;

import no.fractal.socket.TcpServer;

/**
 * Hello world!
 */
public class App {

    /*
     * Change the format of the java util logger to single line message in format:
     * [Data time] [type] message @ class
     */
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                           "[%1$tb %1$td %1$tY] [%1$tT] %2$s%n%4$s: %5$s%n"
        );
    }

    public static void main(String[] args) {
        TcpServer sr = new TcpServer();
        sr.start();
    }
}
