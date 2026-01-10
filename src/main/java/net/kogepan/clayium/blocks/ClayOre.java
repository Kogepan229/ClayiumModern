package net.kogepan.clayium.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ClayOre extends Block {

    public ClayOre() {
        super(BlockBehaviour.Properties.of().destroyTime(3.0f).explosionResistance(5.0f).sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
    }
}
