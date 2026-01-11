package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ClayWorkTableBlockEntity extends BlockEntity {

    public ClayWorkTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_WORK_TABLE_BLOCK_ENTITY.get(), pos, blockState);
    }
}
