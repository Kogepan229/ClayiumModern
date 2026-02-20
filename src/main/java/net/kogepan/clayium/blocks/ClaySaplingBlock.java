package net.kogepan.clayium.blocks;

import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;

/**
 * Clay Sapling block. Can be placed on dirt/grass. Texture from ClayiumOriginal (sapling_claytree).
 * Tree growth (Clay Tree) is not implemented yet; this block is decorative / for laser conversion.
 */
public class ClaySaplingBlock extends BushBlock {

    public static final MapCodec<ClaySaplingBlock> CODEC = simpleCodec(ClaySaplingBlock::new);

    public ClaySaplingBlock() {
        super(BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.0f)
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ));
    }

    public ClaySaplingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
