package net.kogepan.clayium.gui.factory;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;
import net.kogepan.clayium.blocks.machine.ClayInterfaceBlock;
import net.kogepan.clayium.registries.ClayiumMenuTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.factory.IContainerUIHolder;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerMenu;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Dedicated menu type for Clay Interface so target information can be serialized explicitly.
 */
public class ClayInterfaceUIMenuType {

    private static final StreamCodec<RegistryFriendlyByteBuf, GlobalPos> GLOBAL_POS_STREAM_CODEC = ByteBufCodecs
            .fromCodecWithRegistries(GlobalPos.CODEC);

    public static boolean openUI(@NotNull ServerPlayer player, @NotNull BlockPos interfacePos) {
        BlockState interfaceState = player.level().getBlockState(interfacePos);
        if (!(interfaceState.getBlock() instanceof ClayInterfaceBlock)) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(interfacePos);
        if (!(blockEntity instanceof ClayInterfaceBlockEntity interfaceBlockEntity)) {
            return false;
        }

        GlobalPos target = interfaceBlockEntity.getLinkedTargetPos();
        // Resolve and serialize the target state on the server so the client can rebuild the same UI
        // even when the target dimension is not locally loaded.
        ClayInterfaceUIHolder holder = new ClayInterfaceUIHolder(
                player,
                interfacePos,
                interfaceState,
                target,
                resolveTargetStateForUI(player, target));
        return player.openMenu(holder).isPresent();
    }

    @NotNull
    public static ModularUIContainerMenu create(int windowId, @NotNull Inventory inventory,
                                                @NotNull RegistryFriendlyByteBuf data) {
        BlockPos interfacePos = data.readBlockPos();
        BlockState interfaceState = BlockUIMenuType.BLOCK_STATE_STREAM_CODEC.decode(data);
        @Nullable
        GlobalPos target = data.readBoolean() ? GLOBAL_POS_STREAM_CODEC.decode(data) : null;
        @Nullable
        // Optional target state used for client-side UI reconstruction.
        BlockState targetState = data.readBoolean() ? BlockUIMenuType.BLOCK_STATE_STREAM_CODEC.decode(data) : null;

        ClayInterfaceUIHolder holder = new ClayInterfaceUIHolder(inventory.player, interfacePos, interfaceState,
                target, targetState);
        return new ModularUIContainerMenu(ClayiumMenuTypes.CLAY_INTERFACE_UI.get(), windowId, inventory, holder);
    }

    @Nullable
    private static BlockState resolveTargetStateForUI(@NotNull ServerPlayer player, @Nullable GlobalPos target) {
        if (target == null) {
            return null;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return null;
        }

        ServerLevel targetLevel = server.getLevel(target.dimension());
        if (targetLevel == null || !targetLevel.isLoaded(target.pos())) {
            return null;
        }

        // Only serialize container targets that can actually provide a machine/container UI.
        BlockEntity targetBlockEntity = targetLevel.getBlockEntity(target.pos());
        if (targetBlockEntity instanceof ClayContainerBlockEntity &&
                !(targetBlockEntity instanceof ClayInterfaceBlockEntity)) {
            return targetBlockEntity.getBlockState();
        }

        return null;
    }

    public static class ClayInterfaceUIHolder implements MenuProvider, IContainerUIHolder {

        public final Player player;
        public final BlockPos interfacePos;
        public final BlockState interfaceState;
        @Nullable
        public final GlobalPos target;
        @Nullable
        public final BlockState targetState;

        public ClayInterfaceUIHolder(@NotNull Player player, @NotNull BlockPos interfacePos,
                                     @NotNull BlockState interfaceState, @Nullable GlobalPos target,
                                     @Nullable BlockState targetState) {
            this.player = player;
            this.interfacePos = interfacePos;
            this.interfaceState = interfaceState;
            this.target = target;
            this.targetState = targetState;
        }

        @Override
        public boolean isStillValid(@NotNull Player player) {
            return this.interfaceState.is(player.level().getBlockState(this.interfacePos).getBlock());
        }

        @Override
        public @NotNull Component getDisplayName() {
            return this.interfaceState.getBlock().getName();
        }

        @Override
        public @Nullable ModularUIContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory,
                                                           @NotNull Player player) {
            return new ModularUIContainerMenu(ClayiumMenuTypes.CLAY_INTERFACE_UI.get(), containerId, playerInventory,
                    this);
        }

        @Override
        public void writeClientSideData(@NotNull AbstractContainerMenu menu, @NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeBlockPos(this.interfacePos);
            BlockUIMenuType.BLOCK_STATE_STREAM_CODEC.encode(buffer, this.interfaceState);
            buffer.writeBoolean(this.target != null);
            if (this.target != null) {
                GLOBAL_POS_STREAM_CODEC.encode(buffer, this.target);
            }
            // Keep this optional to avoid forcing chunk loads or fake states when the target is unavailable.
            buffer.writeBoolean(this.targetState != null);
            if (this.targetState != null) {
                BlockUIMenuType.BLOCK_STATE_STREAM_CODEC.encode(buffer, this.targetState);
            }
        }

        @Override
        public @NotNull ModularUI createUI(@NotNull Player player) {
            BlockEntity blockEntity = player.level().getBlockEntity(this.interfacePos);
            if (!(blockEntity instanceof ClayInterfaceBlockEntity interfaceBlockEntity)) {
                return new ModularUI(UI.empty(), player);
            }
            if (!(this.interfaceState.getBlock() instanceof BlockUIMenuType.BlockUI blockUI)) {
                return new ModularUI(UI.empty(), player);
            }

            BlockUIMenuType.BlockUIHolder blockHolder = new BlockUIMenuType.BlockUIHolder(
                    blockUI, player, this.interfacePos, this.interfaceState);
            return interfaceBlockEntity.createUI(blockHolder, this.target, this.targetState);
        }
    }
}
