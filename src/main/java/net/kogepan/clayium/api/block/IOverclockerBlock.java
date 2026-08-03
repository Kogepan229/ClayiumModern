package net.kogepan.clayium.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/**
 * A block that accelerates adjacent Clayium machines.
 *
 * <p>
 * Implementations must return a finite factor greater than or equal to {@code 1.0}. Machines
 * ignore values that do not satisfy this contract.
 */
public interface IOverclockerBlock {

    double getOverclockFactor(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos);
}
