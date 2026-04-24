package net.kogepan.clayium.inventory;

import net.kogepan.clayium.blockentities.MultitrackBufferBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Per-face item view for multitrack buffers: union of tracks visible for input and/or output on that face, with
 * per-track insert rules and separate insert vs extract routing. Used for
 * {@link MultitrackBufferBlockEntity#getExposedItemHandler}
 * and {@link MultitrackBufferBlockEntity#getAutomationItemHandler}; both wrap with {@link FilteredItemHandler} when a
 * side item filter is set.
 */
public final class MultitrackFaceItemHandler implements IItemHandler {

    private final MultitrackBufferBlockEntity blockEntity;
    private final Direction side;

    public MultitrackFaceItemHandler(@NotNull MultitrackBufferBlockEntity blockEntity, @NotNull Direction side) {
        this.blockEntity = blockEntity;
        this.side = side;
    }

    @Override
    public int getSlots() {
        return this.blockEntity.orderedVisibleTracks(this.side).length * this.blockEntity.getSlotsPerTrack();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        int[] tracks = this.blockEntity.orderedVisibleTracks(this.side);
        int slotsPerTrack = this.blockEntity.getSlotsPerTrack();
        int trackOrdinal = slot / slotsPerTrack;
        int localSlot = slot % slotsPerTrack;
        if (trackOrdinal < 0 || trackOrdinal >= tracks.length) {
            return ItemStack.EMPTY;
        }
        int track = tracks[trackOrdinal];
        if (!this.blockEntity.isTrackReadableOnFace(this.side, track)) {
            return ItemStack.EMPTY;
        }
        return this.blockEntity.getTrackInventory(track).getStackInSlot(localSlot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        int[] tracks = this.blockEntity.orderedVisibleTracks(this.side);
        int slotsPerTrack = this.blockEntity.getSlotsPerTrack();
        int trackOrdinal = slot / slotsPerTrack;
        int localSlot = slot % slotsPerTrack;
        if (trackOrdinal < 0 || trackOrdinal >= tracks.length) {
            return stack;
        }
        int track = tracks[trackOrdinal];
        if (!this.blockEntity.inputExposesTrack(this.side, track)) {
            return stack;
        }
        if (!this.blockEntity.passesTrackFilterForInsert(track, stack)) {
            return stack;
        }
        return this.blockEntity.getTrackInventory(track).insertItem(localSlot, stack, simulate);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int[] tracks = this.blockEntity.orderedVisibleTracks(this.side);
        int slotsPerTrack = this.blockEntity.getSlotsPerTrack();
        int trackOrdinal = slot / slotsPerTrack;
        int localSlot = slot % slotsPerTrack;
        if (trackOrdinal < 0 || trackOrdinal >= tracks.length) {
            return ItemStack.EMPTY;
        }
        int track = tracks[trackOrdinal];
        if (!this.blockEntity.outputExposesTrack(this.side, track)) {
            return ItemStack.EMPTY;
        }
        var inv = this.blockEntity.getTrackInventory(track);
        ItemStack preview = inv.extractItem(localSlot, amount, true);
        if (preview.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!this.blockEntity.passesTrackFilterForInsert(track, preview)) {
            return ItemStack.EMPTY;
        }
        return inv.extractItem(localSlot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        int[] tracks = this.blockEntity.orderedVisibleTracks(this.side);
        int slotsPerTrack = this.blockEntity.getSlotsPerTrack();
        int trackOrdinal = slot / slotsPerTrack;
        int localSlot = slot % slotsPerTrack;
        if (trackOrdinal < 0 || trackOrdinal >= tracks.length) {
            return 0;
        }
        int track = tracks[trackOrdinal];
        return this.blockEntity.getTrackInventory(track).getSlotLimit(localSlot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int[] tracks = this.blockEntity.orderedVisibleTracks(this.side);
        int slotsPerTrack = this.blockEntity.getSlotsPerTrack();
        int trackOrdinal = slot / slotsPerTrack;
        int localSlot = slot % slotsPerTrack;
        if (trackOrdinal < 0 || trackOrdinal >= tracks.length) {
            return false;
        }
        int track = tracks[trackOrdinal];
        if (!this.blockEntity.inputExposesTrack(this.side, track)) {
            return false;
        }
        if (!this.blockEntity.passesTrackFilterForInsert(track, stack)) {
            return false;
        }
        return this.blockEntity.getTrackInventory(track).isItemValid(localSlot, stack);
    }
}
