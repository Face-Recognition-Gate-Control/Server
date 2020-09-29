package no.fractal.server;

import no.fractal.database.Datatypes.GateStation;
import no.fractal.database.Datatypes.TensorData;
import no.fractal.database.Datatypes.User;
import no.fractal.database.GateQueries;
import no.fractal.util.FileUtils;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
/*
    todo: find out whether or not the url gate id shold be included in the url

     */


public class FractalServer {


    private static FractalServer instance;

    private final String nodeUrl;
    public final File imageSaveDir;
    public final File imagePermSaveDir;
    public final File imageTmpSaveDir;

    public FractalServer() {
        nodeUrl = System.getenv("NODE_URL");
        imageSaveDir = new File("fractal_thumbnail_images");
        imagePermSaveDir = new File(imageSaveDir, "perm_storage");
        imageTmpSaveDir = new File(imageSaveDir, "temp_storage");
    }

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
    public User getUser(UUID uid) throws SQLException {
        User res = GateQueries.getUser(uid);

        // if any fields are empty there are no object in the db return null
        res = res.firstName==null ? null: res;
        return res;
    }

    /**
     * registers that a user has entered som room for infection tracking
     * @param userId the id of the user whom entered
     * @param gateId the id of the gate/room
     */
    public void registerUserEntering(UUID userId, UUID gateId) throws SQLException {
        GateQueries.registerUserEnteredRoom(userId, gateId);

    }


    /**
     * Cheks if the provided id is in the wait que if it is, a registration url is generated and returned
     * @param userId the id of the new user
     * @param gateId the id og of the gate used to register
     * @return a strig with the url
     * @throws SQLException
     */
    public String getNewRegistrationURL(UUID userId, UUID gateId) throws SQLException {
        if (GateQueries.isIdInNewQue(userId)){
            return this.nodeUrl + "/" + userId + gateId;
        }

        return null;
    }

    /**
     * registers a new user in the waiting to be made que
     * @param userId the users id
     * @param tensorData the users face tensor data
     * @param stationId the staion the user where registered at
     * @throws SQLException
     */
    public void registerUserInQue(UUID userId, TensorData tensorData, UUID stationId) throws SQLException {
        GateQueries.addUserToNewQueue(userId, tensorData, stationId);
    }

    /**
     * adds a thumbnail image to a user waiting in the new user que
     * @param userId
     * @param imageFile
     * @throws SQLException
     */
    public void registerImageToUser(UUID userId, File imageFile) throws SQLException {
        if (FileUtils.isFileChildOfDir(FileUtils.systemTmpDir, imageFile)){
            GateQueries.addImageToWaitQue(userId,imageFile);
        } else {
            // exeption mabye somthing is wrong
        }
    }

    /**
     * starts corutines like garbage cleaning and user deregistration
     */
    private void startCorutines(){

    }



}
