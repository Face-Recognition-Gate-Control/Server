package no.fractal.database.Models;

import java.util.UUID;

/**
 * Model class for a Gate station.
 */
public class GateStation {
    public final UUID stationId;
    public final UUID sessionId;

    public GateStation(UUID stationId, UUID sessionId) {
        this.stationId = stationId;
        this.sessionId = sessionId;
    }
}
