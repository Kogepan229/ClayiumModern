package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.ClayWorkTableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClayWorkTableBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {

    public ClayWorkTableBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .destroyTime(2.0f)
                .explosionResistance(2.0f)
                .requiresCorrectToolForDrops());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ClayWorkTableBlockEntity(blockPos, blockState);
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                               @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (player instanceof ServerPlayer serverPlayer) {
            BlockUIMenuType.openUI(serverPlayer, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        if (holder.player.level().getBlockEntity(holder.pos) instanceof ClayWorkTableBlockEntity entity) {
            return entity.createUI(holder);
        }

        return null;
    }

    @Override
    protected void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState newState,
                            boolean movedByPiston) {
        if (!level.isClientSide() && !newState.is(state.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ClayWorkTableBlockEntity cbe) {
                cbe.getInventory().dropContents(level, pos);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
