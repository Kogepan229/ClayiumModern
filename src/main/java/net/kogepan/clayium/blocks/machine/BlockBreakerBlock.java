package net.kogepan.clayium.blocks.machine;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.machine.BlockBreakerBlockEntity;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockBreakerBlock extends ClayContainerBlock {

    public BlockBreakerBlock() {
        super(6);
    }

    @Override
    public DirectionProperty getFacingProperty() {
        return DirectionalBlock.FACING;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BlockBreakerBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return createTickerHelper(level, type, ClayiumBlockEntityTypes.BLOCK_BREAKER_BLOCK_ENTITY.get(),
                ClayContainerBlockEntity::tick, null);
    }
}
