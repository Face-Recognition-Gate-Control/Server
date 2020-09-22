package no.fractal.database.Datatypes;

import no.fractal.util.ArrayUtils;
import no.fractal.util.ArrayUtils.BigDecimalArrays;


import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.UUID;


/**
 * Representing a face tensor id pair
 *
 */
public class TensorData {
    public BigDecimal[] tensor;
    public UUID id;

    /**
     *
     * @param id
     */
    public TensorData(UUID id) {
        this(new BigDecimal[512], id);
    }

    /**
     * build
     * @param tensor the big decimal[512] tensor for the id
     * @param id the id object
     */
    public TensorData(BigDecimal[] tensor, UUID id) {
        this.tensor = tensor;
        this.id = id;
    }


    // this is probably going to change while desiding withc error algo to use
    public float euclideanDistance(TensorData other){
        return BigDecimalArrays.sumElements(
                BigDecimalArrays.elementSquare(
                        BigDecimalArrays.elementSubtract(this.tensor, other.tensor)))
                .divide(BigDecimal.valueOf(tensor.length))
                .sqrt(MathContext.DECIMAL32).floatValue();
    }


    
    
}
