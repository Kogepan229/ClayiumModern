package net.kogepan.clayium.blocks.machine;

import net.kogepan.clayium.blockentities.machine.ChunkLoaderBlockEntity;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkLoaderBlock extends Block implements EntityBlock {

    public ChunkLoaderBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(6.0f, 25.0f)
                .requiresCorrectToolForDrops());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChunkLoaderBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return ClayContainerBlock.createTickerHelper(level, type,
                ClayiumBlockEntityTypes.CHUNK_LOADER_BLOCK_ENTITY.get(),
                ChunkLoaderBlockEntity::tick, null);
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                            @NotNull BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide() && !newState.is(state.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChunkLoaderBlockEntity chunkLoader) {
                chunkLoader.releaseForcedChunks();
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
