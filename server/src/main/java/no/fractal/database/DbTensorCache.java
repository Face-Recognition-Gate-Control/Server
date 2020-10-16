package no.fractal.database;

import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.TensorComparison.Range;
import no.fractal.TensorComparison.TensorComparator;
import no.fractal.database.Datatypes.TensorData;
import no.fractal.util.SimpleStopwatch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.*;
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
    private static DbTensorCache instance;
    private final Semaphore updatePending = new Semaphore(1);
    private final Semaphore currentSearching = new Semaphore(CONCURRENT_SEARCHES);
    private final ExecutorService executor;
    private long lastTableChangeTime = 0;
    private boolean isAdding;

    // todo: change back to private
    public ArrayList<TensorData> tensorData;
    public double[][] quickSearchArray;
    public Range[] ranges;

    private DbTensorCache() {
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            //updateDb();
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
     * compairs the provided Tensor data with the database to many seartch ops or
     * add operation in progress this wil block
     *
     * @param searchData the data to compair
     *
     * @return the highest scored comparison result
     */
//    public ComparisonResult getClosestMatch(TensorData searchData) throws SQLException {
//        //updateIfChanged();
//        try {
//            updatePending.acquire();
//            currentSearching.acquire(1);
//            updatePending.release();
//
//            int interval = Math.floorDiv(tensorData.size(), NUM_THREADS);
//
//            // shit solution
//            ArrayList<Future<ComparisonResult[]>> res = new ArrayList<>();
//
//            for (int i = 0; i < NUM_THREADS - 1; i++) {
//                int start = i  * interval;
//                int end   = (i + 1) * interval;
//                res.add(executor.submit(
//                        () -> TensorComparator.euclideanDistanceCalculationTask(start, end, searchData, tensorData)));
//            }
//
//            res.add(executor.submit(() -> TensorComparator
//                    .euclideanDistanceCalculationTask((NUM_THREADS - 1) * interval, searchData, tensorData)));
//
//            // uhh this may be low so we bump the comma
//            ArrayList<ComparisonResult> results = res.stream().map(future -> {
//                try {
//                    return future.get();
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//                return null; // lets hope this does not happen
//            }).flatMap(Stream::of).sorted((o1, o2) -> (int) (o1.diffValue - o2.diffValue) * 1000)// icas values are
//                                                     // below 0
//                                                     .collect(Collectors.toCollection(ArrayList::new));
//
//            currentSearching.release();
//            return results.get(0);
//
//        } catch (Exception e) {
//            currentSearching.release();
//            e.printStackTrace();
//
//            return null;
//        }
//
//    }

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

            tensorData = GateQueries.getCurrentTensorData();



            currentSearching.release(CONCURRENT_SEARCHES);
            updatePending.release();
        } catch (Exception e) {
            e.printStackTrace();
            this.isAdding = false;
        }
        this.isAdding = false;

    }


    private Range[] getRanges(){
        int length = this.quickSearchArray.length;
        ArrayList<Range> ranges = new ArrayList<>();
        int interval = Math.floorDiv(length, NUM_THREADS);

        for (int i = 0; i < NUM_THREADS - 1; i++) {
            int start = i * interval;
            int end   = (i + 1) * interval;
            ranges.add(new Range(start, end));
        }

        ranges.add(new Range(ranges.get(NUM_THREADS - 2).to, -1));

        return ranges.toArray(Range[]::new);
    }

    // --- jupdidup optimizzazzion --- //




    public ComparisonResult getClosestMatch(TensorData searchData) throws SQLException {
        //updateIfChanged();
        try {
            updatePending.acquire();
            currentSearching.acquire(1);
            updatePending.release();


            Future<TensorComparator.Index_result_package>[] res = new Future[NUM_THREADS];

            for (int i = 0; i < NUM_THREADS; i++) {
                final int idx = i;
                res[i] = executor.submit(
                        () -> TensorComparator.euclidianFast(ranges[idx], searchData.tensor, quickSearchArray));
            }

            int bestIdx = -1;
            double bestDist = 1 << 5;


            for (int i = 0; i < NUM_THREADS; i++) {
                TensorComparator.Index_result_package resultPackage = res[i].get();
                double dist = resultPackage.result;
                if (dist < bestDist){
                    bestDist = dist;
                    bestIdx = resultPackage.index;
                }
            }

            currentSearching.release();
            TensorData bestMatch = tensorData.get(bestIdx);
            return new ComparisonResult(bestMatch.id,(float) bestDist);

        } catch (Exception e) {
            currentSearching.release();
            e.printStackTrace();

            return null;
        }

    }
    private static double[][] buildFastArrays(ArrayList<TensorData> tensorData){
        int size = tensorData.size();
        double[][] quickSearchArray = new double[size][512];
        for (int i = 0; i < size; i++) {
            quickSearchArray[i] = tensorData.get(i).tensor;
        }

        return quickSearchArray;

    }

    private static final XoRoShiRo128PlusRandom r = new XoRoShiRo128PlusRandom();
    private static TensorData generateRandomTensorData(int a){

        double[] tensor = new double[512];
        for (int i = 0; i < 512; i++) {
           tensor[i] = r.nextDoubleFast();

        }
        return new TensorData(tensor, UUID.randomUUID());
    }

    public static void main(String[] args) {
        SimpleStopwatch.start("build");
        int numElements = 1000000;
        ArrayList<TensorData> tensorData = IntStream.range(0,numElements)
                                                    .parallel().mapToObj(DbTensorCache::generateRandomTensorData).collect(
                        Collectors.toCollection(ArrayList::new));

        SimpleStopwatch.stop("build", true);
        DbTensorCache tc = new DbTensorCache();



        tc.tensorData = tensorData;
        tc.quickSearchArray = buildFastArrays(tensorData);
        tc.ranges = tc.getRanges();

        SimpleStopwatch.start("tot");
        ComparisonResult result = null;
        TensorData pattern = DbTensorCache.generateRandomTensorData(0);
        tc.tensorData .set(tensorData.size() - 20, pattern);
        tc.quickSearchArray[tensorData.size() - 20] = pattern.tensor;
        for (int i = 0; i < 10; i++) {
            //SimpleStopwatch.start(String.format("run-%s", i));


            try {
                result = tc.getClosestMatch(pattern);

                //TensorComparator.Index_result_package pkg= TensorComparator.euclidianFast(new Range(0, numElements), pattern.tensor, tc.quickSearchArray);
                //result = new ComparisonResult(tc.tensorData.get(pkg.index).id,(float) pkg.result);

            } catch (Exception e) {

                e.printStackTrace();
            }

            //SimpleStopwatch.stop(String.format("run-%s", i), true);

            System.out.printf("correct is: %s\n" +"guess is: %s\n", pattern.id,result.id);
            //System.out.println("dist: " + result.diffValue);


        }
        SimpleStopwatch.stop("tot", true);




    }
}
