package com.xonro.project.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数学工具类
 */

public class MathUtil {
    /**
     * 加法
     *
     * @param a 被加数
     * @param b 加数
     * @return 结果
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add( b );
    }

    /**
     * 减法
     *
     * @param a 被减数
     * @param b 减数
     * @return 结果
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract( b );
    }

    /**
     * 乘法
     *
     * @param a 被乘数
     * @param b 乘数
     * @return 结果
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply( b );
    }

    /**
     * 除法
     *
     * @param a 被除数
     * @param b 除数
     * @return 结果
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (0 == a.doubleValue()) { //被除数是零,结果一定为零
            return new BigDecimal( 0 );
        }
        return a.divide( b, 8, RoundingMode.HALF_UP );
    }

    /**
     * 超过8位就用科学计数法
     *
     * @param big 传进一个需要格式化的BigDecimal
     * @return 返回格式化后字符串
     */
    public static String formatData(BigDecimal big) {
        String strAll = big.stripTrailingZeros().toPlainString();
        int strLength = strAll.contains( "." ) ? strAll.length() - 1 : strAll.length();
        if (strLength > 8) {
            String string = big.stripTrailingZeros().toString();
            if (string.contains( "E" )) {
                int index = string.indexOf( "E" );
                if ((string.charAt( index + 1 ) + "").equals( "-" )) {
                    return string;
                } else {
                    return new StringBuilder().append( string.substring( 0, index + 1 ) )
                            .append( string.substring( index + 2, string.length() ) ).toString();
                }
            } else {
                return string;
            }
        } else {
            return strAll;
        }
    }

    /**
     * 保留小数点后面几位
     *
     * @param big
     * @param bit 要保留的位数
     * @return
     */
    public static BigDecimal formatKeepDigits(BigDecimal big, int bit) {
        return big.setScale( bit, BigDecimal.ROUND_HALF_UP );
    }
}