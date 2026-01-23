package net.kogepan.clayium.capability;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

import org.jetbrains.annotations.Nullable;

/**
 * Capabilities for Clayium mod.
 */
public final class ClayiumCapabilities {

    /**
     * Capability for Clay Energy Holder.
     * Provides access to IClayEnergyHolder from block entities.
     */
    public static final BlockCapability<IClayEnergyHolder, @Nullable Direction> CLAY_ENERGY_HOLDER = BlockCapability
            .createSided(
                    ResourceLocation.fromNamespaceAndPath(Clayium.MODID, "clay_energy_holder"),
                    IClayEnergyHolder.class);
}
