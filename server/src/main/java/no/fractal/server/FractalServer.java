package no.fractal.server;

import no.fractal.database.Datatypes.GateStation;
import no.fractal.database.Datatypes.User;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class FractalServer {

    private static FractalServer instance;

    public static FractalServer getInstance() {
        if (instance == null){
            instance = new FractalServer();
        }
        return instance;
    }

    public StationManager stationManager;


    /**
     * returns the user objet from the uuid gained from the tensor comparison
     * @param uid user id
     * @return The user object null if none is found
     */
    public User getUser(UUID uid){
        return null;
    }

    /**
     * registers that a user has entered som room for infection tracking
     * @param userId the id of the user whom entered
     * @param gateId the id of the gate/room
     */
    public void registerUserEntering(UUID userId, UUID gateId){

    }

    /**
     * in the event of an unknown user is spotted the image and gate id is called here and returns a
     * url to show the user that allows for user registration
     * @param userImage the image of the user
     * @param gateId the id of the gate
     * @return the url for registration
     */
    public URL getNewRegistrationURL(File userImage, UUID gateId){
        return null;
    }

    /**
     * starts corutines like garbage cleaning and user deregistration
     */
    private void startCorutines(){

    }



}
