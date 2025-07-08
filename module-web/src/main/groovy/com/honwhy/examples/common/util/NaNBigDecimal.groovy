package com.honwhy.examples.common.util

/**
 * 运算符重载，https://blog.csdn.net/a568478312/article/details/79910691
 * 元编程：https://www.jianshu.com/p/2c17a50ff7f1
 */
class NaNBigDecimal extends NaNNumber implements GroovyInterceptable {

    NaNBigDecimal(def val) {
        super(val)
    }

    @Override
    Object invokeMethod(String name, Object args) {
        if (!(args instanceof Object[])) {
            throw new IllegalArgumentException("Invalid arguments")
        }

        switch(name) {
            case 'equals':
                return super.handleEquals(args[0])
            case 'compareTo':
                return super.handleComparison({ BigDecimal other -> value > other }, args[0])
            case 'isGreaterThan':
                return super.handleComparison({ BigDecimal other -> value > other }, args[0])
            case 'isGreaterThanOrEqual':
                return super.handleComparison({ BigDecimal other -> value >= other }, args[0])
            case 'isLessThan':
                return super.handleComparison({ BigDecimal other -> value < other }, args[0])
            case 'isLessThanOrEqual':
                return super.handleComparison({ BigDecimal other -> value <= other }, args[0])
            default:
                throw new MissingMethodException(name, this.class, args)
        }
    }

    static void main(String[] args) {
        def a = new NaNBigDecimal("")
        def b = new NaNBigDecimal(10)

        assert !(a == 10)
        assert a != 10
        assert !a.compareTo(10, '>')
        assert !a.compareTo(10, '>=')
        assert !a.compareTo(10, '<')
        assert !a.compareTo(10, '<=')

        assert a != b
        assert b == 10
    }
}