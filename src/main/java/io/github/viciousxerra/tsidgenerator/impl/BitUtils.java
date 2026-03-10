package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.exception.BitOverflowException;

import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.BIT_OVERFLOW_MESSAGE_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.MAX_INT_BIT_SIZE_EXCEEDED;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.MAX_LONG_BIT_SIZE_EXCEEDED;

final class BitUtils {

    static final int LONG_MAX_BIT_SIZE = 64;
    static final int INT_MAX_BIT_SIZE = 32;

    private BitUtils() {
    }

    static long getLongMaxValue(int bits) {
        if (bits > LONG_MAX_BIT_SIZE) {
            throw new BitOverflowException(MAX_LONG_BIT_SIZE_EXCEEDED);
        }
        return fillLeastSignificantBits(bits);
    }

    static int getIntMaxValue(int bits) {
        if (bits > INT_MAX_BIT_SIZE) {
            throw new BitOverflowException(MAX_INT_BIT_SIZE_EXCEEDED);
        }
        return (int) fillLeastSignificantBits(bits);
    }

    static void checkBitOverflow(long currentValue, long maxValue) {
        if (currentValue > maxValue) {
            throw new BitOverflowException(String.format(BIT_OVERFLOW_MESSAGE_TEMPLATE, currentValue, maxValue));
        }
    }

    static long shiftBits(long value, int bits) {
        return value << bits;
    }

    private static long fillLeastSignificantBits(int bits) {
        return (1L << bits) - 1;
    }
}
