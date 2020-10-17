package no.fractal.database;

import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import no.fractal.database.Datatypes.TensorData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A caching class for Tensor data. It provides facilities for fetching new updates from datbase, and store
 * them in memory for later.
 */
public class TensorCache {

    private static final TensorCache instance = new TensorCache();

    /**
     * 'Time stamp for last database change
     */
    private long lastTableChangeTime = 0;

    /**
     * Flag set when cache is getting new data from database
     */
    private boolean cacheIsUpdating;

    /**
     * The tensor cache list
     */
    private CopyOnWriteArrayList<TensorData> tensorListCache = new CopyOnWriteArrayList<TensorData>();

    /**
     * Test tensor used for testing purposes only
     */
    private TensorData testTensor = generateRandomTensorData(1);

    private TensorCache() {
        try {
            updateCahce();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns single singleton instance
     *
     * @return returns the cache instance
     */
    public static TensorCache getInstance() {
        return instance;
    }

    /**
     * Returns the cached tensor data.
     * Before it is returned it performs a check against the database
     * for verifying integrity. And updates the cache if database is modified.
     *
     * @return cached tensor data
     */
    public synchronized CopyOnWriteArrayList<TensorData> getCachedTensors() {
        try {
            if (this.hasDatabaseChanged()) {
                updateCahce();
            }
        } catch (SQLException e) {
        }
        return this.tensorListCache;
    }


    /**
     * Check the database if there has been any updates on the tensor table,
     * if changes has occurred return true, else false
     *
     * @throws SQLException thrown if SQL error
     */
    private boolean hasDatabaseChanged() throws SQLException {
        var changed = false;
        if (!cacheIsUpdating) {
            long lastChange = GateQueries.getLastTensorTableUpdate();
            if (this.lastTableChangeTime != lastChange) {
                this.cacheIsUpdating = true;
                changed = true;
                this.lastTableChangeTime = lastChange;
            }
        }
        return changed;

    }

    /**
     * Updates the cache to match database
     */
    private void updateCahce() throws SQLException {
        this.cacheIsUpdating = true;
        this.tensorListCache = new CopyOnWriteArrayList<TensorData>(GateQueries.getCurrentTensorData());
        this.cacheIsUpdating = false;
    }

    /**
     * Generates a random cache for this class; used for testing
     *
     * @param cacheSize the size of the cache
     * @return the list generated of tensor data
     */
    public CopyOnWriteArrayList<TensorData> generateRandomCache(int cacheSize) {
        ArrayList<TensorData> tensorData = IntStream.range(0, cacheSize)
                .parallel()
                .mapToObj(this::generateRandomTensorData)
                .collect(
                        Collectors.toCollection(ArrayList::new));
        tensorData.set((int) (Math.random() * cacheSize), this.testTensor);
        this.tensorListCache = new CopyOnWriteArrayList<TensorData>(tensorData);
        return this.tensorListCache;
    }

    /**
     * Generates a single random tensor
     *
     * @param a
     * @return a random tensor
     */
    public TensorData generateRandomTensorData(int a) {
        final XoRoShiRo128PlusRandom r = new XoRoShiRo128PlusRandom();
        double[] tensor = new double[TensorData.TENSOR_SIZE];
        for (int i = 0; i < 512; i++) {
            tensor[i] = r.nextDoubleFast();
        }
        return new TensorData(tensor, UUID.randomUUID());
    }

    /**
     * Sets a test tensor, this will be applied to a random index in the random cache, if it is used.
     *
     * @param testTensor tensor to set.
     */
    public void setTestTensor(TensorData testTensor) {
        this.testTensor = testTensor;
    }

}
