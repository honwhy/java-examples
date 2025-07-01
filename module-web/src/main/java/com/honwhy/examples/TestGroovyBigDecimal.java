package com.honwhy.examples;

import com.honwhy.examples.common.util.NaNBigDecimal;

public class TestGroovyBigDecimal {
    public static void main(String[] args) {
        NaNBigDecimal a = NaNBigDecimal.fromString("");
        NaNBigDecimal b = NaNBigDecimal.fromString("123.45");

        System.out.println(a); // 输出: NaN
        System.out.println(a.plus(b)); // 输出: NaN
        System.out.println(b.plus(b)); // 输出: 246.90
        System.out.println(a.equals(b)); // 输出: false
        System.out.println(!a.equals(b)); // 输出: true
        System.out.println(a.equals(NaNBigDecimal.fromString(""))); // 输出: true
    }
}
