package net.kogepan.clayium.blocks;

import net.kogepan.clayium.api.block.ICAReactorCoil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/** A constant-tier coil block for CA Reactor rings. */
public class CAReactorCoilBlock extends Block implements ICAReactorCoil {

    private final int tier;

    public CAReactorCoilBlock(int tier) {
        super(BlockBehaviour.Properties.of()
                .destroyTime(8.0F)
                .explosionResistance(5.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
        if (tier < 10 || tier > 13) {
            throw new IllegalArgumentException("CA Reactor coil tier must be between 10 and 13");
        }
        this.tier = tier;
    }

    public int getTier() {
        return this.tier;
    }

    @Override
    public int getCAReactorTier(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return this.tier;
    }
}
