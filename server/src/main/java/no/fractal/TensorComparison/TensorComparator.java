package no.fractal.TensorComparison;

import no.fractal.database.Datatypes.TensorData;

import java.util.ArrayList;

public class TensorComparator {

    public static ComparisonResult[] comparisonTask(int from, TensorData testData, ArrayList<TensorData> data) {
        return comparisonTask(from, data.size(), testData, data);
    }

    public static ComparisonResult[] comparisonTask(int from, int to, TensorData testData, ArrayList<TensorData> data){
        // REMEMBER DO NOT MODIFY ARRAY FOM DATA THAT WIL CAUSE A DATA RACE

        ComparisonResult[] results = new ComparisonResult[to - from];

        for (int i = 0; i < to - from; i++) {
            results[i] = new ComparisonResult(data.get(from + i).id,data.get(from + i).euclideanDistance(testData));
        }

        return results;

    }
}
