package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.ItemFilterHolderTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.inventory.FilteredItemHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractFilteredContainerBlockEntity extends ClayContainerBlockEntity {

    private static final int AUTO_IO_TIER = 6;
    public static final String GLOBAL_FILTER_TAG = "globalFilter";
    private static final String SAVED_FACING_TAG = "savedFacing";
    private boolean applyingSerializedFilter;

    private final ClayiumItemStackHandler globalFilterInventory = new ClayiumItemStackHandler(this) {

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            ItemStack filter = stack.copy();
            if (!filter.isEmpty()) {
                filter.setCount(1);
            }
            super.setStackInSlot(slot, filter);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!AbstractFilteredContainerBlockEntity.this.applyingSerializedFilter) {
                super.onContentsChanged(slot);
                AbstractFilteredContainerBlockEntity.this.sendContainerUpdate();
            }
        }
    };

    protected AbstractFilteredContainerBlockEntity(@NotNull BlockEntityType<?> type, @NotNull BlockPos pos,
                                                   @NotNull BlockState blockState) {
        super(type, pos, blockState, List.of(MachineIOMode.NONE, MachineIOMode.ALL),
                List.of(MachineIOMode.NONE, MachineIOMode.ALL));
        this.addTrait(new FilteredContainerAutoIOTrait(this));
    }

    @Override
    public void initDefaultRoutes() {
        this.inputModes.setMode(Direction.UP, MachineIOMode.ALL);
    }

    public final IItemHandlerModifiable getGlobalFilterInventory() {
        return this.globalFilterInventory;
    }

    @Override
    public void onPlacedByServer(@Nullable LivingEntity placer, ItemStack stack) {
        CompoundTag blockEntityData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        if (blockEntityData.contains(SAVED_FACING_TAG) &&
                this.getBlockState().getBlock() instanceof ClayContainerBlock containerBlock) {
            Direction savedFacing = Direction.from3DDataValue(blockEntityData.getInt(SAVED_FACING_TAG));
            Direction currentFacing = this.getBlockState().getValue(containerBlock.getFacingProperty());
            if (savedFacing.getAxis().isHorizontal() && currentFacing.getAxis().isHorizontal()) {
                Rotation rotation = horizontalRotationBetween(savedFacing, currentFacing);
                this.inputModes.rotate(rotation);
                this.outputModes.rotate(rotation);
                var trait = this.getTrait(ItemFilterHolderTrait.TRAIT_ID);
                if (trait instanceof ItemFilterHolderTrait filterHolder) {
                    filterHolder.rotateSides(rotation);
                }
                this.invalidateItemHandlerCapability();
            }
        }
        super.onPlacedByServer(placer, stack);
    }

    @NotNull
    public final ItemStack getGlobalFilter() {
        return this.globalFilterInventory.getStackInSlot(0);
    }

    protected final boolean matchesExactGlobalFilter(@NotNull ItemStack stack) {
        ItemStack filter = this.getGlobalFilter();
        return filter.isEmpty() || ItemStack.isSameItemSameComponents(filter, stack);
    }

    @Override
    @Nullable
    public IItemHandler getExposedItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        }

        IItemHandler handler = this.getPassiveItemHandler();
        var filter = this.getFilterForSide(side);
        return filter == null ? handler : new FilteredItemHandler(handler, filter);
    }

    protected abstract IItemHandler getPassiveItemHandler();

    @Override
    @NotNull
    protected List<IItemHandler> getInventoryHandlersForDrops() {
        return List.of();
    }

    @Override
    public boolean acceptsClayInterfaceSynchronization() {
        return false;
    }

    protected final void sendContainerUpdate() {
        this.setChanged();
        Level level = this.level;
        if (level != null && !level.isClientSide()) {
            BlockState state = this.getBlockState();
            level.sendBlockUpdated(this.getBlockPos(), state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.writeFilteredContainerData(tag, provider);
        if (this.getBlockState().getBlock() instanceof ClayContainerBlock containerBlock) {
            tag.putInt(SAVED_FACING_TAG,
                    this.getBlockState().getValue(containerBlock.getFacingProperty()).get3DDataValue());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.readFilteredContainerData(tag, provider);
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        this.writeFilteredContainerData(tag, provider);
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        this.readFilteredContainerData(tag, provider);
        super.onReceivePacket(tag, provider);
    }

    @Override
    protected boolean shouldRefreshModelDataForUpdate(@NotNull CompoundTag tag,
                                                      @NotNull HolderLookup.Provider provider) {
        if (tag.contains("inputModes") &&
                !this.inputModes.serializeNBT(provider).equals(tag.getCompound("inputModes"))) {
            return true;
        }
        if (tag.contains("outputModes") &&
                !this.outputModes.serializeNBT(provider).equals(tag.getCompound("outputModes"))) {
            return true;
        }

        var trait = this.getTrait(ItemFilterHolderTrait.TRAIT_ID);
        if (!(trait instanceof ItemFilterHolderTrait filterHolder) ||
                !tag.contains(ItemFilterHolderTrait.TRAIT_ID)) {
            return false;
        }
        return !filterHolder.hasSameClientFilterFlags(tag.getCompound(ItemFilterHolderTrait.TRAIT_ID));
    }

    private void writeFilteredContainerData(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.put(GLOBAL_FILTER_TAG, this.getGlobalFilter().saveOptional(provider));
    }

    private void readFilteredContainerData(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        ItemStack filter = ItemStack.parseOptional(provider, tag.getCompound(GLOBAL_FILTER_TAG));
        this.applyingSerializedFilter = true;
        try {
            this.globalFilterInventory.setStackInSlot(0, filter);
        } finally {
            this.applyingSerializedFilter = false;
        }
    }

    @NotNull
    private static Rotation horizontalRotationBetween(@NotNull Direction from, @NotNull Direction to) {
        for (Rotation rotation : Rotation.values()) {
            if (rotation.rotate(from) == to) {
                return rotation;
            }
        }
        throw new IllegalArgumentException("Directions must be horizontal: " + from + " -> " + to);
    }

    private static final class FilteredContainerAutoIOTrait extends AutoIOTrait.Combined {

        private FilteredContainerAutoIOTrait(@NotNull AbstractFilteredContainerBlockEntity blockEntity) {
            super(blockEntity, AUTO_IO_TIER, false);
        }

        @Override
        protected void importItemsFromNeighbors(int amount) {
            for (Direction direction : Direction.values()) {
                if (!this.canImport(direction)) {
                    continue;
                }
                IItemHandler source = this.blockEntity.getNeighborItemHandler(direction);
                IItemHandler destination = this.getImportTarget(direction);
                if (source != null && destination != null) {
                    int remaining = amount;
                    while (remaining > 0) {
                        int previousRemaining = remaining;
                        remaining = this.transferItems(source, destination, remaining);
                        if (remaining == previousRemaining) {
                            break;
                        }
                    }
                }
            }
        }

        @Override
        protected void exportItemsToNeighbors(int amount) {
            for (Direction direction : Direction.values()) {
                if (!this.canExport(direction)) {
                    continue;
                }
                IItemHandler source = this.blockEntity.getExposedItemHandler(direction);
                IItemHandler destination = this.blockEntity.getNeighborItemHandler(direction);
                if (source == null || destination == null) {
                    continue;
                }

                int remaining = amount;
                while (remaining > 0) {
                    int previousRemaining = remaining;
                    remaining = this.transferItems(source, destination, remaining);
                    if (remaining == previousRemaining) {
                        break;
                    }
                }
            }
        }
    }
}
