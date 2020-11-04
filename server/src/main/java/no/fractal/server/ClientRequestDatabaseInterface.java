package no.fractal.server;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.database.Models.TensorData;
import no.fractal.database.Models.User;
import no.fractal.database.TensorSearcher;
import no.fractal.database.GateQueries;
import no.fractal.server.corutenes.OldEntryRemover;
import no.fractal.util.FileUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/*
    todo: find out whether or not the url gate id shold be included in the url

     */


public class ClientRequestDatabaseInterface {


    public static final long CLEANUP_INTERVAL = 360000L; // 100*60*60
    public static final long ALLOWED_AGE_FOR_THE_NEW_STUF = 8640000L; // 100*60*60*24
    private static ClientRequestDatabaseInterface instance;
    public final File imageSaveDir;
    public final File imagePermSaveDir;
    public final File imageTmpSaveDir;
    private final String nodeUrl;
    private final ScheduledExecutorService scheduledExecutor = Executors
            .newSingleThreadScheduledExecutor();
    public StationManager stationManager;

    public ClientRequestDatabaseInterface() {
        nodeUrl          = System.getenv("NODE_URL");
        imageSaveDir     = new File("fractal_thumbnail_images");
        imagePermSaveDir = new File(imageSaveDir, "perm_storage");
        imageTmpSaveDir  = new File(imageSaveDir, "temp_storage");

        this.stationManager = new StationManager();
    }

    public static ClientRequestDatabaseInterface getInstance() {
        if (instance == null) {
            instance = new ClientRequestDatabaseInterface();
        }
        return instance;
    }

    /**
     * returns the user objet from the uuid gained from the tensor comparison
     *
     * @param uid user id
     *
     * @return The user object null if none is found
     */
    public User getUser(UUID uid) throws SQLException {
        User res = GateQueries.getUserByID(uid);

        // if any fields are empty there are no object in the db return null
        res = res.firstName == null ? null : res;
        return res;
    }

    /**
     * registers that a user has entered som room for infection tracking
     *
     * @param userId the id of the user whom entered
     * @param gateId the id of the gate/room
     */
    public void registerUserEntering(UUID userId, UUID gateId) throws SQLException {
        GateQueries.createUserEnteredEvent(userId, gateId);

    }


    /**
     * Cheks if the provided id is in the wait que if it is, a registration url is generated and returned
     *
     * @param userId the id of the new user
     *
     * @return a strig with the url
     * @throws SQLException
     */
    public String getNewRegistrationURL(UUID userId) throws SQLException {
        if (GateQueries.isIdInRegistrationQueue(userId)) {
            return this.nodeUrl + "/" + userId;
        }

        return null;
    }

    /**
     * registers a new user in the waiting to be made que
     *
     * @param userId     the users id
     * @param tensorData the users face tensor data
     * @param stationId  the staion the user where registered at
     *
     * @throws SQLException
     */
    public void registerUserInQue(UUID userId, TensorData tensorData, UUID stationId) throws SQLException {
        GateQueries.addNewUserToRegistrationQueue(userId, tensorData, stationId);
    }

    /**
     * adds a thumbnail image to a user waiting in the new user que
     *
     * @param userId
     * @param imageFile
     * @throws SQLException
     */
    public void registerImageToUser(UUID userId, File imageFile) throws SQLException {
        if (FileUtils.isFileChildOfDir(imageFile, this.imageTmpSaveDir)) {
            GateQueries.addThumbnailToNewUserInRegistrationQueue(userId, imageFile);
        } else {
            // exeption mabye somthing is wrong
        }
    }

    public ComparisonResult getBestMatch(TensorData tensorData) throws SQLException {
        return TensorSearcher.getInstance().getClosestMatch(tensorData);
    }

    /**
     * starts corutines like garbage cleaning and user deregistration
     */
    private void startCorutines() {
        this.scheduledExecutor.scheduleAtFixedRate(
                new OldEntryRemover(ALLOWED_AGE_FOR_THE_NEW_STUF),
                0L,
                CLEANUP_INTERVAL,
                TimeUnit.SECONDS
        );


    }


}
