package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.exception.BitOverflowException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.viciousxerra.tsidgenerator.impl.BitUtils.INT_MAX_BIT_SIZE;
import static io.github.viciousxerra.tsidgenerator.impl.BitUtils.LONG_MAX_BIT_SIZE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.BIT_OVERFLOW_MESSAGE_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.MAX_INT_BIT_SIZE_EXCEEDED;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.MAX_LONG_BIT_SIZE_EXCEEDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BitUtilsTest {
    private static final int BITS = 5;
    private static final long EXPECTED = 31;
    private static final long MAX_VALUE = 7;
    private static final long STARTING_VALUE = 13; //1101 in binary
    private static final int SHIFT_BITS = 5;
    private static final int EXPECTED_VALUE = 416; //1 1010 0000 in binary

    @Test
    @DisplayName("Test \"getLongMaxValue\" method")
    void testGetLongMaxValueMethod() {
        //When
        long actual = BitUtils.getLongMaxValue(BITS);
        //Then
        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    @DisplayName("Test \"getLongMaxValue\" method throws BitOverflowException")
    void testGetLongMaxValueMethodThrowsBitOverflowException() {
        //Given
        int bits = LONG_MAX_BIT_SIZE + 1;
        assertThatThrownBy(() -> BitUtils.getLongMaxValue(bits))
                .isExactlyInstanceOf(BitOverflowException.class)
                .hasMessage(MAX_LONG_BIT_SIZE_EXCEEDED);
    }

    @Test
    @DisplayName("Test \"getIntMaxValue\" method")
    void testGetIntMaxValueMethod() {
        //When
        int actual = BitUtils.getIntMaxValue(BITS);
        //Then
        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    @DisplayName("Test \"getIntMaxValue\" method throws BitOverflowException")
    void testGetIntMaxValueMethodThrowsBitOverflowException() {
        //Given
        int bits = INT_MAX_BIT_SIZE + 1;
        assertThatThrownBy(() -> BitUtils.getIntMaxValue(bits))
                .isExactlyInstanceOf(BitOverflowException.class)
                .hasMessage(MAX_INT_BIT_SIZE_EXCEEDED);
    }

    @Test
    @DisplayName("Test \"checkBitOverflow\" method throws BitOverflowException")
    void testCheckBitOverflowMethodThrowsBitOverflowException() {
        //Then
        assertThatThrownBy(() -> BitUtils.checkBitOverflow(MAX_VALUE + 1, MAX_VALUE))
                .isExactlyInstanceOf(BitOverflowException.class)
                .hasMessage(String.format(BIT_OVERFLOW_MESSAGE_TEMPLATE, MAX_VALUE + 1, MAX_VALUE));
    }

    @Test
    @DisplayName("Test \"checkBitOverflow\" method doesn't throw any")
    void testCheckBitOverflowMethodDoesNotThrowAny() {
        //Then
        assertThatCode(() -> BitUtils.checkBitOverflow(MAX_VALUE - 1, MAX_VALUE)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Test \"shiftBits\" method")
    void testShiftBitsMethod() {
        //When
        long actual = BitUtils.shiftBits(STARTING_VALUE, SHIFT_BITS);
        //Then
        assertThat(actual).isEqualTo(EXPECTED_VALUE);
    }

}
