package net.kogepan.clayium.laser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/**
 * Handles block-irradiation logic when the laser does not hit a laser acceptor.
 */
@FunctionalInterface
public interface LaserBlockIrradiationHandler {

    /**
     * Called every tick while a block (without laser acceptor) is irradiated.
     *
     * @param level                 The world level
     * @param targetPos             The hit position
     * @param targetState           The current block state at the target position
     * @param tickEnergy            Laser energy for this tick (long, same as {@link net.kogepan.clayium.laser.Laser#energy()})
     * @param totalEnergyIrradiated Accumulated energy on the same target block
     * @param irradiationTicks      Number of consecutive irradiation ticks on the same target block
     */
    void irradiate(
                   @NotNull Level level,
                   @NotNull BlockPos targetPos,
                   @NotNull BlockState targetState,
                   long tickEnergy,
                   long totalEnergyIrradiated,
                   int irradiationTicks);
}
