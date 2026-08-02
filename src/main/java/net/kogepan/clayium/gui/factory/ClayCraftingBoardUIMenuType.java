package net.kogepan.clayium.gui.factory;

import net.kogepan.clayium.blockentities.ClayCraftingBoardBlockEntity;
import net.kogepan.clayium.blocks.ClayCraftingBoardBlock;
import net.kogepan.clayium.gui.menu.ClayCraftingBoardMenu;
import net.kogepan.clayium.gui.menu.ClayCraftingBoardMenu.Session;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.factory.IContainerUIHolder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClayCraftingBoardUIMenuType {

    private ClayCraftingBoardUIMenuType() {}

    public static boolean openUI(@NotNull ServerPlayer player, @NotNull BlockPos boardPos) {
        BlockState boardState = player.level().getBlockState(boardPos);
        if (!(boardState.getBlock() instanceof ClayCraftingBoardBlock)) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(boardPos);
        if (!(blockEntity instanceof ClayCraftingBoardBlockEntity boardEntity)) {
            return false;
        }

        NeighborSelection neighbor = findNeighbor(player.serverLevel(), boardPos);
        int neighborSlots = neighbor == null ? 0 : neighbor.handler().getSlots();
        Session session = new Session(player, boardPos, boardState, boardEntity, boardEntity.getInventory(),
                neighbor == null ? null : neighbor.handler(), neighbor == null ? null : neighbor.cache(),
                neighborSlots);
        return player.openMenu(new ClayCraftingBoardUIHolder(session, boardPos, boardState, neighborSlots)).isPresent();
    }

    @NotNull
    public static ClayCraftingBoardMenu create(int containerId, @NotNull Inventory inventory,
                                               @NotNull RegistryFriendlyByteBuf data) {
        BlockPos boardPos = data.readBlockPos();
        BlockState boardState = BlockUIMenuType.BLOCK_STATE_STREAM_CODEC.decode(data);
        int neighborSlots = data.readVarInt();
        Session session = new Session(inventory.player, boardPos, boardState, null, new ItemStackHandler(9),
                neighborSlots > 0 ? new ItemStackHandler(neighborSlots) : null, null, neighborSlots);
        return new ClayCraftingBoardMenu(containerId, inventory,
                new ClayCraftingBoardUIHolder(session, boardPos, boardState, neighborSlots));
    }

    @Nullable
    private static NeighborSelection findNeighbor(ServerLevel level, BlockPos boardPos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = boardPos.relative(direction);
            Direction context = direction.getOpposite();
            IItemHandler probe = level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, context);
            if (probe == null || probe.getSlots() <= 0) {
                continue;
            }

            BlockCapabilityCache<IItemHandler, Direction> cache = BlockCapabilityCache.create(
                    Capabilities.ItemHandler.BLOCK, level, neighborPos, context);
            IItemHandler handler = cache.getCapability();
            if (handler != null && handler.getSlots() > 0) {
                return new NeighborSelection(cache, handler);
            }
        }
        return null;
    }

    private record NeighborSelection(BlockCapabilityCache<IItemHandler, Direction> cache, IItemHandler handler) {}

    public static final class ClayCraftingBoardUIHolder implements MenuProvider, IContainerUIHolder {

        @Getter
        private final Session session;
        private final BlockPos boardPos;
        private final BlockState boardState;
        private final int neighborSlots;

        private ClayCraftingBoardUIHolder(Session session, BlockPos boardPos, BlockState boardState,
                                          int neighborSlots) {
            this.session = session;
            this.boardPos = boardPos;
            this.boardState = boardState;
            this.neighborSlots = neighborSlots;
        }

        @Override
        public boolean isStillValid(@NotNull Player player) {
            return this.session.isStillValid(player);
        }

        @Override
        @NotNull
        public Component getDisplayName() {
            return this.boardState.getBlock().getName();
        }

        @Override
        @Nullable
        public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory,
                                                @NotNull Player player) {
            return new ClayCraftingBoardMenu(containerId, playerInventory, this);
        }

        @Override
        public void writeClientSideData(@NotNull AbstractContainerMenu menu,
                                        @NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeBlockPos(this.boardPos);
            BlockUIMenuType.BLOCK_STATE_STREAM_CODEC.encode(buffer, this.boardState);
            buffer.writeVarInt(this.neighborSlots);
        }

        @Override
        @NotNull
        public ModularUI createUI(@NotNull Player player) {
            return this.session.createUI();
        }
    }
}
