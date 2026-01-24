package net.kogepan.clayium.blocks.machine;

import net.kogepan.clayium.blockentities.ClayBufferBlockEntity;
import net.kogepan.clayium.blockentities.machine.BendingMachineBlockEntity;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BendingMachineBlock extends ClayContainerBlock {

    public BendingMachineBlock(int tier) {
        super(tier);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new BendingMachineBlockEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return createTickerHelper(level, type, ClayiumBlockEntityTypes.BENDING_MACHINE_BLOCK_ENTITY.get(),
                ClayBufferBlockEntity::tick, null);
    }
}
