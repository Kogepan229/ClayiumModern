package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.MultitrackBufferBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Auto I/O for multitrack buffers. When both sides are multitrack buffers, transfers align by track index.
 */
public class MultitrackBufferAutoIOTrait extends AutoIOTrait.Combined {

    public MultitrackBufferAutoIOTrait(@NotNull MultitrackBufferBlockEntity blockEntity, int tier) {
        super(blockEntity, tier, true);
    }

    @Override
    @Nullable
    protected IItemHandler getImportTarget(Direction direction) {
        return ((MultitrackBufferBlockEntity) this.blockEntity).getAutomationItemHandler(direction);
    }

    @Override
    protected void importItemsFromNeighbors(int amount) {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        MultitrackBufferBlockEntity self = (MultitrackBufferBlockEntity) this.blockEntity;
        for (Direction direction : Direction.values()) {
            if (!this.canImport(direction)) {
                continue;
            }
            var neighborBe = level.getBlockEntity(this.blockEntity.getBlockPos().relative(direction));
            if (neighborBe instanceof MultitrackBufferBlockEntity neighborMtb) {
                this.importFromMultitrackNeighbor(neighborMtb, direction.getOpposite(), self, direction, amount);
            } else {
                IItemHandler from = this.blockEntity.getNeighborItemHandler(direction);
                if (from == null) {
                    continue;
                }
                for (int t : self.tracksExposedForInputOnFace(direction)) {
                    IItemHandler to = new MultitrackBufferBlockEntity.TrackInsertHandler(self, direction, t);
                    this.transferItems(from, to, amount);
                }
            }
        }
    }

    @Override
    protected void exportItemsToNeighbors(int amount) {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        MultitrackBufferBlockEntity self = (MultitrackBufferBlockEntity) this.blockEntity;
        for (Direction direction : Direction.values()) {
            if (!this.canExport(direction)) {
                continue;
            }
            var neighborBe = level.getBlockEntity(this.blockEntity.getBlockPos().relative(direction));
            if (neighborBe instanceof MultitrackBufferBlockEntity neighborMtb) {
                this.exportToMultitrackNeighbor(self, direction, neighborMtb, direction.getOpposite(), amount);
            } else {
                IItemHandler to = this.blockEntity.getNeighborItemHandler(direction);
                if (to == null) {
                    continue;
                }
                for (int t : self.tracksExposedForOutputOnFace(direction)) {
                    IItemHandler from = self.getTrackInventoryForExtractThroughFace(direction, t);
                    this.transferItems(from, to, amount);
                }
            }
        }
    }

    private void importFromMultitrackNeighbor(@NotNull MultitrackBufferBlockEntity from,
                                              @NotNull Direction fromOutputFace,
                                              @NotNull MultitrackBufferBlockEntity to,
                                              @NotNull Direction toInputFace, int perTrackBudget) {
        int tracks = Math.min(from.getTrackCount(), to.getTrackCount());
        for (int t = 0; t < tracks; t++) {
            if (!from.pairedPassiveOutputAllowsTrack(fromOutputFace, t)) {
                continue;
            }
            if (!to.inputExposesTrack(toInputFace, t)) {
                continue;
            }
            IItemHandler insertTarget = new MultitrackBufferBlockEntity.TrackInsertHandler(to, toInputFace, t);
            IItemHandler extractSource = from.getTrackInventoryForExtractThroughFace(fromOutputFace, t);
            this.transferItems(extractSource, insertTarget, perTrackBudget);
        }
    }

    private void exportToMultitrackNeighbor(@NotNull MultitrackBufferBlockEntity from,
                                            @NotNull Direction fromOutputFace,
                                            @NotNull MultitrackBufferBlockEntity neighbor,
                                            @NotNull Direction neighborInputFace, int perTrackBudget) {
        int tracks = Math.min(from.getTrackCount(), neighbor.getTrackCount());
        for (int t = 0; t < tracks; t++) {
            if (!from.outputExposesTrack(fromOutputFace, t)) {
                continue;
            }
            if (!neighbor.pairedPassiveInputAllowsTrack(neighborInputFace, t)) {
                continue;
            }
            IItemHandler insertTarget = new MultitrackBufferBlockEntity.TrackInsertHandler(neighbor, neighborInputFace,
                    t, true);
            IItemHandler extractSource = from.getTrackInventoryForExtractThroughFace(fromOutputFace, t);
            this.transferItems(extractSource, insertTarget, perTrackBudget);
        }
    }
}
