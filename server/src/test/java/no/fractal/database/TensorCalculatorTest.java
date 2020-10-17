package no.fractal.database;

import no.fractal.TensorComparison.ComparisonResult;
import no.fractal.database.Datatypes.TensorData;
import no.fractal.util.SimpleStopwatch;

class TensorCalculatorTest {

    @org.junit.jupiter.api.Test
    void getClosestMatch() throws InterruptedException {
        SimpleStopwatch.start("build");
        TensorData pattern = TensorCache.getInstance().generateRandomTensorData(0);
        TensorCache.getInstance().setTestTensor(pattern);
        TensorCache.getInstance().generateRandomCache(1000000);
        SimpleStopwatch.stop("build", true);
        SimpleStopwatch.start("tot");
        int nThreads = 20;
        Thread[] threads = new Thread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            MyThread mt = new MyThread(pattern);
            Thread t = new Thread(mt);
            threads[i] = t;
            t.start();
        }

        for (var t : threads) {
            t.join();
        }
        SimpleStopwatch.stop("tot", true);
    }


    static class MyThread implements Runnable {

        private TensorData pattern;

        public MyThread(TensorData pattern) {
            this.pattern = pattern;
        }

        public void run() {
            ComparisonResult result = null;
            SimpleStopwatch.start(String.format("THREAD-%s", Thread.currentThread().getId()));
            try {
                result = TensorCalculator.getInstance().getClosestMatch(pattern);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.printf("correct is: %s\n" + "guess is: %s\n", pattern.id, result.id);
            SimpleStopwatch.stop(String.format("THREAD-%s", Thread.currentThread().getId()), true);
        }
    }

}