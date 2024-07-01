package org.example.javageneral;

import java.math.BigDecimal;

public class BigBigDecimals {

    public static void main(String[] args) {
        BigDecimal bd = new BigDecimal("42.0");
        BigDecimal bd2 = new BigDecimal("42.00");
        BigDecimal bd3 = new BigDecimal("42.000");

        System.out.println(bd.scale());
        System.out.println(bd2.scale());
        System.out.println(bd3.scale());
    }

}
