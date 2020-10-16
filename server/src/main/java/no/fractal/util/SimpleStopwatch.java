package no.fractal.util;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SimpleStopwatch {

    private static HashMap<String, TimeRange> timeMap = new HashMap<>();

    public static void start(String key){
        TimeRange t = new TimeRange();
        t.start = System.nanoTime();

        if (timeMap.containsKey(key)){
            System.out.printf("TIMER WITH KEY %s EXISTS\n", key);
        } else {
            timeMap.put(key,t );
        }
    }

    public static void stop(String key, boolean display){
        stop(key);
        if (display){
            show(key);
        }
    }
    public static void stop(String key){

        if (timeMap.containsKey(key)){
            timeMap.get(key).stop = System.nanoTime();
        } else {
            System.out.printf("TIMER WITH KEY %s EXISTS\n", key);
        }
    }

    public static void show(String key) {
        show(key, key);
    }

   public static void show(String key, String name){
        if (timeMap.containsKey(key)){
            TimeRange unit = timeMap.get(key);
            if(unit.stop != -1 &&  unit.start != -1  ){

                long delta = unit.stop - unit.start;
                System.out.println("-----------------------------------");
                System.out.printf("Timings for block %s\n" +
                                          "in seconds : %s \n" +
                                          "in millisec: %s \n" +
                                          "in microsec: %s \n" +
                                          "in nanons  : %s \n",name, seconds.convert(delta,nanos), millis.convert(delta, nanos), microsecond.convert(delta, nanos), delta);
                System.out.println("-----------------------------------");

            }
        }
   }
    private static TimeUnit seconds = TimeUnit.SECONDS;
    private static TimeUnit millis = TimeUnit.MILLISECONDS;
    private static TimeUnit microsecond = TimeUnit.MICROSECONDS;
    private static TimeUnit nanos = TimeUnit.NANOSECONDS;

    private static class TimeRange{
        public long start = -1;
        public long stop = -1;
    }
}

//                System.out.println("-----------------------------------");
//                        System.out.printf("Timings for block {}\n", name);
//                        System.out.printf("in seconds : {} \n",seconds.convert(delta,nanos));
//                        System.out.printf("in millisec: {} \n",millis.convert(delta, nanos));
//                        System.out.printf("in nanons  : {} \n", delta);
//                        System.out.println("-----------------------------------");