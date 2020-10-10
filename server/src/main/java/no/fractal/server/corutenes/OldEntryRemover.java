package no.fractal.server.corutenes;


import no.fractal.database.GateQueries;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Responsible for deletiong the useres that where offered a chanse to make a accont but declined
 */
public class OldEntryRemover extends TimerTask {

    private final long killAgeInMillis; // 100*60*60*24

    public OldEntryRemover(long killAgeInMillis) {
        this.killAgeInMillis = killAgeInMillis;
    }


    @Override
    public void run() {
        try {
            long                currentTime = Instant.now().getEpochSecond();
            long                killTime    = currentTime + killAgeInMillis;
            HashMap<UUID, File> res         = GateQueries.removeTimedOutIdsFromNewQueue(killTime);

            for (Map.Entry<UUID, File> entry : res.entrySet()) {
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
