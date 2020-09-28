package no.fractal.server;

import no.fractal.database.Datatypes.GateStation;

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


}
