package no.fractal.database;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.TensorComparison.TensorComparator;
import no.fractal.database.Datatypes.TensorData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DbTensorCash {

    private static DbTensorCash instance;

    public static DbTensorCash getInstance() {
        if (instance == null){
            instance = new DbTensorCash();
        }
        return instance;
    }


    private static final int NUM_THREADS = 6;
    private static final int CONCURRENT_SEARCHES = 5;

    private ExecutorService executor;

    private ArrayList<TensorData> tensorData;
    private Semaphore updatePending = new Semaphore(1);
    private Semaphore currentSearching = new Semaphore(CONCURRENT_SEARCHES);

    public DbTensorCash() {

        executor = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            tensorData = GateQueries.getWorkerResourceManagerById();

        } catch (Exception e){e.printStackTrace();}


    }

    public ComparisonResult getClosestMatch(TensorData searchData){
        try{
            updatePending.acquire();
            currentSearching.acquire(1);
            updatePending.release();

            int interval = Math.floorDiv(tensorData.size(), NUM_THREADS);

            //shit solution
            ArrayList<Future<ComparisonResult[]>> res = new ArrayList<>();

            for (int i = 0; i < NUM_THREADS - 1; i++) {
                int start = (i - 1) * interval;
                int end = i * interval;
                res.add(executor.submit(() ->TensorComparator.comparisonTask(start, end,searchData, tensorData)));
            }

            res.add(executor.submit(() ->TensorComparator.comparisonTask((NUM_THREADS - 1) * interval,searchData, tensorData)));

            ArrayList<ComparisonResult> results = res.stream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null; // lets hope this does not happen
            }).flatMap(Stream::of).collect(Collectors.toCollection(ArrayList::new));

            results.sort((o1, o2) -> (int) (o1.diffValue-o2.diffValue) * 1000); // uhh this may be low so we bump the comma


            currentSearching.release();
            return results.get(0);

        } catch (Exception  e){
            currentSearching.release();
            e.printStackTrace();

            return null;
        }

    }



    public void addDatapoint(TensorData newData){

        try{
            updatePending.acquire();
            currentSearching.acquire(CONCURRENT_SEARCHES);


            // addynes goes here




            currentSearching.release(CONCURRENT_SEARCHES);
            updatePending.release();
        } catch (Exception  e){e.printStackTrace();}



    }



}
