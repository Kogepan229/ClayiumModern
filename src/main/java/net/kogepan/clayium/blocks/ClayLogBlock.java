package net.kogepan.clayium.blocks;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;

/**
 * Log block for Clay Tree. Same role as ClayTreeLog in ClayiumOriginal.
 */
public class ClayLogBlock extends RotatedPillarBlock {

    public static final MapCodec<ClayLogBlock> CODEC = simpleCodec(ClayLogBlock::new);

    public ClayLogBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(1.5f)
                .sound(SoundType.WOOD)
                .ignitedByLava());
    }

    public ClayLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }
}
