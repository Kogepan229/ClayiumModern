package net.kogepan.clayium.utils;

import net.kogepan.clayium.registries.ClayiumDataMaps;

import net.minecraft.world.item.ItemStack;

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

    private static final String[] CE_NUMERALS = { "u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y" };

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

    /**
     * Formats ClayEnergy internal long value to a string with units.
     * Based on UtilLocale.ClayEnergyNumeral implementation.
     *
     * @param energy The long value of energy
     * @return Formatted string
     */
    public static String formatCE(long energy) {
        return formatCE(energy, true) + "CE";
    }

    /**
     * Formats ClayEnergy internal long value to a string with units.
     * Based on UtilLocale.ClayEnergyNumeral implementation.
     *
     * @param energy The long value of energy
     * @param flag   Whether to omit decimal zeros
     * @return Formatted string
     */
    public static String formatCE(long energy, boolean flag) {
        long n = energy * 10L;
        String s = "";
        if (n == 0L) {
            return "0";
        }
        if (n < 0L) {
            n = -n;
            s = "-";
        }

        int k = (int) Math.floor(Math.log10((double) n));
        int p = Math.min(k / 3, CE_NUMERALS.length - 1);
        int d = (int) ((double) n * 1000.0D / Math.pow(10.0D, (double) (p * 3)));
        return s + formatCEHelper(d, p, p == 0 || flag);
    }

    private static String formatCEHelper(int d, int p, boolean flag) {
        if (d % 10 == 0 && flag) {
            if (d % 100 != 0) {
                return (d / 1000) + "." + (d / 100 % 10) + (d / 10 % 10) + CE_NUMERALS[p];
            } else {
                return (d % 1000 != 0) ? (d / 1000) + "." + (d / 100 % 10) + CE_NUMERALS[p] :
                        (d / 1000) + CE_NUMERALS[p];
            }
        } else {
            return (d / 1000) + "." + (d / 100 % 10) + (d / 10 % 10) + (d % 10) + CE_NUMERALS[p];
        }
    }

    /**
     * Gets the Clay Energy of an item stack using Data Maps.
     *
     * @param stack The item stack to check
     * @return The Clay Energy value (internal long), or 0 if not defined
     */
    public static long getItemEnergy(ItemStack stack) {
        if (stack.isEmpty())
            return 0;
        Long energy = stack.getItemHolder().getData(ClayiumDataMaps.CLAY_ENERGY);
        return energy != null ? energy : 0;
    }
}
