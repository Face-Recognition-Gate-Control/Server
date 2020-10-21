package no.fractal;

import no.fractal.socket.TcpServer;

import java.util.logging.Level;
import java.util.logging.Logger;

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
                "[%1$tb %1$td %1$tY] [%1$tT] %2$s%n%4$s: %5$s%6$s%n"
        );
    }

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        try {
            new Initialize();
            TcpServer sr = new TcpServer();
            sr.start();
        } catch (InitException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }
}
