package net.kogepan.clayium.utils;

/**
 * Tier-based energy and time multipliers for crafting machines. Each machine type (e.g. Smelter,
 * base/generic) has its own multiplier arrays; only the "base" set is shared here for
 * Condenser/Grinder. Smelter uses its own constants in
 * {@link net.kogepan.clayium.blockentities.trait.SmelterRecipeLogic}.
 */
public final class TierMultipliers {

    private TierMultipliers() {}

    /**
     * Base (tierGeneric) energy multiplier per tier (tier 0–13). Used by Condenser, Grinder,
     * Centrifuge.
     */
    private static final double[] BASE_ENERGY = { 0, 1.0, 1.0, 1.0, 1.0, 5.0, 25.0, 0, 0, 0, 250, 0, 0, 0 };

    /**
     * Base (tierGeneric) time multiplier per tier (tier 0–13). From
     * {@code CRAFTING_BASE_TIME_MULTIPLIER_DEFAULT}.
     */
    private static final double[] BASE_TIME = { 0, 1.0, 1.0, 1.0, 1.0, 0.25, 0.0625, 0, 0, 0, 0.01, 0, 0, 0 };

    /**
     * Returns the base energy multiplier for the given tier. Returns 1.0 if tier is out of
     * range or the configured value is 0 (tier not used for this machine type).
     */
    public static double getBaseEnergyMultiplier(int tier) {
        if (tier >= 0 && tier < BASE_ENERGY.length) {
            double v = BASE_ENERGY[tier];
            return v != 0 ? v : 1.0;
        }
        return 1.0;
    }

    /**
     * Returns the base time multiplier for the given tier. Returns 1.0 if tier is out of range
     * or the configured value is 0.
     */
    public static double getBaseTimeMultiplier(int tier) {
        if (tier >= 0 && tier < BASE_TIME.length) {
            double v = BASE_TIME[tier];
            return v != 0 ? v : 1.0;
        }
        return 1.0;
    }
}
