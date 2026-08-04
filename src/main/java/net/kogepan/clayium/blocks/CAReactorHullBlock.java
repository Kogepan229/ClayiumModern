package net.kogepan.clayium.blocks;

import net.kogepan.clayium.api.block.ICAReactorHull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/** A constant-rank hull block for CA Reactor coil covers. */
public class CAReactorHullBlock extends Block implements ICAReactorHull {

    private final int rank;

    public CAReactorHullBlock(int rank) {
        super(BlockBehaviour.Properties.of()
                .destroyTime(4.0F)
                .explosionResistance(25.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
        if (rank < 1 || rank > 10) {
            throw new IllegalArgumentException("CA Reactor hull rank must be between 1 and 10");
        }
        this.rank = rank;
    }

    public int getRank() {
        return this.rank;
    }

    @Override
    public int getCAReactorRank(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return this.rank;
    }
}
