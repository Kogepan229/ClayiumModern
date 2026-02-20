package net.kogepan.clayium.blocks;

import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;

/**
 * Leaves block for Clay Tree. Same role as ClayTreeLeaf in ClayiumOriginal.
 * Decays when not adjacent to clay log. Drops clay_dust from loot table.
 */
public class ClayLeavesBlock extends LeavesBlock {

    public static final MapCodec<ClayLeavesBlock> CODEC = simpleCodec(ClayLeavesBlock::new);

    public ClayLeavesBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(0.2f)
                .sound(SoundType.GRASS)
                .noOcclusion()
                .isValidSpawn((state, level, pos, type) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .ignitedByLava());
    }

    public ClayLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }
}
