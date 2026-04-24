package net.kogepan.clayium.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgressionRatesTest {

    @Test
    void defaultRateLeavesValuesUnchanged() {
        assertEquals(3.0D, ProgressionRates.multiplyDouble(3.0D, 1.0D));
        assertEquals(3.0F, ProgressionRates.divideFloat(3.0F, 1.0D));
        assertEquals(3, ProgressionRates.multiplyInt(3, 1.0D));
        assertEquals(3L, ProgressionRates.divideLong(3L, 1.0D));
    }

    @Test
    void halfRateSlowsCostsAndPreservesNonzeroMultiplication() {
        assertEquals(6, ProgressionRates.divideInt(3, 0.5D));
        assertEquals(1, ProgressionRates.multiplyInt(1, 0.5D));
    }

    @Test
    void doubleRateSpeedsCostsAndMultipliesProgress() {
        assertEquals(1, ProgressionRates.divideInt(3, 2.0D));
        assertEquals(6, ProgressionRates.multiplyInt(3, 2.0D));
    }

    @Test
    void integerHelpersPreserveNonzeroSign() {
        assertEquals(1, ProgressionRates.multiplyInt(1, 0.001D));
        assertEquals(-1, ProgressionRates.multiplyInt(-1, 0.001D));
        assertEquals(1L, ProgressionRates.divideLong(1L, 9999.0D));
        assertEquals(-1L, ProgressionRates.divideLong(-1L, 9999.0D));
    }

    @Test
    void stackSizeHelpersCapAtVanillaStackLimit() {
        assertEquals(64, ProgressionRates.multiplyStackSize(40, 2.0D));
        assertEquals(64, ProgressionRates.divideStackSize(40, 0.5D));
    }
}
