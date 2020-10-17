package no.fractal.database;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.TensorComparison.Range;
import no.fractal.TensorComparison.TensorComparator;
import no.fractal.database.Datatypes.TensorData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * this is the singelton interface used to acsess the db tensors
 * <p>
 * the reason for this interface is for caching
 */
public class TensorCalculator {

    // Number of threads per search operation
    private static int NUM_THREADS = 4;

    // How many parallel calculations which can occur at the same time
    private static final int CONCURRENT_SEARCHES = 5;

    private static final TensorCalculator instance = new TensorCalculator();

    private final Semaphore currentSearching = new Semaphore(CONCURRENT_SEARCHES);

    private final ExecutorService executor;

    private TensorCalculator() {
        NUM_THREADS = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(NUM_THREADS);
    }

    /**
     * returns instance
     *
     * @return instance
     */
    public static TensorCalculator getInstance() {
        return instance;
    }

    /**
     * Generates an array of the ranges for a given list length, for evenly distribute a list against N threads.
     *
     * @return an array with the ranges
     */
    public Range[] getRanges(int length) {
        List<Range> ranges = new ArrayList<>();
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
     * Tries to find the closest match for a given tensor against data stored in database/cache.
     * *
     *
     * @param searchData the tensor to match against.
     * @return returns the closest match
     * @throws SQLException
     */
    public ComparisonResult getClosestMatch(TensorData searchData) throws SQLException {
        try {
            currentSearching.acquire(1);
            var tensorData = TensorCache.getInstance().getCachedTensors();
            var ranges = getRanges(tensorData.size());
            Future<TensorComparator.indexResultPackage>[] res = new Future[NUM_THREADS];

            for (int i = 0; i < NUM_THREADS; i++) {
                final int idx = i;
                res[i] = executor.submit(
                        () -> TensorComparator.euclideanFast(ranges[idx], searchData.tensor, tensorData));
            }

            int bestIdx = -1;
            double bestDist = 1 << 5;
            for (int i = 0; i < NUM_THREADS; i++) {
                var resultPackage = res[i].get();
                double dist = resultPackage.result;
                if (dist < bestDist) {
                    bestDist = dist;
                    bestIdx = resultPackage.index;
                }
            }
            TensorData bestMatch = tensorData.get(bestIdx);
            return new ComparisonResult(bestMatch.id, (float) bestDist);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            currentSearching.release();
        }

    }

}
