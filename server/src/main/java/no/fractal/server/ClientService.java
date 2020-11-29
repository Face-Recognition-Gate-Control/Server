package no.fractal.server;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.database.Models.TensorData;
import no.fractal.database.Models.User;
import no.fractal.database.TensorSearcher;
import no.fractal.database.GateQueries;
import no.fractal.server.corutenes.ExpiredUserRegistrationTask;
import no.fractal.util.FileUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class ClientService {

    /**
     * Time in millisecond for how often temp data cleanup should be performed
     */
    public static final long CLEANUP_INTERVAL = 360000L; // 100*60*60

    /**
     * How long temp data can live
     */
    public static final long EXPIRATION_TIME = 8640000L; // 100*60*60*24

    private static ClientService instance;

    public final File imageSaveDir;

    public final File imagePermSaveDir;

    public final File imageTmpSaveDir;

    private final String nodeUrl;

    private final ScheduledExecutorService scheduledExecutor = Executors
            .newSingleThreadScheduledExecutor();

    public StationManager stationManager;

    public ClientService() {
        nodeUrl          = System.getenv("NODE_URL");
        imageSaveDir     = new File("fractal_thumbnail_images");
        imagePermSaveDir = new File(imageSaveDir, "perm_storage");
        imageTmpSaveDir  = new File(imageSaveDir, "temp_storage");

        this.stationManager = new StationManager();
        this.startScheduledCleanupService();
    }

    public static ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    /**
     * Tries to get a user by its ID.
     * If user is not found, return null
     *
     * @param uid id of the user to find
     * @return found user or null
     * @throws SQLException thrown if problems with database
     */
    public User getUserById(UUID uid) throws SQLException {
        User res = GateQueries.getUserByID(uid);
        res = res.firstName == null ? null : res;
        return res;
    }

    /**
     * Creates a user entered event.
     *
     * @param userId the id of the user entered.
     * @param gateId the id of the gate station the user entered on.
     * @throws SQLException thrown if problems with database
     */
    public void createUserEnteredEvent(UUID userId, UUID gateId) throws SQLException {
        GateQueries.createUserEnteredEvent(userId, gateId);

    }


    /**
     * Returns the registration URL for the user with the given ID if id exists in the new user registration queue.
     * If the Id does not exist, null is returned.
     * @param userId the id of the user to get registration url for.
     * @return user registration url or null
     * @throws SQLException thrown if problems with database
     */
    public String getNewRegistrationURL(UUID userId) throws SQLException {
        if (GateQueries.isIdInRegistrationQueue(userId)) {
            return this.nodeUrl + "/" + userId;
        }
        return null;
    }

    /**
     * Adds a new user to the registration queue.
     * The user is created with an UUID, face feature tensor, and a
     * station id for the station the user was unidentified on.
     *
     * @param userId the id of the new user
     * @param tensorData the face feature of the user
     * @param stationId the id of the station the was unidentified on
     * @throws SQLException thrown if problems with database
     */
    public void addNewUserToRegistrationQueue(UUID userId, TensorData tensorData, UUID stationId) throws SQLException {
        GateQueries.addNewUserToRegistrationQueue(userId, tensorData, stationId);
    }

    /**
     * Adds the thumbnail image for a user in the registration queue.
     *
     * @param userId the id of the user to add the thumbnail to.
     * @param imageFile the thumbnail file
     * @throws SQLException thrown if problems with database
     */
    public void addThumbnailToNewUserInRegistrationQueue(UUID userId, File imageFile) throws SQLException {
        if (FileUtils.isFileChildOfDir(imageFile, this.imageTmpSaveDir)) {
            GateQueries.addThumbnailToNewUserInRegistrationQueue(userId, imageFile);
        } else {
            // exeption mabye somthing is wrong
        }
    }

    /**
     * Returns a comparison result for the best match for the provided face feature tensor.
     *
     * @param tensorData tensor data to match against.
     * @return comparison result for the match for the provided tensor.
     * @throws SQLException thrown if problems with database
     */
    public ComparisonResult matchFaceFeatures(TensorData tensorData) throws SQLException {
        return TensorSearcher.getInstance().getClosestMatch(tensorData);
    }

    /**
     * Starts a scheduled clean up service at a fixed rate.
     */
    private void startScheduledCleanupService() {
        this.scheduledExecutor.scheduleAtFixedRate(
                new ExpiredUserRegistrationTask(EXPIRATION_TIME),
                0L,
                CLEANUP_INTERVAL,
                TimeUnit.SECONDS
        );


    }


}
