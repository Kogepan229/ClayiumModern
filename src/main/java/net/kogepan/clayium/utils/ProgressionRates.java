package net.kogepan.clayium.utils;

import net.kogepan.clayium.Config;

public final class ProgressionRates {

    public static final double DEFAULT = 1.0D;
    public static final int MAX_STACK_SIZE = 64;

    private ProgressionRates() {}

    public static double current() {
        try {
            return Config.PROGRESSION_RATE.getAsDouble();
        } catch (IllegalStateException exception) {
            return Config.PROGRESSION_RATE.getDefault();
        }
    }

    public static double multiplyDouble(double value) {
        return multiplyDouble(value, current());
    }

    public static double multiplyDouble(double value, double rate) {
        return value * rate;
    }

    public static float multiplyFloat(float value) {
        return multiplyFloat(value, current());
    }

    public static float multiplyFloat(float value, double rate) {
        return (float) ((double) value * rate);
    }

    public static int multiplyInt(int value) {
        return multiplyInt(value, current());
    }

    public static int multiplyInt(int value, double rate) {
        int result = (int) ((double) value * rate);
        return result != 0 ? result : Integer.compare(value, 0);
    }

    public static long multiplyLong(long value) {
        return multiplyLong(value, current());
    }

    public static long multiplyLong(long value, double rate) {
        long result = (long) ((double) value * rate);
        return result != 0L ? result : Long.compare(value, 0L);
    }

    public static int multiplyStackSize(int value) {
        return multiplyStackSize(value, current());
    }

    public static int multiplyStackSize(int value, double rate) {
        return Math.min(multiplyInt(value, rate), MAX_STACK_SIZE);
    }

    public static double divideDouble(double value) {
        return divideDouble(value, current());
    }

    public static double divideDouble(double value, double rate) {
        return value / rate;
    }

    public static float divideFloat(float value) {
        return divideFloat(value, current());
    }

    public static float divideFloat(float value, double rate) {
        return (float) ((double) value / rate);
    }

    public static int divideInt(int value) {
        return divideInt(value, current());
    }

    public static int divideInt(int value, double rate) {
        int result = (int) ((double) value / rate);
        return result != 0 ? result : Integer.compare(value, 0);
    }

    public static long divideLong(long value) {
        return divideLong(value, current());
    }

    public static long divideLong(long value, double rate) {
        long result = (long) ((double) value / rate);
        return result != 0L ? result : Long.compare(value, 0L);
    }

    public static int divideStackSize(int value) {
        return divideStackSize(value, current());
    }

    public static int divideStackSize(int value, double rate) {
        return Math.min(divideInt(value, rate), MAX_STACK_SIZE);
    }

    public static int multiplyByProgressionRateI(int value) {
        return multiplyInt(value);
    }

    public static int divideByProgressionRateI(int value) {
        return divideInt(value);
    }
}
