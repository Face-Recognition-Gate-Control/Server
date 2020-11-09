package no.fractal.database;

import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.TensorComparison.Range;
import no.fractal.TensorComparison.TensorComparator;
import no.fractal.database.Models.TensorData;

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
 * this is the singleton interface used to search through the tensors for a mach
 * <p>
 * The class cashes the current tensors for faster access
 */
public class TensorSearcher {

    // num threads to use for a single seartch op
    private static final int NUM_THREADS = 6;
    // max paralel sertch ops
    private static final int CONCURRENT_SEARCHES = 5;
    private static final XoRoShiRo128PlusRandom r = new XoRoShiRo128PlusRandom();
    private static TensorSearcher instance;
    private final Semaphore updatePending = new Semaphore(1);
    private final Semaphore currentSearching = new Semaphore(CONCURRENT_SEARCHES);
    private final ExecutorService executor;
    private long lastTableChangeTime = 0;
    private boolean isAdding;
    private ArrayList<TensorData> tensorData;
    private double[][] quickSearchArray;
    private Range[] ranges;

    private TensorSearcher() {
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
    public static TensorSearcher getInstance() {
        if (instance == null) {
            instance = new TensorSearcher();
        }
        return instance;
    }

    /**
     * generates the ranges to split the current list of seartch objects based on the number of threads used
     *
     * @return an array with the ranges
     */
    private Range[] getRanges() {
        int length = this.quickSearchArray.length;
        ArrayList<Range> ranges = new ArrayList<>();
        int interval = Math.floorDiv(length, NUM_THREADS);

        for (int i = 0; i < NUM_THREADS - 1; i++) {
            int start = i * interval;
            int end = (i + 1) * interval;
            ranges.add(new Range(start, end));
        }

        ranges.add(new Range(ranges.get(NUM_THREADS - 2).to, -1));

        return ranges.toArray(Range[]::new);
    }

    /**
     * compairs the provided Tensor data with the database to many seartch ops or
     * add operation in progress this wil block
     *
     * @param searchData the data to compair
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

            int bestIdx = -1;
            double bestDist = 1 << 5;


            for (int i = 0; i < NUM_THREADS; i++) {
                TensorComparator.indexResultPackage resultPackage = res[i].get();
                double dist = resultPackage.result;
                if (dist < bestDist) {
                    bestDist = dist;
                    bestIdx = resultPackage.index;
                }
            }

            TensorData bestMatch = (bestIdx != -1) ? tensorData.get(bestIdx) : searchData;
            return new ComparisonResult(bestMatch.id, (float) bestDist);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            currentSearching.release();

        }

    }

    /**
     * chek if the database has changed the tensortable update the local cash if so
     *
     * @throws SQLException
     */
    private synchronized void updateIfChanged() throws SQLException {
        if (!isAdding) {
            long lastChange = GateQueries.getLastTensorTableUpdate();

            if (this.lastTableChangeTime != lastChange) {
                this.isAdding = true;
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
            System.out.println("UPDATE DB");
            tensorData = GateQueries.getAllTensors();
            quickSearchArray = buildFastArrays(tensorData);
            System.out.println(quickSearchArray.length);
            ranges = getRanges();



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            currentSearching.release(CONCURRENT_SEARCHES);
            updatePending.release();
            this.isAdding = false;
        }

    }

    /**
     * Builds an array with only the vectors for faster access in searching
     *
     * @param tensorData the tensor data array with the id tensor pairs
     * @return a 2d array with the tensors
     */
    private static double[][] buildFastArrays(ArrayList<TensorData> tensorData) {
        int size = tensorData.size();
        double[][] quickSearchArray = new double[size][512];
        for (int i = 0; i < size; i++) {
            quickSearchArray[i] = tensorData.get(i).tensor;
        }

        return quickSearchArray;

    }

    /**
     * Generates a random cache for this class; used for testing
     *
     * @param cacheSize the size of the cache
     * @return the list generated of tensor data
     */
    public ArrayList<TensorData> generateRandomCache(int cacheSize, TensorData testTensor) {
        ArrayList<TensorData> tensorData = IntStream.range(0, cacheSize)
                .parallel()
                .mapToObj(this::generateRandomTensorData)
                .collect(
                        Collectors.toCollection(ArrayList::new));
        tensorData.set((int) (Math.random() * cacheSize), testTensor);
        this.tensorData = tensorData;
        this.quickSearchArray = buildFastArrays(tensorData);
        this.ranges = getRanges();
        return this.tensorData;
    }


    public TensorData generateRandomTensorData(int a) {

        double[] tensor = new double[512];
        for (int i = 0; i < 512; i++) {
            tensor[i] = r.nextDoubleFast();

        }
        return new TensorData(tensor, UUID.randomUUID());
    }


}
