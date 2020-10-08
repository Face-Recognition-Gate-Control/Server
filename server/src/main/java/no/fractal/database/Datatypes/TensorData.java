package no.fractal.database.Datatypes;

import no.fractal.util.ArrayUtils.BigDecimalArrays;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.UUID;

/**
 * Represents a tensor dataset, with an unique id.
 */
public class TensorData {

    private static final int TENSOR_SIZE = 512;

    public BigDecimal[] tensor;

    public UUID id;

    /**
     * 
     * @param id the id of the tensor
     */
    public TensorData(UUID id) {
        this(new BigDecimal[TENSOR_SIZE], id);
    }

    /**
     * The tensor should be of size 512
     *  @param tensor tensor array
     * 
     */
    public TensorData(BigDecimal[] tensor) {
        this(tensor, null);
    }

    /**
     * The tensor should be of size 512
     * 
     * @param tensor tensor array
     * @param id     id of the tensor
     */
    public TensorData(BigDecimal[] tensor, UUID id) {
        this.tensor = tensor;
        this.id = id;
    }

    /**
     * Calculates the euclidean distance between this tensor and another. Returning
     * the result(distance).
     * 
     * @param other tensor to calculate distance against
     * @return distance
     */
    public float euclideanDistance(TensorData other) {
        return BigDecimalArrays
                .sumElements(
                        BigDecimalArrays.elementSquare(BigDecimalArrays.elementSubtract(this.tensor, other.tensor)))
                .divide(BigDecimal.valueOf(tensor.length)).sqrt(MathContext.DECIMAL32).floatValue();
    }

    /**
     * returns the array as an sql formated string that is '{a1,a2,a3,a4...,an}'
     * @return
     */
    public String asSQLString(){
        StringBuilder builder = new StringBuilder();

        builder.append("'{");
        for (BigDecimal val : tensor){
            builder.append(val +",");
        }

        builder.deleteCharAt(builder.length() -1);
        builder.append("}'");
        return builder.toString();
    }

}
