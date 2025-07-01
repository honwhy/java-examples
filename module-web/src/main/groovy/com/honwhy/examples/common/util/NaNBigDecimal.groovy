
package com.honwhy.examples.common.util

class NaNBigDecimal extends BigDecimal {

    private static final NaNBigDecimal NaN = new NaNBigDecimal()

    // 私有默认构造函数，用于创建 NaN 实例
    private NaNBigDecimal() {
        super("0")
    }

    // 新增：支持 new NaNBigDecimal(BigDecimal val)
    NaNBigDecimal(BigDecimal val) {
        super(val != null ? val.unscaledValue() : ZERO.unscaledValue(), val != null ? val.scale() : 0)
    }

    static NaNBigDecimal valueOf(String val) {
        if (val == null || val.trim().isEmpty()) {
            return NaN
        }
        try {
            return new NaNBigDecimal(new BigDecimal(val))
        } catch (NumberFormatException ignored) {
            return NaN
        }
    }

    // 支持 new BigDecimal("") -> NaNBigDecimal.NaN
    static NaNBigDecimal fromString(String val) {
        return valueOf(val)
    }

    // 运算符重载：加法
    def plus(NaNBigDecimal other) {
        if (this.is(NaN) || other.is(NaN)) return NaN
        return new NaNBigDecimal(super.add(other as BigDecimal))
    }

    // 运算符重载：减法
    def minus(NaNBigDecimal other) {
        if (this.is(NaN) || other.is(NaN)) return NaN
        return new NaNBigDecimal(super.subtract(other as BigDecimal))
    }

    // 运算符重载：乘法
    def multiply(NaNBigDecimal other) {
        if (this.is(NaN) || other.is(NaN)) return NaN
        return new NaNBigDecimal(super.multiply(other as BigDecimal))
    }

    // 运算符重载：除法
    def div(NaNBigDecimal other) {
        if (this.is(NaN) || other.is(NaN)) return NaN
        return new NaNBigDecimal(super.divide(other as BigDecimal, ROUND_HALF_UP))
    }

    // 运算符重载：等于
    boolean equals(Object obj) {
        if (obj == null) return false
        if (!(obj instanceof NaNBigDecimal)) return false
        return this.is(NaN) && obj.is(NaN) || super.equals(obj)
    }

    // 覆盖 hashCode
    int hashCode() {
        return this.is(NaN) ? Double.NaN.hashCode() : super.hashCode()
    }

    String toString() {
        return this.is(NaN) ? "NaN" : super.toString()
    }
}