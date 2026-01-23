package net.kogepan.clayium.capability;

import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.NotNull;

/**
 * A Clay Energy (CE) storage, similar to the {@link net.neoforged.neoforge.energy.IEnergyStorage}.
 *
 * Energy unit conversion:
 * - 1 CE = 100,000 (long value)
 * - 1 mCE = 100 (long value)
 * - 10 uCE = 1 (long value)
 * - Minimum unit: 10 uCE = 1 (long value)
 */
public interface IClayEnergyHolder {

    /**
     * CE is generated from Items that has IClayEnergyProvider capability.
     * This handler is an inventory for that.
     */
    @NotNull
    IItemHandler getEnergizedClayItemHandler();

    /**
     * Gets the stored Clay Energy.
     * 
     * @return The stored energy (1 CE = 100,000, 1 mCE = 100, 10 uCE = 1)
     */
    long getEnergyStored();

    /**
     * Drains energy from the holder.
     * 
     * @param amount   The amount of energy to drain (1 CE = 100,000, 1 mCE = 100, 10 uCE = 1)
     * @param simulate If true, the drain will only be simulated
     * @return true if energy can/was drained, otherwise false
     */
    boolean drawEnergy(long amount, boolean simulate);

    /**
     * Adds energy to the holder.
     * Low tier machines can generate energy from clicking GUI button or waterwheels.
     * This method is used in those cases.
     * 
     * @param amount The amount of energy to add (1 CE = 100,000, 1 mCE = 100, 10 uCE = 1)
     */
    void addEnergy(long amount);

    /**
     * Checks if the holder has enough energy to perform an operation.
     * If not enough, you should try to consume an Energized Clay itemStack from the energizedClayItemHandler.
     * 
     * @param amount The amount of energy to check (1 CE = 100,000, 1 mCE = 100, 10 uCE = 1)
     * @return true if the holder has enough energy
     */
    boolean hasEnoughEnergy(long amount);
}
