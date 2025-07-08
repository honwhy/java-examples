package com.honwhy.examples.common.util

class NaNNumber {
    private BigDecimal value
    private boolean isNaN

    NaNNumber(def val) {
        if (val == null || (val instanceof String && val.trim().isEmpty())) {
            this.isNaN = true
            this.value = null
        } else {
            this.isNaN = false
            this.value = new BigDecimal(val.toString())
        }
    }

    Boolean handleEquals(other) {
        if (isNaN) return false
        if (other instanceof NaNNumber) {
            return !other.isNaN && value.compareTo(other.value) == 0
        }
        BigDecimal otherVal = convertOther(other)
        return otherVal != null && value.compareTo(otherVal) == 0
    }
    Boolean compareTo(other, String sign) {
        if (isNaN) return false
        if (other == null) return false
        if (other instanceof NaNNumber && other.isNaN) {
            return false
        }
        BigDecimal otherVal = convertOther(other)
        switch (sign) {
            case ">":
                return value.compareTo(otherVal) > 0
            case ">=":
                return value.compareTo(otherVal) >= 0
            case "<":
                return value.compareTo(otherVal) < 0
            case "<=":
                return value.compareTo(otherVal) <= 0
            default:
                return false
        }
    }
    private Boolean handleComparison(Closure<Boolean> comparison, other) {
        if (isNaN) return false
        BigDecimal otherVal = convertOther(other)
        return otherVal != null ? comparison(otherVal) : false
    }

    static BigDecimal convertOther(other) {
        if (other == null) return null

        if (other instanceof NaNNumber) {
            return other.isNaN ? null : other.value
        }

        try {
            return new BigDecimal(other.toString())
        } catch (NumberFormatException e) {
            return null
        }
    }
}