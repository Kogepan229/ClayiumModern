package net.kogepan.clayium.capability;

import net.kogepan.clayium.laser.Laser;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides current laser emission data from a block entity.
 */
public interface IClayLaserSource {

    /**
     * Gets the laser currently being irradiated.
     *
     * @return Current laser, or {@code null} if not irradiating
     */
    @Nullable
    Laser getIrradiatingLaser();

    /**
     * Gets the emission direction.
     *
     * @return Laser direction
     */
    @NotNull
    Direction getDirection();

    /**
     * Gets the current irradiation length in blocks.
     *
     * @return Laser length
     */
    int getLength();
}
