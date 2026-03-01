package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;

import org.jetbrains.annotations.NotNull;

public class ChunkLoaderBlockEntity extends BlockEntity {

    private static final int CHUNK_LOAD_RADIUS = 1;
    private static final int CHUNK_FORCE_INTERVAL_TICKS = 20;

    public static final TicketController TICKET_CONTROLLER = new TicketController(
            Clayium.id("chunk_loader"),
            ChunkLoaderBlockEntity::validateTickets);

    private int chunkForceTickCounter = 0;

    public ChunkLoaderBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CHUNK_LOADER_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @NotNull ChunkLoaderBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (blockEntity.chunkForceTickCounter == 0) {
            blockEntity.forceChunks(serverLevel, true);
        }
        blockEntity.chunkForceTickCounter = (blockEntity.chunkForceTickCounter + 1) % CHUNK_FORCE_INTERVAL_TICKS;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level instanceof ServerLevel serverLevel) {
            forceChunks(serverLevel, true);
        }
    }

    public void releaseForcedChunks() {
        if (this.level instanceof ServerLevel serverLevel) {
            forceChunks(serverLevel, false);
        }
    }

    private void forceChunks(@NotNull ServerLevel level, boolean add) {
        ChunkPos centerChunk = new ChunkPos(this.worldPosition);
        for (int dx = -CHUNK_LOAD_RADIUS; dx <= CHUNK_LOAD_RADIUS; dx++) {
            for (int dz = -CHUNK_LOAD_RADIUS; dz <= CHUNK_LOAD_RADIUS; dz++) {
                TICKET_CONTROLLER.forceChunk(
                        level,
                        this.worldPosition,
                        centerChunk.x + dx,
                        centerChunk.z + dz,
                        add,
                        true);
            }
        }
    }

    private static void validateTickets(@NotNull ServerLevel level, @NotNull TicketHelper ticketHelper) {
        for (BlockPos ownerPos : ticketHelper.getBlockTickets().keySet()) {
            if (!level.getBlockState(ownerPos).is(ClayiumBlocks.CHUNK_LOADER.get())) {
                ticketHelper.removeAllTickets(ownerPos);
            }
        }
    }
}
