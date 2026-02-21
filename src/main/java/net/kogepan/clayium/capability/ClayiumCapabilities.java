package net.kogepan.clayium.capability;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

import org.jetbrains.annotations.Nullable;

/**
 * Capabilities for Clayium mod.
 */
public final class ClayiumCapabilities {

    /**
     * Capability for item filter. Provides IItemFilter from filter items (Simple, Fuzzy, UnlocalizedName).
     */
    public static final ItemCapability<IItemFilter, Void> ITEM_FILTER = ItemCapability
            .createVoid(ResourceLocation.fromNamespaceAndPath(Clayium.MODID, "item_filter"), IItemFilter.class);

    /**
     * Capability for Clay Energy Holder.
     * Provides access to IClayEnergyHolder from block entities.
     */
    public static final BlockCapability<IClayEnergyHolder, @Nullable Direction> CLAY_ENERGY_HOLDER = BlockCapability
            .createSided(
                    ResourceLocation.fromNamespaceAndPath(Clayium.MODID, "clay_energy_holder"),
                    IClayEnergyHolder.class);

    /**
     * Capability for Clay Laser Source.
     * Provides access to IClayLaserSource from block entities.
     */
    public static final BlockCapability<IClayLaserSource, @Nullable Direction> CLAY_LASER_SOURCE = BlockCapability
            .createSided(
                    ResourceLocation.fromNamespaceAndPath(Clayium.MODID, "clay_laser_source"),
                    IClayLaserSource.class);

    /**
     * Capability for Clay Laser Acceptor.
     * Provides access to IClayLaserAcceptor from block entities.
     */
    public static final BlockCapability<IClayLaserAcceptor, @Nullable Direction> CLAY_LASER_ACCEPTOR = BlockCapability
            .createSided(
                    ResourceLocation.fromNamespaceAndPath(Clayium.MODID, "clay_laser_acceptor"),
                    IClayLaserAcceptor.class);

    /**
     * Capability for applying item filters per face on ClayContainers.
     * When the player uses a filter item on a block face, this capability is used to set/get the filter.
     */
    public static final BlockCapability<IItemFilterApplicatable, @Nullable Direction> ITEM_FILTER_APPLICATABLE = BlockCapability
            .createSided(
                    ResourceLocation.fromNamespaceAndPath(Clayium.MODID, "item_filter_applicatable"),
                    IItemFilterApplicatable.class);
}
