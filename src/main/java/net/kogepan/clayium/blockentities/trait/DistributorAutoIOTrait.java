package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.machine.DistributorBlockEntity;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DistributorAutoIOTrait extends ClayContainerTrait {

    public static final String TRAIT_ID = "distributorAutoIo";

    private final DistributorBlockEntity distributor;
    private final int importMax;
    private final int exportMax;
    private final int interval;

    private int importIntervalCount;
    private int exportIntervalCount;
    private int importPtr;
    private int exportPtr;
    private boolean oneLapBehind;
    private int lastDirection = -1;

    public DistributorAutoIOTrait(@NotNull DistributorBlockEntity blockEntity, int importMax, int exportMax,
                                  int interval) {
        super(blockEntity, TRAIT_ID);
        this.distributor = blockEntity;
        this.importMax = importMax;
        this.exportMax = exportMax;
        this.interval = interval;
    }

    @Override
    public void tick() {
        Level level = this.distributor.getLevel();
        if (level == null || level.isClientSide()) return;

        if (++importIntervalCount >= interval) {
            importItemsFromNeighbors(importMax);
            importIntervalCount = 0;
        }
        if (++exportIntervalCount >= interval) {
            exportItemsToNeighbors(exportMax);
            exportIntervalCount = 0;
        }
    }

    private void importItemsFromNeighbors(int amount) {
        if (this.oneLapBehind || amount <= 0) {
            return;
        }
        while (!this.oneLapBehind && !isGroupEmpty(this.importPtr)) {
            this.advanceImportPointer();
        }
        if (this.oneLapBehind) {
            return;
        }

        int remainingImport = amount;
        for (Direction direction : Direction.values()) {
            if (remainingImport <= 0) {
                break;
            }
            if (this.distributor.getInputMode(direction) == MachineIOMode.NONE) {
                continue;
            }
            IItemHandler from = this.distributor.getNeighborItemHandler(direction);
            if (from == null) {
                continue;
            }
            IItemHandler to = this.distributor.getExposedItemHandler(direction);
            if (to == null) {
                continue;
            }
            remainingImport = transferItems(from, to, remainingImport);
        }
        if (!isGroupEmpty(this.importPtr)) {
            this.advanceImportPointer();
        }
    }

    private void exportItemsToNeighbors(int amount) {
        if (amount <= 0) {
            return;
        }
        List<DirectionalHandler> outputs = collectOutputHandlers();
        if (outputs.isEmpty()) {
            return;
        }
        boolean wasCurrentGroupNonEmpty = !isGroupEmpty(this.exportPtr);

        int remainingExport = amount;
        IItemHandler from = this.distributor.getGroupHandler(this.exportPtr);
        for (int slot = 0; slot < from.getSlots() && remainingExport > 0; slot++) {
            ExportResult result = exportSlotEvenly(from, slot, remainingExport, outputs, this.lastDirection);
            if (result.moved() > 0) {
                remainingExport -= result.moved();
                this.lastDirection = result.lastDirection();
            }
        }

        if (isGroupEmpty(this.exportPtr) && (wasCurrentGroupNonEmpty || this.oneLapBehind)) {
            this.advanceExportPointer();
            this.oneLapBehind = false;
        }
    }

    private boolean isGroupEmpty(int groupIndex) {
        IItemHandler group = this.distributor.getGroupHandler(groupIndex);
        for (int slot = 0; slot < group.getSlots(); slot++) {
            if (!group.getStackInSlot(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private ExportResult exportSlotEvenly(IItemHandler from, int slot, int maxAmount,
                                          List<DirectionalHandler> outputs, int lastDirection) {
        ItemStack slotStack = from.extractItem(slot, maxAmount, true);
        if (slotStack.isEmpty()) {
            return new ExportResult(0, lastDirection);
        }

        int remaining = slotStack.getCount();
        int movedTotal = 0;
        int each = remaining / outputs.size();
        int nextLastDirection = lastDirection;

        if (each > 0) {
            for (DirectionalHandler output : outputs) {
                if (remaining <= 0) {
                    break;
                }
                int target = Math.min(each, remaining);
                int moved = moveFromSlotToHandler(output.direction(), slot, output.handler(), target);
                movedTotal += moved;
                remaining -= moved;
            }
        }

        while (remaining > 0) {
            int start = indexAfterDirection(outputs, nextLastDirection);
            boolean insertedAny = false;
            for (int offset = 0; offset < outputs.size() && remaining > 0; offset++) {
                int index = (start + offset) % outputs.size();
                DirectionalHandler output = outputs.get(index);
                int moved = moveFromSlotToHandler(output.direction(), slot, output.handler(), 1);
                if (moved > 0) {
                    insertedAny = true;
                    movedTotal += moved;
                    remaining -= moved;
                    nextLastDirection = output.direction().ordinal();
                }
            }
            if (!insertedAny) {
                break;
            }
        }

        return new ExportResult(movedTotal, nextLastDirection);
    }

    private int moveFromSlotToHandler(Direction side, int slot, IItemHandler to, int amount) {
        if (amount <= 0) {
            return 0;
        }
        IItemHandler from = this.distributor.getFilteredOutputGroupHandler(side);
        if (from == null) {
            return 0;
        }
        ItemStack extractedSim = from.extractItem(slot, amount, true);
        if (extractedSim.isEmpty()) {
            return 0;
        }
        ItemStack remainSim = ItemHandlerHelper.insertItem(to, extractedSim, true);
        int movable = extractedSim.getCount() - remainSim.getCount();
        if (movable <= 0) {
            return 0;
        }

        ItemStack extracted = from.extractItem(slot, movable, false);
        if (extracted.isEmpty()) {
            return 0;
        }
        ItemStack remain = ItemHandlerHelper.insertItem(to, extracted, false);
        return extracted.getCount() - remain.getCount();
    }

    private List<DirectionalHandler> collectOutputHandlers() {
        List<DirectionalHandler> outputs = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (this.distributor.getOutputMode(direction) == MachineIOMode.NONE) {
                continue;
            }
            IItemHandler to = this.distributor.getNeighborItemHandler(direction);
            if (to != null) {
                outputs.add(new DirectionalHandler(direction, to));
            }
        }
        return outputs;
    }

    private void advanceImportPointer() {
        this.importPtr = (this.importPtr + 1) % this.distributor.getGroupCount();
        if (this.importPtr == this.exportPtr) {
            this.oneLapBehind = true;
        }
    }

    private void advanceExportPointer() {
        this.exportPtr = (this.exportPtr + 1) % this.distributor.getGroupCount();
    }

    public int getImportPtr() {
        return this.importPtr;
    }

    public int getExportPtr() {
        return this.exportPtr;
    }

    private static int transferItems(IItemHandler from, IItemHandler to, int amount) {
        int remaining = amount;
        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack extracted = from.extractItem(i, remaining, true);
            if (extracted.isEmpty()) {
                continue;
            }
            ItemStack remain = ItemHandlerHelper.insertItem(to, extracted, true);
            int toMove = extracted.getCount() - remain.getCount();
            if (toMove <= 0) {
                continue;
            }
            ItemStack moved = from.extractItem(i, toMove, false);
            ItemHandlerHelper.insertItem(to, moved, false);
            remaining -= moved.getCount();
            if (remaining <= 0) {
                break;
            }
        }
        return remaining;
    }

    private static int indexAfterDirection(List<DirectionalHandler> handlers, int directionOrdinal) {
        for (int i = 0; i < handlers.size(); i++) {
            if (handlers.get(i).direction().ordinal() == directionOrdinal) {
                return (i + 1) % handlers.size();
            }
        }
        return 0;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.putInt("importPtr", this.importPtr);
        tag.putInt("exportPtr", this.exportPtr);
        tag.putBoolean("oneLapBehind", this.oneLapBehind);
        tag.putInt("lastDirection", this.lastDirection);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        this.importPtr = tag.getInt("importPtr");
        this.exportPtr = tag.getInt("exportPtr");
        this.oneLapBehind = tag.getBoolean("oneLapBehind");
        this.lastDirection = tag.contains("lastDirection") ? tag.getInt("lastDirection") : -1;
    }

    record DirectionalHandler(Direction direction, IItemHandler handler) {}

    private record ExportResult(int moved, int lastDirection) {}
}
