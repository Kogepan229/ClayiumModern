package net.kogepan.clayium.integration.jade;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.capability.IClayLaserSource;
import net.kogepan.clayium.laser.Laser;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * Jade component provider that displays the current laser energy output
 * for block entities implementing {@link IClayLaserSource}.
 */
public enum ClayLaserSourceComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    INSTANCE;

    private static final ResourceLocation CLAY_LASER_SOURCE_ENERGY = ResourceLocation.fromNamespaceAndPath(
            Clayium.MODID, "clay_laser_source_energy");
    private static final String KEY_ENERGY = "clayium_laser_energy";

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getServerData().contains(KEY_ENERGY)) {
            long energy = accessor.getServerData().getLong(KEY_ENERGY);
            tooltip.add(Component.translatable("gui.clayium.jade.laser_energy", formatEnergy(energy)));
        }
    }

    @Override
    public void appendServerData(net.minecraft.nbt.CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof IClayLaserSource source) {
            Laser laser = source.getIrradiatingLaser();
            if (laser != null) {
                data.putLong(KEY_ENERGY, laser.energy());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return CLAY_LASER_SOURCE_ENERGY;
    }

    private static String formatEnergy(long energy) {
        if (energy >= 1_000_000) {
            return String.format("%.2fM", energy / 1_000_000.0);
        }
        if (energy >= 1_000) {
            return String.format("%.2fK", energy / 1_000.0);
        }
        return String.valueOf(energy);
    }
}
