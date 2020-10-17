package no.fractal.TensorComparison;

import no.fractal.database.Datatypes.TensorData;

import java.util.List;

/**
 * holder for the tensor comparison task
 */
public class TensorComparator {

    /**
     * Calculates the euclidean distance between the testData tensor and all
     * elements in provided tensor data list, starting within the provided range
     *
     * @param range     the range of the db data to move over
     * @param queryData tensor for calculating distance with
     * @param dbData    tensor list to to calculate against
     * @return the distance and range for the element with the closest range
     */
    public static indexResultPackage euclideanFast(Range range, double[] queryData, List<TensorData> dbData) {

        int bestIdx = -1;
        double bestDist = 1 << 5;

        int toIndex = range.to;

        if (range.to == -1) {
            toIndex = dbData.size() - 1;
        }

        for (int i = range.from; i < toIndex; i++) {
            double dist = getEuclideanDistance(queryData, dbData.get(i));
            if (dist < bestDist) {
                bestDist = dist;
                bestIdx = i;
            }

        }
        return new indexResultPackage(bestDist, bestIdx);
    }

    /**
     * calculates the euclidian distance between two vectors
     *
     * @param alpha one of the vectors
     * @param beta  the other one
     * @return the euclidian distance between the vectors
     */
    public static double getEuclideanDistance(double[] alpha, TensorData beta) {
        float distance = 0;
        for (int i = 512; --i >= 0; ) {
            double dif = alpha[i] - beta.tensor[i];
            distance += (dif * dif);
        }
        return Math.sqrt(distance);
    }

    /**
     * Dataholder to return the distance and index
     */
    public static class indexResultPackage {
        public double result;
        public int index;

        public indexResultPackage(double result, int index) {
            this.result = result;
            this.index = index;
        }
    }
}
