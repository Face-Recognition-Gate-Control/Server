package no.fractal.server;

import no.fractal.database.Datatypes.GateStation;
import no.fractal.database.GateQueries;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;


/**
 * manages the fractal gate stations and controlling what stations are online by quering them with
 * liveliness cheks
 */
public class StationManager {

    private ArrayList<GateStation> activeStations;

    public GateStation registerStation(UUID gateId){
        return null;
    }

    /**
     * cheks if the loggin is valid
     * @param stationId the id for the station to chek
     * @param logginKey the loggin key for the station
     * @return wheter the loggin is valid
     * @throws SQLException
     */
    public boolean IsStationValid(UUID stationId, String logginKey) throws SQLException {
        return GateQueries.isStationLoginValid(stationId, logginKey);
    }


}
