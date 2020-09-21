package no.fractal.database.Datatypes;

import no.fractal.util.ArrayUtils;
import no.fractal.util.ArrayUtils.BigDecimalArrays;


import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.UUID;

public class TensorData {
    public BigDecimal[] tensor;
    public UUID id;


    public TensorData(UUID id) {
        this(new BigDecimal[512], id);
    }

    public TensorData(BigDecimal[] tensor, UUID id) {
        this.tensor = tensor;
        this.id = id;
    }

    public void setValue(BigDecimal value, int index){
        tensor[index] = value;
    }

    public float euclideanDistance(TensorData other){
        return BigDecimalArrays.sumElements(
                BigDecimalArrays.elementSquare(
                        BigDecimalArrays.elementSubtract(this.tensor, other.tensor)))
                .divide(BigDecimal.valueOf(tensor.length))
                .sqrt(MathContext.DECIMAL32).floatValue();
    }
    
    
}
