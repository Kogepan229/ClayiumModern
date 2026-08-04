package net.kogepan.clayium.blocks.machine;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.RedstoneInterfaceBlockEntity;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Redstone input and output interface for externally controllable Clayium machines. */
public class RedstoneInterfaceBlock extends ClayContainerBlock {

    public RedstoneInterfaceBlock(int tier) {
        super(tier, BlockBehaviour.Properties.of()
                .destroyTime(2.0F)
                .explosionResistance(5.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RedstoneInterfaceBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return createTickerHelper(level, type, ClayiumBlockEntityTypes.REDSTONE_INTERFACE_BLOCK_ENTITY.get(),
                ClayContainerBlockEntity::tick, null);
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                               @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer) ||
                !(level.getBlockEntity(pos) instanceof RedstoneInterfaceBlockEntity redstoneInterface)) {
            return InteractionResult.PASS;
        }

        RedstoneInterfaceBlockEntity.Mode mode = player.isShiftKeyDown() ? redstoneInterface.getMode() :
                redstoneInterface.cycleMode();
        player.displayClientMessage(Component.translatable("message.clayium.redstone_interface.mode",
                mode.getDisplayName()), false);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean isSignalSource(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                            @NotNull Direction direction) {
        return level.getBlockEntity(pos) instanceof RedstoneInterfaceBlockEntity redstoneInterface ?
                redstoneInterface.getOutputPower() : 0;
    }
}
