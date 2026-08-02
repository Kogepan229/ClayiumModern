package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.ClayBufferBlockEntity;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

public final class ClayBufferBlock extends ClayContainerBlock {

    public ClayBufferBlock(int tier, BlockBehaviour.Properties properties) {
        super(tier, properties);
    }

    @Override
    public ClayBufferBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClayBufferBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
                                                                            Level level,
                                                                            BlockState state,
                                                                            BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(
                type,
                ClayiumBlockEntityTypes.CLAY_BUFFER_BLOCK_ENTITY.get(),
                ClayBufferBlockEntity::tick);
    }
}
