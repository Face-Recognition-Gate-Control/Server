package no.fractal.database;

import no.fractal.database.Datatypes.TensorData;
import no.fractal.database.Datatypes.User;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GateQueries extends PsqlDb {

    public static ArrayList<TensorData> getCurrentTensorData() throws SQLException {
        String query = "SELECT user_id, face_vec FROM login_referance;";

        ArrayList<TensorData> ret = new ArrayList<>();

        sqlQuery(query, resultSet -> {
            ret.add(new TensorData(
                    (BigDecimal[]) resultSet.getArray("face_vec").getArray(),
                    UUID.fromString(resultSet.getString("user_id"))
                    ));
        });

        return ret;
    }

    public static User getUser(UUID userID) throws SQLException {

        String query = String.format(
                "SELECT firstname, lastname FROM users where id = '%s';", userID.toString());

        AtomicReference<User> ret = new AtomicReference<>();

        sqlQuery(query, resultSet -> {
            ret.set(new User(
                    userID,
                    resultSet.getString("firstname"),
                    resultSet.getString("lastname")
            ));
        });

        return ret.get();
    }

    public static void registerUserEnteredRoom(UUID user_id, UUID stationId) throws SQLException {
        String query = String.format(
                "INSERT INTO user_enter_events (user_id, station_id)  VALUES ('%s', '%s');",
                user_id,
                stationId);

        sqlUpdate(query);
    }

    public static void addUserToNewQueue(UUID user_id, TensorData tensorData, UUID stationId) throws SQLException {
        String query = String.format(
                "INSERT INTO new_user_queue (tmp_id, face_vec, station_id)  VALUES ('%s', %s, '%s');",
                user_id,
                tensorData.asSQLString(),
                stationId);

        sqlUpdate(query);
    }

    public static void addImageToWaitQue(UUID user_id, File imgFile) throws SQLException {
        String query = String.format(
                "UPDATE new_user_queue SET file_name='%s' where tmp_id='%s';",
                imgFile.toString(),
                user_id);

        sqlUpdate(query);
    }

    public static boolean isIdInNewQue(UUID userId) throws SQLException {
        String query = String.format(
                "SELECT tmp_id FROM new_user_queue where tmp_id = '%s';", userId.toString());

        AtomicBoolean ret = new AtomicBoolean(false);

        sqlQuery(query, resultSet -> {
            ret.set(resultSet.first());
        });

        return ret.get();

    }

    public static boolean isStationLoginValid(UUID stationId, String stationSecret) throws SQLException {
        String query = String.format(
                "SELECT login_key FROM stations where id = '%s';", stationId.toString());

        AtomicBoolean ret = new AtomicBoolean(false);

        sqlQuery(query, resultSet -> {
            if (resultSet.first()){
                if (resultSet.getString("login_key").equals(stationSecret)){
                    ret.set(true);
                }
            }
        });

        return ret.get();

    }

    public static long getLastTensorTableUpdate() throws SQLException {
        String query = String.format(
                "SELECT last_change FROM updates_table where id = '%s';", "new_user_queue");

        AtomicLong ret = new AtomicLong();
        sqlQuery(query, resultSet -> {
            ret.set(resultSet.getLong("last_change"));
        });

        return ret.get();

    }

    public static HashMap<UUID, File> removeTimedOutIdsFromNewQueue(long timeLimit) throws SQLException {

        String query = String.format(
                "DELETE FROM new_user_queue WHERE added_ts > %d RETURNING tmp_id, file_name;", timeLimit);

        HashMap<UUID, File> ret = new HashMap<>();

        sqlQuery(query, resultSet -> {
            UUID uuid = resultSet.getString("tmp_id") != null ? UUID.fromString(resultSet.getString("tmp_id")) : null;
            File file = resultSet.getString("file_name") != null ? new File("file_name") : null;
            ret.put(uuid, file);
        });



        return ret;

    }




    // examples
    /*
    public static WorkerNodeResourceManager getWorkerResourceManagerById(UUID workerId) {
        AtomicReference<WorkerNodeResourceManager> resourceManager = new AtomicReference<>(null);
        String query = String.format(
                "SELECT k.id, k.gpu, k.cpu, k.gig_ram, k.timeout_Seconds " +
                        "FROM compute_nodes " +
                        "INNER JOIN resource_keys k ON compute_nodes.resource_key = k.id " +
                        "WHERE compute_nodes.id = '%s';",workerId);
        sqlQuery(query, resultSet -> {
            ComputeResources.ResourceKey resourceKey = new ComputeResources.ResourceKey(
                    resultSet.getString("id"),
                    resultSet.getInt("gpu"),
                    resultSet.getInt("cpu"),
                    resultSet.getInt("gig_ram"),
                    resultSet.getInt("timeout_Seconds"));
            resourceManager.set(new WorkerNodeResourceManager(ComputeResources.mapUnitToComputeResource(resourceKey), workerId));
        });


        return resourceManager.get();
    }


    public static void workerChekIn(UUID workerId){
        String query = "UPDATE compute_nodes SET last_check_in = extract(epoch from now()) WHERE id='%s';";
        sqlUpdate(String.format(query, workerId));
    }




    */
}
