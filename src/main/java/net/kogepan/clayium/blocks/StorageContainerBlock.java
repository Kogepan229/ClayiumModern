package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.StorageContainerBlockEntity;
import net.kogepan.clayium.items.blockitem.StorageContainerBlockItem;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StorageContainerBlock extends AbstractFilteredContainerBlock {

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new StorageContainerBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return createTickerHelper(level, type, ClayiumBlockEntityTypes.STORAGE_CONTAINER_BLOCK_ENTITY.get(),
                StorageContainerBlockEntity::tick, null);
    }

    @Override
    @NotNull
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
                                              @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                              @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!stack.is(ClayiumItems.CLAY_CORE)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof StorageContainerBlockEntity container &&
                container.upgradeToMaximumCapacity()) {
            stack.shrink(1);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    @NotNull
    public ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        int capacity = level.getBlockEntity(pos) instanceof StorageContainerBlockEntity container ?
                container.getCapacity() : StorageContainerBlockEntity.DEFAULT_CAPACITY;
        return StorageContainerBlockItem.createWithCapacity(capacity);
    }
}
