package net.kogepan.clayium.utils;

/** Utility methods for converting and formatting Clay Energy units. */
public final class CEUtils {

    public static final long ONE_CE = 100_000L;
    public static final long ONE_MILLI_CE = 100L;
    public static final long TEN_MICRO_CE = 1L;

    private static final String[] CE_NUMERALS = { "u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y" };

    private CEUtils() {}

    public static long ceToLong(long ce) {
        return ce * ONE_CE;
    }

    public static long ceToLong(double ce) {
        return (long) (ce * (double) ONE_CE);
    }

    public static long milliCeToLong(long milliCe) {
        return milliCe * ONE_MILLI_CE;
    }

    public static long tenMicroCeToLong(long tenMicroCe) {
        return tenMicroCe * TEN_MICRO_CE;
    }

    public static double longToCe(long energy) {
        return energy / (double) ONE_CE;
    }

    public static double longToMilliCe(long energy) {
        return energy / (double) ONE_MILLI_CE;
    }

    public static String formatCE(long energy) {
        return formatCE(energy, true) + "CE";
    }

    public static String formatCE(long energy, boolean omitTrailingZeros) {
        long scaledEnergy = energy * 10L;
        if (scaledEnergy == 0L) {
            return "0";
        }

        String sign = "";
        if (scaledEnergy < 0L) {
            scaledEnergy = -scaledEnergy;
            sign = "-";
        }

        int magnitude = (int) Math.floor(Math.log10((double) scaledEnergy));
        int prefixIndex = Math.min(magnitude / 3, CE_NUMERALS.length - 1);
        int digits = (int) ((double) scaledEnergy * 1000.0D / Math.pow(10.0D, (double) (prefixIndex * 3)));
        return sign + formatCE(digits, prefixIndex, prefixIndex == 0 || omitTrailingZeros);
    }

    private static String formatCE(int digits, int prefixIndex, boolean omitTrailingZeros) {
        if (digits % 10 == 0 && omitTrailingZeros) {
            if (digits % 100 != 0) {
                return (digits / 1000) + "." + (digits / 100 % 10) + (digits / 10 % 10) +
                        CE_NUMERALS[prefixIndex];
            }
            return digits % 1000 != 0 ?
                    (digits / 1000) + "." + (digits / 100 % 10) + CE_NUMERALS[prefixIndex] :
                    (digits / 1000) + CE_NUMERALS[prefixIndex];
        }
        return (digits / 1000) + "." + (digits / 100 % 10) + (digits / 10 % 10) + (digits % 10) +
                CE_NUMERALS[prefixIndex];
    }
}
