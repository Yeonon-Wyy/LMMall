package top.yeonon.lmmall.utils;

import java.math.BigDecimal;

/**
 * @Author yeonon
 * @date 2018/4/7 0007 19:04
 **/
public class BigDecimalUtil {

    private BigDecimalUtil() {}

    public static BigDecimal add(double a1, double a2) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(a1));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(a2));
        return bigDecimal1.add(bigDecimal2);
    }

    public static BigDecimal sub(double a1, double a2) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(a1));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(a2));
        return bigDecimal1.subtract(bigDecimal2);
    }

    public static BigDecimal mul(double a1, double a2) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(a1));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(a2));
        return bigDecimal1.multiply(bigDecimal2);
    }

    public static BigDecimal div(double a1, double a2) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(a1));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(a2));
        return bigDecimal1.divide(bigDecimal2, 2, BigDecimal.ROUND_HALF_UP);
    }
}
