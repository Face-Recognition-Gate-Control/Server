package no.fractal.TensorComparison;

import no.fractal.database.Datatypes.TensorData;

import java.util.ArrayList;

/**
 * holder for the tensor comparison task
 */
public class TensorComparator {
    // mebbe just return the highest will be a tad faster becaus way less writing to mem wil be purely stack based


    /**
     * compairs the provided testData with the elements in the provided data from the provided point inclusive to the end
     * @param from incluse where in test data to start compairing
     * @param testData the tensordata to compair with the dbdata
     * @param data the data to compair with
     * @return an array containing the comparison result for every comparison
     */
    public static ComparisonResult[] comparisonTask(int from, TensorData testData, ArrayList<TensorData> data) {
        return comparisonTask(from, data.size(), testData, data);
    }

    /**
     * compairs the provided testData with the elements in the provided data within the range from inclusive to exclusive
     * @param from incluse where in test data to start compairing
     * @param to exclusive where in testdata to stop compairing
     * @param testData the tensordata to compair with the dbdata
     * @param data the data to compair with
     * @return an array containing the comparison result for every comparison
     */
    public static ComparisonResult[] comparisonTask(int from, int to, TensorData testData, ArrayList<TensorData> data){
        // REMEMBER DO NOT MODIFY ARRAY FOM DATA THAT WIL CAUSE A DATA RACE

        ComparisonResult[] results = new ComparisonResult[to - from];

        for (int i = 0; i < to - from; i++) {
            results[i] = new ComparisonResult(data.get(from + i).id,data.get(from + i).euclideanDistance(testData));
        }

        return results;

    }
}
