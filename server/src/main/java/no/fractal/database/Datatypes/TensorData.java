package no.fractal.database.Datatypes;

import java.util.UUID;

/**
 * Represents a tensor dataset, with an unique id.
 */
public class TensorData {

    public static final int TENSOR_SIZE = 512;

    public double[] tensor;

    public UUID id;

    /**
     * @param id the id of the tensor
     */
    public TensorData(UUID id) {
        this(new double[TENSOR_SIZE], id);
    }

    /**
     * The tensor should be of size 512
     *
     * @param tensor tensor array
     */
    public TensorData(double[] tensor) {
        this(tensor, null);
    }

    /**
     * The tensor should be of size 512
     *
     * @param tensor tensor array
     * @param id     id of the tensor
     */
    public TensorData(double[] tensor, UUID id) {
        this.tensor = tensor;
        this.id = id;
    }


    /**
     * returns the array as an sql formated string that is '{a1,a2,a3,a4...,an}'
     *
     * @return
     */
    public String asSQLString() {
        StringBuilder builder = new StringBuilder();

        builder.append("'{");
        for (double val : tensor) {
            builder.append(val + ",");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("}'");
        return builder.toString();
    }

}
