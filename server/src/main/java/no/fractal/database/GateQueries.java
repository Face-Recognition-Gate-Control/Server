package no.fractal.database;

import no.fractal.database.Datatypes.TensorData;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class GateQueries extends PsqlDb {

    public static ArrayList<TensorData> getWorkerResourceManagerById() throws SQLException {
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
