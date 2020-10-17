package no.fractal.database;

import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.TensorComparison.Range;
import no.fractal.TensorComparison.TensorComparator;
import no.fractal.database.Datatypes.TensorData;
import no.fractal.util.SimpleStopwatch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * this is the singelton interface used to acsess the db tensors
 * <p>
 * the reason for this interface is for caching
 */
public class DbTensorCache {

    // num threads to use for a single seartch op
    private static final int NUM_THREADS = 6;
    // max paralel sertch ops
    private static final int CONCURRENT_SEARCHES = 5;
    private static final XoRoShiRo128PlusRandom r = new XoRoShiRo128PlusRandom();
    private static DbTensorCache instance;
    private final Semaphore updatePending = new Semaphore(1);
    private final Semaphore currentSearching = new Semaphore(CONCURRENT_SEARCHES);
    private final ExecutorService executor;
    private long lastTableChangeTime = 0;
    private boolean isAdding;
    private ArrayList<TensorData> tensorData;
    private double[][] quickSearchArray;
    private Range[] ranges;

    private DbTensorCache() {
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            updateDb();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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

    /**
     * generates the ranges to split the current list of seartch objects based on the number of threads used
     *
     * @return an array with the ranges
     */
    private Range[] getRanges() {
        int              length   = this.quickSearchArray.length;
        ArrayList<Range> ranges   = new ArrayList<>();
        int              interval = Math.floorDiv(length, NUM_THREADS);

        for (int i = 0; i < NUM_THREADS - 1; i++) {
            int start = i * interval;
            int end   = (i + 1) * interval;
            ranges.add(new Range(start, end));
        }

        ranges.add(new Range(ranges.get(NUM_THREADS - 2).to, - 1));

        return ranges.toArray(Range[]::new);
    }

    /**
     * compairs the provided Tensor data with the database to many seartch ops or
     * add operation in progress this wil block
     *
     * @param searchData the data to compair
     *
     * @return the highest scored comparison result
     */
    public ComparisonResult getClosestMatch(TensorData searchData) throws SQLException {
        updateIfChanged();
        try {
            updatePending.acquire();
            currentSearching.acquire(1);
            updatePending.release();


            Future<TensorComparator.indexResultPackage>[] res = new Future[NUM_THREADS];

            for (int i = 0; i < NUM_THREADS; i++) {
                final int idx = i;
                res[i] = executor.submit(
                        () -> TensorComparator.euclideanFast(ranges[idx], searchData.tensor, quickSearchArray));
            }

            int    bestIdx  = - 1;
            double bestDist = 1 << 5;


            for (int i = 0; i < NUM_THREADS; i++) {
                TensorComparator.indexResultPackage resultPackage = res[i].get();
                double                              dist          = resultPackage.result;
                if (dist < bestDist) {
                    bestDist = dist;
                    bestIdx  = resultPackage.index;
                }
            }

            currentSearching.release();
            TensorData bestMatch = tensorData.get(bestIdx);
            return new ComparisonResult(bestMatch.id, (float) bestDist);

        } catch (Exception e) {
            currentSearching.release();
            e.printStackTrace();

            return null;
        }

    }

    /**
     * chek if the database has changed the tensortable update the local cash if so
     *
     * @throws SQLException
     */
    private synchronized void updateIfChanged() throws SQLException {
        if (! isAdding) {
            long lastChange = GateQueries.getLastTensorTableUpdate();
            if (this.lastTableChangeTime != lastChange) {
                this.isAdding            = true;
                this.lastTableChangeTime = lastChange;
                updateDb();
            }
        }

    }

    /**
     * Updates the values in the db
     */
    private void updateDb() {

        try {
            updatePending.acquire();
            currentSearching.acquire(CONCURRENT_SEARCHES);

            tensorData       = GateQueries.getCurrentTensorData();
            quickSearchArray = buildFastArrays(tensorData);
            ranges           = getRanges();


            currentSearching.release(CONCURRENT_SEARCHES);
            updatePending.release();
        } catch (Exception e) {
            e.printStackTrace();
            this.isAdding = false;
        }
        this.isAdding = false;

    }

    /**
     * Builds an array with only the vectors for faster access in searching
     *
     * @param tensorData the tensor data array with the id tensor pairs
     *
     * @return a 2d array with the tensors
     */
    private static double[][] buildFastArrays(ArrayList<TensorData> tensorData) {
        int        size             = tensorData.size();
        double[][] quickSearchArray = new double[size][512];
        for (int i = 0; i < size; i++) {
            quickSearchArray[i] = tensorData.get(i).tensor;
        }

        return quickSearchArray;

    }

    // --- testing stuff --- //
    private static TensorData generateRandomTensorData(int a) {

        double[] tensor = new double[512];
        for (int i = 0; i < 512; i++) {
            tensor[i] = r.nextDoubleFast();

        }
        return new TensorData(tensor, UUID.randomUUID());
    }

    public static void main(String[] args) {
        SimpleStopwatch.start("build");
        int numElements = 1000000;
        ArrayList<TensorData> tensorData = IntStream.range(0, numElements)
                                                    .parallel()
                                                    .mapToObj(DbTensorCache::generateRandomTensorData)
                                                    .collect(
                                                            Collectors.toCollection(ArrayList::new));

        SimpleStopwatch.stop("build", true);
        DbTensorCache tc = new DbTensorCache();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("./embedings_test")));

            ArrayList<float[]> embedings = new ArrayList<>();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        tc.tensorData       = tensorData;
        tc.quickSearchArray = buildFastArrays(tensorData);
        tc.ranges           = tc.getRanges();

        SimpleStopwatch.start("tot");
        ComparisonResult result  = null;
        TensorData       pattern = DbTensorCache.generateRandomTensorData(0);
        tc.tensorData.set(tensorData.size() - 20, pattern);
        tc.quickSearchArray[tensorData.size() - 20] = pattern.tensor;
        for (int i = 0; i < 10; i++) {
            SimpleStopwatch.start(String.format("run-%s", i));


            try {
                result = tc.getClosestMatch(pattern);

            } catch (Exception e) {

                e.printStackTrace();
            }

            SimpleStopwatch.stop(String.format("run-%s", i), true);

            System.out.printf("correct is: %s\n" + "guess is: %s\n", pattern.id, result.id);
            //System.out.println("dist: " + result.diffValue);


        }
        SimpleStopwatch.stop("tot", true);


    }
}
