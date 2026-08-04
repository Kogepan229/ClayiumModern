package net.kogepan.clayium.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/** Supplies a multiplicative resonance factor to nearby CA machines. */
public interface IResonatingBlock {

    /**
     * Returns this block's resonance factor at the supplied position.
     *
     * <p>
     * Implementations should return a finite value greater than or equal to {@code 1.0}. CA machines ignore
     * values outside that contract.
     */
    double getResonance(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos);
}
