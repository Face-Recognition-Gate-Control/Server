package no.fractal.database.Datatypes;

import java.util.UUID;

/**
 * a object representing a gate station probably to be replaced by the fractal client
 */
public class GateStation {
    public final UUID stationId;
    public final UUID sessionId;

    public GateStation(UUID stationId, UUID sessionId) {
        this.stationId = stationId;
        this.sessionId = sessionId;
    }
}
