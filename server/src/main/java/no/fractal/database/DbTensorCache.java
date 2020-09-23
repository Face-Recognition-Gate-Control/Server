package no.fractal.database;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.TensorComparison.TensorComparator;
import no.fractal.database.Datatypes.TensorData;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * this is the singelton interface used to acsess the db tensors
 *
 * the reason for this interface is for caching
 */
public class DbTensorCache {

    private static DbTensorCache instance;

    /**
     * returns instance
     * 
     * @return instance
     */
    public static DbTensorCache getInstance() {
        if (instance == null) {
            instance = new DbTensorCache();
        }
        return instance;
    }

    // num threads to use for a single seartch op
    private static final int NUM_THREADS = 6;
    // max paralel sertch ops
    private static final int CONCURRENT_SEARCHES = 5;

    private ExecutorService executor;

    private ArrayList<TensorData> tensorData;
    private final Semaphore updatePending = new Semaphore(1);
    private final Semaphore currentSearching = new Semaphore(CONCURRENT_SEARCHES);

    private DbTensorCache() {
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            tensorData = GateQueries.getWorkerResourceManagerById();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * compairs the provided Tensor data with the database to many seartch ops or
     * add operation in progress this wil block
     *
     * @param searchData the data to compair
     * @return the highest scored comparison result
     */
    public ComparisonResult getClosestMatch(TensorData searchData) {
        try {
            updatePending.acquire();
            currentSearching.acquire(1);
            updatePending.release();

            int interval = Math.floorDiv(tensorData.size(), NUM_THREADS);

            // shit solution
            ArrayList<Future<ComparisonResult[]>> res = new ArrayList<>();

            for (int i = 0; i < NUM_THREADS - 1; i++) {
                int start = (i - 1) * interval;
                int end = i * interval;
                res.add(executor.submit(
                        () -> TensorComparator.euclideanDistanceCalculationTask(start, end, searchData, tensorData)));
            }

            res.add(executor.submit(() -> TensorComparator
                    .euclideanDistanceCalculationTask((NUM_THREADS - 1) * interval, searchData, tensorData)));

            // uhh this may be low so we bump the comma
            ArrayList<ComparisonResult> results = res.stream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null; // lets hope this does not happen
            }).flatMap(Stream::of).sorted((o1, o2) -> (int) (o1.diffValue - o2.diffValue) * 1000)// icas values are
                                                                                                 // below 0
                    .collect(Collectors.toCollection(ArrayList::new));

            currentSearching.release();
            return results.get(0);

        } catch (Exception e) {
            currentSearching.release();
            e.printStackTrace();

            return null;
        }

    }

    /**
     * adds a datapoint to the local cash
     *
     * this shold be called from wherever tensor data is put in to the db
     *
     * @param newData
     */
    public void addDatapoint(TensorData newData) {

        try {
            updatePending.acquire();
            currentSearching.acquire(CONCURRENT_SEARCHES);

            // addynes goes here

            currentSearching.release(CONCURRENT_SEARCHES);
            updatePending.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
