package net.kogepan.clayium.utils;

/** Tier-based energy and time multipliers shared by generic crafting machines. */
public final class TierMultipliers {

    private static final double[] BASE_ENERGY = { 0, 1.0, 1.0, 1.0, 1.0, 5.0, 25.0, 0, 0, 0, 250, 0, 0, 0 };
    private static final double[] BASE_TIME = { 0, 1.0, 1.0, 1.0, 1.0, 0.25, 0.0625, 0, 0, 0, 0.01, 0, 0, 0 };

    private TierMultipliers() {}

    /**
     * Returns the generic machine energy multiplier for a tier.
     *
     * @return the configured multiplier, or {@code 1.0} for unsupported tiers
     */
    public static double getBaseEnergyMultiplier(int tier) {
        if (tier >= 0 && tier < BASE_ENERGY.length) {
            double multiplier = BASE_ENERGY[tier];
            return multiplier != 0.0D ? multiplier : 1.0D;
        }
        return 1.0D;
    }

    /**
     * Returns the generic machine time multiplier for a tier.
     *
     * @return the configured multiplier, or {@code 1.0} for unsupported tiers
     */
    public static double getBaseTimeMultiplier(int tier) {
        if (tier >= 0 && tier < BASE_TIME.length) {
            double multiplier = BASE_TIME[tier];
            return multiplier != 0.0D ? multiplier : 1.0D;
        }
        return 1.0D;
    }
}
