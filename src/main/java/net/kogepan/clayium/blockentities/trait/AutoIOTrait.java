package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.client.utils.TierUtils;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AutoIOTrait extends ClayContainerTrait {

    protected final int tier;
    protected final int importInterval;
    protected final int exportInterval;
    protected final int importMax;
    protected final int exportMax;

    protected int importIntervalCount = 0;
    protected int exportIntervalCount = 0;

    protected AutoIOTrait(@NotNull ClayContainerBlockEntity blockEntity, int tier, boolean isBuffer) {
        super(blockEntity, "autoIoHandler");
        this.tier = tier;
        this.importInterval = isBuffer ? TierUtils.getBufferAutoImportInterval(tier) :
                TierUtils.getMachineAutoImportInterval(tier);
        this.exportInterval = isBuffer ? TierUtils.getBufferAutoExportInterval(tier) :
                TierUtils.getMachineAutoExportInterval(tier);
        this.importMax = isBuffer ? TierUtils.getBufferAutoImportMax(tier) : TierUtils.getMachineAutoImportMax(tier);
        this.exportMax = isBuffer ? TierUtils.getBufferAutoExportMax(tier) : TierUtils.getMachineAutoExportMax(tier);
    }

    @Override
    public void tick() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) return;

        if (++importIntervalCount >= importInterval) {
            importItemsFromNeighbors(this.importMax);
            importIntervalCount = 0;
        }
        if (++exportIntervalCount >= exportInterval) {
            exportItemsToNeighbors(this.exportMax);
            exportIntervalCount = 0;
        }
    }

    protected boolean canImport(Direction direction) {
        return this.blockEntity.getInputMode(direction) != MachineIOMode.NONE;
    }

    protected boolean canExport(Direction direction) {
        return this.blockEntity.getOutputMode(direction) != MachineIOMode.NONE;
    }

    /**
     * Returns the item handler to import items into for the given direction.
     * Override in subclasses (e.g. EcImporter) to use a different target.
     */
    @Nullable
    protected IItemHandler getImportTarget(Direction direction) {
        return this.blockEntity.getExposedItemHandler(direction);
    }

    protected void importItemsFromNeighbors(final int amount) {
        int remainingImport = amount;
        for (Direction direction : Direction.values()) {
            if (remainingImport > 0 && this.canImport(direction)) {
                IItemHandler from = this.blockEntity.getNeighborItemHandler(direction);
                if (from == null) continue;
                IItemHandler to = this.getImportTarget(direction);
                if (to == null) continue;

                remainingImport = transferItems(from, to, remainingImport);
            }
        }
        // TODO: imported
    }

    protected void exportItemsToNeighbors(final int amount) {
        int remainingExport = amount;
        for (Direction direction : Direction.values()) {
            if (remainingExport > 0 && this.canExport(direction)) {
                IItemHandler to = this.blockEntity.getNeighborItemHandler(direction);
                if (to == null) continue;
                IItemHandler from = this.blockEntity.getExposedItemHandler(direction);
                if (from == null) continue;

                remainingExport = transferItems(from, to, remainingExport);
            }
        }
        // TODO: exported
    }

    protected int transferItems(IItemHandler from, IItemHandler to, int amount) {
        int remaining = amount;

        for (int i = 0; i < from.getSlots(); i++) {
            final ItemStack extracted = from.extractItem(i, remaining, true);
            if (extracted.isEmpty()) {
                continue;
            }
            final ItemStack remain = ItemHandlerHelper.insertItem(to, extracted, true);

            final ItemStack stackToInsert = from.extractItem(i, extracted.getCount() - remain.getCount(), false);
            ItemHandlerHelper.insertItem(to, stackToInsert, false);
            remaining -= stackToInsert.getCount();

            if (remaining <= 0) break;
        }

        return remaining;
    }

    public static class Impoter extends AutoIOTrait {

        public Impoter(@NotNull ClayContainerBlockEntity blockEntity, int tier, boolean isBuffer) {
            super(blockEntity, tier, isBuffer);
        }

        @Override
        protected void exportItemsToNeighbors(int amount) {}
    }

    public static class Expoter extends AutoIOTrait {

        public Expoter(@NotNull ClayContainerBlockEntity blockEntity, int tier, boolean isBuffer) {
            super(blockEntity, tier, isBuffer);
        }

        @Override
        protected void importItemsFromNeighbors(int amount) {}
    }

    public static class Combined extends AutoIOTrait {

        public Combined(@NotNull ClayContainerBlockEntity blockEntity, int tier, boolean isBuffer) {
            super(blockEntity, tier, isBuffer);
        }
    }

    /**
     * Importer specialized for Energized Clay (CE).
     * Uses separate limits from the normal importer, so CE can be imported even when
     * the normal importer is at full capacity.
     * Only imports when the input mode for a direction is CE.
     */
    public static class EcImporter extends Impoter {

        private final IItemHandler energizedClayItemHandler;

        public EcImporter(@NotNull ClayContainerBlockEntity blockEntity,
                          @NotNull IItemHandler energizedClayItemHandler) {
            super(blockEntity, blockEntity.tier, false);
            this.energizedClayItemHandler = energizedClayItemHandler;
        }

        @Override
        protected boolean canImport(Direction direction) {
            return this.blockEntity.getInputMode(direction) == MachineIOMode.CE;
        }

        @Override
        @Nullable
        protected IItemHandler getImportTarget(Direction direction) {
            return this.energizedClayItemHandler;
        }
    }
}
