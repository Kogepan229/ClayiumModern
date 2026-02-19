package net.kogepan.clayium.capability;

import net.kogepan.clayium.laser.Laser;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Receives laser irradiation updates from a laser propagation engine.
 */
public interface IClayLaserAcceptor {

    /**
     * Called every tick while a laser irradiates this block entity.
     *
     * @param irradiatedSide The side where irradiation arrives
     * @param laser          Current laser, or {@code null} when irradiation stops
     */
    void acceptLaser(@NotNull Direction irradiatedSide, @Nullable Laser laser);
}
