package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.TransferUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockBreakerBlockEntity extends AdjacentWorkerBlockEntity {

    private BlockState progressTargetState;
    private BlockPos progressTargetPos;

    public BlockBreakerBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.BLOCK_BREAKER_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE), List.of(MachineIOMode.NONE, MachineIOMode.ALL));
        this.addTrait(new AutoIOTrait.Expoter(this, this.tier, false, 2, 16));
    }

    @Override
    public void initDefaultRoutes() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof ClayContainerBlock containerBlock) {
            Direction front = state.getValue(containerBlock.getFacingProperty());
            this.outputModes.setMode(front, MachineIOMode.ALL);
        }
    }

    @Override
    protected void tickWorker(@NotNull ServerLevel level) {
        BlockPos targetPos = this.getWorkPos();
        BlockState targetState = level.getBlockState(targetPos);
        float hardness = targetState.getDestroySpeed(level, targetPos);
        if (targetState.isAir() || hardness < 0.0F || !targetState.getFluidState().isEmpty() ||
                !this.matchesBlockFilter(targetState)) {
            this.resetProgress(targetState);
            return;
        }

        if (this.progressTargetPos != null && !this.progressTargetPos.equals(targetPos) ||
                this.progressTargetState != null && !this.progressTargetState.equals(targetState)) {
            this.progress = 0;
        }
        this.progressTargetPos = targetPos;
        this.progressTargetState = targetState;
        int requiredProgress = Math.max(1, (int) Math.floor(400.0D * (0.1D + hardness)));
        this.progress = Math.min(requiredProgress, this.progress + PROGRESS_PER_TICK);
        this.setChanged();
        if (this.progress < requiredProgress) {
            return;
        }

        BlockEntity targetBlockEntity = level.getBlockEntity(targetPos);
        List<ItemStack> drops = Block.getDrops(targetState, level, targetPos, targetBlockEntity);
        if (!TransferUtils.simulateInsertItemsToHandler(this.itemInventory, drops)) {
            return;
        }
        if (!level.destroyBlock(targetPos, false)) {
            return;
        }
        for (ItemStack drop : drops) {
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(this.itemInventory, drop, false);
            if (!remainder.isEmpty()) {
                Containers.dropItemStack(level, this.worldPosition.getX() + 0.5D,
                        this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D, remainder);
            }
        }
        this.progressTargetPos = null;
        this.progressTargetState = null;
        this.completeJob();
    }

    private void resetProgress(@NotNull BlockState targetState) {
        this.progressTargetPos = this.getWorkPos();
        this.progressTargetState = targetState;
        if (this.progress != 0) {
            this.progress = 0;
            this.setChanged();
        }
    }
}
