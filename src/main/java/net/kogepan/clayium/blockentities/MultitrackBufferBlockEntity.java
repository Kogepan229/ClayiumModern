package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.trait.MultitrackBufferAutoIOTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.client.ldlib.elements.PhantomItemSlot;
import net.kogepan.clayium.client.ldlib.textures.SlotTextures;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.inventory.FilteredItemHandler;
import net.kogepan.clayium.inventory.MultitrackFaceItemHandler;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultitrackBufferBlockEntity extends ClayContainerBlockEntity {

    private static final List<MachineIOMode> PLACEHOLDER_VALID = List.of(MachineIOMode.NONE, MachineIOMode.M_ALL);

    private final int trackCount;
    private final int slotsPerTrack;
    private final ClayiumItemStackHandler[] trackInventories;
    private final ClayiumItemStackHandler filterInventory;
    private final CombinedInvWrapper combinedItemInventory;

    public MultitrackBufferBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.MULTITRACK_BUFFER_BLOCK_ENTITY.get(), pos, blockState, PLACEHOLDER_VALID,
                PLACEHOLDER_VALID);

        this.trackCount = trackCountForTier(this.tier);
        this.slotsPerTrack = slotsPerTrackForTier(this.tier);

        this.trackInventories = new ClayiumItemStackHandler[this.trackCount];
        for (int i = 0; i < this.trackCount; i++) {
            final int trackIndex = i;
            this.trackInventories[i] = new ClayiumItemStackHandler(this, this.slotsPerTrack) {

                @Override
                public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                    return MultitrackBufferBlockEntity.this.passesTrackFilterForInsert(trackIndex, stack) &&
                            super.isItemValid(slot, stack);
                }

                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                    if (!MultitrackBufferBlockEntity.this.passesTrackFilterForInsert(trackIndex, stack)) {
                        return stack;
                    }
                    return super.insertItem(slot, stack, simulate);
                }
            };
        }
        this.filterInventory = new ClayiumItemStackHandler(this, this.trackCount);

        this.combinedItemInventory = new CombinedInvWrapper(this.trackInventories);

        this.addTrait(new MultitrackBufferAutoIOTrait(this, this.tier));
    }

    public static int trackCountForTier(int tier) {
        return switch (tier) {
            case 4 -> 2;
            case 5 -> 3;
            case 6 -> 4;
            case 7 -> 5;
            case 8, 9, 10, 11, 12, 13 -> 6;
            default -> throw new IllegalStateException("Invalid tier for multitrack buffer: " + tier);
        };
    }

    public static int slotsPerTrackForTier(int tier) {
        return switch (tier) {
            case 4 -> 1;
            case 5 -> 2;
            case 6, 7 -> 4;
            case 8 -> 6;
            case 9, 10, 11, 12, 13 -> 9;
            default -> throw new IllegalStateException("Invalid tier for multitrack buffer: " + tier);
        };
    }

    public int getTrackCount() {
        return this.trackCount;
    }

    public int getSlotsPerTrack() {
        return this.slotsPerTrack;
    }

    @NotNull
    public ClayiumItemStackHandler getTrackInventory(int track) {
        return this.trackInventories[track];
    }

    public boolean inputExposesTrack(@NotNull Direction side, int track) {
        if (track < 0 || track >= this.trackCount) {
            return false;
        }
        MachineIOMode mode = this.getInputMode(side);
        if (mode == MachineIOMode.M_ALL) {
            return true;
        }
        return mode.multitrackSingleTrackIndex().isPresent() && mode.multitrackSingleTrackIndex().getAsInt() == track;
    }

    public boolean outputExposesTrack(@NotNull Direction side, int track) {
        if (track < 0 || track >= this.trackCount) {
            return false;
        }
        MachineIOMode mode = this.getOutputMode(side);
        if (mode == MachineIOMode.M_ALL) {
            return true;
        }
        return mode.multitrackSingleTrackIndex().isPresent() && mode.multitrackSingleTrackIndex().getAsInt() == track;
    }

    /**
     * Passive side in paired MTB↔MTB auto-transfer: whether {@code face}'s output may supply {@code track}.
     * {@link MachineIOMode#NONE} treats all tracks as exposed (Original {@code route == -1} extract).
     */
    public boolean pairedPassiveOutputAllowsTrack(@NotNull Direction face, int track) {
        if (track < 0 || track >= this.trackCount) {
            return false;
        }
        MachineIOMode mode = this.getOutputMode(face);
        if (mode == MachineIOMode.NONE || mode == MachineIOMode.M_ALL) {
            return true;
        }
        return mode.multitrackSingleTrackIndex().isPresent() && mode.multitrackSingleTrackIndex().getAsInt() == track;
    }

    /**
     * Passive side in paired MTB↔MTB auto-transfer: whether {@code face}'s input may receive {@code track}.
     * {@link MachineIOMode#NONE} treats all tracks as open (Original {@code route == -1} insert).
     */
    public boolean pairedPassiveInputAllowsTrack(@NotNull Direction face, int track) {
        if (track < 0 || track >= this.trackCount) {
            return false;
        }
        MachineIOMode mode = this.getInputMode(face);
        if (mode == MachineIOMode.NONE || mode == MachineIOMode.M_ALL) {
            return true;
        }
        return mode.multitrackSingleTrackIndex().isPresent() && mode.multitrackSingleTrackIndex().getAsInt() == track;
    }

    /**
     * One track's slots for extract through {@code face}: side item filter always; track phantom filter when
     * {@link #outputExposesTrack} (routed output), matching Original {@code canExtractItemUnsafe} for non-{@code -1}
     * routes.
     */
    @NotNull
    public IItemHandler getTrackInventoryForExtractThroughFace(@NotNull Direction face, int track) {
        return new TrackInventoryExtractThroughFace(this, face, track);
    }

    public boolean isTrackReadableOnFace(@NotNull Direction side, int track) {
        return this.inputExposesTrack(side, track) || this.outputExposesTrack(side, track);
    }

    public boolean passesTrackFilterForInsert(int track, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        ItemStack filterStack = this.filterInventory.getStackInSlot(track);
        if (filterStack.isEmpty()) {
            return true;
        }
        ItemFilterData data = filterStack.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
        if (data != null) {
            return data.test(stack);
        }
        return ItemStack.isSameItemSameComponents(filterStack, stack);
    }

    /**
     * Used by auto I/O when inserting into a single track: side filter + track filter + inventory rules.
     */
    public boolean canAcceptInsertOnTrackForAutoIo(@NotNull Direction face, int track, @NotNull ItemStack stack) {
        if (!this.inputExposesTrack(face, track)) {
            return false;
        }
        if (!this.passesTrackFilterForInsert(track, stack)) {
            return false;
        }
        ItemFilterData sideFilter = this.getFilterForSide(face);
        return sideFilter == null || sideFilter.test(stack);
    }

    /**
     * Passive neighbor in paired MTB export: insert allowed when {@link #pairedPassiveInputAllowsTrack} passes
     * (including NONE = all tracks), plus track phantom and face item filters — not {@link #inputExposesTrack}.
     */
    public boolean canAcceptPassivePairedInsertOnTrack(@NotNull Direction face, int track, @NotNull ItemStack stack) {
        if (!this.pairedPassiveInputAllowsTrack(face, track)) {
            return false;
        }
        if (!this.passesTrackFilterForInsert(track, stack)) {
            return false;
        }
        ItemFilterData sideFilter = this.getFilterForSide(face);
        return sideFilter == null || sideFilter.test(stack);
    }

    @NotNull
    private IItemHandler createFaceItemHandlerWithSideFilter(@NotNull Direction side) {
        IItemHandler face = new MultitrackFaceItemHandler(this, side);
        ItemFilterData sideFilter = this.getFilterForSide(side);
        if (sideFilter != null) {
            return new FilteredItemHandler(face, sideFilter);
        }
        return face;
    }

    /**
     * Per-face handler for auto I/O (multitrack routes + track filters + side item filter when set).
     */
    @NotNull
    public IItemHandler getAutomationItemHandler(@NotNull Direction side) {
        return this.createFaceItemHandlerWithSideFilter(side);
    }

    public int @NotNull [] orderedVisibleTracks(@NotNull Direction side) {
        IntSortedSet tracks = new IntAVLTreeSet();
        this.collectTracksForInput(side, tracks);
        this.collectTracksForOutput(side, tracks);
        return tracks.toIntArray();
    }

    private void collectTracksForInput(@NotNull Direction side, IntSortedSet out) {
        MachineIOMode mode = this.getInputMode(side);
        if (mode == MachineIOMode.M_ALL) {
            for (int t = 0; t < this.trackCount; t++) {
                out.add(t);
            }
        } else {
            mode.multitrackSingleTrackIndex().ifPresent(i -> {
                if (i < this.trackCount) {
                    out.add(i);
                }
            });
        }
    }

    private void collectTracksForOutput(@NotNull Direction side, IntSortedSet out) {
        MachineIOMode mode = this.getOutputMode(side);
        if (mode == MachineIOMode.M_ALL) {
            for (int t = 0; t < this.trackCount; t++) {
                out.add(t);
            }
        } else {
            mode.multitrackSingleTrackIndex().ifPresent(i -> {
                if (i < this.trackCount) {
                    out.add(i);
                }
            });
        }
    }

    /**
     * Tracks whose <em>input</em> route on {@code side} accepts inserts (for auto I/O and per-track transfer).
     */
    public int @NotNull [] tracksExposedForInputOnFace(@NotNull Direction side) {
        IntSortedSet set = new IntAVLTreeSet();
        this.collectTracksForInput(side, set);
        return set.toIntArray();
    }

    /**
     * Tracks whose <em>output</em> route on {@code side} allows extract (for auto I/O and per-track transfer).
     */
    public int @NotNull [] tracksExposedForOutputOnFace(@NotNull Direction side) {
        IntSortedSet set = new IntAVLTreeSet();
        this.collectTracksForOutput(side, set);
        return set.toIntArray();
    }

    @Override
    @NotNull
    protected List<MachineIOMode> getCycleValidInputModes(@NotNull Direction direction) {
        return multitrackModesFor(this.trackCount);
    }

    @Override
    @NotNull
    protected List<MachineIOMode> getCycleValidOutputModes(@NotNull Direction direction) {
        return multitrackModesFor(this.trackCount);
    }

    private static List<MachineIOMode> multitrackModesFor(int tracks) {
        List<MachineIOMode> list = new ArrayList<>();
        list.add(MachineIOMode.NONE);
        list.add(MachineIOMode.M_ALL);
        for (int i = 0; i < tracks; i++) {
            list.add(MachineIOMode.fromMode(MachineIOMode.M_1.getMode() + i));
        }
        return List.copyOf(list);
    }

    @Override
    public void initDefaultRoutes() {
        BlockState blockState = this.getBlockState();
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            this.inputModes.setMode(blockState.getValue(containerBlock.getFacingProperty()).getOpposite(),
                    MachineIOMode.M_ALL);
        }
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.combinedItemInventory;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.combinedItemInventory;
    }

    @Override
    @Nullable
    public IItemHandler getExposedItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        }
        return this.createFaceItemHandlerWithSideFilter(side);
    }

    @Override
    protected void invalidateItemHandlerCapability() {}

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        for (int i = 0; i < this.trackCount; i++) {
            tag.put("track_" + i, this.trackInventories[i].serializeNBT(provider));
        }
        tag.put("filterInventory", this.filterInventory.serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        for (int i = 0; i < this.trackCount; i++) {
            String key = "track_" + i;
            if (tag.contains(key)) {
                this.trackInventories[i].deserializeNBT(provider, tag.getCompound(key));
            }
        }
        if (tag.contains("filterInventory")) {
            this.filterInventory.deserializeNBT(provider, tag.getCompound("filterInventory"));
        }
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement tracksElement = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.COLUMN)
                .justifyContent(AlignContent.CENTER));
        root.addChild(tracksElement);
        for (int t = 0; t < this.trackCount; t++) {
            final int track = t;
            UIElement rowElement = new UIElement().layout(
                    layout -> layout.flexDirection(FlexDirection.ROW).justifyContent(AlignContent.CENTER));
            tracksElement.addChild(rowElement);
            for (int s = 0; s < this.slotsPerTrack; s++) {
                int index = s + track * this.slotsPerTrack;
                rowElement.addChild(new ItemSlot()
                        .bind(new ItemHandlerSlot(this.combinedItemInventory, index))
                        .style(style -> style.backgroundTexture(SlotTextures.multitrackItemSlotForTrack(track))));
            }
            UIElement phantomWithGap = new UIElement().layout(layout -> layout.marginLeft(4));
            phantomWithGap.addChild(PhantomItemSlot.create(this.filterInventory, track)
                    .style(style -> style.backgroundTexture(SlotTextures.multitrackPhantomFilterSlotForTrack(track))));
            rowElement.addChild(phantomWithGap);
        }
    }

    /**
     * Extract-only wrapper for one track and face (paired MTB transfer and similar). Side filter always; track phantom
     * only when that face has routed output ({@link #outputExposesTrack}), not for passive NONE output (Original route
     * {@code -1}).
     */
    private static final class TrackInventoryExtractThroughFace implements IItemHandler {

        private final MultitrackBufferBlockEntity blockEntity;
        private final ClayiumItemStackHandler inventory;
        private final int track;
        @Nullable
        private final ItemFilterData sideFilter;
        private final boolean enforceTrackPhantomOnExtract;

        private TrackInventoryExtractThroughFace(@NotNull MultitrackBufferBlockEntity blockEntity,
                                                 @NotNull Direction face, int track) {
            this.blockEntity = blockEntity;
            this.inventory = blockEntity.getTrackInventory(track);
            this.track = track;
            this.sideFilter = blockEntity.getFilterForSide(face);
            this.enforceTrackPhantomOnExtract = blockEntity.outputExposesTrack(face, track);
        }

        @Override
        public int getSlots() {
            return this.inventory.getSlots();
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            return this.inventory.getStackInSlot(slot);
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack preview = this.inventory.extractItem(slot, amount, true);
            if (preview.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (this.enforceTrackPhantomOnExtract &&
                    !this.blockEntity.passesTrackFilterForInsert(this.track, preview)) {
                return ItemStack.EMPTY;
            }
            if (this.sideFilter != null && !this.sideFilter.test(preview)) {
                return ItemStack.EMPTY;
            }
            return this.inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return this.inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return this.inventory.isItemValid(slot, stack);
        }
    }

    /**
     * Insert-only view of one track for paired MTB auto I/O. When {@code passivePairedNeighborInsert}, uses
     * {@link #canAcceptPassivePairedInsertOnTrack} (neighbor receiving our export with NONE/M_ALL/M_n semantics).
     */
    public static final class TrackInsertHandler implements IItemHandler {

        private final MultitrackBufferBlockEntity blockEntity;
        private final Direction face;
        private final int track;
        private final boolean passivePairedNeighborInsert;

        public TrackInsertHandler(@NotNull MultitrackBufferBlockEntity blockEntity, @NotNull Direction face,
                                  int track) {
            this(blockEntity, face, track, false);
        }

        public TrackInsertHandler(@NotNull MultitrackBufferBlockEntity blockEntity, @NotNull Direction face,
                                  int track, boolean passivePairedNeighborInsert) {
            this.blockEntity = blockEntity;
            this.face = face;
            this.track = track;
            this.passivePairedNeighborInsert = passivePairedNeighborInsert;
        }

        private boolean canInsertStack(@NotNull ItemStack stack) {
            return this.passivePairedNeighborInsert ?
                    this.blockEntity.canAcceptPassivePairedInsertOnTrack(this.face, this.track, stack) :
                    this.blockEntity.canAcceptInsertOnTrackForAutoIo(this.face, this.track, stack);
        }

        @Override
        public int getSlots() {
            return this.blockEntity.getTrackInventory(this.track).getSlots();
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            return this.blockEntity.getTrackInventory(this.track).getStackInSlot(slot);
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (!this.canInsertStack(stack)) {
                return stack;
            }
            return this.blockEntity.getTrackInventory(this.track).insertItem(slot, stack, simulate);
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return this.blockEntity.getTrackInventory(this.track).getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return this.canInsertStack(stack) &&
                    this.blockEntity.getTrackInventory(this.track).isItemValid(slot, stack);
        }
    }
}
