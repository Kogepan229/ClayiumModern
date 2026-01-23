package net.kogepan.clayium.utils;

/**
 * Utility class for Clay Energy unit conversions and constants.
 *
 * Energy unit conversion:
 * - 1 CE = 100,000 (long value)
 * - 1 mCE = 100 (long value)
 * - 10 uCE = 1 (long value)
 * - Minimum unit: 10 uCE = 1 (long value)
 */
public final class CEUtils {

    private CEUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * 1 CE in long value (100,000)
     */
    public static final long ONE_CE = 100_000L;

    /**
     * 1 mCE in long value (100)
     */
    public static final long ONE_MILLI_CE = 100L;

    /**
     * 10 uCE in long value (1) - minimum unit
     */
    public static final long TEN_MICRO_CE = 1L;

    /**
     * Converts CE to long value.
     * 
     * @param ce The amount in CE
     * @return The long value (ce * 100,000)
     */
    public static long ceToLong(long ce) {
        return ce * ONE_CE;
    }

    /**
     * Converts mCE to long value.
     * 
     * @param mce The amount in mCE
     * @return The long value (mce * 100)
     */
    public static long milliCeToLong(long mce) {
        return mce * ONE_MILLI_CE;
    }

    /**
     * Converts 10uCE units to long value.
     * 
     * @param tenMicroCe The amount in 10uCE units
     * @return The long value (same as input, since 10uCE = 1)
     */
    public static long tenMicroCeToLong(long tenMicroCe) {
        return tenMicroCe * TEN_MICRO_CE;
    }

    /**
     * Converts long value to CE.
     * 
     * @param energy The long value
     * @return The amount in CE (energy / 100,000)
     */
    public static double longToCe(long energy) {
        return energy / (double) ONE_CE;
    }

    /**
     * Converts long value to mCE.
     * 
     * @param energy The long value
     * @return The amount in mCE (energy / 100)
     */
    public static double longToMilliCe(long energy) {
        return energy / (double) ONE_MILLI_CE;
    }
}
