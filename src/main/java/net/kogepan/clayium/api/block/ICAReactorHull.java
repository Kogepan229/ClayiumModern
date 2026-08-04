package net.kogepan.clayium.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/** A hull block that can cover a CA Reactor coil ring. */
public interface ICAReactorHull {

    /** Returns the one-based CA rank supplied by this hull. */
    int getCAReactorRank(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos);
}
