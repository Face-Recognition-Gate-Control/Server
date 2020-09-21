package no.fractal.util;

import java.io.BufferedInputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;

public class ArrayUtils {

    public static class BigDecimalArrays{
        public static BigDecimal[] elementSubtract(BigDecimal[] alpha, BigDecimal[] beta){
            // shold probably test for equal size
            BigDecimal[] outArray = new BigDecimal[alpha.length];
            for (int i = 0; i < alpha.length; i++) {
                outArray[i] = alpha[i].subtract(beta[i]);
            }
            return outArray;
        }

        public static BigDecimal[] elementSquare(BigDecimal[] alpha){
            BigDecimal[] outArray = new BigDecimal[alpha.length];
            for (int i = 0; i < alpha.length; i++) {
                outArray[i] = alpha[i].multiply(alpha[i]);
            }
            return outArray;
        }

        public static BigDecimal sumElements(BigDecimal[] alpha){
            BigDecimal returnValue = BigDecimal.valueOf(0L);
            for (int i = 0; i < alpha.length; i++) {
                returnValue = returnValue.add(alpha[i]);
            }
            return returnValue;
        }



    }


}
