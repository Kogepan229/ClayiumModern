package net.kogepan.clayium.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/** A coil block that can be used in a CA Reactor ring. */
public interface ICAReactorCoil {

    /** Returns the Clayium tier supported by this coil. */
    int getCAReactorTier(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos);
}
