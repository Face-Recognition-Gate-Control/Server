package no.fractal.server.corutenes;


import no.fractal.database.GateQueries;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Responsible for cleaning/removing expired user registrations.
 */
public class ExpiredUserRegistrationTask extends TimerTask {

    private final long expirationTime; // 100*60*60*24

    public ExpiredUserRegistrationTask(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public void run() {
        try {
            long                currentTime = Instant.now().getEpochSecond();
            long                killTime    = currentTime + expirationTime;
            HashMap<UUID, File> expiredRegistrations         = GateQueries.removeExpiredNewUserRegistrations(killTime);

            for (Map.Entry<UUID, File> entry : expiredRegistrations.entrySet()) {
                if (entry.getValue() != null) {
                    boolean suc = entry.getValue().delete();
                    System.out.printf(
                            "deleted: id-%s with a image at %s the operation was %s",
                            entry.getKey(),
                            entry.getValue().getName(),
                            suc
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
