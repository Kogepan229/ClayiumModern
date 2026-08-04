package net.kogepan.clayium.blocks;

import net.kogepan.clayium.api.block.IResonatingBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/** A constant-factor CA resonator. */
public class ResonatorBlock extends Block implements IResonatingBlock {

    private final int tier;
    private final double resonance;

    public ResonatorBlock(int tier, double resonance) {
        super(BlockBehaviour.Properties.of()
                .destroyTime(2.0F)
                .explosionResistance(2.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
        if (tier < 0) {
            throw new IllegalArgumentException("Tier must not be negative");
        }
        if (!Double.isFinite(resonance) || resonance < 1.0D) {
            throw new IllegalArgumentException("Resonance must be finite and at least 1.0");
        }
        this.tier = tier;
        this.resonance = resonance;
    }

    public int getTier() {
        return this.tier;
    }

    public double getResonance() {
        return this.resonance;
    }

    @Override
    public double getResonance(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return this.resonance;
    }
}
