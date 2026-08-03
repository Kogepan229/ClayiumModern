package net.kogepan.clayium.blocks;

import net.kogepan.clayium.api.block.IOverclockerBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/** A constant-factor Overclocker block. */
public class OverclockerBlock extends Block implements IOverclockerBlock {

    private final int tier;
    private final double overclockFactor;

    public OverclockerBlock(int tier, double overclockFactor) {
        super(BlockBehaviour.Properties.of()
                .destroyTime(2.0F)
                .explosionResistance(2.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
        if (tier < 0) {
            throw new IllegalArgumentException("Tier must not be negative");
        }
        if (!Double.isFinite(overclockFactor) || overclockFactor < 1.0D) {
            throw new IllegalArgumentException("Overclock factor must be finite and at least 1.0");
        }
        this.tier = tier;
        this.overclockFactor = overclockFactor;
    }

    public int getTier() {
        return this.tier;
    }

    public double getOverclockFactor() {
        return this.overclockFactor;
    }

    @Override
    public double getOverclockFactor(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return this.overclockFactor;
    }
}
