package no.fractal.TensorComparison;

import no.fractal.database.Datatypes.TensorData;

import java.util.ArrayList;

/**
 * holder for the tensor comparison task
 */
public class TensorComparator {
    // mebbe just return the highest will be a tad faster becaus way less writing to
    // mem wil be purely stack based

    /**
     * Calculates the euclidean distance between the testData tensor and all
     * elements in provided tensor data list, starting from given index(inclusive)
     * to the end.
     * 
     * @param from     incluse where in test data to start calculating
     * @param testData tensor for calculating distance with
     * @param data     tensor list to to calculate against
     * @return an array containing the result for every disntance calculations
     */
    public static ComparisonResult[] euclideanDistanceCalculationTask(int from, TensorData testData,
            ArrayList<TensorData> data) {
        return euclideanDistanceCalculationTask(from, data.size(), testData, data);
    }

    /**
     * /** Calculates the euclidean distance between the testData tensor and all
     * elements in provided tensor data list, starting from given index(inclusive)
     * to given lenth (exclusive)
     * 
     * @param from     incluse where in test data to start calculating
     * @param to       exclusive where in the test data to stop calculating
     * @param testData tensor for calculating distance with
     * @param data     tensor list to to calculate against
     * @return an array containing the result for every disntance calculations
     */
    public static ComparisonResult[] euclideanDistanceCalculationTask(int from, int to, TensorData testData,
            ArrayList<TensorData> data) {
        // DO NOT MODIFY THE DATA ARRAY - CAN CAUSE DATA RACE ?!

        ComparisonResult[] results = new ComparisonResult[to - from];

        for (int i = 0; i < to - from; i++) {
            results[i] = new ComparisonResult(data.get(from + i).id, data.get(from + i).euclideanDistance(testData));
        }

        return results;

    }
}
